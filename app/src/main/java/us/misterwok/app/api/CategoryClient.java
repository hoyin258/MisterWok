package us.misterwok.app.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by hoyin on 14/4/14.
 */
public class CategoryClient extends BaseClient {
    public static final String PATH = "category.json";

    public static void get(ResponseHandlerInterface responseHandler) {
        new AsyncHttpClient().get(getAbsoluteUrl(PATH), responseHandler);
    }
}
