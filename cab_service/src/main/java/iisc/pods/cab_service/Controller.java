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
        return true;
    }

    @RequestMapping("/rideStarted")
    public boolean rideStarted(@RequestParam int cabId, @RequestParam int rideId) {
        return true;
    }

    @RequestMapping("/rideCanceled")
    public boolean rideCanceled(@RequestParam int cabId, @RequestParam int rideId) {
        return true;
    }

    @RequestMapping("/rideEnded")
    public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId) {
        return true;
    }

    @RequestMapping("/signIn")
    public boolean signIn(@RequestParam int cabId, @RequestParam int initialPos) {
        return true;
    }

    @RequestMapping("/signOut")
    public boolean signOut(@RequestParam int cabId) {
        return true;
    }

    @RequestMapping("/numRides")
    public boolean numRides(@RequestParam int cabId) {
        return true;
    }
}
