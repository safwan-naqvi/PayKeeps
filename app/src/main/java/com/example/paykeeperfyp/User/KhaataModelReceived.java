package com.example.paykeeperfyp.User;

public class KhaataModelReceived {
    String credit_id;
    String credit_name;
    String credit_balance;
    String credit_date;

    public KhaataModelReceived() {
    }

    public KhaataModelReceived(String credit_id, String credit_name, String credit_balance, String credit_date) {
        this.credit_id = credit_id;
        this.credit_name = credit_name;
        this.credit_balance = credit_balance;
        this.credit_date = credit_date;
    }

    public String getCredit_id() {
        return credit_id;
    }

    public void setCredit_id(String credit_id) {
        this.credit_id = credit_id;
    }

    public String getCredit_name() {
        return credit_name;
    }

    public void setCredit_name(String credit_name) {
        this.credit_name = credit_name;
    }

    public String getCredit_balance() {
        return credit_balance;
    }

    public void setCredit_balance(String credit_balance) {
        this.credit_balance = credit_balance;
    }

    public String getCredit_date() {
        return credit_date;
    }

    public void setCredit_date(String credit_date) {
        this.credit_date = credit_date;
    }
}
