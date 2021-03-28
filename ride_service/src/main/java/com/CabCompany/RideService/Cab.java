package com.CabCompany.RideService;

public class Cab {
    int cabId;
    int numRides;
    CabState state;

    int rideId;
    int location;
    int sourceLoc;
    int destinationLoc;
    int custId=0;

    public Cab() {

    }

    public Cab(int cabId) {
        this.cabId = cabId;
        this.numRides = 0;
        this.state = CabState.SIGNED_OUT;
        this.rideId = -1;
        this.location = -1;
        this.custId = -1;
        this.sourceLoc = -1;
        this.destinationLoc = -1;
    }

    public int getId() {
        return cabId;
    }

    public int getRideId() {
        return rideId;
    }
    public int getNumRides() {
        return numRides;
    }

    public CabState getState() {
        return state;
    }

    public int getLocation() {
        return location;
    }

    public boolean isSignedIn() {
        return (state != CabState.SIGNED_OUT);
    }

    public void setRideId(int id) {
        this.rideId=id;
    }

    public void setState(CabState State) {
        this.state=State;
    }

    public void setSourceLoc(int sourcLoc) {
        this.sourceLoc=sourcLoc;
    }

    public void setDestLoc(int destLoc) {
        this.destinationLoc=destLoc;
    }

    public void incrNumRides() {
        this.numRides+=1;
    }

    public void setCustId(int custId2) {
        this.custId=custId2;
    }
}
