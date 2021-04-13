curl -s "http://localhost:8081/reset"
curl -s "http://localhost:8082/reset"

# total distance travelled:
# 1. InitialPos to sourceDes of any one ride = 10
# sourceDes to final des of one ride = 90
# Destination of first ride to source of second ride = 90
# second ride source to destination = 90
# final pos would be 100
signInStatus=$(curl -s "http://localhost:8080/signIn?cabId=101&initialPos=0")

if [ "$signInStatus" = "true" ];
then
    echo "cab 101 signed in"
fi


bash sh1.sh &
bash sh1.sh &
bash sh1.sh &

wait

cabStatus=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")

echo "$cabStatus"

numRides=$(curl -s "http://localhost:8080/numRides?cabId=101")

if [ "$numRides" = "9" ];
then
    echo "Correct number of rides"
else
    echo "Wrong number of rides"
fi
