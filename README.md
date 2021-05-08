# Simple implementation of `Change data capture` pattern with `Debezium` and `Apache Camel`

[Change data capture pattern](https://en.wikipedia.org/wiki/Change_data_capture)
[Debezium](https://debezium.io/)
[Apache Camel](https://camel.apache.org/)

## Goal
1. Initial fill destination database form source
1. Send changes to destination database

## Start
```sh
make up
```

## Restart
```sh
rm /tmp/offset.dat
```
