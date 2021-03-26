package iisc.pods.wallet_service;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.stereotype.Service;

@Service
public class WalletDataService {
    private ArrayList<Wallet> wallets;

    public WalletDataService() {
        wallets = new ArrayList<>(Arrays.asList(
            new Wallet(1, 1000), new Wallet(2, 100), new Wallet(3, 0)
        ));
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
