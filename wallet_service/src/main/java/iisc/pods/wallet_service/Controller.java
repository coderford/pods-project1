package iisc.pods.wallet_service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    private WalletDataService dataService;

    @RequestMapping("/")
    public ArrayList<Wallet> home() {
        return dataService.getAllWallets();
    }

    @RequestMapping("/getBalance")
    public int getBalance(@RequestParam int custId) {
        try {
            Wallet w = dataService.getWalletWithCustId(custId);
            return w.getBalance();
        } catch(Exception e) {
            return -1;
        }
    }

    @RequestMapping("/deductAmount")
    public boolean deductAmount(@RequestParam int custId, @RequestParam int amount) {
        try {
            Wallet w = dataService.getWalletWithCustId(custId);
            return w.deductAmount(amount);
        } catch(Exception e) {
            return false;
        }
    }

    @RequestMapping("/addAmount")
    public boolean addAmount(@RequestParam int custId, @RequestParam int amount) {
        try {
            Wallet w = dataService.getWalletWithCustId(custId);
            return w.addAmount(amount);
        } catch(Exception e) {
            return false;
        }
    }

    @RequestMapping("/reset")
    public void reset() {
        dataService.resetAllWallets();
    }
}
