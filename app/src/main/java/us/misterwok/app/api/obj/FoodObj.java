package us.misterwok.app.api.obj;

/**
 * Created by hoyin on 14/4/14.
 */
public class FoodObj extends BaseAPIObj {

    public Food[] data;

    public static class Food extends BaseAPIObj {
        public String menu_number;
        public String name;
        public String description;
        public boolean spicy;
        public String original;
        public Item[] items;

    }

    public static class Item  {

        public String price;
        public Size size;

    }

    public static class Size {
        public int id;
        public String name;
    }
}