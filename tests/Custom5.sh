#! /bin/sh
# This test case checks whether fare calculation is correct

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

#customer 201 requests a ride that uses up all his balance
rideId=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=0&destinationLoc=1000")
if [ "$rideId" != "-1" ];
then
    echo "Ride by customer 201 started"
else
    echo "Ride to customer 201 denied"
    testPassed="no"
fi

#The first ride concludes successfully
resp=$(curl -s "http://localhost:8080/rideEnded?cabId=101&rideId=$rideId")
if [ "$resp" != "true" ];
then
    echo "First ride by customer 201 could be completed"
    testPassed="no"
else
    echo "First ride by customer 201 completed successfully"
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

#Customer 201 tries to request another very short ride.
#This should be picked by cab 102 since cab 101 is uninterested.
rideId=$(curl -s "http://localhost:8081/requestRide?custId=201&sourceLoc=0&destinationLoc=1")
if [ "$rideId" != "-1" ];
then
    echo "Second ride by customer 201 started"
    testPassed="no"
else
    echo "Second ride to customer 201 denied"
fi

echo "Test Passing Status: " $testPassed
