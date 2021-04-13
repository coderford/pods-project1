#!/bin/bash
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

function f1() {
    rm -f tmp/sh1out

    #Step 1 : cab 101 signs in
    resp=$(curl -s "http://localhost:8080/signIn?cabId=101&initialPos=100")
    if [ "$resp" = "true" ];
    then
        echo "Cab 101 signed in"
    else
        echo "Cab 101 could not sign in"
    fi

    #Step 2: customer 201 requests a cab
    rideDetails=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=110&destinationLoc=200")
    rideId=$(echo $rideDetails | cut -d' ' -f 1)
    cabId=$(echo $rideDetails | cut -d' ' -f 2)
    fare=$(echo $rideDetails | cut -d' ' -f 3)
    if [ "$rideId" != "-1" ];
    then
        echo "Ride by customer 201 started"
        echo $fare >> tmp/sh1out
    else
        echo "Ride to customer 201 denied"
    fi
}

function f2() {
    rm -f tmp/sh2out

    #Step 1 : cab 102 signs in
    resp=$(curl -s "http://localhost:8080/signIn?cabId=102&initialPos=100")
    if [ "$resp" = "true" ];
    then
        echo "Cab 102 signed in"
    else
        echo "Cab 102 could not sign in"
    fi

    #Step 2: customer 202 requests a cab
    rideDetails=$(curl -s "http://localhost:8081/requestRide?custId=202&sourceLoc=90&destinationLoc=200")
    rideId=$(echo $rideDetails | cut -d' ' -f 1)
    cabId=$(echo $rideDetails | cut -d' ' -f 2)
    fare=$(echo $rideDetails | cut -d' ' -f 3)
    if [ "$rideId" != "-1" ];
    then
        echo "Ride by customer 202 started"
        echo $fare >> tmp/sh2out
    else
        echo "Ride to customer 202 denied"
    fi
}

# Run two test scripts in parellel

f1 & f2 &

# f1 creates the output file sh1out, which contains fares
# of all rides given in sh1. Similarly, sh2out.

wait

totalFare=0
for i in $(cat tmp/sh1out tmp/sh2out)
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
