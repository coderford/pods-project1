#! /bin/sh
# This test case checks whether a customer can get a ride when
# no cabs have signed in

# reset RideService and Wallet.
# every test case should begin with these two steps
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

testPassed="yes"

#customer 201 requests a ride
rideId=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=2&destinationLoc=10")
if [ "$rideId" != "-1" ];
then
    echo "Ride by customer 201 started"
    testPassed="no"
else
    echo "Ride to customer 201 denied"
fi

echo "Test Passing Status: " $testPassed
