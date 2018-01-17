#!/bin/sh
cd Server
mvn clean compile
echo "enter 1 for reactor, 2 for tpc server"
select server in BBreactor.ReactorMain BBtpc.TPCMain
do
if [ -e Database/Users1.json ]
then
    rm Database/Users.json
    mv Database/Users1.json Database/Users.json
fi
cp Database/Users.json Database/Users1.json
if [ -e Database/Movies1.json ]
then
    rm Database/Movies.json
    mv Database/Movies1.json Database/Movies.json
fi
cp Database/Movies.json Database/Movies1.json
mvn exec:java -Dexec.mainClass=bgu.spl181.net.impl.$server -Dexec.args="7777"
done
exit
