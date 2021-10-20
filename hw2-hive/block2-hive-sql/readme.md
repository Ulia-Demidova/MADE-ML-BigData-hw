Работа с HIVE
==============================

Поднять локально HIVE:
~~~
git clone https://github.com/tech4242/docker-hadoop-hive-parquet
cd docker-hadoop-hive-parquet
docker-compose up
~~~

Датасет для работы: https://www.kaggle.com/pieca111/music-artists-popularity

Проще всего создать и загрузить таблицу оказалось через beeline:
~~~
docker cp artists.csv {container_id}:/opt/artists.csv
docker-compose exec hive-server bash
sed -i '1d' artists.csv  # удалить header 
/opt/hive/bin/beeline -u jdbc:hive2://localhost:10000
create table artists (mbid string, 
                      artist_mb string, 
                      artist_lastfm string, 
                      country_mb string, 
                      country_lastfm string, 
                      tags_mb string, 
                      tags_lastfm string, 
                      listeners_lastfm int, 
                      scrobbles_lastfm int, 
                      ambiguous_artist boolean) 
                      row format delimited fields terminated by ',';
load data local inpath '/opt/artists.csv' overwrite into table artists;
~~~

Дальше запросы через PyCharm (команды и результаты в файле results.txt)