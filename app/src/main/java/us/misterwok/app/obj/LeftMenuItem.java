package us.misterwok.app.obj;

/**
 * Created by hoyin on 17/4/14.
 */

public class LeftMenuItem {
    String name;
    int icon;

    public LeftMenuItem(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }
}
