

curl -s "http://localhost:8081/reset"
curl -s "http://localhost:8082/reset"



function f1() {
    rideReqStatus=$(curl -s \
                   "http://localhost:8081/requestRide?custId=201&sourceLoc=0&destinationLoc=10")
}

function f2()
{
    rideReqStatus=$(curl -s \
                   "http://localhost:8081/requestRide?custId=202&sourceLoc=0&destinationLoc=10")
}

function f3()
{
    rideReqStatus=$(curl -s \
                   "http://localhost:8081/requestRide?custId=203&sourceLoc=0&destinationLoc=10")
}


#All cabs sign in 
#from different shells each customer requests ride
#testcase pass if
#all the customers are assigned the ride  

for  cid in {101..104};
do

signInStatus=$(curl -s "http://localhost:8080/signIn?cabId=${cid}&initialPos=0")

if [ "$signInStatus" = "true" ];
then
    echo "cab ${cid} signed in"
fi

done

f1 & f2 & f3 &

wait

count=0
for i in {101..104};
do 

cabStatus=$(curl -s "http://localhost:8081/getCabStatus?cabId=${i}" | { read a b c d ; echo $a ; } ) 

echo "cabstatus $cabStatus "
if [ "$cabStatus" = "giving-ride" ];
then
((count++))
fi

done
echo "count $count "
if [ "$count" = "3" ]
then
    echo "Test Passing Status: yes"
else
    echo "Test Passing Status: no"
fi




