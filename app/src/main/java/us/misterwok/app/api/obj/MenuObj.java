package us.misterwok.app.api.obj;

import android.text.TextUtils;

import us.misterwok.app.api.APIEngine;

/**
 * Created by hoyin on 14/4/14.
 */
public class MenuObj extends BaseAPIObj {


    public Category[] categories;
    public Food[] foods;


    public static class Category {
        public int id;
        public String name;
        public String description;
    }

    public static class Food  {
        public String menu_number;
        public String name;
        public String description;
        public boolean spicy;
        public String large;
        public Item[] items;

        public int category_id;

        public String getLargeImage() {
            if (!TextUtils.isEmpty(large))
                return APIEngine.DOMAIN + large;
            return null;
        }
    }

    public static class Item {

        public int id;
        public String price;
        public Size size;

    }

    public static class Size {

        public String name;


        public String getName() {

            String result = name;
            if (result == null) {
                result = "";
            }
            result = result.replace("Regular", "");

            if (TextUtils.isEmpty(result)) {
                return "$";
            } else {
                return name + ": $";
            }
        }
    }
}