package com.CabCompany.RideService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class RideController {

    int rideId = 0;
    @Autowired
    private CabDataService cabDataService;

    @Autowired
    RideRepo cabrepo;

    @Autowired
    CustRepo custrepo;

    @Autowired
    private CustDataService custDataService;

    
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
    public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId) {
        // Cab cab = cabDataService.getCabWithId(cabId);
        Cab cabInDB = cabrepo.findById(cabId).get();
        Customer custInDB = custrepo.findById(cabInDB.custId).get();
        // Customer cust=custDataService.getCustWithId(cabInDB.custId);
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
            Cab cabInDB = cabrepo.findById(cabId).get();
            cabInDB.state = CabState.AVAILABLE.toString();
            cabInDB.location = initialPos;
            cabrepo.save(cabInDB);
            /*
             * Cab cab = cabDataService.getCabWithId(cabId); cab.location = initialPos;
             * cab.setState(CabState.AVAILABLE);
             */
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @RequestMapping("/cabSignsOut")
    public boolean cabsignsOut(@RequestParam int cabId) {
        Cab cab = cabrepo.findById(cabId).get();
        cab.setState(CabState.SIGNED_OUT);
        cab.location = -1;
        cabrepo.save(cab);
        return true;
    }

    @RequestMapping("/requestRide")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String requestRide(@RequestParam int custId, @RequestParam int sourceLoc, @RequestParam int destinationLoc) {
        rideId++;
        int fare = 0;
        int requestCount = 0;

        // cab selection mechanism
        int i = 0;
        Iterable<Cab> cabs = cabrepo.findAll();
        Customer custData;

        try {
            custData = custrepo.findById(custId).get();
        } catch (Exception e) {
            return "-1";
        }
        Iterator<Cab> iterator = cabs.iterator();
        Cab cab = iterator.next();

        while (iterator.hasNext() || requestCount <= 3) {
            // Send requset to available cabs
            System.out.println("Sending request to cab : " + cab.cabId);
            if (cab.state.equals(CabState.AVAILABLE.toString())) {
                System.out.println("Cab " + cab.cabId + " is available. Sending request...");
                requestCount++;

                String requestRideURL = "http://localhost:8080/requestRide";
                String charset = "UTF-8";
                String paramCabId = String.format("%d", cab.cabId);
                String paramrideId = String.format("%d", rideId);
                String paramsourcLoc = String.format("%d", sourceLoc);
                String paramdestLoc = String.format("%d", destinationLoc);
                String query;
                try {
                    query = String.format("cabId=%s&rideId=%s&sourceLoc=%s&destinationLoc=%s",
                            URLEncoder.encode(paramCabId, charset), URLEncoder.encode(paramrideId, charset),
                            URLEncoder.encode(paramsourcLoc, charset), URLEncoder.encode(paramdestLoc, charset));
                } catch (UnsupportedEncodingException e) {
                    System.out.println("ERROR: Unsupported encoding format!");
                    rideId--;
                    return "-1";
                }

                URLConnection connection;
                String cabReqResponse;
                try {
                    connection = new URL(requestRideURL + "?" + query).openConnection();
                    connection.setRequestProperty("Accept-Charset", charset);
                    InputStream response = connection.getInputStream();
                    Scanner scanner = new Scanner(response);
                    cabReqResponse = scanner.useDelimiter("\\A").next();
                    System.out.println("Cab response: " + cabReqResponse);
                    scanner.close();
                } catch (Exception e) {
                    System.out.println("ERROR: Some error occured while trying to send ride request to cab service!");
                    rideId--;
                    return "-1";
                }

                if (cabReqResponse.equals("true")) {
                    // cab accepted request
                    System.out.println("Cab " + cab.cabId + " accepted");
                    // deducting amount from wallet
                    fare = 10 * (Math.abs(cab.location - sourceLoc) + Math.abs(sourceLoc - destinationLoc));
                    // deduct fare from wallet
                    System.out.println("deducting " + fare + " from wallet");
                    String deductAmountURL = "http://10.11.0.3:8082/deductAmount";
                    String paramcustId = String.format("%d", custId);
                    String paramfare = String.format("%d", fare);
                    try {
                        query = String.format("custId=%s&amount=%s", URLEncoder.encode(paramcustId, charset),
                                URLEncoder.encode(paramfare, charset));
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("ERROR: Unsupported encoding format!");
                        rideId--;
                        return "-1";
                    }

                    String amtDeductResponse;
                    try {
                        connection = new URL(deductAmountURL + "?" + query).openConnection();
                        connection.setRequestProperty("Accept-Charset", charset);
                        InputStream response = connection.getInputStream();
                        Scanner scanner = new Scanner(response);
                        amtDeductResponse = scanner.useDelimiter("\\A").next();
                        scanner.close();
                    } catch (Exception e) {
                        System.out.println(
                                "ERROR: Some error occured while trying to send deduct Amount request to wallet service!");
                        rideId--;
                        return "-1";
                    }

                    if (amtDeductResponse.equals("false")) // Amount deduction failed. Cancel the ride
                    {
                        String rideCancelURL = "http://localhost:8080/rideCanceled";
                        paramCabId = String.format("%d", cab.cabId);
                        paramrideId = String.format("%d", rideId);
                        try {
                            query = String.format("cabId=%s&rideId=%s", URLEncoder.encode(paramCabId, charset),
                                    URLEncoder.encode(paramrideId, charset));
                        } catch (UnsupportedEncodingException e) {
                            System.out.println("ERROR: Unsupported encoding format!");
                            rideId--;
                            return "-1";
                        }

                        try {
                            connection = new URL(rideCancelURL + "?" + query).openConnection();
                            connection.setRequestProperty("Accept-Charset", charset);
                            InputStream response = connection.getInputStream();
                            Scanner scanner = new Scanner(response);
                            cabReqResponse = scanner.useDelimiter("\\A").next();
                            scanner.close();
                        } catch (Exception e) {
                            System.out.println(
                                    "ERROR: Some error occured while trying to send cancel request to cab service!");
                            e.printStackTrace();
                            rideId--;
                            return "-1";
                        }
                        rideId--;
                        return "-1";
                    }

                    String rideStartedURL = "http://localhost:8080/rideStarted";
                    paramCabId = String.format("%d", cab.cabId);
                    paramrideId = String.format("%d", rideId);
                    try {
                        query = String.format("cabId=%s&rideId=%s", URLEncoder.encode(paramCabId, charset),
                                URLEncoder.encode(paramrideId, charset));
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("ERROR: Unsupported encoding format!");
                        return "-1";
                    }

                    try {
                        connection = new URL(rideStartedURL + "?" + query).openConnection();
                        connection.setRequestProperty("Accept-Charset", charset);
                        InputStream response = connection.getInputStream();
                        Scanner scanner = new Scanner(response);
                        cabReqResponse = scanner.useDelimiter("\\A").next();
                        scanner.close();
                    } catch (Exception e) {
                        System.out.println(
                                "ERROR: Some error occured while trying to send ride started request to cab service!");
                        return "-1";
                    }

                    // update values of cab
                    // Cab cab
                    cab.setRideId(rideId);
                    cab.location = sourceLoc;
                    cab.setState(CabState.GIVING_RIDE);
                    cab.setSourceLoc(sourceLoc);
                    cab.setDestLoc(destinationLoc);
                    cab.setCustId(custId);
                    cab.numRides += 1;
                    cabrepo.save(cab);

                    // update values of customer
                    custData.setRideId(rideId);
                    custData.setRideState(RideState.STARTED);
                    custrepo.save(custData);

                    System.out.println("Sending true...");
                    String str=rideId+" "+cab.cabId+" "+fare;
                    return str;
                }
            }
            if (requestCount == 3 || !iterator.hasNext()) {
                rideId--;
                return "-1";
            }
            // i++;
            cab = iterator.next();  
        }
        return "-1";
    }

    @RequestMapping("/getCabStatus")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String getCabStatus(@RequestParam int cabId) {
        return cabDataService.getCabStatus(cabId);
    }

    @RequestMapping("/reset")
    public void reset() {
        System.out.println("Resetting everything...");
        cabDataService.reset();
        custDataService.reset();
    }
}