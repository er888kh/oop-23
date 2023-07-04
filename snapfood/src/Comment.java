
import com.edgedb.driver.annotations.EdgeDBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;

@EdgeDBType
public class Comment {
    private static final Logger logger = LoggerFactory.getLogger(Comment.class);
    public Restaurant restaurant;
    public User user;
    public String text;
    public String response;

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setResponse(String response) {
        this.response = response;
    }

}
