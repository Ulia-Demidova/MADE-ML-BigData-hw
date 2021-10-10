import pandas as pd
import numpy as np

data_path = "AB_NYC_2019.csv"

data = pd.read_csv(data_path)

print(data["price"].values.mean(), data["price"].values.var(), sep='\t')
