package com.redhat.coolstore.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config {

    private int threshold;
    private List<String> sms;
    private List<String> email;

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public List<String> getSms() {
        return sms;
    }

    public void setSms(List<String> sms) {
        this.sms = sms;
    }

    public static List<String> getListFromCSV(String str) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<>();
        } else {
            return Stream.of(str.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String toString() {
        return ("config: threshold :" + threshold + " sms: " + sms + " email: " + email);
    }
}
