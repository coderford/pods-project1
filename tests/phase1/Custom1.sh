#! /bin/sh
# This test case checks whether a customer can request
# a ride while already riding a cab

# reset RideService and Wallet.
# every test case should begin with these two steps
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

testPassed="yes"

#cab 101 signs in
resp=$(curl -s "http://localhost:8080/signIn?cabId=101&initialPos=0")
if [ "$resp" = "true" ];
then
    echo "Cab 101 signed in"
else
    echo "Cab 101 could not sign in"
    testPassed="no"
fi

#customer 201 requests a ride
rideId=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=2&destinationLoc=10")
if [ "$rideId" != "-1" ];
then
    echo "Ride by customer 201 started"
else
    echo "Ride by customer 201 denied"
    testPassed="no"
fi

#customer 201 requests another ride, while already in a cab
rideId=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=22&destinationLoc=12")
if [ "$rideId" != "-1" ];
then
    echo "Second ride by customer 201 started"
    testPassed="no"
else
    echo "Second ride to customer 201 denied"
fi

echo "Test Passing Status: " $testPassed
