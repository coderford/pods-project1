package com.CabCompany.RideService;

public class Customer
{
    int custId;
    int sourcLoc;
    int rideId;
    Ridestate state;

    public Customer(int custId)
    {
        this.custId=custId;
        this.sourcLoc=0;
        this.rideId=0;
        this.state=Ridestate.ENDED;
    }

    public int getCustId()
    {
        return custId;
    }

    public int getCustLoc()
    {
        return sourcLoc;
    }
    public int getRideId()
    {
        return rideId;
    }

    public Ridestate getcustState()
    {
        return state;
    }

   

    public void setRideId(int rideId2) {
        this.rideId=rideId2;
    }

    public void setState(Ridestate started) {
        this.state=started; 
    }


    
}
    
