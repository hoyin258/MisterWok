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
import us.misterwok.app.api.obj.ItemsObj;

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

    public void parse(ItemsObj.Item item) {
        mName.setText(item.menu_id + ". " + item.name);
        mDescription.setText(item.desc);
        ImageLoader.getInstance().displayImage(item.image, mImageView);

        mType1.setVisibility(View.GONE);
        mType2.setVisibility(View.GONE);
        mType3.setVisibility(View.GONE);
        if (item.types != null && item.types.length > 0) {
            ItemsObj.Type types;
            if (item.types.length > 0 && item.types[0] != null) {
                types = item.types[0];
                mType1.setText(types.unit + ": " + types.price);
                mType1.setVisibility(View.VISIBLE);
            }
            if (item.types.length > 1 && item.types[1] != null) {
                types = item.types[1];
                mType2.setText(types.unit + ": " + types.price);
                mType2.setVisibility(View.VISIBLE);
            }
            if (item.types.length > 2 && item.types[2] != null) {
                types = item.types[2];
                mType3.setText(types.unit + ": " + types.price);
                mType3.setVisibility(View.VISIBLE);
            }
        } else {
            mTypeGroup.setVisibility(View.GONE);
        }
    }

}
