
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
    public Comment(){}
    public Comment(Restaurant r, User u, String t, String resp) {
        this.restaurant = r;
        this.user = u;
        this.text = t;
        this.response = resp;
    }

    public static String Selector(String rname, String uname) {
        return String.format(".restaurant = (SELECT Restaurant FILTER .name = '%s') and .user = " +
                "(SELECT User FILTER .username = '%s')",
                rname, uname);
    }

    public String InsertQuery() {
        return String.format("INSERT Comment {restaurant := (SELECT Restaurant FILTER %s), " +
                "user := (SELECT User FILTER %s), text := '%s', response := '%s'",
                this.restaurant.Selector(),
                this.user.Selector(),
                this.text,
                this.response
        );
    }
}
