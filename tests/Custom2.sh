#! /bin/sh
# This test case checks whether multiple customers can get rides,
# when enough cabs are available.

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

#cab 103 signs in
resp=$(curl -s "http://localhost:8080/signIn?cabId=103&initialPos=10")
if [ "$resp" = "true" ];
then
    echo "Cab 103 signed in"
else
    echo "Cab 103 could not sign in"
    testPassed="no"
fi

#customer 201 requests a ride
rideId=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=2&destinationLoc=10")
if [ "$rideId" != "-1" ];
then
    echo "Ride by customer 201 started"
else
    echo "Ride to customer 201 denied"
    testPassed="no"
fi

#customer 202 requests a ride
rideId=$(curl -s "http://localhost:8081/requestRide?custId=202&sourceLoc=1&destinationLoc=11")
if [ "$rideId" != "-1" ];
then
    echo "Ride by customer 202 started"
else
    echo "Ride to customer 202 denied"
    testPassed="no"
fi

echo "Test Passing Status: " $testPassed
