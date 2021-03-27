package com.CabCompany.RideService;

public class Customer
{
    int custId;
    private int sourcLoc;
    private int rideId;
    private Ridestate state;

    public Customer(int custId)
    {
        this.custId=custId;
        this.sourcLoc=0;
        this.rideId=0;
        this.state=Ridestate.ENDED;
    }

    public Ridestate getcustState()
    {
        return state;
    }

    public int getRideId()
    {
        return rideId;
    }

    public void setRideId(int rideId2) {
        this.rideId=rideId2;
    }

    public void setState(Ridestate started) {
        this.state=started; 
    }


    
}
    
