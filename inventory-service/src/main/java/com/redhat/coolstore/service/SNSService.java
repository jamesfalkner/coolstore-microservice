package com.redhat.coolstore.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class SNSService {

    private AmazonSNS client;

    @PostConstruct
    public void init() {
        // will look for:
        // AWS_ACCESS_KEY_ID
        // AWS_SECRET_ACCESS_KEY
        // AWS_REGION
        // INVENTORY_NOTIFICATION_PHONE_NUMBER
        //
        client = AmazonSNSClientBuilder.standard().build();
    }

    @PreDestroy
    public void teardown() {
        if (client != null) {
            client.shutdown();
        }
    }

    public void sendNotification(String phoneNumber, String msg) {
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<>();

        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("RHCoolStore") //The sender ID shown on the device.
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("0.50") //Sets the max price to 0.50 USD.
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Promotional") //Sets the type to promotional.
                .withDataType("String"));

        PublishResult result = client.publish(new PublishRequest()
                .withMessage(msg)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
    }

}
