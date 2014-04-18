package us.misterwok.app.api.obj;

/**
 * Created by hoyin on 14/4/14.
 */
public class CategoriesObj extends BaseAPIObj {

    public Category[] categories;

    public static class Category {
        public int id;
        public String name;
        public String desc;
    }
}