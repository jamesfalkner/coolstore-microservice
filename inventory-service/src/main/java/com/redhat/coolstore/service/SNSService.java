package com.redhat.coolstore.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public List<String> unsubscribeByProtocol(String protocol, List<String> ignoreList) {
        // unsubscribe everything
        List<String> ignored = new ArrayList<>();
        ListSubscriptionsByTopicResult result = client.listSubscriptionsByTopic(topicArn);
        logger.info("unsubscribe from all " + protocol + ": ignoreList: " + ignoreList + ": considering from " + result.getSubscriptions().size() + " subscriptions");
        for (Subscription sub : result.getSubscriptions()) {
            logger.info("unsubscribe: Inspecting " + sub + " for protocol: " + protocol);
            if (sub.getProtocol().equals(protocol)) {
                if (ignoreList == null || !ignoreList.contains(sub.getEndpoint())) {
                    logger.info("Unsubscribing " + protocol + " from " + sub);
                    UnsubscribeResult unResult = client.unsubscribe(sub.getSubscriptionArn());
                    logger.info("Unsubscribe result : " + unResult);
                } else {
                    ignored.add(sub.getEndpoint());
                }
            }
        }
        return ignored;

    }

    public void subscribeSms(List<String> phoneNumbers) {

        List<String> stillSubscribed = unsubscribeByProtocol("sms", phoneNumbers);
        // subscribe new phone number
        phoneNumbers.forEach(phoneNumber -> {
            if (!stillSubscribed.contains(phoneNumber)) {
                SubscribeRequest subscribe = new SubscribeRequest(topicArn, "sms",
                        phoneNumber);
                SubscribeResult subscribeResult = client.subscribe(subscribe);
                logger.info("Subscribe sms request: " +
                        client.getCachedResponseMetadata(subscribe));
                logger.info("Subscribe sms result: " + subscribeResult);
            } else {
                logger.info("skipping already subscribed sms endpoint " + phoneNumber);
            }
        });
    }

    public void subscribeEmail(List<String> emails) {

        List<String> stillSubscribed = unsubscribeByProtocol("email", emails);
        // subscribe new emails
        emails.forEach(email -> {
            if (!stillSubscribed.contains(email)) {
                SubscribeRequest subscribe = new SubscribeRequest(topicArn, "email",
                        email);
                SubscribeResult subscribeResult = client.subscribe(subscribe);
                logger.info("Subscribe email request: " +
                        client.getCachedResponseMetadata(subscribe));
                logger.info("Subscribe email result: " + subscribeResult);
            } else {
                logger.info("skipping already subscribed email endpoint " + email);
            }
        });

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
