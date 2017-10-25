package com.redhat.coolstore.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@ApplicationScoped
public class SNSService {

    private Logger logger = Logger.getLogger(SNSService.class.getName());
    private AmazonSNS client;
    private String topicArn;

    @PostConstruct
    public void init() {
        //
        // will look for:
        // AWS_ACCESS_KEY
        // AWS_SECRET_KEY
        // AWS_REGION_NAME
        //
        client = AmazonSNSClientBuilder.standard()
                .withCredentials(new SNSServiceCredentialsProvider())
                .withRegion(System.getenv("AWS_REGION_NAME"))
                .build();

        topicArn = System.getenv("SNS_TOPIC_ARN");
    }

    @PreDestroy
    public void teardown() {
        if (client != null) {
            client.shutdown();
        }
    }

    public void subscribeSms(String phoneNumber) {

        // unsubscribe everything
        ListSubscriptionsResult result = client.listSubscriptions();
        for (Subscription sub : result.getSubscriptions()) {
            logger.info("Unsubscribing from " + sub);
            UnsubscribeResult unResult = client.unsubscribe(sub.getSubscriptionArn());
            logger.info("Unsubscribe result : " + unResult);
        }

        // subscribe new phone number
        SubscribeRequest subscribe = new SubscribeRequest(topicArn, "sms",
                phoneNumber);
        SubscribeResult subscribeResult = client.subscribe(subscribe);
        logger.info("Subscribe request: " +
                client.getCachedResponseMetadata(subscribe));
        logger.info("Subscribe result: " + subscribeResult);

    }

    public void sendNotification(String msg) {
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

        PublishRequest publishRequest = new PublishRequest()
                .withMessage(msg)
                .withTopicArn(topicArn)
                .withMessageAttributes(smsAttributes);
        PublishResult result = client.publish(publishRequest);
        logger.info("Notification send result: " + result);
    }

}
