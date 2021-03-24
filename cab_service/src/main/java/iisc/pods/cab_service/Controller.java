package iisc.pods.cab_service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    private CabDataService dataService;

    @RequestMapping("/")
    public ArrayList<Cab> home() {
        return dataService.getAllCabs();
    }

    @RequestMapping("/requestRide")
    public boolean requestRide(
        @RequestParam int cabId,
        @RequestParam int rideId,
        @RequestParam int sourceLoc,
        @RequestParam int destinationLoc
    ) {
        Cab cab;
        try { cab = dataService.getCabWithId(cabId); }
        catch(Exception e) { return false; }

        return cab.requestRide(rideId, sourceLoc, destinationLoc);
    }

    @RequestMapping("/rideStarted")
    public boolean rideStarted(@RequestParam int cabId, @RequestParam int rideId) {
        Cab cab;
        try { cab = dataService.getCabWithId(cabId); }
        catch(Exception e) { return false; }

        return cab.rideStarted(rideId);
    }

    @RequestMapping("/rideCanceled")
    public boolean rideCanceled(@RequestParam int cabId, @RequestParam int rideId) {
        Cab cab;
        try { cab = dataService.getCabWithId(cabId); }
        catch(Exception e) { return false; }

        return cab.rideCanceled(rideId);
    }

    @RequestMapping("/rideEnded")
    public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId) {
        Cab cab;
        try { cab = dataService.getCabWithId(cabId); }
        catch(Exception e) { return false; }

        return cab.rideEnded(rideId);
    }

    @RequestMapping("/signIn")
    public boolean signIn(@RequestParam int cabId, @RequestParam int initialPos) {
        Cab cab;
        try { cab = dataService.getCabWithId(cabId); }
        catch(Exception e) { return false; }

        return cab.signIn(initialPos);
    }

    @RequestMapping("/signOut")
    public boolean signOut(@RequestParam int cabId) {
        Cab cab;
        try { cab = dataService.getCabWithId(cabId); }
        catch(Exception e) { return false; }

        return cab.signOut();
    }

    @RequestMapping("/numRides")
    public int numRides(@RequestParam int cabId) {
        Cab cab;
        try { cab = dataService.getCabWithId(cabId); }
        catch(Exception e) { return -1; }

        return cab.getNumRides();
    }
}
