package us.misterwok.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import us.misterwok.app.R;
import us.misterwok.app.api.obj.FoodObj;

/**
 * Created by hoyin on 14/4/14.
 */
public class MenuItemView extends FrameLayout {
    private ViewGroup mTypeGroup;
    private ImageView mImageView;
    private TextView mName;
    private TextView mDescription;
    private TextView mType1;
    private TextView mType2;
    private TextView mType3;

    public MenuItemView(Context context) {
        super(context);
        init();
    }

    public MenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_item, this);
        mImageView = (ImageView) findViewById(R.id.image_view_item);
        mName = (TextView) findViewById(R.id.text_view_name);
        mDescription = (TextView) findViewById(R.id.text_view_desc);
        mTypeGroup = (ViewGroup) findViewById(R.id.view_group_type);
        mType1 = (TextView) findViewById(R.id.text_view_type_1);
        mType2 = (TextView) findViewById(R.id.text_view_type_2);
        mType3 = (TextView) findViewById(R.id.text_view_type_3);
    }

    public void parse(FoodObj.Food food) {
        mName.setText(food.menu_number + ". " + food.name);
        mDescription.setText(food.description);
        ImageLoader.getInstance().displayImage(food.original, mImageView);

        mType1.setVisibility(View.GONE);
        mType2.setVisibility(View.GONE);
        mType3.setVisibility(View.GONE);
        if (food.items != null && food.items.length > 0) {
            FoodObj.Item item;
            if (food.items.length > 0 && food.items[0] != null) {
                item = food.items[0];
                mType1.setText(item.size + ": " + item.price);
                mType1.setVisibility(View.VISIBLE);
            }
            if (food.items.length > 1 && food.items[1] != null) {
                item = food.items[1];
                mType2.setText(item.size + ": " + item.price);
                mType2.setVisibility(View.VISIBLE);
            }
            if (food.items.length > 2 && food.items[2] != null) {
                item = food.items[2];
                mType3.setText(item.size + ": " + item.price);
                mType3.setVisibility(View.VISIBLE);
            }
        } else {
            mTypeGroup.setVisibility(View.GONE);
        }
    }

}
