package com.CabCompany.RideService;

public class Cab {
    int cabId;
    int numRides;
    CabState state;

    int rideId;
    int location;
    RideState rideState;
    int sourceLoc;
    int destinationLoc;
    int custId=0;

    public Cab() {

    }

    public Cab(int cabId) {
        this.cabId = cabId;
        this.numRides = 0;
        this.state = CabState.SIGNED_OUT;
        this.rideId = 0;
        this.location = 0;
        this.custId=0;
        this.sourceLoc = 0;
        this.destinationLoc = 0;
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

    public void setRideState(RideState state) {
        this.rideState=state;
    }

    public void setCustId(int custId2) {
        this.custId=custId2;
    }
}
