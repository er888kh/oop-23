package com.example.snapfoodgui;

import com.edgedb.driver.annotations.EdgeDBLinkType;
import com.edgedb.driver.annotations.EdgeDBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

@EdgeDBType
public class CustomerOrder {
    private static final Logger logger = LoggerFactory.getLogger(CustomerOrder.class);
    public User user;
    public UUID orderId;
    public Double price;
    public Long mapNode;
    public Boolean delivered;

    public Restaurant restaurant;
    public ZonedDateTime createdAt;
    @EdgeDBLinkType(Food.class)
    public ArrayList<Food> food;

    public CustomerOrder(){}
    public CustomerOrder(User user, Double price, Long mapNode, Restaurant rest, ArrayList<Food> food) {
        this.user = user;
        this.price = price;
        this.orderId = UUID.randomUUID();
        this.mapNode = mapNode;
        this.delivered = true;
        this.restaurant = rest;
        this.createdAt = ZonedDateTime.now();
        this.food = food;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void setFood(ArrayList<Food> food) {
        this.food = food;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setMapNode(Long mapNode) {
        this.mapNode = mapNode;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String InsertQuery() {
        String foods = "";
        int cnt = 0;
        for(var f:this.food) {
            if(cnt > 0) {
                foods += ",";
            }
            cnt++;
            foods += String.format("(SELECT Food FILTER %s)", f.Selector());
        }
        return String.format("INSERT CustomerOrder {user := (SELECT User FILTER %s)," +
                "price := %f, order_id := '%s', map_node := %d, delivered := %b, " +
                "restaurant := (SELECT Restaurant FILTER %s), createdAt := '%s', " +
                "food := {%s}};",
                this.user.Selector(),
                this.price,
                this.orderId.toString(),
                this.mapNode,
                this.delivered,
                this.restaurant.Selector(),
                this.createdAt.toString(),
                foods
        );
    }
}
