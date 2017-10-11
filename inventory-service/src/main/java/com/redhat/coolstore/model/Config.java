package com.redhat.coolstore.model;

public class Config {

    private int threshold;
    private String sms;

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    @Override
    public String toString() {
        return ("config: threshold :" + threshold + " sms: " + sms);
    }
}
