package iisc.pods.wallet_service;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.stereotype.Service;

@Service
public class WalletDataService {
    private ArrayList<Wallet> wallets;

    public WalletDataService() {
        wallets = new ArrayList<>();
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
                    else if(section == 3) {
                        initBalance = Integer.parseInt(line);
                    }
                }
            }

            in.close();
        }
        catch(Exception e) {
            System.out.println("ERROR: Could not read input file!");
        }

        for(int custId : custIds) {
            wallets.add(new Wallet(custId, initBalance));
        }
    }

    public ArrayList<Wallet> getAllWallets() {
        return wallets;
    }

    public Wallet getWalletWithCustId(int custId) {
        return wallets.stream().filter(w -> (w.getCustId() == custId)).findFirst().get();
    }

    public void resetAllWallets() {
        for(Wallet w : wallets) {
            w.resetBalance();
        }
    }
}
