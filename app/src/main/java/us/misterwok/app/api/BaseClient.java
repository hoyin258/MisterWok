package us.misterwok.app.api;

/**
 * Created by hoyin on 14/4/14.
 */
public class BaseClient {
    public static final String BASE_URL = "https://raw.githubusercontent.com/hoyin258/Misterwok/master/app/src/main/assets/";

    protected static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
