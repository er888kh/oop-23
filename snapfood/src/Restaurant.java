import java.util.ArrayList;

import com.edgedb.driver.annotations.EdgeDBLinkType;
import com.edgedb.driver.annotations.EdgeDBType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;

@EdgeDBType
public class Restaurant {
    private static final Logger logger = LoggerFactory.getLogger(Restaurant.class);

    public String name;
    public User manager;
    public Long mapNode;
    @EdgeDBLinkType(Food.class)
    public ArrayList<Food> menu;
    @EdgeDBLinkType(Food.class)
    public ArrayList<Food> activeMenu;
    @EdgeDBLinkType(Food.class)
    public ArrayList<Food> saleMenu;
    @EdgeDBLinkType(Comment.class)
    public ArrayList<Comment> comments;
    public void setName(String name) {
        this.name = name;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public void setMapNode(Long mapNode) {
        this.mapNode = mapNode;
    }

    public void setMenu(ArrayList<Food> menu) {
        this.menu = menu;
    }

    public void setActiveMenu(ArrayList<Food> activeMenu) {
        this.activeMenu = activeMenu;
    }

    public void setSaleMenu(ArrayList<Food> saleMenu) {
        this.saleMenu = saleMenu;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public boolean isRestaurantManager(User u) {
        return u.username.equals(this.manager.username);
    }

    public String Selector() {
        return String.format(".name = '%s'", this.name);
    }

    public static String ListAllFields() {
        return "{name, manager, map_node, menu, active_menu, sale_menu, comments}";
    }
}
