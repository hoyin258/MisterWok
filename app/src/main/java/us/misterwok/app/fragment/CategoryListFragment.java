package us.misterwok.app.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;

import us.misterwok.app.R;
import us.misterwok.app.activity.MainActivity;
import us.misterwok.app.api.APIEngine;
import us.misterwok.app.api.obj.CategoriesObj;
import us.misterwok.app.db.CartItemSQLiteHelper;

/**
 * Created by hoyin on 14/4/14.
 */
public class CategoryListFragment extends BaseFragment {

    public static final String KEY_CART_BROADCAST_RECEIVER = "us.misterwok.app.cert";

    protected PagerSlidingTabStrip mPagerSlidingTabStrip;
    protected ViewPager mViewPager;
    protected ProgressBar mProgressBar;
    private TextView mCartCount;

    private BroadcastReceiver mCertUpdateBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (mCartCount != null) {
                int cartItemCount = new CartItemSQLiteHelper(context).getCartItemCount();
                mCartCount.setText(cartItemCount + "");
            } else {
                mCartCount.setText(0 + "");
            }
        }
    };

    public static CategoryListFragment newInstance() {
        CategoryListFragment fragment = new CategoryListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.tab_fragment_category);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager_fragment_category);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_fragment_category);
        mPagerSlidingTabStrip.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        APIEngine.getCategories(getString(R.string.store_id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                mProgressBar.setVisibility(View.GONE);
                CategoriesObj categoriesObj = new Gson().fromJson(responseBody, CategoriesObj.class);
                try {
                    mPagerSlidingTabStrip.setVisibility(View.VISIBLE);
                    mViewPager.setAdapter(new CategoryPagerAdapter(getChildFragmentManager(), categoriesObj.data));
                    mPagerSlidingTabStrip.setViewPager(mViewPager);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                super.onSuccess(statusCode, headers, responseBody);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(R.string.title_menu);
        getActivity().registerReceiver(mCertUpdateBroadcastReceiver, new IntentFilter(KEY_CART_BROADCAST_RECEIVER));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mCertUpdateBroadcastReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.cart, menu);
        RelativeLayout cartLayout = (RelativeLayout) menu.findItem(R.id.action_cart).getActionView();
        mCartCount = (TextView) cartLayout.findViewById(R.id.actionbar_notifcation_textview);
        Intent intent = new Intent(CategoryListFragment.KEY_CART_BROADCAST_RECEIVER);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                ((MainActivity) getActivity()).onNavigationDrawerItemSelected(1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class CategoryPagerAdapter extends FragmentPagerAdapter {
        CategoriesObj.Category[] mCategories;

        public CategoryPagerAdapter(FragmentManager fm, CategoriesObj.Category[] categories) {
            super(fm);
            mCategories = categories;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCategories[position].name;
        }

        @Override
        public int getCount() {
            return mCategories.length;
        }

        @Override
        public Fragment getItem(int position) {
            return FoodListFragment.newInstance(mCategories[position].id + "");
        }
    }
}
