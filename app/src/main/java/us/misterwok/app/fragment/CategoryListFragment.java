package us.misterwok.app.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import us.misterwok.app.Constants;
import us.misterwok.app.R;
import us.misterwok.app.activity.MainActivity;
import us.misterwok.app.api.APIEngine;
import us.misterwok.app.api.obj.MenuObj;
import us.misterwok.app.db.CartItemSQLiteHelper;

/**
 * Created by hoyin on 14/4/14.
 */
public class CategoryListFragment extends BaseFragment implements OnRefreshListener {


    public static final String KEY_CART_BROADCAST_RECEIVER = "us.misterwok.app.cert";

    protected PagerSlidingTabStrip mPagerSlidingTabStrip;
    protected ViewPager mViewPager;
    protected TextView mCartCount;
    protected ProgressBar mProgressBar;

    private BroadcastReceiver mCertUpdateBroadcastReceiver;
    private SharedPreferences mSharedPreferences;

    public static CategoryListFragment newInstance() {
        CategoryListFragment fragment = new CategoryListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(R.string.app_name);

        mSharedPreferences = getActivity().getSharedPreferences(
                getActivity().getPackageName(), Activity.MODE_PRIVATE);
        mCertUpdateBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (mCartCount != null) {
                    int cartItemCount = new CartItemSQLiteHelper(context).getCartItemCount();
                    mCartCount.setText(cartItemCount + "");
                } else {
                    mCartCount.setText(0 + "");
                }
            }
        };
        getActivity().registerReceiver(mCertUpdateBroadcastReceiver, new IntentFilter(KEY_CART_BROADCAST_RECEIVER));
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
        mProgressBar.setVisibility(View.VISIBLE);
        mPagerSlidingTabStrip.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MenuObj menuObj = getFromCache();
        if (menuObj != null) {
            bindFoodToList(menuObj);
        } else {
            onRefreshStarted(null);
        }
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

    @Override
    public void onRefreshStarted(View view) {
        APIEngine.getFoods(getString(R.string.store_id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                edit.putString(Constants.PREFERENCE_MENU_DATE, getCurrentMonthInString());
                edit.putString(Constants.PREFERENCE_MENU_DATA, responseBody);
                final MenuObj menuObj = new Gson().fromJson(responseBody, MenuObj.class);
                bindFoodToList(menuObj);
            }
        });
    }

    private void clearCache() {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(Constants.PREFERENCE_MENU_DATE, null);
        edit.putString(Constants.PREFERENCE_MENU_DATA, null);
        edit.commit();
    }

    private MenuObj getFromCache() {
        try {
            String date = mSharedPreferences.getString(Constants.PREFERENCE_MENU_DATE, null);
            if (date.compareTo(getCurrentMonthInString()) == 0) {
                String dataRecorded = mSharedPreferences.getString(Constants.PREFERENCE_MENU_DATA, null);
                return new Gson().fromJson(dataRecorded, MenuObj.class);
            }
        } catch (Exception ex) {
            clearCache();
        }
        return null;
    }

    private void bindFoodToList(MenuObj menuObj) {
        mProgressBar.setVisibility(View.GONE);
        try {
            CategoryPagerAdapter categoryPagerAdapter = new CategoryPagerAdapter(getChildFragmentManager(), menuObj);
            mViewPager.setAdapter(categoryPagerAdapter);
            mPagerSlidingTabStrip.setViewPager(mViewPager);
            mPagerSlidingTabStrip.setVisibility(View.VISIBLE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private String getCurrentMonthInString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        return formatter.format(new java.util.Date());
    }

    public static interface FilterFoodListener {
        public MenuObj.Food[] getFilterFood(int categoryId);
    }

    private class CategoryPagerAdapter extends FragmentPagerAdapter implements FilterFoodListener {
        MenuObj.Category[] mCategories;
        MenuObj.Food[] mFoods;

        public CategoryPagerAdapter(FragmentManager fm, MenuObj menuObj) {
            super(fm);
            mCategories = menuObj.categories;
            mFoods = menuObj.foods;
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
            FoodListFragment foodListFragment = FoodListFragment.newInstance(mCategories[position].id, this);
            return foodListFragment;
        }


        @Override
        public MenuObj.Food[] getFilterFood(int categoryId) {
            ArrayList<MenuObj.Food> foods = new ArrayList<MenuObj.Food>();
            for (int i = 0; i < mFoods.length; i++) {
                if (mFoods[i].category_id == categoryId) {
                    foods.add(mFoods[i]);
                }
            }
            return foods.toArray(new MenuObj.Food[foods.size()]);
        }
    }

}
