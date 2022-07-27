package com.example.paykeeperfyp.Admin;

public class RequestModel {
    String message,req_by;

    public RequestModel(String message, String req_by) {
        this.message = message;
        this.req_by = req_by;
    }

    public RequestModel() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReq_by() {
        return req_by;
    }

    public void setReq_by(String req_by) {
        this.req_by = req_by;
    }
}
