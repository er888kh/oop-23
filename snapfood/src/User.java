import com.edgedb.driver.annotations.EdgeDBLinkType;
import com.edgedb.driver.annotations.EdgeDBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

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
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public User(String username, String firstname, String lastname, String pw, boolean isAdmin) {
        this.balance = Double.valueOf(0);
        this.username = username;
        this.salt = generateSalt();
        this.passwordHash = hashPassword(pw, this.salt);
        this.firstname = firstname;
        this.lastname = lastname;
        this.isAdmin = Boolean.valueOf(isAdmin);
    }

    public User() {
    }

    public static String hashPassword(String password, String salt){
        try {
            char[] passwordChars = password.toCharArray();
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            KeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashedBytes = factory.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            System.err.println("Fatal Error: " + e);
            System.exit(1);
        }
        return "";
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

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

    public void setAdmin(Boolean admin) {
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

    public String InsertQuery() {
        return String.format("INSERT User {username := '%s',  password_hash := '%s', " +
                "salt := '%s', firstname := '%s', lastname := '%s', " +
                "is_admin := %b, balance := %f};",
                username,
                passwordHash,
                salt,
                firstname,
                lastname,
                isAdmin,
                balance);
    }
    public static String ListAllFields() {
        return "{username, password_hash, salt, firstname, lastname, is_admin, balance, ratings, comments, orders}";
    }

    public String Selector() {
        return String.format(".username = '%s'", this.username);
    }
    public HashMap<String, Object> ObjectDict() {
        var result = new HashMap<String, Object>();
        result.put("username", this.username);
        result.put("password_hash", this.passwordHash);
        result.put("salt", this.salt);
        result.put("firstname", this.firstname);
        result.put("lastname", this.lastname);
        result.put("is_admin", this.isAdmin);
        result.put("balance", this.balance);
        return result;
    }
}

class PasswordHashing {
}

