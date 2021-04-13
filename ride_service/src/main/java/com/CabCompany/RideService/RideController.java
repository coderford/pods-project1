package com.CabCompany.RideService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class RideController {

    @Autowired
    private CabDataService cabDataService;

    @Autowired
    RideRepo cabrepo;

    @Autowired
    CustRepo custrepo;

    @Autowired
    NextRideIdRepo nextRideIdRepo;

    @Autowired
    private CustDataService custDataService;

    @PersistenceContext
    EntityManager em;

    @RequestMapping("/cabs")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ArrayList<Cab> Displaycabs() {
        return cabDataService.getAllCabs();
    }

    @RequestMapping("/customers")
    public ArrayList<Customer> Displaycustomers() {
        return custDataService.getAllCustomers();
    }

    @RequestMapping("/rideEnded")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId) {
        //Acquire Lock on Cab Table
        Cab cabInDB = em.find(Cab.class, cabId, LockModeType.PESSIMISTIC_WRITE);
        Customer custInDB = custrepo.findById(cabInDB.custId).get();

     
        //End Ride if Cab is in GIVING_RIDE state and given rideId is valid 
        if (cabInDB.state.equals(CabState.GIVING_RIDE.toString()) && cabInDB.rideId == rideId) {
            cabInDB.location = cabInDB.destinationLoc;
            cabInDB.rideId = 0;
            cabInDB.destinationLoc = 0;
            cabInDB.setState(CabState.AVAILABLE);
            custInDB.rideState = RideState.ENDED.toString();
            custInDB.rideId = 0;
            cabrepo.save(cabInDB);
            custrepo.save(custInDB);
            return true;
        }

        return false;
    }

    @RequestMapping("/cabSignsIn")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean cabSignsIn(@RequestParam int cabId, @RequestParam int initialPos) {
        try {
             //Acquire Lock on Cab Table
            Cab cabInDB = em.find(Cab.class, cabId, LockModeType.PESSIMISTIC_WRITE);
            cabInDB.state = CabState.AVAILABLE.toString();
            cabInDB.location = initialPos;
            cabrepo.save(cabInDB);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @RequestMapping("/cabSignsOut")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean cabsignsOut(@RequestParam int cabId) {
        // Cab cab = cabrepo.findById(cabId).get();
        Cab cab = em.find(Cab.class, cabId, LockModeType.PESSIMISTIC_WRITE);
        cab.setState(CabState.SIGNED_OUT);
        cab.location = -1;
        cabrepo.save(cab);
        return true;
    }

    @RequestMapping("/requestRide")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String requestRide(
        @RequestParam int custId, 
        @RequestParam int sourceLoc, 
        @RequestParam int destinationLoc
    ) {
        String requestRideURL  = "http://cab-service:8080/requestRide";
        String deductAmountURL = "http://wallet-service:8082/deductAmount";
        String rideCancelURL   = "http://cab-service:8080/rideCanceled";
        String rideStartedURL  = "http://cab-service:8080/rideStarted";

        if(sourceLoc < 0 || destinationLoc < 0) return "-1";

        int rideId = nextRideIdRepo.findById(0).get().getRideId();
        int fare = 0;
        int requestCount = 0;

        // cab selection mechanism
        Iterable<Cab> cabs = cabrepo.findAll();
        Customer custData;

        try {
            custData = custrepo.findById(custId).get();
        } catch (Exception e) {
            return "-1";
        }

        // Send request to available cabs
        Iterator<Cab> iterator = cabs.iterator();
        while (iterator.hasNext() && requestCount < 3) {
            Cab cab = iterator.next();

            if (cab.state.equals(CabState.AVAILABLE.toString())) {
                System.out.println("Cust "+custId+": Cab " + cab.cabId + " is available. Sending request...");
                requestCount++;

                String cabReqResponse = RequestSender.getHTTPResponse(
                    requestRideURL, 
                    Arrays.asList("cabId", "rideId", "sourceLoc", "destinationLoc"), 
                    Arrays.asList(
                            String.format("%d", cab.cabId), 
                            String.format("%d", rideId),
                            String.format("%d", sourceLoc),
                            String.format("%d", destinationLoc)
                    )
                );

                if (cabReqResponse.equals("true")) {
                    // cab accepted request
                    System.out.println("Cust "+custId+": Cab " + cab.cabId + " accepted");

                    // deducting amount from wallet
                    fare = 10 * (Math.abs(cab.location - sourceLoc) + Math.abs(sourceLoc - destinationLoc));

                    String amtDeductResponse = RequestSender.getHTTPResponse(
                        deductAmountURL, 
                        Arrays.asList("custId", "amount"), 
                        Arrays.asList(
                            String.format("%d", custId),
                            String.format("%d", fare)
                        )
                    );

                    if (!amtDeductResponse.equals("true")) { 
                        // Amount deduction failed. Cancel the ride
                        RequestSender.getHTTPResponse(
                            rideCancelURL, 
                            Arrays.asList("cabId", "rideId"), 
                            Arrays.asList(
                                String.format("%d", cab.cabId),
                                String.format("%d", rideId)
                            )
                        );
                        return "-1";
                    }

                    // Amount deduction succeeded. Send ride started request
                    RequestSender.getHTTPResponse(
                        rideStartedURL, 
                        Arrays.asList("cabId", "rideId"), 
                        Arrays.asList(
                            String.format("%d", cab.cabId),
                            String.format("%d", rideId)
                        )
                    );

                    // update databse entries of cab
                    cab.setRideId(rideId);
                    cab.location = sourceLoc;
                    cab.setState(CabState.GIVING_RIDE);
                    cab.setSourceLoc(sourceLoc);
                    cab.setDestLoc(destinationLoc);
                    cab.setCustId(custId);
                    cab.numRides += 1;
                    cabrepo.save(cab);

                    // update database entries of customer
                    custData.setRideId(rideId);
                    custData.setRideState(RideState.STARTED);
                    custrepo.save(custData);

                    // update next ride id
                    nextRideIdRepo.save(new NextRideId(rideId + 1));

                    System.out.println("Cust "+custId+": Assigned ride ID "+rideId);
                    System.out.println("Cust "+custId+": Sending true...");
                    String str = rideId + " " + cab.cabId + " " + fare;
                    return str;
                }
            }
        }
        return "-1";
    }

    @RequestMapping("/getCabStatus")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String getCabStatus(@RequestParam int cabId) {
        //Call get cabstatus fucntion
        return cabDataService.getCabStatus(cabId);
    }

    @RequestMapping("/reset")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void reset() {
        //Reset Database 
        System.out.println("Resetting everything...");
        nextRideIdRepo.save(new NextRideId(1));
        cabDataService.reset();
        custDataService.reset();
    }

}