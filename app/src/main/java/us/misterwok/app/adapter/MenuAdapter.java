package us.misterwok.app.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import us.misterwok.app.R;
import us.misterwok.app.obj.LeftMenuItem;

/**
 * Created by hoyin on 17/4/14.
 */
public class MenuAdapter extends BaseAdapter {

    ArrayList<LeftMenuItem> mLeftMenuItem;
    Activity mActivity;

    public MenuAdapter(Activity activity) {
        mActivity = activity;
        mLeftMenuItem = new ArrayList<LeftMenuItem>();
    }

    public void setLeftMenuItem(ArrayList<LeftMenuItem> leftMenuItems) {
        mLeftMenuItem.clear();
        mLeftMenuItem.addAll(leftMenuItems);
    }

    @Override
    public int getCount() {
        return mLeftMenuItem.size();
    }

    @Override
    public LeftMenuItem getItem(int position) {
        return mLeftMenuItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mActivity, R.layout.view_left_menu_item, null);
            ImageView mIcon = (ImageView) convertView.findViewById(R.id.image_view_icon);
            TextView mName = (TextView) convertView.findViewById(R.id.text_view_name);

            mIcon.setImageResource(getItem(position).getIcon());
            mName.setText(getItem(position).getName());
        }
        return convertView;
    }

}
