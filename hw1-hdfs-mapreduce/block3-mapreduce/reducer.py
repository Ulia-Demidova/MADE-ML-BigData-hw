#!/usr/bin/env python3

import sys

cur_key = None
cur_mean = 0
cur_var = 0
cur_size = 0

for line in sys.stdin:

    try:
        key, size, mean, var = line.strip().split('\t')
        mean = float(mean)
        var = float(var)
        size = int(size)
    except ValueError as e:
        continue

    if cur_key != key:
        if cur_key:
            print('%s\t%s' % (cur_mean, cur_var))
        cur_key = key
        cur_mean = 0
        cur_var = 0
        cur_size = 0

    cur_var = (cur_size * cur_var + size * var) / (cur_size + size) + \
                    cur_size * size * ((cur_mean - mean) / (cur_size + size)) ** 2

    cur_mean = (cur_size * cur_mean + size * mean) / (cur_size + size)

    cur_size += size

if cur_key:
    print('%s\t%s' % (cur_mean, cur_var))
