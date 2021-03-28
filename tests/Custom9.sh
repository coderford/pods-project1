#! /bin/sh
# This test checks whether negative source and destination locations
# are accepted while requesting ride

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

#cab 102 signs in
resp=$(curl -s "http://localhost:8080/signIn?cabId=102&initialPos=0")
if [ "$resp" = "true" ];
then
    echo "Cab 102 signed in"
else
    echo "Cab 102 could not sign in"
    testPassed="no"
fi

#customer 201 requests a ride with negative source location
rideId=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=-20&destinationLoc=10")
if [ "$rideId" != "-1" ];
then
    echo "Ride with negative source by customer 201 started"
    testPassed="no"
else
    echo "Ride with negative source by customer 201 denied"
fi

#customer 202 requests a ride with negative destination location
rideId=$(curl -s "http://localhost:8081/requestRide?custId=202&sourceLoc=20&destinationLoc=-10")
if [ "$rideId" != "-1" ];
then
    echo "Ride with negative dest by customer 202 started"
    testPassed="no"
else
    echo "Ride with negative dest by customer 202 denied"
fi

echo "Test Passing Status: " $testPassed
