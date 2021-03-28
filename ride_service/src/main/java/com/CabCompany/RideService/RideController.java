package com.CabCompany.RideService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class RideController {

    int rideId = 0;
    @Autowired
    private CabDataService cabDataService;

    @RequestMapping("/cabs")
    public ArrayList<Cab> Displaycabs() {
        return cabDataService.getAllCabs();
    }

    @Autowired
    private CustDataService custDataService;

    @RequestMapping("/customers")
    public ArrayList<Customer> Displaycustomers() {
        return custDataService.getAllCustomers();
    }

    @RequestMapping("/rideEnded")
    public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId) {
        Cab cab = cabDataService.getCabWithId(cabId);
        Customer cust=custDataService.getCustWithId(cab.custId);
        if (cab.state == CabState.GIVING_RIDE && cab.rideId == rideId) {
            cab.location = cab.destinationLoc;
            cab.rideId = 0;
            cab.destinationLoc = 0;
            cab.setState(CabState.AVAILABLE);
            cust.rideState=RideState.ENDED;
            cust.rideId=0;
            return true;
        }

        return false;
    }

    @RequestMapping("/cabSignsIn")
    public boolean cabSignsIn(@RequestParam int cabId, @RequestParam int initialPos) {
        try {
            Cab cab = cabDataService.getCabWithId(cabId);
            cab.location = initialPos;
            cab.setState(CabState.AVAILABLE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @RequestMapping("/cabSignsOut")
    public boolean cabsignsOut(@RequestParam int cabId) {
        Cab cab = cabDataService.getCabWithId(cabId);
        cab.setState(CabState.SIGNED_OUT);
        cab.location = -1;
        return true;
    }

    @RequestMapping("/requestRide")
    public int requestRide(
        @RequestParam int custId, 
        @RequestParam int sourceLoc,
        @RequestParam int destinationLoc
    ) {
        rideId++;
        int fare=0;
        int requestCount = 0;

        // cab selection mechanism
        int i = 0;
        ArrayList<Cab> cabs = cabDataService.getAllCabs();
        Customer custData;

        try {
            custData = custDataService.getCustWithId(custId);
        }
        catch(Exception e) {
            return -1;
        }

        Cab cab = cabs.get(i);

        if(custData.rideState==RideState.STARTED)
        {
            System.out.println("Customer "+custId+" is already in a cab");
            return -1;
        }

        while (i < cabs.size() || requestCount <= 3) {
            // Send requset to available cabs
            System.out.println("Sending request to cab : " + cab.cabId);
            if (cab.state == CabState.AVAILABLE) {
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
                    return -1;
                }

                URLConnection connection;
                String cabReqResponse;
                try {
                    connection = new URL(requestRideURL + "?" + query).openConnection();
                    connection.setRequestProperty("Accept-Charset", charset);
                    InputStream response = connection.getInputStream();
                    Scanner scanner = new Scanner(response);
                    cabReqResponse = scanner.useDelimiter("\\A").next();
                    System.out.println("Cab response: "+cabReqResponse);
                    scanner.close();
                } catch (Exception e) {
                    System.out.println("ERROR: Some error occured while trying to send ride request to cab service!");
                    rideId--;
                    return -1;
                }

                if (cabReqResponse.equals("true")) {
                    // cab accepted request
                    System.out.println("Cab " + cab.cabId + " accepted");
                    // deducting amount from wallet
                    fare = 10 * (Math.abs(cab.location - sourceLoc) + Math.abs(sourceLoc - destinationLoc));
                    // deduct fare from wallet
                    System.out.println("deducting " + fare + " from wallet");
                    String deductAmountURL = "http://localhost:8082/deductAmount";
                    String paramcustId = String.format("%d", custId);
                    String paramfare = String.format("%d", fare);
                    try {
                        query = String.format("custId=%s&amount=%s", 
                            URLEncoder.encode(paramcustId, charset),
                            URLEncoder.encode(paramfare, charset)
                        );
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("ERROR: Unsupported encoding format!");
                        rideId--;
                        return -1;
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
                        System.out.println("ERROR: Some error occured while trying to send deduct Amount request to wallet service!");
                        rideId--;
                        return -1;
                    }

                    if (amtDeductResponse.equals("false")) // Amount deduction failed. Cancel the ride
                    {
                        String rideCancelURL = "http://localhost:8080/rideCanceled";
                        paramCabId = String.format("%d", cab.cabId);
                        paramrideId = String.format("%d", rideId);
                        try {
                            query = String.format("cabId=%s&rideId=%s",
                                    URLEncoder.encode(paramCabId, charset), 
                                    URLEncoder.encode(paramrideId, charset)
                            );
                        } catch (UnsupportedEncodingException e) {
                            System.out.println("ERROR: Unsupported encoding format!");
                            rideId--;
                            return -1;
                        }

                        try {
                            connection = new URL(rideCancelURL + "?" + query).openConnection();
                            connection.setRequestProperty("Accept-Charset", charset);
                            InputStream response = connection.getInputStream();
                            Scanner scanner = new Scanner(response);
                            cabReqResponse = scanner.useDelimiter("\\A").next();
                            scanner.close();
                        } catch (Exception e) {
                            System.out.println("ERROR: Some error occured while trying to send cancel request to cab service!");
                            e.printStackTrace();
                            rideId--;
                            return -1;
                        }
                        rideId--;
                        return -1;
                    }

                    String rideStartedURL = "http://localhost:8080/rideStarted";
                    paramCabId = String.format("%d", cab.cabId);
                    paramrideId = String.format("%d", rideId);
                    try {
                        query = String.format("cabId=%s&rideId=%s", 
                            URLEncoder.encode(paramCabId, charset),
                            URLEncoder.encode(paramrideId, charset)
                        );
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("ERROR: Unsupported encoding format!");
                        return -1;
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
                        return -1;
                    }

                    // update values of cab
                    cab.setRideId(rideId);
                    cab.location = sourceLoc;
                    cab.setState(CabState.GIVING_RIDE);
                    cab.setSourceLoc(sourceLoc);
                    cab.setDestLoc(destinationLoc);
                    cab.setCustId(custId);
                    cab.numRides += 1;

                    // update values of customer
                    custData.setRideId(rideId);
                    custData.setRideState(RideState.STARTED);

                    System.out.println("Sending true...");
                    return rideId;
                }
            }
            if (requestCount == 3 || i == (cabs.size() - 1)) {
                rideId--;
                return -1;
            }
            i++;
            cab = cabs.get(i);
        }
        return -1;
    }

    @RequestMapping("/getCabStatus")
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