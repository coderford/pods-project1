package iisc.pods.cab_service;

import java.util.Arrays;

public class Cab {
    private int id;
    private int numRides;
    private CabState state;

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
        this.state = CabState.SIGNED_OUT;
        this.rideId = -1;
        this.location = 0;
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
        return (state != CabState.SIGNED_OUT);
    }

    public boolean isInterested() {
        return interested;
    }

    public synchronized boolean requestRide(int rideId, int sourceLoc, int destinationLoc) {
        System.out.println("Recieved request for rideId: "+rideId+" source: "+sourceLoc+" dest: "+destinationLoc);

        if(interested) {
            interested = false;
        } else {
            interested = true;
            return false;
        }

        if(sourceLoc < 0 || destinationLoc < 0) return false;

        if(state == CabState.AVAILABLE) {

            this.rideId = rideId;
            this.state = CabState.COMMITTED;
            this.sourceLoc = sourceLoc;
            this.destinationLoc = destinationLoc;

            System.out.println("Accepting ride request...");
            return true;
        }

        return false;
    }

    public boolean rideStarted(int rideId) {
        if(state != CabState.COMMITTED) return false;

        state = CabState.GIVING_RIDE;
        location = sourceLoc;
        numRides++;
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
        System.out.println("Received ride-end request for cab "+this.id+" and ride "+rideId);
        if(this.state != CabState.GIVING_RIDE || this.rideId != rideId)
            return false;

        String rideEndedURL = "http://localhost:8081/rideEnded";
        String response = RequestSender.getHTTPResponse(
            rideEndedURL, 
            Arrays.asList("cabId", "rideId"), 
            Arrays.asList(
                String.format("%d", this.id),
                String.format("%d", this.rideId)
            )
        );

        if(!response.equals("true")) return false;

        this.state = CabState.AVAILABLE;
        this.rideId = -1;
        this.location = this.destinationLoc;
        this.sourceLoc = -1;
        this.destinationLoc = -1;

        return true;
    }

    public boolean signIn(int initialPos) {
        boolean signInAllowed = (state == CabState.SIGNED_OUT && initialPos >= 0);

        if(signInAllowed && sendSignInRequest(id, initialPos)) {
            state = CabState.AVAILABLE;
            location = initialPos;
            return true;
        }
        return false;
    }

    public boolean signOut() {
        // Cab shouldn't already be signed out, or in giving-ride or committed state
        boolean signOutAllowed = (state != CabState.SIGNED_OUT &&
                                  state != CabState.GIVING_RIDE &&
                                  state != CabState.COMMITTED);

        if(signOutAllowed && sendSignOutRequest(id)) {
            state = CabState.SIGNED_OUT;
            location = 0;
            interested = true;
            numRides = 0;
            return true;
        }
        return false;
    }

    public boolean sendSignInRequest(int id, int initialPos) {
        String signInURL = "http://localhost:8081/cabSignsIn";
        String response = RequestSender.getHTTPResponse(
            signInURL, 
            Arrays.asList("cabId", "initialPos"),
            Arrays.asList(
                String.format("%d", this.id),
                String.format("%d", initialPos)
            ) 
        );

        if(!response.equals("true")) return false;
        return true;
    }
    
    public boolean sendSignOutRequest(int id) {
        String signOutURL = "http://localhost:8081/cabSignsOut";
        String response = RequestSender.getHTTPResponse(
            signOutURL, 
            Arrays.asList("cabId"),
            Arrays.asList(String.format("%d", this.id)) 
        );

        if(!response.equals("true")) return false;
        return true;
    }
}