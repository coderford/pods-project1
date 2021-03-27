/*
    cab is a spring service which keeps track of available cabs
    and returns whatever data is needed to methods in the controller.
*/
package com.CabCompany.RideService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.springframework.stereotype.Service;

@Service
public class CabDataService {
    private ArrayList<Cab> cabs=new ArrayList<>();

    public CabDataService() {
    //    cabs = new ArrayList<>(Arrays.asList(
     //       new Cab(1), new Cab(2), new Cab(3), new Cab(4)
    //    ));
    ArrayList<Integer> cabIds = new ArrayList<>();
    try {
        File inputFile = new File("IDs.txt");
        Scanner in = new Scanner(inputFile);

        int section = 0;
        while(in.hasNextLine()) {
            String line = in.nextLine();
            if(line.compareTo("****") == 0) {
                section++;
            }
            else {
                if(section == 1) {
                    cabIds.add(Integer.parseInt(line));
                }
              //  else if(section == 3) {
                //    initBalance = Integer.parseInt(line);
                //}
            }
        }

        in.close();
    }
    catch(Exception e) {
        System.out.println("ERROR: Could not read input file!");
    }

    for(int cabId : cabIds) {
        cabs.add(new Cab(cabId));
    }

    }

    public void Displaycabs()
    {
        for(Cab cab:cabs)
        {  
          // Customer data=customers.get(i);
            System.out.println("cabId :"+cab.cabId+"  rideId:"+cab.rideId );
         
        }
    }

    public ArrayList<Cab> getAllCabs() {
        return cabs;
    }

    public String cabStatus(int cabId)
    {

        return "-1";
    }

    public Cab getCabId(int id) {
        try{
            return (cabs.stream().filter(c -> (c.cabId == id)).findFirst().get());
            }catch(Exception E){
                return null;
            }
        }


        public void reset()
    {
        for(Cab cab:cabs)
        {

        cab.numRides = 0;
        cab.state = Cabstate.SIGNEDOUT;
        cab.ridestate=Ridestate.ENDED;
        cab.rideId = 0;
        cab.initialPos = 0;
        cab.sourceLoc = 0;
        cab.destinationLoc = 0;
        }
    }


    
}