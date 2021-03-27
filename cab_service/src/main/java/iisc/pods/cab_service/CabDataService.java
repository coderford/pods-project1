/*
    This is a spring service which keeps track of available cabs
    and returns whatever data is needed to methods in the controller.
*/
package iisc.pods.cab_service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.springframework.stereotype.Service;

@Service
public class CabDataService {
    private ArrayList<Cab> cabs;

    public CabDataService() {
        cabs = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();

        try {
            File inputFile = new File("input.txt");
            Scanner in = new Scanner(inputFile);

            int section = 0;
            while(in.hasNextLine()) {
                String line = in.nextLine();
                if(line.compareTo("****") == 0) {
                    section++;
                }
                else if(section == 1) {
                    ids.add(Integer.parseInt(line));
                }
            }

            in.close();
        }
        catch(Exception e) {
            System.out.println("ERROR: Could not read input file!");
        }

        for(int id : ids) {
            cabs.add(new Cab(id));
        }
    }

    public ArrayList<Cab> getAllCabs() {
        return cabs;
    }

    public Cab getCabWithId(int id) {
        return cabs.stream().filter(c -> (c.getId() == id)).findFirst().get();
    }
}
