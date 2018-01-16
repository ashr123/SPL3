#!/bin/sh
cd Client
make clean
make
echo "enter 1 for Admin test or 2 for admin and then client test or 3 for two clients at the same time"
select test in Admin1 AdminAndClient 2clientsTheSameTime
do
if [ "$test" = "AdminAndClient" ]; then
        expect ../Tests/Admin1.exp && expect ../Tests/client1.exp
else if [ "$test" = "2clientsTheSameTime" ]; then
   gnome-terminal --tab -e "bash -c \"expect ../Tests/client2.exp; exec bash\"" --tab -e "bash -c \"expect ../Tests/client3.exp; exec bash\"" 
    else if [ "$test" = "Admin1" ]; then
    expect ../Tests/Admin1.exp
    fi
fi
fi
done
exec bash
