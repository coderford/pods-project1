#!/bin/bash
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

# In this testcase, all cabs try to sign in at the same time.
# To pass, all of the sign-in requests must return true.

testPassing="yes"

function signIn() {
    #Step 1 : cab 101 signs in
    resp=$(curl -s "http://localhost:8080/signIn?cabId=$1&initialPos=100")
    if [ "$resp" = "true" ];
    then
        echo "Cab $1 signed in"
    else
        echo "Cab $1 could not sign in"
        testPassing="no"
    fi
}

# Run signin requests in parellel

signIn 101 & signIn 102 & signIn 103 & signIn 104 &

wait

echo "Test Passing Status: $testPassing"
