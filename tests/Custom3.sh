#! /bin/sh
#This test checks whether ride cancellation works correctly

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

#customer 201 requests a ride that he cannot pay for
rideId=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=2&destinationLoc=50000")
if [ "$rideId" != "-1" ];
then
    echo "Ride by customer 201 started"
    testPassed="no"
else
    echo "Ride to customer 201 denied"
fi

#cab 101's status should be available
stat=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
if [ "$stat" != "available 0" ];
then
    echo "Invalid status for cab 101 : $stat"
    testPassed="no"
else
    echo "Correct status for cab 101"
fi
