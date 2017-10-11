/**
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.coolstore.api_gateway;

import com.redhat.coolstore.api_gateway.model.InventoryConfig;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class InventoryConfigGateway extends RouteBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(InventoryConfigGateway.class);

	@Autowired
	private Environment env;
	
    @Override
    public void configure() throws Exception {
    	try {
    		getContext().setTracing(Boolean.parseBoolean(env.getProperty("ENABLE_TRACER", "false")));	
		} catch (Exception e) {
			LOG.error("Failed to parse the ENABLE_TRACER value: {}", env.getProperty("ENABLE_TRACER", "false"));
		}

        rest("/availability/").description("Product Inventory Service Configuration")

            .post("/config").description("Set config")
				.bindingMode(RestBindingMode.off)
                .route().id("setConfigRoute")
				.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
				.setHeader(Exchange.HTTP_URI, simple("http://{{env:INVENTORY_ENDPOINT:inventory:8080}}/api/availability/config"))
				.to("http4://DUMMY")
				.setBody(simple(null))
            .endRest()

			.get("/config").description("Get config")
				.produces(MediaType.APPLICATION_JSON_VALUE)
				.outType(InventoryConfig.class)
				.route().id("getConfigRoute")
				.removeHeaders("CamelHttp*")
				.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
				.setHeader(Exchange.HTTP_URI, simple("http://{{env:INVENTORY_ENDPOINT:inventory:8080}}/api/availability/config"))
				.to("http4://DUMMY")
				.setHeader("CamelJacksonUnmarshalType", simple(InventoryConfig.class.getName()))
				.unmarshal().json(JsonLibrary.Jackson, InventoryConfig.class)
			.endRest();
    }

    
}
