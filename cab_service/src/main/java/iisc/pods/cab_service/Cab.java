package iisc.pods.cab_service;

public class Cab {
    private int id;
    private int numRides;
    private CabState state;

    private boolean signedIn;
    private boolean interested;
    private int rideId;
    private int location;
    private int sourceLoc;
    private int destinationLoc;

    public Cab() {

    }

    public Cab(int id) {
        this.id = id;
        this.numRides = 0;
        this.state = CabState.AVAILABLE;
        this.rideId = -1;
        this.location = 0;
        this.signedIn = false;
        this.interested = true;
        this.sourceLoc = -1;
        this.destinationLoc = -1;
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
        return interested;
    }

    public boolean requestRide(int rideId, int sourceLoc, int destinationLoc) {
        if(signedIn && state == CabState.AVAILABLE) {
            if(interested) {
                interested = false;
            } else {
                interested = true;
                return false;
            }

            this.rideId = rideId;
            this.state = CabState.COMMITTED;
            this.sourceLoc = sourceLoc;
            this.destinationLoc = destinationLoc;
            return true;
        }
        return false;
    }

    public boolean rideStarted(int rideId) {
        if(state != CabState.COMMITTED) return false;

        state = CabState.GIVING_RIDE;
        return true;
    }

    public boolean rideCanceled(int rideId) {
        if(this.state != CabState.COMMITTED || this.rideId != rideId)
            return false;
        this.state = CabState.AVAILABLE;
        this.rideId = -1;
        this.sourceLoc = -1;
        this.destinationLoc = -1;
        return true;
    }

    public boolean rideEnded(int rideId) {
        if(this.state != CabState.GIVING_RIDE || this.rideId != rideId)
            return false;
        this.state = CabState.AVAILABLE;
        this.rideId = -1;
        this.location = this.destinationLoc;
        this.sourceLoc = -1;
        this.destinationLoc = -1;
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
