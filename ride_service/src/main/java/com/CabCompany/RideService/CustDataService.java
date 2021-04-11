package com.CabCompany.RideService;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustDataService {

   // private ArrayList<Customer> customers = new ArrayList<>();

    @Autowired
    CustRepo repo;
    


    public void init(){

        ArrayList<Integer> custIds = new ArrayList<>();

        try {
            File inputFile = new File("IDs.txt");
            Scanner in = new Scanner(inputFile);

            int section = 0;
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.compareTo("****") == 0) {
                    section++;
                } else {
                    if (section == 2) {
                        custIds.add(Integer.parseInt(line));
                    }
                }
            }

            in.close();
        } catch (Exception e) {
            System.out.println("ERROR: Could not read input file!" + e);
        }

        for (int custId : custIds) {
            Customer customer=new Customer(custId);
            repo.save(customer);
        }

    }

    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customers=new ArrayList<>();
        repo.findAll().forEach(customers::add);
        return customers;
    }

    public void reset() {
        //init();
        ArrayList<Customer> customers=getAllCustomers();
        for (Customer cust : customers) {
            cust.sourceLoc = 0;
            cust.rideId = 0;
            cust.rideState = RideState.ENDED.toString();
            repo.save(cust);
        }
    }
}
