package solution

import org.apache.spark.sql._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

import math.log

object SparkTfIdf {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("made-demo")
      .getOrCreate()

    import spark.implicits._


    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("tripadvisor_hotel_reviews.csv")


    val df1 = df.withColumn("Review", regexp_replace($"Review", "n't", "not"))
    val without_punct = df1.withColumn("Review", trim(regexp_replace($"Review", """\p{Punct}""", " ")))

    val splitted = without_punct.select(split(lower(Symbol("Review")), "\\s+").alias("words"))

    val docs = splitted
      .withColumn("doc_id", row_number().over(Window.orderBy(monotonically_increasing_id())) - 1)
      .withColumn("doc_len", size(col("words")))


    val flatten = docs
      .select(
        explode(col("words")).as("word"),
        col("doc_id"),
        col("doc_len"))

    val count = flatten.groupBy("word", "doc_id", "doc_len").count()
    val tf = count.select(
      col("word"),
      col("doc_id"),
      (col("count") / col("doc_len")).as("tf"))


    val doc_count = flatten
      .groupBy("word")
      .agg(countDistinct("doc_id").as("df"))
      .orderBy(desc("df"))
      .limit(100)


    val total_doc_count = df.count

    val idf_func = udf((df: Long) => {
      log(total_doc_count / df)
    })

    val idf = doc_count.withColumn("idf", idf_func(col("df"))).drop("df")

    val tf_idf = tf.join(idf, Seq("word"), "inner")
      .withColumn("tf_idf", round(col("tf") * col("idf"), 4))
      .drop("tf", "idf")

    val pivoted = tf_idf.groupBy("doc_id").pivot("word").sum("tf_idf").na.fill(0.0)
    pivoted.show
  }
}

