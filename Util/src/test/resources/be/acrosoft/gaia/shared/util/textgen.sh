#!/bin/bash
for ((i=1;i<=$2;i++));
do
 echo $3
done
echo before pause
sleep $4
echo after pause
exit $1
