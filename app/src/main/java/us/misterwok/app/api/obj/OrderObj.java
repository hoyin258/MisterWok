package us.misterwok.app.api.obj;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by hoyin on 14/4/14.
 */
public class OrderObj extends BaseAPIObj {

    public Order[] data;


    public static class Order {
        public int id;
        public String order_num;
        public String created_at;
        public String phone;
        public Item[] items;
        public User user;
        public int status;

        public String getCreatedAt() {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = fmt.parse(created_at, new ParsePosition(0));

            SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            sdff.setTimeZone(TimeZone.getDefault());
            String localTime = sdff.format(date);
            return localTime;
        }
    }

    public static class Item {
        public String price;
        public Size size;
        public Food food;
    }

    public static class Size {
        public String name;
    }

    public static class Food {
        public String menu_number;
        public String name;
        public String description;
        public boolean spicy;
        public String large;
    }
    public static class User{
        public String facebook_id;
        public String facebook_name;
    }

}