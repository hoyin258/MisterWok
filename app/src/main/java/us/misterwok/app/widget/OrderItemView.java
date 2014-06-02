package us.misterwok.app.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import us.misterwok.app.Constants;
import us.misterwok.app.R;
import us.misterwok.app.api.APIEngine;
import us.misterwok.app.api.obj.OrderObj;

/**
 * Created by hoyin on 14/4/14.
 */
public class OrderItemView extends FrameLayout {

    private TextView textViewDate;
    private TextView textViewOrderNum;
    private TextView textViewOrderPhone;
    private TextView textViewUserName;

    private TextView buttonAction;
    private LinearLayout linearLayoutContainer;
    private Button buttonFacebook;

    private void assignViews() {
        textViewDate = (TextView) findViewById(R.id.text_view_date);
        textViewOrderNum = (TextView) findViewById(R.id.text_view_order_num);
        textViewUserName = (TextView) findViewById(R.id.text_view_user_name);
        textViewOrderPhone = (TextView) findViewById(R.id.text_view_order_phone);
        linearLayoutContainer = (LinearLayout) findViewById(R.id.linear_layout_container);
        buttonFacebook = (Button) findViewById(R.id.button_facebook);
        buttonAction = (Button) findViewById(R.id.button_action);
    }

    public OrderItemView(Context context) {
        super(context);
        init();
    }

    public OrderItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OrderItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_order, this);
        assignViews();
    }

    public void parse(final OrderObj.Order order) {
        textViewDate.setText("Date : " + order.getCreatedAt());
        textViewOrderNum.setText("Number : " + order.order_num);
        textViewOrderPhone.setText("Phone : " + order.phone);
        textViewUserName.setText("Name : " + order.user.facebook_name);


        buttonAction.setVisibility(View.VISIBLE);
        if (order.status == 0) {
            buttonAction.setText(R.string.action_received);
            buttonAction.setBackgroundColor(getResources().getColor(R.color.green));
            buttonAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                            getContext().getPackageName(), Activity.MODE_PRIVATE);
                    String email = sharedPreferences.getString(Constants.PREFERENCE_ADMIN_EMAIL, "");
                    String password = sharedPreferences.getString(Constants.PREFERENCE_ADMIN_PASSWORD, "");

                    RequestParams requestParams = new RequestParams();
                    requestParams.put("email", email);
                    requestParams.put("password", password);

                    APIEngine.setOrderReceived(order.id, requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                            super.onSuccess(statusCode, headers, responseBody);
                            order.status = 1;
                            parse(order);
                            invalidate();
                        }

                        @Override
                        public void onFailure(Throwable e, JSONObject errorResponse) {
                            super.onFailure(e, errorResponse);
                        }
                    });
                }
            });
        } else if (order.status == 1) {
            buttonAction.setText(R.string.action_made);
            buttonAction.setBackgroundColor(getResources().getColor(R.color.blue));
            buttonAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                            getContext().getPackageName(), Activity.MODE_PRIVATE);
                    String email = sharedPreferences.getString(Constants.PREFERENCE_ADMIN_EMAIL, "");
                    String password = sharedPreferences.getString(Constants.PREFERENCE_ADMIN_PASSWORD, "");

                    RequestParams requestParams = new RequestParams();
                    requestParams.put("email", email);
                    requestParams.put("password", password);

                    APIEngine.setOrderMade(order.id, requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                            order.status = 2;
                            parse(order);
                            invalidate();
                            super.onSuccess(statusCode, headers, responseBody);
                        }
                    });
                }
            });
        } else {
            buttonAction.setVisibility(View.GONE);
        }

        buttonFacebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + order.user.facebook_id)));
                } catch (Exception e) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + order.user.facebook_name)));
                }
            }
        });

        linearLayoutContainer.removeAllViews();
        for (int i = 0; i < order.items.length; i++) {
            OrderObj.Item item = order.items[i];
            View view = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.view_order_item, null, false);

            TextView textViewName = (TextView) view.findViewById(R.id.text_view_name);
            TextView textViewPrice = (TextView) view.findViewById(R.id.text_view_price);

            textViewName.setText(
                    item.food.menu_number + " "
                            + item.food.name + " :"
                            + " (" + item.size.name + ")"
            );
            textViewPrice.setText(item.price);

            linearLayoutContainer.addView(view);
        }
        requestLayout();
        invalidate();
    }

}
