package com.CabCompany.RideService;

//import com.CabCompany.Cab.Cabstate;
//import com.CabCompany.Cab.Ridestate;
import java.lang.*;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.web.bind.annotation.ResponseBody;


public class Ride {
    private int custId;
    private int cabId;
    private int rideId=0;
    private Cabstate cabstate;
    private Ridestate ridestate;
    private int Pos;
    private int sourcLoc;
    private int destLoc;
    private int fare;
    private boolean signedIn;
/*
    public Ride(int custId,int sourcLoc,int destLoc)
    {
        this.custId=custId;
        this.cabId=-1;
        this.rideId=-1;
        this.cabstate=cabstate.AVAILABLE;
        this.ridestate=ridestate.ENDED;
        this.Pos=0;
        this.sourcLoc=sourcLoc;
        this.destLoc=destLoc;
        this.fare=0;
        this.signedIn=false;
    }
*/

    public boolean requestRide(int custId,int sourcLoc,int destLoc)
    {
        rideId++;
        int requestcount=0;
        //cab selection mechanism
        int i=0;
        CabDataService obj=new CabDataService();
        CustDataService customerObj=new CustDataService();
        ArrayList<Customer> customers=customerObj.getAllCustomers();
        ArrayList<Cab> cabs=obj.getAllCabs();
        Customer custData=customerObj.getCustId(custId);
        Cab data=cabs.get(i);

        
        while(data!=null || requestcount!=3)
    {
        
        //Cab data=list.get(i);
      //  if(obj.getCabId(data.getId())!=0)
      //  {


            if(data.getState()==cabstate.AVAILABLE) //send requset to available cabs
        {
         requestcount++;   
             // Reference: https://stackoverflow.com/a/2793153
        String requestRideURL = "http://localhost:8080/requestRide";
        String charset = "UTF-8";
        String paramCabId = String.format("%d", cabId); 
        String paramrideId=String.format("%d", rideId);
        String paramsourcLoc = String.format("%d", sourcLoc);
        String paramdestLoc = String.format("%d", destLoc);
        String query;
        try {
            query =  String.format("cabId=%s&riedId=%s&sourcLoc=%s&destLoc=%s",
                URLEncoder.encode(paramCabId, charset),
                URLEncoder.encode(paramrideId, charset),
                URLEncoder.encode(paramsourcLoc, charset),
                URLEncoder.encode(paramdestLoc, charset)
            );
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
            scanner.close();

            
        } catch (Exception e) {
            System.out.println("ERROR: Some error occured while trying to send sign-in request to ride service!");
            return false;
        }

        if(cabReqResponse=="true")
        {


              // cab acccepted request 
        //deducting amount from wallet
        fare=10*(Math.abs(Pos-sourcLoc)+Math.abs(sourcLoc-destLoc));
        //deduct fare from wallet
        String deductAmountURL = "http://localhost:8082/deductAmount";
        String paramcustId = String.format("%d", custId); 
        String paramfare = String.format("%d", fare);
        try {
            query =  String.format("custId=%s&Amount=%s",
                URLEncoder.encode(paramcustId, charset),
                URLEncoder.encode(paramfare, charset)
            );
        } catch (UnsupportedEncodingException e) {
            System.out.println("ERROR: Unsupported encoding format!");
            return false;
        };

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
            System.out.println("ERROR: Some error occured while trying to send deduct Amount request to wallet service!");
            return false;
        }
/****** 
 * 
*/


        if(amtDeductResponse == "false") // amount deduction failed cance the ride
        {

         String rideCancelURL = "http://localhost:8080/rideCanceled";
        //String charset = "UTF-8";
         paramCabId = String.format("%d", cabId); 
        paramrideId=String.format("%d", rideId);
        try {
            query =  String.format("cabId=%s&riedId=%s&sourcLoc=%s&destLoc=%s",
                URLEncoder.encode(paramCabId, charset),
                URLEncoder.encode(paramrideId, charset),
                URLEncoder.encode(paramsourcLoc, charset),
                URLEncoder.encode(paramdestLoc, charset)
            );
        } catch (UnsupportedEncodingException e) {
            System.out.println("ERROR: Unsupported encoding format!");
            return false;
        }

      //  URLConnection connection;
       // String cabReqResponse;
        try {
            connection = new URL(requestRideURL + "?" + query).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();
           // Scanner scanner = new Scanner(response);
           // cabReqResponse = scanner.useDelimiter("\\A").next();
           // scanner.close();
            
        } catch (Exception e) {
            System.out.println("ERROR: Some error occured while trying to send sign-in request to ride service!");
            return false;
        }
            return false;
        }

        //sending cabService.rideStarted 

        String rideStartedURL = "http://localhost:8080/rideCanceled";
        //String charset = "UTF-8";
         paramCabId = String.format("%d", cabId); 
        paramrideId=String.format("%d", rideId);
        try {
            query =  String.format("cabId=%s&riedId=%s",
                URLEncoder.encode(paramCabId, charset),
                URLEncoder.encode(paramrideId, charset)
                
            );
        } catch (UnsupportedEncodingException e) {
            System.out.println("ERROR: Unsupported encoding format!");
            return false;
        }

      //  URLConnection connection;
       // String cabReqResponse;
        try {
            connection = new URL(rideStartedURL + "?" + query).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
           // InputStream response = connection.getInputStream();
           // Scanner scanner = new Scanner(response);
           // cabReqResponse = scanner.useDelimiter("\\A").next();
           // scanner.close();
            
        } catch (Exception e) {
            System.out.println("ERROR: Some error occured while trying to send ride started request to cab service!");
            return false;
        }
        
        //update values of cab
        data.setRideId(rideId);
        data.setState(Cabstate.GIVING_RIDE);
        data.setsourceLoc(sourcLoc);
        data.setDestLoc(destLoc);
        data.setNumRide();
        //changing values of customer
        custData.setRideId(rideId);
        custData.setState(Ridestate.STARTED);

        return true;

        }
        i++;
        data=cabs.get(i);

     }  
        
   // }
}
    return false;
    
}

    public boolean cabSignsIn(int cabId, int Pos)
    {
     
        CabDataService obj=new CabDataService();
        Cab data=obj.getCabId(cabId);

        if(data.state==Cabstate.SIGNEDOUT && IsValidCabId(cabId))
        {
            data.setState(Cabstate.AVAILABLE);
            data.initialPos=Pos;
            return true;
        }
        System.out.println("Cab Already Signed In");
        return false;
    }


    public boolean cabSignOut(int cabId)
    {
        if(cabstate==Cabstate.AVAILABLE && IsValidCabId(cabId))
        {
            this.signedIn=false;
            return true;
        }
        return false;
    }



    public String getCabStatus(int cabId)
    {
        if(ridestate==Ridestate.GIVING_RIDE)
        {
            return (ridestate+" "+sourcLoc+" "+custId+" "+destLoc);
        }
        else if(signedIn)return (ridestate+" "+Pos);

        return "-1";
    }

    public boolean IsValidCabId(int cabId) // to be implemented 
    {
        CabDataService obj=new CabDataService();
    
        if(obj.getCabId(cabId)!=null){
            return true;
        }
       return false;

    }
    
}
