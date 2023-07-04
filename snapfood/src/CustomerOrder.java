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
    public ZonedDateTime createdAt;
    @EdgeDBLinkType(Food.class)
    public ArrayList<Food> food;

    public void setUser(User user) {
        this.user = user;
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

}
