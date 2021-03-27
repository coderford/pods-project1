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
       // custobject.Displaycabs();

       // CabDataService cabobject =new CabDataService();
            
    }

    @Autowired
    private CustDataService custobject;
    @RequestMapping("/customers")
    public ArrayList<Customer> Displaycustomers()
    {
        ArrayList<Customer> customers=custobject.getAllCustomers();
        try{

            return custobject.getAllCustomers();
            
        }catch(Exception e){
            return null;

        }
    }


    @RequestMapping("/rideEnded")
    public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId) {
        return true;
    }

    @RequestMapping("/signsIn")
    public boolean cabSignsIn(@RequestParam int cabId, @RequestParam int initialPos) {
        return true;
    }

    @RequestMapping("/signsOut")
    public boolean cabsignsOut(@RequestParam int cabId) {
        return true;
    }

    @RequestMapping("/requestRide")
    public  int  requestRide(@RequestParam int custId,@RequestParam int sourceLoc,@RequestParam int  destinationLoc)
    {

         return -1;
    }

    @RequestMapping("/getcabstatus")
    public String getCabStatus(@RequestParam int cabId)
    {
        return "-1";
    }

    @RequestMapping("/reset")
    public void reset()
    {
        
        CabDataService cabobject =new CabDataService();
        cabobject.reset();

        CustDataService custobject=new  CustDataService();
        custobject.reset();
    }

    

   
}