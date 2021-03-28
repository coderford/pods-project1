package com.CabCompany.RideService;

public class Cab {
    int cabId;
    int numRides;
    Cabstate state;

    private boolean signedIn;
   // private boolean interested;
    int rideId;
    int initialPos;
    Ridestate ridestate;
    int sourceLoc;
    int destinationLoc;
    int custId=0;

    public Cab() {

    }

    public Cab(int cabId) {
        this.cabId = cabId;
        this.numRides = 0;
        this.state = Cabstate.SIGNEDOUT;
        this.rideId = 0;
        this.initialPos = 0;
        this.custId=0;
      //  this.signedIn = false;
     //   this.interested = true;
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

    public Cabstate getState() {
        return state;
    }

    public int getLocation() {
        return initialPos;
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    public void setRideId(int id) {
        this.rideId=id;
    }

    public void setState(Cabstate State) {
        this.state=State;
    }

    public void setsourceLoc(int sourcLoc) {
        this.sourceLoc=sourcLoc;
    }

    public void setDestLoc(int destLoc) {
        this.destinationLoc=destLoc;
    }

    public void setNumRide() {
        this.numRides+=1;
    }

    public void setRidestate(Ridestate state) {
        this.ridestate=state;
    }

    public void setCustId(int custId2) {
        this.custId=custId2;
    }

}
