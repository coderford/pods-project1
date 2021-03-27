package com.CabCompany.RideService;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.springframework.stereotype.Service;

@Service
public class CustDataService {


    private ArrayList<Customer> customers=new ArrayList<>();

    public CustDataService() {
    //    cabs = new ArrayList<>(Arrays.asList(
     //       new Cab(1), new Cab(2), new Cab(3), new Cab(4)
    //    ));


    ArrayList<Integer> custIds = new ArrayList<>();
        int initBalance = 0;

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
                    if(section == 2) {
                        custIds.add(Integer.parseInt(line));
                    }
                  /*  else if(section == 3) {
                        initBalance = Integer.parseInt(line);
                    }*/
                }
            }

            in.close();
        }
        catch(Exception e) {
            System.out.println("ERROR: Could not read input file!"+e);
        }

        for(int custId : custIds) {
            customers.add(new Customer(custId));
        }

    }

    public ArrayList<Customer> getAllCustomers() {
        return customers;
    }

    public void Displaycustomers()
    {
        for(Customer cust:customers)
        {  
          // Customer data=customers.get(i);
            System.out.println(cust.custId+" ");
         
        }
    }

    public Customer getCustId(int id) {
        try{
            return (customers.stream().filter(c -> (c.custId == id)).findFirst().get());
            }catch(Exception E){
                return null;
            }
        }

    public void reset()
    {
        for(Customer cust:customers)
        {
            cust.sourcLoc=0;
            cust.rideId=0;
            cust.state=Ridestate.ENDED;
        }
    }
    
}
