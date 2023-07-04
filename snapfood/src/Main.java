import com.edgedb.driver.EdgeDBClient;
import com.edgedb.driver.EdgeDBClientConfig;
import com.edgedb.driver.exceptions.EdgeDBException;
import com.edgedb.driver.namingstrategies.NamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws EdgeDBException, IOException, ExecutionException, InterruptedException {
        Pattern addUser = Pattern.compile(
                "ADD USER "+
                        "(?<username>[0-9a-zA-Z]{5,}) "+
                        "(?<password>[0-9a-zA-Z]{8,}) "+
                        "(?<firstname>[a-zA-Z]+) "+
                        "(?<lastname>[a-zA-Z]+) "+
                        "(?<isAdmin>[01])"
                );
        Pattern resetPassword = Pattern.compile(
                "RESET PASSWORD "+
                        "(?<username>[0-9a-zA-Z]{5,}) "+
                        "(?<password>[0-9a-zA-Z]{8,}) "+
                        "(?<salt>[0-9a-zA-Z]{8,})"
        );
        Pattern login = Pattern.compile("LOGIN (?<username>[0-9a-zA-Z]{5,}) (?<password>[0-9a-zA-Z]{8,}))");
        Pattern logout = Pattern.compile("LOGOUT");
        Pattern selectRestaurant = Pattern.compile("SELECT RESTAURANT (?<restaurant>[0-9a-zA-Z]{5,}.+)");
        Pattern addFood = Pattern.compile("ADD FOOD (?<name>[ 0-9a-zA-Z]{3,}) (?<price>[\\.0-9]+)");
        Pattern editFood = Pattern.compile(
                "EDIT FOOD (?<name>[ 0-9a-zA-Z]{3,}) "+
                        "(?<newName>[ 0-9a-zA-Z]{3,}) "+
                        "(?<price>[\\.0-9]+) "+
                        "(?<discountPrice>[\\.0-9]+) " +
                        "(?<active>[01])"
                );
        Pattern deleteFood = Pattern.compile("DELETE FOOD (?<name>[ 0-9a-zA-Z]{3,})");
        Pattern selectFood = Pattern.compile("SELECT FOOD (?<name>[ 0-9a-zA-Z]{3,})");
        Pattern addToCart = Pattern.compile("ADD TO CART");
        Pattern displayRatings = Pattern.compile("DISPLAY RATINGS");
        Pattern editRating = Pattern.compile("EDIT RATING (?<rating>[\\.0-9]+)");
        Pattern newResponse = Pattern.compile(
                "EDIT RESPONSE (?<username>[0-9a-zA-Z]{5,}) "+
                "(?<response>.+)");
        Pattern sendOrder = Pattern.compile("SEND ORDER (?<uuid>[\\-a-zA-Z0-9]+)");
        Pattern searchRestaurant = Pattern.compile("SEARCH RESTAURANT (?<name>[ a-zA-Z0-9]+)");
        Pattern showMenu = Pattern.compile("SHOW MENU");
        Pattern showComments = Pattern.compile("SHOW COMMENTS");
        Pattern editComment = Pattern.compile("EDIT COMMENT (?<text>.+)");
        Pattern showOrders = Pattern.compile("SHOW ORDERS");
        Pattern showCart = Pattern.compile("SHOW CART");
        Pattern confirmOrder = Pattern.compile("CONFIRM ORDER");
        Pattern showETA = Pattern.compile("SHOW ETA (?<uuid>[\\-a-zA-Z0-9]+)");

        var client = new EdgeDBClient(EdgeDBClientConfig.builder()
                .withNamingStrategy(NamingStrategy.snakeCase())
                .useFieldSetters(true)
                .build()).withModule("main");

        runQuery(client);
        System.exit(0);
    }

    private static void runQuery(EdgeDBClient client) {
        try {
            client.querySingle(String.class, "SELECT 'hello java'")
                    .thenAccept(t -> System.out.println(t))
                    .toCompletableFuture().get();
        } catch (Exception e) {
            logger.info("Error: {}", e);
        }
    }
}