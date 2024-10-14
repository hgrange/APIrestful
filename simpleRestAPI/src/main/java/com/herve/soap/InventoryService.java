package com.herve.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.Date;
import java.util.List;

@WebService(serviceName = "InventoryWS")
public class InventoryService {
    @WebMethod(operationName = "InventoryItem")
    public InventoryItem getInventoryItem(@WebParam int id,@WebParam String name){
        return new InventoryItem(id,name,new Date());
    }
    @WebMethod(operationName = "InventoryItemList")
    public List<InventoryItem> starwarsCharacterList(){
        return List.of(new InventoryItem(1,"Labtop",new Date()),
                new InventoryItem(1,"Book",new Date()),
                new InventoryItem(1,"Monitor",new Date()));
    }
}