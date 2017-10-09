package com.redhat.coolstore.rest;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.coolstore.model.Inventory;
import com.redhat.coolstore.service.InventoryService;

@RequestScoped
@Path("/availability")
public class AvailabilityEndpoint implements Serializable {

	private static final long serialVersionUID = -7227732980791688773L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityEndpoint.class);

	@Inject
	private InventoryService inventoryService;

	@GET
	@Path("{itemId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Inventory getAvailability(@PathParam("itemId") String itemId) {
		LOGGER.debug("Calling the inventory service");
		return inventoryService.getInventory(itemId);
	}

	@PUT // should really be PATCH but not supported in JAX-RS 2.1
	@Path("{itemId}/reduce/{quantity}")
	@Produces(MediaType.APPLICATION_JSON)
	public Inventory reduceQuantity(@PathParam("itemId") String itemId, @PathParam("quantity") int amt) {
		LOGGER.debug("Updating inventory item " + itemId + " by reducing quantity by " + amt);
		return inventoryService.reduceQuantity(itemId, amt);
	}

}
