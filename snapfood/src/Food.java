
import com.edgedb.driver.annotations.EdgeDBLinkType;
import com.edgedb.driver.annotations.EdgeDBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;

@EdgeDBType
public class Food {
    private static final Logger logger = LoggerFactory.getLogger(Food.class);
    public String name;
    public Double price;
    public Double salePrice;
    public Boolean active;
    public Restaurant restaurant;

    @EdgeDBLinkType(Rating.class)
    public ArrayList<Rating> ratings;

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Food() {}
    public Food(String name, Double price, Restaurant rest) {
        this.name = name;
        this.price = price;
        this.salePrice = 1.0;
        this.active = false;
        this.restaurant = rest;
    }

    public Food(String name, Double price, Double salePrice, Boolean active, Restaurant rest) {
        this.name = name;
        this.price = price;
        this.salePrice = salePrice;
        this.active = active;
        this.restaurant = rest;
    }

    public String InsertQuery() {
        return String.format("INSERT Food {name := '%s', "+
                "price := %f, sale_price := %f, active := %b, "+
                "restaurant := (SELECT Restaurant FILTER .name = '%s')}"
                , this.name, this.price, this.salePrice, this.active, this.restaurant.name);
    }

    public String Selector() {
        return String.format("name = '%s', restaurant = (SELECT Restaurant FILTER .name = '%s')",
                this.name, this.restaurant.name);
    }

    public static String ListAllFields() {
        return "{name, price, sale_price, active, restaurant, ratings}";
    }
}
