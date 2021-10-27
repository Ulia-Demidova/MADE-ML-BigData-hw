package solution

import java.io._

import scala.language.postfixOps
import scala.util.control.Breaks._
import scala.util.Random

import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._


object LinearRegression {  // train_path, test_path, log_path, prediction_output_path
  def main(args: Array[String]): Unit = {
    val w = train(args(0), 1000, 0.01, args(2))
    test(args(1), args(3), w)
  }

  def loss(preds: DenseVector[Double], targets: DenseVector[Double]) : Double = {
    return sum(pow(preds - targets, 2)) / preds.size
  }

  def predict(x: DenseMatrix[Double], w: DenseVector[Double]) : DenseVector[Double] = {
    return x * w
  }

  def grad(x: DenseMatrix[Double], w: DenseVector[Double], y: DenseVector[Double]) : DenseVector[Double] = {
    var g = x.t * (x * w - y)
    g :*= 2.0 / y.size
    return g
  }

  def split(X: DenseMatrix[Double],
            y: DenseVector[Double],
            ratio: Double): (DenseMatrix[Double], DenseMatrix[Double], DenseVector[Double], DenseVector[Double]) = {
    val size = y.size
    val idx = 0 until size toList
    val idx_shuffled = Random.shuffle(idx)

    val train_size = (ratio * size).toInt
    val train_idx = idx_shuffled.slice(0, train_size)
    val val_idx = idx_shuffled.slice(train_size, size)
    val X_train = X(train_idx, ::).toDenseMatrix
    val X_val = X(val_idx, ::).toDenseMatrix
    val y_train = y(train_idx).toDenseVector
    val y_val = y(val_idx).toDenseVector
    return (X_train, X_val, y_train, y_val)
  }

  def read_dataset(dataset_path: String): (DenseMatrix[Double], DenseVector[Double]) = {
    val m = csvread(new File(dataset_path))
    val feature_size = m.cols - 1
    val X = DenseMatrix.horzcat(m(::, 0 until feature_size), DenseMatrix.ones[Double](m.rows, 1))
    val y = m(::, feature_size)
    return (X, y)
  }

  def train(train_dataset_path: String,
            iter_n: Int, alpha: Double, log_path: String): DenseVector[Double] = {

    val (x: DenseMatrix[Double], y: DenseVector[Double]) = read_dataset(train_dataset_path)

    val (x_train : DenseMatrix[Double],
    x_val   : DenseMatrix[Double],
    y_train : DenseVector[Double],
    y_val   : DenseVector[Double]) = split(x, y, 0.8)

    var w = DenseVector.rand(x_train.cols, Rand.uniform)
    val tolerance = 1e-5
    val pw = new PrintWriter(new File(log_path))
    pw.write("Train...\n")
    breakable {
      for (iter <- 0 until iter_n) {
        val val_loss = loss(predict(x_val, w), y_val)
        val train_loss = loss(predict(x_train, w), y_train)
        pw.write(s"Iter $iter, train_loss: $train_loss, val_loss: $val_loss \n")
        val w1 = w - alpha * grad(x_train, w, y_train)
        if (norm(w - w1) < tolerance) {
          w = w1
          break
        } else { w = w1 }
      }
    }
    pw.close()
    return w
  }

  def test(test_dataset_path: String, output_path: String, w: DenseVector[Double]): Unit = {
    val (x_test: DenseMatrix[Double], y_test: DenseVector[Double]) = read_dataset(test_dataset_path)
    val pw = new PrintWriter(new File(output_path))
    val preds = predict(x_test, w)
    pw.write(preds.data.mkString("\n"))
    pw.close()
    println("Final MSE for the test dataset: " + loss(preds, y_test))
  }
}
