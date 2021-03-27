package com.CabCompany.RideService;

public class Cab {
    int cabId;
    private int numRides;
    Cabstate state;

    private boolean signedIn;
   // private boolean interested;
    private int rideId;
    int initialPos;
    private int sourceLoc;
    private int destinationLoc;

    public Cab() {

    }

    public Cab(int cabId) {
        this.cabId = cabId;
        this.numRides = 0;
        this.state = Cabstate.SIGNEDOUT;
        this.rideId = 0;
        this.initialPos = 0;
      //  this.signedIn = false;
     //   this.interested = true;
        this.sourceLoc = 0;
        this.destinationLoc = 0;
    }

    public int getId() {
        return cabId;
    }

    public Cabstate getState() {
        return state;
    }

    public int getNumRides() {
        return numRides;
    }

    public int getLocation() {
        return initialPos;
    }

    public int getRideId() {
        return rideId;
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

}
