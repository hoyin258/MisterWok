package us.misterwok.app.api.obj;

import com.google.gson.Gson;

/**
 * Created by hoyin on 18/4/14.
 */
public class BaseAPIObj {

    String status;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
