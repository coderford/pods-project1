#!/bin/bash
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

# Run two test scripts in parellel

bash sh1 & bash sh2

# sh1 creates the output file sh1out, which contains fares
# of all rides given in sh1. Similarly, sh2out.

wait

totalFare=0
for i in $(cat sh1out sh2out)
do
    totalFare=$(expr $totalFare + $i)
done
# totalFare contains the sum cost of all rides

# Now check if current total balance
# in all wallets is equal to 
# original total balance in all wallets (which is a constant)
# MINUS totalFare.
b1=$(curl -s "localhost:8082/getBalance?custId=201")
b2=$(curl -s "localhost:8082/getBalance?custId=202")
b3=$(curl -s "localhost:8082/getBalance?custId=203")
totalBalance=$(expr $b1 + $b2 + $b3)

echo "totalBalance = $totalBalance"
echo "totalFare = $totalFare"

if [ "$totalBalance" = "$(expr 30000 - $totalFare)" ]
then
    echo "Test Passing Status: yes"
else
    echo "Test Passing Status: no"
fi
