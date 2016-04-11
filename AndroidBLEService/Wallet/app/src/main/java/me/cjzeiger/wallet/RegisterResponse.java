package me.cjzeiger.wallet;

public class RegisterResponse {

    public String bankid;
    public String regid;
    public int status;


    public String toString(){
        return "status: " + status + " bankid: " + bankid + " regid: " + regid;
    }
}

