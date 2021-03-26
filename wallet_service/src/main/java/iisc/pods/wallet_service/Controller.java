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
        return 0;
    }

    @RequestMapping("/deductAmount")
    public boolean deductAmount(@RequestParam int custId, @RequestParam int amount) {
        return true;
    }

    @RequestMapping("/addAmount")
    public boolean addAmount(@RequestParam int custId, @RequestParam int amount) {
        return true;
    }

    public void reset() {

    }
    
}
