cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
cabLoc=$(echo "$cabLoc" | awk '{print $2}')

rideReqStatus="-1"

while [ "$rideReqStatus" = "-1" ];
do
   rideReqStatus=$(curl -s \
                   "http://localhost:8081/requestRide?custId=201&sourceLoc=${cabLoc}&destinationLoc=10")
done

#echo "ride status ${rideReqStatus}"

rideId=$(echo $rideReqStatus | awk '{print $1;}')

rideEndResp=$(curl -s "http://localhost:8080/rideEnded?cabId=101&rideId=${rideId}")

#echo "$rideId ended with response $rideEndResp"

# _____________________________________________________________________________

cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
cabLoc=$(echo "$cabLoc" | awk '{print $2}')

rideReqStatus="-1"

while [ "$rideReqStatus" = "-1" ];
do
    rideReqStatus=$(curl -s \
                         "http://localhost:8081/requestRide?custId=201&sourceLoc=${cabLoc}&destinationLoc=20")
done

#echo "ride status ${rideReqStatus}"

rideId=$(echo $rideReqStatus | awk '{print $1;}')

rideEndResp=$(curl -s "http://localhost:8080/rideEnded?cabId=101&rideId=${rideId}")

#echo "$rideId ended with response $rideEndResp"
#______________________________________________________________________________


cabLoc=$(curl -s "http://localhost:8081/getCabStatus?cabId=101")
cabLoc=$(echo "$cabLoc" | awk '{print $2}')

rideReqStatus="-1"

while [ "$rideReqStatus" = "-1" ];
do
    rideReqStatus=$(curl -s \
                    "http://localhost:8081/requestRide?custId=201&sourceLoc=${cabLoc}&destinationLoc=30")
done

#echo "ride status ${rideReqStatus}"

rideId=$(echo $rideReqStatus | awk '{print $1;}')

rideEndResp=$(curl -s "http://localhost:8080/rideEnded?cabId=101&rideId=${rideId}")

#echo "$rideId ended with response $rideEndResp"

# ______________________________________________________________________________
