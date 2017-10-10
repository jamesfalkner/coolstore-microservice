package com.redhat.coolstore.service;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.redhat.coolstore.model.Inventory;

@Stateless
public class InventoryService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private SNSService snsService;

	public static final int LOW_INVENTORY_THRESHOLD = 30;

	public static final Logger logger = Logger.getLogger(SNSService.class.getName());

	public InventoryService() {

	}
	
	public Inventory getInventory(String itemId) {
		Inventory inventory = em.find(Inventory.class,itemId);
		
//		List<String> recalledProducts = Arrays.asList("165613","165614");
//		if (recalledProducts.contains(itemId)) {
//			inventory.setQuantity(0);
//		}
		
		return inventory;
	}

	public Inventory reduceQuantity(String itemId, int amt) {
		Inventory inventory = em.find(Inventory.class,itemId);
		int originalQuantity = inventory.getQuantity();
		int newQuantity = originalQuantity - amt;
		inventory.setQuantity(newQuantity);

		if (newQuantity < LOW_INVENTORY_THRESHOLD) {
			try {
				snsService.sendNotification("Low Inventory Warning: Item " +
						inventory.getItemId() + " quantity remaining: " + newQuantity +
						" is below threshold (" + LOW_INVENTORY_THRESHOLD + ").");
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Cannot send SNS: " + ex.getMessage(), ex);
			}
		}
		return inventory;

	}
}
