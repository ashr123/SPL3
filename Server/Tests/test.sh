#!/bin/sh
echo "opening terminal for server managment and terminal for client managment"
gnome-terminal --tab -e "bash Tests/server.sh" --tab -e "bash Tests/client.sh"