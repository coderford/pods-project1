#!/bin/bash
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

# In this test case, all 3 customers request a ride,
# while only 2 cabs are available. To pass, exactly 2
# of the ride requests must go through

testPassing="yes"

function signIn() {
    # Parameters - $1: cabId
    resp=$(curl -s "http://localhost:8080/signIn?cabId=$1&initialPos=100")
    if [ "$resp" = "true" ];
    then
        echo "Cab $1 signed in"
    else
        echo "Cab $1 could not sign in"
        testPassing="no"
    fi
}

function requestRide() {
    # Parameters: $1 - custId, $2 - sourceLoc, $3 - destinationLoc
    resp=$(curl -s "http://localhost:8081/requestRide?custId=$1&sourceLoc=$2&destinationLoc=$3")
    rideId=$( echo $resp | { read a b c; echo $a; } )
    cabId=$(echo $resp | { read a b c; echo $b; } )
    if [ "$rideId" != "-1" ]
    then
        echo "Ride $rideId for customer $1 with cab $cabId started"
        echo 1 > "$4"
    else
        echo "Ride for customer $1 denied"
        echo 0 > "$4"
    fi
}

# Run signin requests in parellel
signIn 101 & signIn 102 &
wait

# Run ride requests in parellel, and count accepted ones
requestRide 201 0 10 tmp/a1 &
requestRide 202 10 50 tmp/a2 &
requestRide 203 50 15 tmp/a3 &
wait

acount=$(expr $(cat tmp/a1) + $(cat tmp/a2) + $(cat tmp/a3))
if [ "$acount" != "2" ]
then
    testPassing="no"
fi

echo "Test Passing Status: $testPassing"
