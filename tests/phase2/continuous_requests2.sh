#!/bin/bash
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

# In this testcase, there is only one cab. However,
# all 3 customers keep sending ride requests continously until
# their requests are accepted. To pass, all three customers
# should eventually get rides

# This testcase puts more load on the system. Each customer continuosly
# sends requests until they have successfully booked 10 rides each

testPassing="yes"

function signIn() {
    # Parameters - $1: cabId
    resp=$(curl -s "http://localhost:8080/signIn?cabId=$1&initialPos=0")
    if [ "$resp" = "true" ];
    then
        echo "Cab $1 signed in"
    else
        echo "Cab $1 could not sign in"
        testPassing="no"
    fi
}

function continuousRequest() {
    # Parameters: $1 - custId, $2 - sourceLoc, $3 - destinationLoc
    for i in {1..10}
    do
        rideId="-1"
        while [ "$rideId" = "-1" ]
        do
            rideStatus=$(curl -s "http://localhost:8081/requestRide?custId=$1&sourceLoc=$2&destinationLoc=$3")
            rideId=$(echo $rideStatus | { read a b c; echo $a; })
            cabId=$(echo $rideStatus | { read a b c; echo $b; })

            if [ "$rideId" != "-1" ]
            then
                echo "Ride $rideId for customer $1 started"
            fi
        done

        resp=$(curl -s "http://localhost:8080/rideEnded?cabId=$cabId&rideId=$rideId")
        if [ "$resp" != "true" ]
        then
            echo "Ride $rideId for customer $1 couldn't be ended"
        else
            echo "Ride $rideId for customer $1 ended"
        fi
    done
}

signIn 101

continuousRequest 201 0 10 &
continuousRequest 202 10 30 &
continuousRequest 203 15 50 &
wait

# Now, the number of rides for cab 101 should be 3
num=$(curl -s "http://localhost:8080/numRides?cabId=101")

if [ "$num" != "30" ]
then
    echo "Wrong number of rides: is $num, should be 3"
    testPassing="no"
else
    echo "Correct number of rides"
fi

echo "Test Passing Status: $testPassing"
