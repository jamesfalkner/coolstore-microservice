package com.redhat.coolstore.service;

import com.redhat.coolstore.model.kie.Inventory;
import com.redhat.coolstore.model.kie.Product;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.PathParam;
import java.util.List;

@FeignClient(name = "inventoryService", url = "${INVENTORY_ENDPOINT}")
public interface InventoryService {
    @RequestMapping(method = RequestMethod.GET, value = "/api/availability")
    List<Inventory> getAvailability();

    @RequestMapping(method = RequestMethod.PUT, value = "/api/availability/{itemId}/reduce/{quantity}")
    Inventory reduceQuantity(@PathVariable("itemId") String itemId, @PathVariable("quantity") int quantity);
}