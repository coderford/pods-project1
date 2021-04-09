/*
    cab is a spring service which keeps track of available cabs
    and returns whatever data is needed to methods in the controller.
*/
package com.CabCompany.RideService;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CabDataService {
    private ArrayList<Cab> cabs = new ArrayList<>();

    
    RideRepo repo;
    @Autowired
    public CabDataService(RideRepo repo) {
        this.repo=repo;
        ArrayList<Integer> cabIds = new ArrayList<>();
        try {
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
            Cab cab=new Cab(cabId);
            cabs.add(cab);
            repo.save(cab);
        }

    }

    public void displayCabs() {
        for (Cab cab : cabs) {
            System.out.println("cabId :" + cab.cabId + "  rideId:" + cab.rideId);
        }
    }

    public ArrayList<Cab> getAllCabs() {
        return cabs;
    }

    public String getCabStatus(int cabId) {
        try {
            Cab cab = cabs.stream().filter(c -> c.getId() == cabId).findFirst().get();
            Cab cabInDB=repo.findById(cabId).get();
           // String stateStr = cabInDB.state.toString().toLowerCase().replaceAll("_", "-");
            String stateStr = cabInDB.state.toString();
            //String stateString = cab.state.toString().toLowerCase().replaceAll("_", "-");
            if (cab.state.equals(CabState.GIVING_RIDE.toString())) {
                return (stateStr + " " + cab.location + " " + cab.custId + " " + cab.destinationLoc);
            } else if (!cabInDB.state.equals(CabState.SIGNED_OUT.toString())) {
                return (stateStr + " " + cab.location);
            }
            else return "signed-out -1";
        }
        catch(Exception e) {
            return "-1";
        }
    }

    public Cab getCabWithId(int id) {
        try {
          //  return (cabs.stream().filter(c -> (c.cabId == id)).findFirst().get());
            return repo.findById(id).get();
        } catch (Exception E) {
            return null;
        }
    }

    public void reset() {
        for (Cab cab : cabs) {
            // Send rideEnded request
            String rideEndedURL = "http://localhost:8080/rideEnded";
            String charset = "UTF-8";
            String paramCabId = String.format("%d", cab.cabId);
            String paramRideId = String.format("%d", cab.rideId);
            String query = "";
            try {
                query = String.format("cabId=%s&rideId=%s", URLEncoder.encode(paramCabId, charset),URLEncoder.encode(paramRideId, charset));
            } catch (UnsupportedEncodingException e) {
                System.out.println("ERROR: Unsupported encoding format!");
            }
            
            URLConnection connection;
            try {
                connection = new URL(rideEndedURL + "?" + query).openConnection();
                connection.setRequestProperty("Accept-Charset", charset);
                InputStream response = connection.getInputStream();
                Scanner scanner = new Scanner(response);
                String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();
            } catch (Exception e) {
                System.out.println("ERROR: Some error occured while trying to send ride-ended request to ride service!");
            }

            // Send signOut request
            String signOutURL = "http://localhost:8080/signOut";
            try {
                query = String.format("cabId=%s", URLEncoder.encode(paramCabId, charset));
            } catch (UnsupportedEncodingException e) {
                System.out.println("ERROR: Unsupported encoding format!");
            }
            
            try {
                connection = new URL(signOutURL + "?" + query).openConnection();
                connection.setRequestProperty("Accept-Charset", charset);
                InputStream response = connection.getInputStream();
                Scanner scanner = new Scanner(response);
                String responseBody = scanner.useDelimiter("\\A").next();
                scanner.close();
            } catch (Exception e) {
                System.out.println("ERROR: Some error occured while trying to send sign-out request to cab service!"+e);
            }
            System.out.println("Resetting values for cab " + cab.cabId);
            cab.numRides = 0;
            cab.state = CabState.SIGNED_OUT.toString();
            cab.rideId = -1;
            cab.location = -1;
            cab.sourceLoc = -1;
            cab.destinationLoc = -1;
            cab.custId = -1;
            repo.save(cab);
        }
    }
}