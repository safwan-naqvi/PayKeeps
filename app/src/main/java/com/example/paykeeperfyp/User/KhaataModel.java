package com.example.paykeeperfyp.User;

public class KhaataModel {
    String debit_balance;
    String debit_date;
    String debit_id;
    String debit_name;
    String debit_return;

    public KhaataModel() {
    }

    public KhaataModel(String debit_balance, String debit_date, String debit_id, String debit_name, String debit_return) {
        this.debit_balance = debit_balance;
        this.debit_date = debit_date;
        this.debit_id = debit_id;
        this.debit_name = debit_name;
        this.debit_return = debit_return;
    }

    public String getDebit_balance() {
        return debit_balance;
    }

    public void setDebit_balance(String debit_balance) {
        this.debit_balance = debit_balance;
    }

    public String getDebit_date() {
        return debit_date;
    }

    public void setDebit_date(String debit_date) {
        this.debit_date = debit_date;
    }

    public String getDebit_id() {
        return debit_id;
    }

    public void setDebit_id(String debit_id) {
        this.debit_id = debit_id;
    }

    public String getDebit_name() {
        return debit_name;
    }

    public void setDebit_name(String debit_name) {
        this.debit_name = debit_name;
    }

    public String getDebit_return() {
        return debit_return;
    }

    public void setDebit_return(String debit_return) {
        this.debit_return = debit_return;
    }
}
