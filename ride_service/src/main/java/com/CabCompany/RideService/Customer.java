package com.CabCompany.RideService;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer
{
    @Id
    int custId;
    int sourceLoc;
    int rideId;
    RideState rideState;

    public Customer(int custId)
    {
        this.custId=custId;
        this.sourceLoc=-1;
        this.rideId=-1;
        this.rideState=RideState.ENDED;
    }
    public Customer(){
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
