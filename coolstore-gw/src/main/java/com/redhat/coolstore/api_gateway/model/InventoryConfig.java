package com.redhat.coolstore.api_gateway.model;

import java.io.Serializable;

/**
 * Created by jfalkner on 10/10/17.
 */
public class InventoryConfig implements Serializable {
    private String sms;
    private int threshold;
    private static final long serialVersionUID = -75459819778382L;

    public InventoryConfig() {

    }
    public InventoryConfig(String sms, int threshold) {
        this.sms = sms;
        this.threshold = threshold;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
