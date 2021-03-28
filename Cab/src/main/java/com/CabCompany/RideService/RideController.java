package com.CabCompany.RideService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.aop.support.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class RideController {

    int rideId=0;
    @Autowired
    private CabDataService cabobject;

    @RequestMapping("/cabs")
    public ArrayList<Cab> Displaycabs()
    {
        ArrayList<Cab> cabs=cabobject.getAllCabs();
        try{

            return cabobject.getAllCabs();
            
        }catch(Exception e){
            return null;

        }
            
    }


    @Autowired
    private CustDataService custobject;
    
    @RequestMapping("/customers")
    public ArrayList<Customer> Displaycustomers()
    {
       // ArrayList<Customer> customers=custobject.getAllCustomers();
        try{

            return custobject.getAllCustomers();
            
        }catch(Exception e){
            return null;

        }
    }


    @RequestMapping("/rideEnded")
    public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId) {
        Cab data=cabobject.getCabId(cabId);
        if(data.ridestate==Ridestate.GOING_ON && data.rideId==rideId)
        {
            data.initialPos=data.destinationLoc;
        data.rideId=0;
        data.destinationLoc=0;
        data.numRides+=1;
        data.setState(Cabstate.AVAILABLE);
        return true;
        }
        

        return false;
    }

    @RequestMapping("/cabSignsIn")
    public boolean cabSignsIn(@RequestParam int cabId, @RequestParam int initialPos) {
        try{
          Cab  data=cabobject.getCabId(cabId);
          data.initialPos=initialPos;
          data.setState(Cabstate.AVAILABLE);
            return true;
            
        }catch(Exception e){
            return false;

        }
    }

    @RequestMapping("/cabSignsOut")
    public boolean cabsignsOut(@RequestParam int cabId) {
        Cab data=cabobject.getCabId(cabId);
        data.setState(Cabstate.SIGNEDOUT);
        return true;
    }

    @RequestMapping("/requestRide")
    public  boolean  requestRide(@RequestParam int custId,@RequestParam int sourceLoc,@RequestParam int  destinationLoc)
    {
        Ride riderequest=new Ride(); 
        rideId++;
        if(!riderequest.requestRide(custId, sourceLoc, destinationLoc,rideId)){
            rideId--;
            return false;
        }
        return true;
    }

    @RequestMapping("/getCabStatus")
    public String getCabStatus(@RequestParam int cabId)
    {
     /*   Ride status=new Ride(); 
        return status.getCabStatus(cabId);*/

        CabDataService obj=new CabDataService();
        Cab data=obj.getCabId(cabId);
        if(data.ridestate==Ridestate.GOING_ON)
        {
            return (data.ridestate+" "+data.initialPos+" "+data.custId+" "+data.destinationLoc);
        }
        else if(data.state!=Cabstate.SIGNEDOUT)return (data.ridestate+" "+data.initialPos);

        return "false";
        
     //   return "-1";
    }

    @RequestMapping("/reset")
    public void reset()
    {

        cabobject.reset();
        custobject.reset();

    }

    

   
}