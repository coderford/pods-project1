package iisc.pods.cab_service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

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

    public boolean requestRide(int rideId, int sourceLoc, int destinationLoc) {
    
        System.out.println("Recieved request for rideId: "+rideId+" source: "+sourceLoc+" dest: "+destinationLoc);
        if(state == CabState.AVAILABLE) {
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

        String rideEndedURL = "http://localhost:8081/rideEnded";
        String charset = "UTF-8";
        String paramRideId = String.format("%d", this.rideId);

        String query;
        try {
            query =  String.format("rideId=%s",
                URLEncoder.encode(paramRideId, charset)
            );
        } catch (UnsupportedEncodingException e) {
            System.out.println("ERROR: Unsupported encoding format!");
            return false;
        };

        URLConnection connection;
        try {
            connection = new URL(rideEndedURL + "?" + query).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();

            if(responseBody.equals("false")) return false;
        } catch (Exception e) {
            System.out.println("ERROR: Some error occured while trying to send sign-out request to ride service!");
            return false;
        }

        this.state = CabState.AVAILABLE;
        this.rideId = -1;
        this.location = this.destinationLoc;
        this.sourceLoc = -1;
        this.destinationLoc = -1;
        numRides++;

        return true;
    }

    public boolean signIn(int initialPos) {
        boolean signInAllowed = (state == CabState.SIGNED_OUT && sendSignInRequest(id, initialPos));
        if(signInAllowed) {
            state = CabState.AVAILABLE;
            location = initialPos;
            return true;
        }
        return false;
    }

    public boolean signOut() {
        boolean signOutAllowed = (state != CabState.SIGNED_OUT && sendSignOutRequest(id));
        if(signOutAllowed) {
            state = CabState.SIGNED_OUT;
            location = 0;
            return true;
        }
        return false;
    }

    public boolean sendSignInRequest(int id, int initialPos) {
        // Reference: https://stackoverflow.com/a/2793153
        String signInURL = "http://localhost:8081/cabSignsIn";
        String charset = "UTF-8";
        String paramCabId = String.format("%d", id);
        String paramInitialPos = String.format("%d", initialPos);

        String query;
        try {
            query =  String.format("cabId=%s&initialPos=%s",
                URLEncoder.encode(paramCabId, charset),
                URLEncoder.encode(paramInitialPos, charset)
            );
        } catch (UnsupportedEncodingException e) {
            System.out.println("ERROR: Unsupported encoding format!");
            return false;
        };

        URLConnection connection;
        try {
            connection = new URL(signInURL + "?" + query).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();

            if(responseBody.equals("false")) return false;
        } catch (Exception e) {
            System.out.println("ERROR: Some error occured while trying to send sign-in request to ride service!");
            return false;
        }

        return true;
    }
    
    public boolean sendSignOutRequest(int id) {
        String signOutURL = "http://localhost:8081/cabSignsOut";
        String charset = "UTF-8";
        String paramCabId = String.format("%d", id);

        String query;
        try {
            query =  String.format("cabId=%s",
                URLEncoder.encode(paramCabId, charset)
            );
        } catch (UnsupportedEncodingException e) {
            System.out.println("ERROR: Unsupported encoding format!");
            return false;
        };

        URLConnection connection;
        try {
            connection = new URL(signOutURL + "?" + query).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();

            if(responseBody.equals("false")) return false;
        } catch (Exception e) {
            System.out.println("ERROR: Some error occured while trying to send sign-out request to ride service!");
            return false;
        }

        return true;
    }
}