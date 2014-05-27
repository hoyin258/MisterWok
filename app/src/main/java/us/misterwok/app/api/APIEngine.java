package us.misterwok.app.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by hoyin on 18/5/14.
 * Yintro.com
 */
public class APIEngine {

    public static final String DOMAIN = "http://yintro.com";
    public static final String BASE_URL = DOMAIN + "/api/v1/";

    protected static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    protected static String getAbsoluteUrl(String relativeUrl, String keyApi) {
        return BASE_URL + relativeUrl + "?token=" + keyApi;
    }

    public static void getStoreDetail(String storeId, ResponseHandlerInterface responseHandler) {
        new AsyncHttpClient().get(getAbsoluteUrl(String.format("stores/%s", storeId)), null, responseHandler);
    }

    public static void getFullMenu(String storeId, ResponseHandlerInterface responseHandler) {
        new AsyncHttpClient().get(getAbsoluteUrl(String.format("stores/%s/foods", storeId)), null, responseHandler);
    }

    public static void getOrder(String storeId,RequestParams requestParams, ResponseHandlerInterface responseHandler) {
        new AsyncHttpClient().get(getAbsoluteUrl(String.format("stores/%s/orders", storeId)), requestParams, responseHandler);
    }

    public static void getCategories(String storeId, ResponseHandlerInterface responseHandler) {
        new AsyncHttpClient().get(getAbsoluteUrl(String.format("stores/%s/categories", storeId)), null, responseHandler);
    }

    public static void getFoods(String catId, ResponseHandlerInterface responseHandler) {
        new AsyncHttpClient().get(getAbsoluteUrl(String.format("categories/%s", catId)), null, responseHandler);
    }

    public static void createUser(RequestParams requestParams, ResponseHandlerInterface responseHandler) {
        new AsyncHttpClient().post(getAbsoluteUrl("users"), requestParams, responseHandler);
    }

    public static void createOrder(String apiKey, RequestParams requestParams, ResponseHandlerInterface responseHandler) {
        new AsyncHttpClient().post(getAbsoluteUrl("orders", apiKey), requestParams, responseHandler);
    }

    public static void registerAdmin(RequestParams requestParams, ResponseHandlerInterface responseHandler) {
        new AsyncHttpClient().post(getAbsoluteUrl("admin_user"), requestParams, responseHandler);
    }

}
