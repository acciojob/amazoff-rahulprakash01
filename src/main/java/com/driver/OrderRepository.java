package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<String, Order>();
        this.partnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
        this.orderToPartnerMap = new HashMap<String, String>();
    }

    public void saveOrder(Order order){
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId){
        // your code here
        // create a new partner with given partnerId and save it
        partnerMap.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            // your code here
            //add order to given partner's order list
            //increase order count of partner
            //assign partner to this order
            partnerToOrderMap.putIfAbsent(partnerId, new HashSet<>());

            // Add order to the partner's assigned orders
            partnerToOrderMap.get(partnerId).add(orderId);

            // Assign the partner to this order
            orderToPartnerMap.put(orderId, partnerId);

            // Increase the partner's order count
            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.incrementOrderCount();
        }
    }

    public Order findOrderById(String orderId){
        // your code here
        return orderMap.get(orderId);
    }

    public DeliveryPartner findPartnerById(String partnerId){
        // your code here
        return partnerMap.get(partnerId);
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        // your code here
        if (partnerId == null || !partnerToOrderMap.containsKey(partnerId)) {
            return 0; // Return 0 if partnerId is null or doesn't exist
        }
        return partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()).size();
    }

    public List<String> findOrdersByPartnerId(String partnerId){
        // your code here
        if (partnerId == null || !partnerToOrderMap.containsKey(partnerId)) {
            return new ArrayList<>(); // Return an empty list if partnerId is null or not found
        }
        return new ArrayList<>(partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()));
    }

    public List<String> findAllOrders(){
        // your code here
        // return list of all orders
        if (orderMap == null || orderMap.isEmpty()) {
            return new ArrayList<>(); // Return an empty list if no orders exist
        }
        return new ArrayList<>(orderMap.keySet());
    }

    public void deletePartner(String partnerId){
        // your code here
        // delete partner by ID
        if(partnerMap.containsKey(partnerId)){
            HashSet<String> orders = partnerToOrderMap.remove(partnerId);
            if (orders != null) {
                for (String orderId : orders) {
                    orderToPartnerMap.remove(orderId);
                }
            }
            partnerMap.remove(partnerId);
        }
    }

    public void deleteOrder(String orderId){
        // your code here
        // delete order by ID
        if (!orderMap.containsKey(orderId)) {
            return; // Order does not exist
        }
        if (orderToPartnerMap.containsKey(orderId)) {
            String partnerId = orderToPartnerMap.get(orderId);
            if (partnerToOrderMap.containsKey(partnerId)) {
                partnerToOrderMap.get(partnerId).remove(orderId);
            }
            partnerMap.get(partnerId).decrementOrderCount();
            orderToPartnerMap.remove(orderId);
        }

        // Remove order from the main order map
        orderMap.remove(orderId);
    }

    public Integer findCountOfUnassignedOrders(){
        // your code here
//        int count = 0;
//        for (String orderId : orderMap.keySet()) {
//            if (!orderToPartnerMap.containsKey(orderId)) {
//                count++;
//            }
//        }
//        return count;
        return orderMap.size() - orderToPartnerMap.size();
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId){
        // your code here
        if (!partnerToOrderMap.containsKey(partnerId)) return 0;

        int timeLimit = convertTimeToMinutes(timeString);
        int count = 0;

        for (String orderId : partnerToOrderMap.get(partnerId)) {
            if (orderMap.get(orderId).getDeliveryTime() > timeLimit) {
                count++;
            }
        }
        return count;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId){
        // your code here
        // code should return string in format HH:MM
        if (!partnerToOrderMap.containsKey(partnerId)) return null;

        int maxTime = 0;

        for (String orderId : partnerToOrderMap.get(partnerId)) {
            maxTime = Math.max(maxTime, orderMap.get(orderId).getDeliveryTime());
        }

        return convertMinutesToTime(maxTime);
    }
    private int convertTimeToMinutes(String timeString){
        String[] parts = timeString.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private String convertMinutesToTime(int totalMinutes){
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }
}