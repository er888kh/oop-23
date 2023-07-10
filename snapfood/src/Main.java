import com.edgedb.driver.EdgeDBClient;
import com.edgedb.driver.EdgeDBClientConfig;
import com.edgedb.driver.exceptions.EdgeDBException;
import com.edgedb.driver.namingstrategies.NamingStrategy;
import graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static EdgeDBClient client = null;
    public static void main(String[] args) throws EdgeDBException, IOException,
            ExecutionException, InterruptedException, TimeoutException {
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
                        "(?<salt>[^ ^\\\\^']{8,})"
        );
        Pattern login = Pattern.compile("LOGIN (?<username>[0-9a-zA-Z]{5,}) (?<password>[0-9a-zA-Z]{8,})");
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
        Pattern editResponse = Pattern.compile(
                "EDIT RESPONSE (?<username>[0-9a-zA-Z]{5,}) "+
                "(?<response>[^\\\\^']+)");
        Pattern searchRestaurant = Pattern.compile("SEARCH RESTAURANT (?<name>[ a-zA-Z0-9]+)");
        Pattern showMenu = Pattern.compile("SHOW MENU");
        Pattern showComments = Pattern.compile("SHOW COMMENTS");
        Pattern addComment = Pattern.compile("ADD COMMENT (?<text>[^\\\\^']+)");
        Pattern showOrders = Pattern.compile("SHOW ORDERS");
        Pattern showCart = Pattern.compile("SHOW CART");
        Pattern confirmOrder = Pattern.compile("CONFIRM ORDER (?<mapNode>[0-9]+)");
        Pattern showETA = Pattern.compile("SHOW ETA (?<uuid>[\\-a-zA-Z0-9]+)");

        // TODO: Handle me in a cross-platform manner
        Dotenv denv = Dotenv.configure().directory("/etc/oop-23").load();
        Graph city = new Graph("city", denv.get("GRAPH_PATH"));

        client = new EdgeDBClient(EdgeDBClientConfig.builder()
                .withNamingStrategy(NamingStrategy.snakeCase())
                .useFieldSetters(true)
                .build()).withModule("default");

        var pingResult = client.querySingle(String.class, "SELECT 'hello java'").toCompletableFuture();
        var stringPingRes = pingResult.get(1000, TimeUnit.MILLISECONDS);
        if(stringPingRes == null || !stringPingRes.equals("hello java")) {
            System.err.println("Failed to PING the database");
            System.exit(0);
        }

        Scanner sc = new Scanner(System.in);
        var pw = System.out;
        String cmdLine = "";
        Matcher mt = null;
        User currUser = null;
        Restaurant currRestaurant = null;
        Food currFood = null;
        ArrayList<Food> currCart = new ArrayList<>();
        while(true) {
            cmdLine = sc.nextLine().trim();
            if(cmdLine.startsWith("END")) {
                break;
            }
            if(((mt = addUser.matcher(cmdLine)) != null) && mt.matches()) {
                String uname = mt.group("username");
                String passwd = mt.group("password");
                String fn = mt.group("firstname");
                String ln = mt.group("lastname");
                boolean ia = Boolean.valueOf(mt.group("isAdmin").equals("1"));
                User nu = new User(uname, fn, ln, passwd, ia);
                pw.println(nu.InsertQuery());
                try{
                    client.execute(nu.InsertQuery()).toCompletableFuture().get();
                    pw.println("Success, recovery code: " + nu.salt);
                } catch (Exception e) {
                    pw.println("Failed to create user, error=" + e);
                }
            } else if(((mt = resetPassword.matcher(cmdLine)) != null) && mt.matches()) {
                String uname = mt.group("username");
                String salt = mt.group("salt");
                String npw = mt.group("password");
                try {
                    String npwh = User.hashPassword(npw, salt);
                    if(npwh.equals("")) {
                        throw new RuntimeException("Invalid password hash");
                    }
                    client.execute(String.format(
                            "UPDATE User FILTER .username = '%s' AND .salt = '%s' SET { password_hash := '%s' };",
                            uname, salt, npwh)).toCompletableFuture().get();
                    var result = client.querySingle(Long.class, "SELECT count((SELECT User FILTER .username = '%s' AND .salt = '%s'))").toCompletableFuture().get();
                    if(result <= 0) {
                        throw new RuntimeException("No such user");
                    }
                    pw.println("Success");
                } catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            } else if(((mt = login.matcher(cmdLine)) != null) && mt.matches()) {
                String uname = mt.group("username");
                String passwd = mt.group("password");
                try {
                    if(currUser != null) {
                        throw new RuntimeException("A user is already logged in");
                    }
                    if(currCart != null){
                        currCart.clear();
                    }
                    var tmpUser = client.querySingle(
                            User.class,
                            String.format("SELECT User %s FILTER .username = '%s'",
                                    User.ListAllFields(), uname)).toCompletableFuture().get();
                    if(!User.hashPassword(passwd, tmpUser.salt).equals(tmpUser.passwordHash)) {
                        throw new RuntimeException("Invalid username/password");
                    }
                    currUser = tmpUser;
                    pw.println("Welcome " + currUser.username + "!");
                } catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            } else if(((mt = logout.matcher(cmdLine)) != null) && mt.matches()) {
                currCart.clear();
                currUser = null;
                currFood = null;
                currRestaurant = null;
            } else if(((mt = selectRestaurant.matcher(cmdLine)) != null) && mt.matches()) {
                String rname = mt.group("restaurant");
                try {
                    if(currUser == null) {
                        throw new RuntimeException("Invalid user");
                    }
                    var tmpRest = client.querySingle(
                            Restaurant.class,
                            String.format("SELECT Restaurant %s FILTER .name = '%s'",
                                    Restaurant.ListAllFields(), rname)).toCompletableFuture().get();
                    currRestaurant = tmpRest;
                    pw.println("Welcome to" + currRestaurant.name + "!");
                    if(currCart.size() > 0) {
                        pw.println("Emptying cart");
                        currCart.clear();
                    }
                } catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            } else if(((mt = addFood.matcher(cmdLine)) != null) && mt.matches()) {
                String fname = mt.group("name");
                Double price = Double.valueOf(mt.group("price"));
                try {
                    if(currUser == null || currUser.isAdmin == false || currRestaurant == null ||
                            !currUser.username.equals(currRestaurant.manager.username)) {
                        throw new RuntimeException("Invalid user/restaurant");
                    }
                    Food nf = new Food(fname, price, currRestaurant);
                    client.execute(nf.InsertQuery()).toCompletableFuture().get();
                } catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            } else if(((mt = editFood.matcher(cmdLine)) != null) && mt.matches()) {
                String fname = mt.group("name");
                String nname = mt.group("newName");
                Double price = Double.valueOf(mt.group("price"));
                Double dprice = Double.valueOf(mt.group("discountPrice"));
                Boolean active = mt.group("active").equals("1");
                try {
                    if(currUser == null || currUser.isAdmin == false || currRestaurant == null ||
                            !currUser.username.equals(currRestaurant.manager.username)) {
                        throw new RuntimeException("Invalid user/restaurant");
                    }
                    Food nf = new Food(nname, price, dprice, active, currRestaurant);
                    client.execute(String.format("DELETE Food FILTER {name = '%s', " +
                            "restaurant = (SELECT Restaurant FILTER .name = '%s')}",
                            fname, currRestaurant.name)).toCompletableFuture().get();
                    client.execute(nf.InsertQuery()).toCompletableFuture().get();
                } catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            } else if(((mt = deleteFood.matcher(cmdLine)) != null) && mt.matches()) {
                String fname = mt.group("name");
                try {
                    if(currUser == null || currUser.isAdmin == false || currRestaurant == null ||
                            !currUser.username.equals(currRestaurant.manager.username)) {
                        throw new RuntimeException("Invalid user/restaurant");
                    }
                    client.execute(String.format("DELETE Food FILTER {name = '%s', " +
                                    "restaurant = (SELECT Restaurant FILTER .name = '%s')}",
                            fname, currRestaurant.name)).toCompletableFuture().get();
                } catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            } else if(((mt = selectFood.matcher(cmdLine)) != null) && mt.matches()) {
                String fname = mt.group("name");
                try {
                    if(currUser == null || currRestaurant == null) {
                        throw new RuntimeException("Invalid user/restaurant");
                    }
                    currFood = client.querySingle(Food.class,
                            String.format("SELECT Food %s FILTER {name = '%s', " +
                                    "restaurant = (SELECT Restaurant FILTER .name = '%s')}",
                                    Food.ListAllFields(), fname, currRestaurant.name))
                                    .toCompletableFuture().get();
                } catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            } else if(((mt = addToCart.matcher(cmdLine)) != null) && mt.matches()) {
                if(currUser != null && currFood != null) {
                    currCart.add(currFood);
                }
            } else if(((mt = displayRatings.matcher(cmdLine)) != null) && mt.matches()) {
                if(currFood == null) {
                    pw.println("Invalid food");
                    continue;
                }
                if(currFood.ratings == null || currFood.ratings.isEmpty()) {
                    pw.println("No ratings found");
                    continue;
                }
                double sum = 0;
                for(var r:currFood.ratings){
                    sum += r.rating;
                    pw.println(r.user.username + "\t\tSays " + r.rating);
                }
                pw.println("Average: " + (sum / currFood.ratings.size()));
            } else if(((mt = editRating.matcher(cmdLine)) != null) && mt.matches()) {
                double rating = Double.valueOf(mt.group("rating"));
                if(currFood == null || currUser == null) {
                    pw.println("Invalid user/food");
                    continue;
                }
                try {
                    client.execute(String.format("DELETE Rating FILTER {food = (SELECT Food FILTER %s), " +
                            "user = (SELECT User FILTER %s)}", currFood.Selector(), currUser.Selector()))
                            .toCompletableFuture().get();
                    client.execute(String.format("INSERT Rating {food := (SELECT Food FILTER %s), " +
                            "user := (SELECT User FILTER %s), rating := %f}",
                            currFood.Selector(), currUser.Selector(), rating))
                            .toCompletableFuture().get();
                } catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            } else if(((mt = editResponse.matcher(cmdLine)) != null) && mt.matches()) {
                String uname = mt.group("username");
                String resp = mt.group("response");
                if(currUser == null || currUser.isAdmin == false ||
                    currRestaurant == null || !currRestaurant.isRestaurantManager(currUser)) {
                    pw.println("Invalid restaurant/user");
                    continue;
                }
                try {
                    client.execute(String.format("UPDATE Comment FILTER %s " +
                                    "SET {comment := '%s'}",
                                    Comment.Selector(uname, currRestaurant.name),
                                    resp))
                            .toCompletableFuture().get();
                } catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            } else if(((mt = showMenu.matcher(cmdLine)) != null) && mt.matches()) {
                if(currRestaurant == null) {
                    pw.println("No restaurant selected");
                    continue;
                }
                pw.println("Showing " + currRestaurant.activeMenu.size() + " items:");
                pw.println("Name | Price | Discount ratio");
                int cnt = 0;
                for(var f:currRestaurant.activeMenu) {
                    cnt++;
                    pw.println("\t" + cnt + ". " + f.name + "\t\t" + f.price + "\t\t" + f.salePrice);
                }
            } else if(((mt = showComments.matcher(cmdLine)) != null) && mt.matches()) {
                if(currRestaurant == null) {
                    pw.println("No restaurant selected");
                    continue;
                }
                for(var c:currRestaurant.comments) {
                    pw.println(c.text);
                    if(c.response != null && !c.response.isEmpty()) {
                        pw.println("Response: " + c.response);
                    } else {
                        pw.println("No Response");
                    }
                }
            } else if(((mt = showOrders.matcher(cmdLine)) != null) && mt.matches()) {
                if(currUser == null) {
                    pw.println("No user selected");
                    continue;
                }
                for(var o:currUser.orders) {
                    pw.println("Order id: " + o.orderId);
                    pw.println("Date: " + o.createdAt);
                    pw.println("Price: " + o.price);
                    pw.println("Destination: " + o.mapNode);
                    pw.println("Items: (Name | Current Price)");
                    for(var f:o.food) {
                        if(f == null){
                            continue;
                        }
                        pw.println("\t" + f.name + "\t\t" + f.price);
                    }
                }
            } else if(((mt = showCart.matcher(cmdLine)) != null) && mt.matches()) {
                if(currUser == null) {
                    pw.println("No user selected");
                    continue;
                }
                if(currCart.isEmpty()) {
                    pw.println("No food selected");
                    continue;
                }
                pw.println("Items: (Name | Calculated Price)");
                for(var f:currCart) {
                    if(f == null){
                        continue;
                    }
                    pw.println("\t" + f.name + "\t\t" + f.calculatePrice());
                }
            } else if(((mt = showETA.matcher(cmdLine)) != null) && mt.matches()) {
                if(currUser == null) {
                    pw.println("No user selected");
                    continue;
                }
                if(currUser.orders.isEmpty()) {
                    pw.println("No order to show ETA for");
                    continue;
                }
                var order = currUser.orders.get(currUser.orders.size() - 1);
                var edges = city.SSSP(
                        city.nodes.get(order.food.get(0).restaurant.mapNode.intValue()),
                        city.nodes.get(order.mapNode.intValue()),
                        (e) -> {return true;}
                );
                double dist = 0;
                for(var e: edges) {
                    dist += e.w;
                }
                pw.println(order.createdAt.plusSeconds((long)dist));
            } else if(((mt = confirmOrder.matcher(cmdLine)) != null) && mt.matches()) {
                Long mapNode = Long.valueOf(mt.group("mapNode"));
                if(currUser == null || currUser.orders.isEmpty()) {
                    pw.println("Invalid user/cart");
                }
                double price = 0.0;
                for(var f:currCart) {
                    price += f.calculatePrice();
                }
                var ord = new CustomerOrder(currUser, price, mapNode, currRestaurant, currCart);
                currUser.orders.add(ord);
                try {
                    client.execute(ord.InsertQuery()).toCompletableFuture().get();
                }catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            } else if(((mt = addComment.matcher(cmdLine)) != null) && mt.matches()) {
                if(currUser == null || currRestaurant == null) {
                    pw.println("Invalid user/restaurant");
                    continue;
                }
                String text = mt.group("text");
                boolean invalid = false;
                for(var c:currUser.comments) {
                    if(c.restaurant.name.equals(currRestaurant.name)) {
                        invalid = true;
                        break;
                    }
                }
                if(invalid) {
                    pw.println("Already commented");
                    continue;
                }
                Comment c = new Comment(currRestaurant, currUser, text, "");
                try {
                    client.execute(c.InsertQuery()).toCompletableFuture().get();
                }catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            } else if(((mt = searchRestaurant.matcher(cmdLine)) != null) && mt.matches()) {
                String name = "%" + mt.group("name") + "%";
                try {
                    var result = client.query(Restaurant.class, String.format(
                            "SELECT Restaurant {name} FILTER .name ilike '%s'", name
                    )).toCompletableFuture().get();
                    for(Restaurant r:result) {
                        pw.println(r.name);
                    }
                } catch (Exception e) {
                    pw.println("Operation failed, error=" + e);
                }
            }
        }

        sc.close();
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