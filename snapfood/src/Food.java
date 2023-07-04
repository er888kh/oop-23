
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

}
