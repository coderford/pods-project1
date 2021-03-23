package iisc.pods.cab_service;

public class Cab {
    private int id;
    private int numRides;
    private CabState state;

    private boolean signedIn;
    private int rideId;
    private int location;

    public Cab() {

    }

    public Cab(int id) {
        this.id = id;
        this.numRides = 0;
        this.state = CabState.AVAILABLE;
        this.rideId = -1;
        this.location = 0;
        this.signedIn = false;
    }

    public int getId() {
        return id;
    }

    public CabState getState() {
        return state;
    }

    public int getNumRides() {
        return numRides;
    }

    public int getLocation() {
        return location;
    }

    public int getRideId() {
        return rideId;
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    public boolean isInterested() {
        if(numRides%2 == 0)
            return true;
        return false;
    }

    public boolean requestRide(int rideId) {
        if(signedIn && isInterested() && state == CabState.AVAILABLE) {
            this.rideId = rideId;
            this.state = CabState.COMMITTED;
            return true;
        }
        return false;
    }

    public boolean rideStarted() {
        if(state != CabState.COMMITTED) return false;

        state = CabState.GIVING_RIDE;
        return true;
    }

    public boolean rideCanceled(int rideId) {
        if(this.state != CabState.COMMITTED || this.rideId != rideId)
            return false;
        this.state = CabState.AVAILABLE;
        this.rideId = -1;
        return true;
    }

    public boolean rideEnded(int rideId) {
        if(this.state != CabState.GIVING_RIDE || this.rideId != rideId)
            return false;
        this.state = CabState.AVAILABLE;
        this.rideId = -1;
        numRides++;
        return true;
    }

    public boolean signIn(int initialPos) {
        boolean signInAllowed = (!signedIn && sendSignInRequest(id, initialPos));
        if(signInAllowed) {
            signedIn = true;
            location = initialPos;
            return true;
        }
        return false;
    }

    public boolean signOut() {
        boolean signOutAllowed = (signedIn && sendSignOutRequest(id));
        if(signOutAllowed) {
            signedIn = false;
            state = CabState.AVAILABLE;
            return true;
        }
        return false;
    }

    public boolean sendSignInRequest(int id, int initialPos) {
        return true;
    }
    
    public boolean sendSignOutRequest(int id) {
        return true;
    }
}
