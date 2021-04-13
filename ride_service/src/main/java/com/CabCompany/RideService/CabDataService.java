package com.CabCompany.RideService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class CabDataService {
    @Autowired
    RideRepo repo;

    @PersistenceContext       
   private EntityManager em;

    public void init()
    {
        ArrayList<Integer> cabIds = new ArrayList<>();
        try {

            //Reading initial information from IDs.txt file

            File inputFile = new File("IDs.txt");
            Scanner in = new Scanner(inputFile);

            int section = 0;
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.compareTo("****") == 0) {
                    section++;
                } else {
                    if (section == 1) {
                        cabIds.add(Integer.parseInt(line));
                    }
                }
            }

            in.close();
        } catch (Exception e) {
            System.out.println("ERROR: Could not read input file!");
        }

        for (int cabId : cabIds) {
            Cab cab = new Cab(cabId);

            //Saving CAB information in database
            repo.save(cab);
        }
    }

    public ArrayList<Cab> getAllCabs() {
        //Returns all cabs information
        ArrayList<Cab> cabs=new ArrayList<>();
        repo.findAll().forEach(cabs::add);
        return cabs;
    }

    public String getCabStatus(int cabId) {
        try {
            //Get cab status of corresponding cab
            Cab cab=em.find(Cab.class, cabId, LockModeType.PESSIMISTIC_WRITE);
            String stateStr = cab.state.toString().toLowerCase().replaceAll("_", "-");
            if (cab.state.equals(CabState.GIVING_RIDE.toString())) {
                return (stateStr + " " + cab.location + " " + cab.custId + " " + cab.destinationLoc);
            } else if (!cab.state.equals(CabState.SIGNED_OUT.toString())) {
                return (stateStr + " " + cab.location);
            }
            else return "signed-out -1";
        }
        catch(Exception e) {
            return "-1";
        }
    }



    public void reset() {
        ArrayList<Cab> cabs=getAllCabs();
        for (Cab cab : cabs) {
            System.out.println("Sending ride end request for cab "+cab.cabId+" and ride "+cab.rideId);

            // Send rideEnded request to Cab-Service
            String rideEndedURL = "http://cab-service:8080/rideEnded";
            RequestSender.getHTTPResponse(
                rideEndedURL, 
                Arrays.asList("cabId", "rideId"), 
                Arrays.asList(
                    String.format("%d", cab.cabId),
                    String.format("%d", cab.rideId)
                )
            );

            // Send signOut request to Cab-Service
            String signOutURL = "http://cab-service:8080/signOut";
            RequestSender.getHTTPResponse(
                signOutURL, 
                Arrays.asList("cabId"),
                Arrays.asList(String.format("%d", cab.cabId))
            );
        }

        init();
    }
}