#! /bin/sh
# This test case checks whether the "interest" mechanism works

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
rideId=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=2&destinationLoc=10" | { read a b c; echo $a; })
if [ "$rideId" != "-1" ];
then
    echo "Ride by customer 201 started"
else
    echo "Ride to customer 201 denied"
    testPassed="no"
fi

#customer 201's ride ends successfully
resp=$(curl -s "http://localhost:8080/rideEnded?cabId=101&rideId=$rideId")
if [ "$resp" = "true" ];
then
    echo "Ride by customer 201 ended successfully"
else
    echo "Ride by customer 201 could not be ended"
    testPassed="no"
fi

#customer 202 requests a ride, but the cab is not interested
rideId=$(curl -s "http://localhost:8081/requestRide?custId=202&sourceLoc=20&destinationLoc=5" | { read a b c; echo $a; })
if [ "$rideId" != "-1" ];
then
    echo "Ride by customer 202 started even though cab is not interested"
    testPassed="no"
else
    echo "Ride by customer 202 denied because cab is not interested"
fi

#customer 202 requests a ride again, and this time it is accepted
rideId=$(curl -s "http://localhost:8081/requestRide?custId=202&sourceLoc=20&destinationLoc=5" | { read a b c; echo $a; })
if [ "$rideId" != "-1" ];
then
    echo "Ride by customer 202 started"
else
    echo "Ride by customer 202 denied"
    testPassed="no"
fi

echo "Test Passing Status: " $testPassed
