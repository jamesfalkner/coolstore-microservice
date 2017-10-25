package com.redhat.coolstore.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.redhat.coolstore.model.Config;
import com.redhat.coolstore.model.Inventory;

@Singleton
public class InventoryService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private SNSService snsService;

	public static final Logger logger = Logger.getLogger(SNSService.class.getName());

	public InventoryService() {

	}

	private Config config;

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		String existingPh = this.config.getSms();
		this.config = config;
		if (config.getSms() != null && !config.getSms().equals(existingPh)) {
			snsService.subscribeSms(config.getSms());
		}
	}

	@PostConstruct
	public void initConfig() {
		this.config = new Config();
		this.config.setSms(System.getenv("INVENTORY_NOTIFICATION_PHONE_NUMBER"));
		this.config.setThreshold(30);
		snsService.subscribeSms(this.config.getSms());
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
		if (newQuantity < config.getThreshold()) {
			try {
				snsService.sendNotification("Low Inventory Warning: Item " +
						inventory.getItemId() + " quantity remaining: " + newQuantity +
						" is below threshold (" + config.getThreshold() + ").");
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Cannot send SNS: " + ex.getMessage(), ex);
			}
		}
		return inventory;

	}
}
