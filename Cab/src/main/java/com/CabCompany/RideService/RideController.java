package com.CabCompany.RideService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.aop.support.annotation.*;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class RideController {

    int rideId = 0;
    @Autowired
    private CabDataService cabobject;

    @RequestMapping("/cabs")
    public ArrayList<Cab> Displaycabs() {
        ArrayList<Cab> cabs = cabobject.getAllCabs();
        try {

            return cabobject.getAllCabs();

        } catch (Exception e) {
            return null;

        }

    }

    @Autowired
    private CustDataService custobject;

    @RequestMapping("/customers")
    public ArrayList<Customer> Displaycustomers() {
        // ArrayList<Customer> customers=custobject.getAllCustomers();
        try {

            return custobject.getAllCustomers();

        } catch (Exception e) {
            return null;

        }
    }

    @RequestMapping("/rideEnded")
    public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId) {
        Cab data = cabobject.getCabId(cabId);
        Customer cust=custobject.getCustId(data.custId);
        if (data.ridestate == Ridestate.GOING_ON && data.rideId == rideId) {
            data.initialPos = data.destinationLoc;
            data.rideId = 0;
            data.destinationLoc = 0;
            data.numRides += 1;
            data.setState(Cabstate.AVAILABLE);
            cust.state=Ridestate.ENDED;
            cust.rideId=0;
            return true;
        }

        return false;
    }

    @RequestMapping("/cabSignsIn")
    public boolean cabSignsIn(@RequestParam int cabId, @RequestParam int initialPos) {
        try {
            Cab data = cabobject.getCabId(cabId);
            data.initialPos = initialPos;
            data.setState(Cabstate.AVAILABLE);
            return true;

        } catch (Exception e) {
            return false;

        }
    }

    @RequestMapping("/cabSignsOut")
    public boolean cabsignsOut(@RequestParam int cabId) {
        Cab data = cabobject.getCabId(cabId);
        data.setState(Cabstate.SIGNEDOUT);
        return true;
    }

    @RequestMapping("/requestRide")
    public boolean requestRide(@RequestParam int custId, @RequestParam int sourceLoc,
            @RequestParam int destinationLoc) {
        Ride riderequest = new Ride();
        rideId++;
        int fare=0;

        //riderequset.rideId = rideId;
        int requestcount = 0;
        // cab selection mechanism
        int i = 0;
        // CabDataService obj=new CabDataService();
        // CustDataService customerObj=new CustDataService();
        // ArrayList<Customer> customers=customerObj.getAllCustomers();
        ArrayList<Cab> cabs = cabobject.getAllCabs();
        Customer custData = custobject.getCustId(custId);
        Cab data = cabs.get(i);

        if(custData.state==Ridestate.STARTED)
        {
            System.out.println("You are already in cab ");
            return false;
        }
        while (i < cabs.size() || requestcount <= 3) {

            // Cab data=list.get(i);
            // if(obj.getCabId(data.getId())!=0)
            // {

            // send requset to available cabs
            System.out.println("Sending request to cab : " + data.cabId);
            if (data.state == Cabstate.AVAILABLE) {
                System.out.println("Cab :" + data.cabId + " is available sending reques");
                requestcount++;
                // Reference: https://stackoverflow.com/a/2793153
                String requestRideURL = "http://localhost:8080/requestRide";
                String charset = "UTF-8";
                String paramCabId = String.format("%d", data.cabId);
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
                    return false;
                }

                URLConnection connection;
                String cabReqResponse;
                try {
                    connection = new URL(requestRideURL + "?" + query).openConnection();
                    connection.setRequestProperty("Accept-Charset", charset);
                    InputStream response = connection.getInputStream();
                    Scanner scanner = new Scanner(response);
                    cabReqResponse = scanner.useDelimiter("\\A").next();
                    System.out.println("cab response :"+cabReqResponse);
                    scanner.close();

                } catch (Exception e) {
                    System.out
                            .println("ERROR: Some error occured while trying to send sign-in request to ride service!");
                    return false;
                }

                if (cabReqResponse.equals("true")) {

                    // cab acccepted request
                    System.out.println(" cab : " + data.cabId + "  accepted");
                    // deducting amount from wallet
                    fare = 10 * (Math.abs(data.initialPos - sourceLoc) + Math.abs(sourceLoc - destinationLoc));
                    // deduct fare from wallet
                    System.out.println("deducting  : " + fare + " from wallet");
                    String deductAmountURL = "http://localhost:8082/deductAmount";
                    String paramcustId = String.format("%d", custId);
                    String paramfare = String.format("%d", fare);
                    try {
                        query = String.format("custId=%s&amount=%s", URLEncoder.encode(paramcustId, charset),
                                URLEncoder.encode(paramfare, charset));
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("ERROR: Unsupported encoding format!");
                        return false;
                    }
                    ;

                    // URLConnection connection;
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
                        return false;
                    }
                    /****** 
                     * 
                    */

                    if (amtDeductResponse.equals("false")) // amount deduction failed cance the ride
                    {

                        String rideCancelURL = "http://localhost:8080/rideCanceled";
                        // String charset = "UTF-8";
                        paramCabId = String.format("%d", data.cabId);
                        paramrideId = String.format("%d", rideId);
                        try {
                            query = String.format("cabId=%s&rideId=%s&sourceLoc=%s&destinationLoc=%s",
                                    URLEncoder.encode(paramCabId, charset), URLEncoder.encode(paramrideId, charset),
                                    URLEncoder.encode(paramsourcLoc, charset),
                                    URLEncoder.encode(paramdestLoc, charset));
                        } catch (UnsupportedEncodingException e) {
                            System.out.println("ERROR: Unsupported encoding format!");
                            return false;
                        }

                        // URLConnection connection;
                        // String cabReqResponse;
                        try {
                            connection = new URL(rideCancelURL + "?" + query).openConnection();
                            connection.setRequestProperty("Accept-Charset", charset);
                            InputStream response = connection.getInputStream();
                            // Scanner scanner = new Scanner(response);
                            // cabReqResponse = scanner.useDelimiter("\\A").next();
                            // scanner.close();

                        } catch (Exception e) {
                            System.out.println(
                                    "ERROR: Some error occured while trying to send sign-in request to ride service!");
                                    rideId--;
                            return false;
                        }
                        rideId--;
                        return false;
                    }

                    // sending cabService.rideStarted

                    String rideStartedURL = "http://localhost:8080/rideStarted";
                    // String charset = "UTF-8";
                    paramCabId = String.format("%d", data.cabId);
                    paramrideId = String.format("%d", rideId);
                    try {
                        query = String.format("cabId=%s&rideId=%s", URLEncoder.encode(paramCabId, charset),
                                URLEncoder.encode(paramrideId, charset)

                        );
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("ERROR: Unsupported encoding format!");
                        return false;
                    }

                    // URLConnection connection;
                    // String cabReqResponse;
                    try {
                        connection = new URL(rideStartedURL + "?" + query).openConnection();
                        connection.setRequestProperty("Accept-Charset", charset);
                        // InputStream response = connection.getInputStream();
                        // Scanner scanner = new Scanner(response);
                        // cabReqResponse = scanner.useDelimiter("\\A").next();
                        // scanner.close();

                    } catch (Exception e) {
                        System.out.println(
                                "ERROR: Some error occured while trying to send ride started request to cab service!");
                        return false;
                    }

                    // update values of cab
                    data.setRideId(rideId);
                    data.setState(Cabstate.GIVING_RIDE);
                    data.setRidestate(Ridestate.GOING_ON);
                    data.setsourceLoc(sourceLoc);
                    data.setDestLoc(destinationLoc);
                    data.setNumRide();
                    data.setCustId(custId);
                    // changing values of customer
                    custData.setRideId(rideId);
                    custData.setState(Ridestate.STARTED);

                    return true;

                }

            }
            if (requestcount == 3 || i == (cabs.size() - 1)) {
                rideId--;
                return false;
            }
            i++;
            data = cabs.get(i);

            // }
        }
        return false;
        /*
         * if(!riderequest.requestRide(custId, sourceLoc, destinationLoc,rideId)){
         * rideId--; return false; } return true;
         */
    }

    @RequestMapping("/getCabStatus")
    public String getCabStatus(@RequestParam int cabId) {
        /*
         * Ride status=new Ride(); return status.getCabStatus(cabId);
         */

        CabDataService obj = new CabDataService();
        Cab data = obj.getCabId(cabId);
        if (data.ridestate == Ridestate.GOING_ON) {
            return (data.ridestate + " " + data.initialPos + " " + data.custId + " " + data.destinationLoc);
        } else if (data.state != Cabstate.SIGNEDOUT)
            return (data.ridestate + " " + data.initialPos);

        return "false";

        // return "-1";
    }

    @RequestMapping("/reset")
    public void reset() {

        cabobject.reset();
        custobject.reset();

    }

}