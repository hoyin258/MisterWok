package us.misterwok.app.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import us.misterwok.app.R;
import us.misterwok.app.api.obj.OrderObj;

/**
 * Created by hoyin on 14/4/14.
 */
public class OrderItemView extends FrameLayout {

    private TextView textViewDate;
    private TextView textViewOrderNum;
    private TextView textViewOrderPhone;
    private TextView textViewUserName;
    private LinearLayout linearLayoutContainer;
    private Button buttonFacebook;

    private void assignViews() {
        textViewDate = (TextView) findViewById(R.id.text_view_date);
        textViewOrderNum = (TextView) findViewById(R.id.text_view_order_num);
        textViewUserName = (TextView) findViewById(R.id.text_view_user_name);
        textViewOrderPhone = (TextView) findViewById(R.id.text_view_order_phone);
        linearLayoutContainer = (LinearLayout) findViewById(R.id.linear_layout_container);
        buttonFacebook = (Button) findViewById(R.id.button_facebook);
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
