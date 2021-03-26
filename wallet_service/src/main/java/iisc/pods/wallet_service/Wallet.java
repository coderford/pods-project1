package iisc.pods.wallet_service;

public class Wallet {
    private int id;
    private int balance;

    public Wallet() {
    }

    public Wallet(int id, int initBalance) {
        this.id = id;
        this.balance = initBalance;
    }

    public int getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public boolean addAmount(int amount) {
        if(amount > 0) {
            balance += amount;
            return true;
        }
        return false;
    }

    public boolean deductAmount(int amount) {
        if(amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
