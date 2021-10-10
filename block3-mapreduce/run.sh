#!/usr/bin/env bash

OUT_DIR="/demidova/output"

hadoop fs -rm -r -skipTrash ${OUT_DIR} > /dev/null

hadoop jar /opt/hadoop-3.2.1/share/hadoop/tools/lib/hadoop-streaming-3.2.1.jar \
    -file /home/student/demidova/mapper.py \
    -file /home/student/demidova/reducer.py \
    -mapper mapper.py \
    -reducer reducer.py \
    -input /demidova/AB_NYC_2019.csv \
    -output ${OUT_DIR}

