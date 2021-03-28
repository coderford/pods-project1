package com.CabCompany.RideService;

public class Customer
{
    int custId;
    int sourceLoc;
    int rideId;
    RideState rideState;

    public Customer(int custId)
    {
        this.custId=custId;
        this.sourceLoc=0;
        this.rideId=0;
        this.rideState=RideState.ENDED;
    }

    public int getCustId()
    {
        return custId;
    }

    public int getCustLoc()
    {
        return sourceLoc;
    }
    public int getRideId()
    {
        return rideId;
    }

    public RideState getRideState()
    {
        return rideState;
    }

    public void setRideId(int rideId2) {
        this.rideId=rideId2;
    }
    public void setRideState(RideState started) {
        this.rideState=started; 
    }
}
