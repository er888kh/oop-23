import com.edgedb.driver.annotations.EdgeDBLinkType;
import com.edgedb.driver.annotations.EdgeDBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;

@EdgeDBType
public class User {
    private static final Logger logger = LoggerFactory.getLogger(User.class);

    public Double balance;
    public String username;
    public String passwordHash;
    public String salt;
    public String firstname;
    public String lastname;
    public Boolean isAdmin;
    @EdgeDBLinkType(Rating.class)
    public ArrayList<Rating> ratings;
    @EdgeDBLinkType(Comment.class)
    public ArrayList<Comment> comments;
    @EdgeDBLinkType(CustomerOrder.class)
    public ArrayList<CustomerOrder> orders;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setSal(String salt) {
        this.salt = salt;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public void setRatings(ArrayList<Rating> ratings) {
        this.ratings = ratings;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void setOrders(ArrayList<CustomerOrder> orders) {
        this.orders = orders;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
