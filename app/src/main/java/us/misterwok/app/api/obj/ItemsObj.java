package us.misterwok.app.api.obj;

/**
 * Created by hoyin on 14/4/14.
 */
public class ItemsObj extends BaseAPIObj {

    public Item[] items;

    public static class Item extends BaseAPIObj {
        public int id;
        public String menu_id;
        public String name;
        public String desc;
        public String spicy;
        public String image;
        public Type[] types;

    }

    public static class Type extends BaseAPIObj {
        public String unit;
        public String price;

        @Override
        public String toString() {
            return "Type{" +
                    "unit='" + unit + '\'' +
                    ", price='" + price + '\'' +
                    '}';
        }
    }

}