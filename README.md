# Kafka sandbox

## Input data

Weather data taken from http://pogodarybnik.pl

Producer running as a cron job every minute:\
``curl -s http://pogodarybnik.pl/realtime.txt | /home/kafka/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic pogodarybnik``

You can use my data from 2020-12-11 to 2020-12-16 attached to the project:\
``gunzip data/pogodarybnik.kfk.gz``\
``bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic pogodarybnik <data/pogodarybnik.kfk``
