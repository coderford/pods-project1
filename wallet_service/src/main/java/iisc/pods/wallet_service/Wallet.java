package iisc.pods.wallet_service;

public class Wallet {
    private int custId;
    private int initBalance;
    private int balance;

    public Wallet() {
    }

    public Wallet(int custId, int initBalance) {
        this.custId = custId;
        this.initBalance = initBalance;
        this.balance = initBalance;
    }

    public int getCustId() {
        return custId;
    }

    public int getBalance() {
        return balance;
    }

    public synchronized boolean addAmount(int amount) {
      //Add amount to the wallet  
        if(amount > 0) {
            balance += amount;
            return true;
        }
        return false;
    }

    public synchronized boolean deductAmount(int amount) {
        //if fare is less than balance then deduct amount from account
        if(amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public void resetBalance() {
        balance = initBalance;
    }
}
