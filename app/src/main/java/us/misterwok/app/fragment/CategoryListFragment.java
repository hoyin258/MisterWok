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
import org.json.JSONObject;

import java.util.ArrayList;

import us.misterwok.app.R;
import us.misterwok.app.activity.MainActivity;
import us.misterwok.app.api.APIEngine;
import us.misterwok.app.api.obj.CategoriesObj;
import us.misterwok.app.api.obj.FoodObj;
import us.misterwok.app.db.CartItemSQLiteHelper;

/**
 * Created by hoyin on 14/4/14.
 */
public class CategoryListFragment extends BaseFragment {

    public static final String KEY_CART_BROADCAST_RECEIVER = "us.misterwok.app.cert";

    protected PagerSlidingTabStrip mPagerSlidingTabStrip;
    protected ViewPager mViewPager;
    protected TextView mCartCount;
    protected ProgressBar mProgressBar;

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
        mProgressBar.setVisibility(View.VISIBLE);
        mPagerSlidingTabStrip.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        APIEngine.getCategories(getString(R.string.store_id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                final CategoriesObj categoriesObj = new Gson().fromJson(responseBody, CategoriesObj.class);

                APIEngine.getFullMenu(getString(R.string.store_id), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                        mProgressBar.setVisibility(View.GONE);
                        try {
                            FoodObj foodObj = new Gson().fromJson(responseBody, FoodObj.class);
                            CategoryPagerAdapter categoryPagerAdapter = new CategoryPagerAdapter(getChildFragmentManager(), categoriesObj.data, foodObj.data);
                            mViewPager.setAdapter(categoryPagerAdapter);
                            mPagerSlidingTabStrip.setViewPager(mViewPager);

                            mPagerSlidingTabStrip.setVisibility(View.VISIBLE);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e, JSONObject errorResponse) {
                        super.onFailure(e, errorResponse);
                    }
                });
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(R.string.app_name);
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

    public static interface FilterFoodListener {
        public FoodObj.Food[] getFilterFood(int categoryId);
    }

    private class CategoryPagerAdapter extends FragmentPagerAdapter implements FilterFoodListener {
        CategoriesObj.Category[] mCategories;
        FoodObj.Food[] mFoods;

        public CategoryPagerAdapter(FragmentManager fm, CategoriesObj.Category[] categories, FoodObj.Food[] foods) {
            super(fm);
            mCategories = categories;
            mFoods = foods;
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
            FoodListFragment foodListFragment = FoodListFragment.newInstance(mCategories[position].id , this);
            return foodListFragment;
        }

        @Override
        public FoodObj.Food[] getFilterFood(int categoryId) {
            ArrayList<FoodObj.Food> foods = new ArrayList<FoodObj.Food>();
            for (int i = 0; i < mFoods.length; i++) {
                if (mFoods[i].category_id == categoryId) {
                    foods.add(mFoods[i]);
                }
            }
            return foods.toArray(new FoodObj.Food[foods.size()]);
        }
    }

}
