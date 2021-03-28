#! /bin/sh
# This test case checks whether a non-existent customer 
# can request a ride

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

#(non-existent) customer 600 requests a ride
rideId=$(curl -s "http://localhost:8081/requestRide?custId=600&sourceLoc=2&destinationLoc=10")
if [ "$rideId" != "-1" ];
then
    echo "Ride by (non-existent) customer 600 started"
    testPassed="no"
else
    echo "Ride by (non-existent) customer 600 denied"
fi

echo "Test Passing Status: " $testPassed
