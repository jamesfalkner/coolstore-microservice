package com.redhat.coolstore.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

public class SNSServiceCredentialsProvider implements AWSCredentialsProvider {

    @Override
    public AWSCredentials getCredentials() {
        return new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return System.getenv("AWS_ACCESS_KEY");
            }

            @Override
            public String getAWSSecretKey() {
                return System.getenv("AWS_SECRET_KEY");
            }
        };
    }

    @Override
    public void refresh() {
        // NO OP
    }
}
