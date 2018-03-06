#!/bin/sh

rm -f tpid

nohup java -Xms64m -Xmx256m -jar mcconference-service-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &

echo $! > tpid

echo Start Success!
