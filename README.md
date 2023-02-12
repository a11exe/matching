# Concurrent transaction matching

## Description
This app demonstrates matching between two types of transaction.
Matching process runs right after saving each transaction.
Transactions can be matched by sum. 
There are two types of transactions. 
Transaction1 type can be matched only with one Transaction2 type by sum.
But Transaction2 type can be matched with multiple Transaction1 type.


*For example, we have these transactions:*

- *Transaction1: id 1, amount 100.00*
- *Transaction1: id 2, amount 100.00*
- *Transaction2: id 3, amount 200.00*
- *Transaction2: id 4, amount 50.00*

*Transaction 2 id 4 can't be matched with any Transaction1, because amount 50.00 don't enough for matching.*

*Transaction 1 id 1 -> matched Transaction2: id 3 with amount 100*

*Transaction 1 id 2 -> matched Transaction2: id 3 with amount 100*

## Clone
 ```git clone git@github.com:a11exe/matching.git```

## Build
    ./gradlew clean build

## Run
    java -jar ./build/libs/matching-*-SNAPSHOT.jar

## Api
Load transactions type 1 and after load transactions type 2 (no concurrent problem)

```curl http://localhost:8080/api/match/1```

Load transactions type 2 and after load transactions type 1 (no concurrent problem)

```curl http://localhost:8080/api/match/2```

Load transactions type 1 and type 2 parallel (demonstrate concurrent problem)

```curl http://localhost:8080/api/match/parallel```

Load transactions type 1 and type 2 parallel (demonstrate decision concurrent problem)

```curl http://localhost:8080/api/match/concurrent/parallel```

## H2 database credentials
H2 database console http://localhost:8080/h2-console/
```
jdbc url: jdbc:h2:mem:testdb
login: sa
password: password
```