package com.CabCompany.RideService;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.stereotype.Service;

@Service
public class CustDataService {

    private ArrayList<Customer> customers = new ArrayList<>();

    public CustDataService() {
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
            customers.add(new Customer(custId));
        }

    }

    public ArrayList<Customer> getAllCustomers() {
        return customers;
    }

    public void displayCustomers() {
        for (Customer cust : customers) {
            System.out.println(cust.custId + " ");
        }
    }

    public Customer getCustWithId(int id) {
        return (customers.stream().filter(c -> (c.custId == id)).findFirst().get());
    }

    public void reset() {
        for (Customer cust : customers) {
            cust.sourceLoc = 0;
            cust.rideId = 0;
            cust.rideState = RideState.ENDED;
        }
    }
}
