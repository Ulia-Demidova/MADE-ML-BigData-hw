#!/usr/bin/env python3
import sys
import csv

data = sys.stdin.readlines()

ch_size = 1
var = 0
key = 'key'
for line in csv.reader(data):
    try:
        price = float(line[9])
    except ValueError:
        continue

    print('%s\t%s\t%s\t%s' % (key, ch_size, price, var))

