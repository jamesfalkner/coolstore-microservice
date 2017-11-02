package com.redhat.coolstore.api_gateway.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jfalkner on 10/10/17.
 */
public class InventoryConfig implements Serializable {
    private List<String> sms;
    private int threshold;
    private List<String> email;

    private static final long serialVersionUID = -75459819778382L;

    public InventoryConfig() {

    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public List<String> getSms() {
        return sms;
    }

    public void setSms(List<String> sms) {
        this.sms = sms;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
