#! /bin/sh
# This test case checks whether sign-out request works and
# sets cab status correctly

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

#cab 101 signs out
resp=$(curl -s "http://localhost:8080/signOut?cabId=101")
if [ "$resp" = "true" ];
then
    echo "Cab 101 signed out"
else
    echo "Cab 101 could not sign out"
    testPassed="no"
fi

#status after signing out
resp=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
if [ "$resp" != "signed-out -1" ];
then
    echo $resp
    echo "Invalid Status for the cab 101"
    testPassed="no"
else
    echo "Correct Status for the cab 101"
fi

echo "Test Passing Status: " $testPassed
