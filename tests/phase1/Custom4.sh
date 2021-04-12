#! /bin/sh
# This test case checks whether the count of rides for a cab
# is maintained correctly

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

#customer 202 requests a ride again, but doesn't have enough balance,
#so his ride is cancelled
rideId=$(curl -s "http://localhost:8081/requestRide?custId=202&sourceLoc=20&destinationLoc=50000" | { read a b c; echo $a; })
if [ "$rideId" != "-1" ];
then
    echo "Extreme distance ride by customer 202 started"
    testPassed="no"
else
    echo "Extreme distance ride by customer 202 denied"
fi

#customer 203 requests a ride, but the cab is not interested
rideId=$(curl -s "http://localhost:8081/requestRide?custId=203&sourceLoc=1&destinationLoc=25" | { read a b c; echo $a; })
if [ "$rideId" != "-1" ];
then
    echo "Ride by customer 202 started even though cab is not interested"
    testPassed="no"
else
    echo "Ride by customer 202 denied because cab is not interested"
fi

#customer 203 requests a ride again, and this time the cab is interested
rideId=$(curl -s "http://localhost:8081/requestRide?custId=203&sourceLoc=1&destinationLoc=25" | { read a b c; echo $a; })
if [ "$rideId" != "-1" ];
then
    echo "Ride by customer 203 started"
else
    echo "Ride to customer 203 denied"
    testPassed="no"
fi

#customer 203's ride hasn't ended yet, but the ride should count,
#and we should have a total of 2 rides
numRides=$(curl -s "http://localhost:8080/numRides?cabId=101")
if [ "$numRides" = "2" ];
then
    echo "Number of rides=$numRides is correct"
else
    echo "Number of rides=$numRides is incorrect"
    testPassed="no"
fi

echo "Test Passing Status: " $testPassed
