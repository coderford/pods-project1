/*
    This is a spring service which keeps track of available cabs
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
            System.out.println(cab.cabId+" ");
         
        }
    }

    public ArrayList<Cab> getAllCabs() {
        return cabs;
    }

    public Cab getCabId(int id) {
        try{
            return (cabs.stream().filter(c -> (c.cabId == id)).findFirst().get());
            }catch(Exception E){
                return null;
            }
        }


    
}