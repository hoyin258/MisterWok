package us.misterwok.app.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by hoyin on 14/4/14.
 */
public class CartItemClient extends BaseClient {
    public static final String PATH = "cartitem.json";

    public static void post(RequestParams requestParams, ResponseHandlerInterface responseHandler) {
        new AsyncHttpClient().post(getAbsoluteUrl(PATH), requestParams, responseHandler);
    }
}
