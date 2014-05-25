package us.misterwok.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import us.misterwok.app.R;
import us.misterwok.app.api.obj.FoodObj;
import us.misterwok.app.widget.MenuItemView;

/**
 * Created by hoyin on 14/4/14.
 */
public class FoodListFragment extends BaseFragment implements OnRefreshListener, AdapterView.OnItemClickListener {

    private static final String KEY_CATEGORY = "category_id";

    PullToRefreshLayout mPullToRefreshLayout;
    ListView mListView;
    MenuAdapter menuAdapter;

    CategoryListFragment.FilterFoodListener mFilterFoodListener;

    public static FoodListFragment newInstance(int categoryId, CategoryListFragment.FilterFoodListener filterFoodListener) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(KEY_CATEGORY, categoryId);
        fragment.setArguments(arguments);
        fragment.setFilterFoodListener(filterFoodListener);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPullToRefreshLayout = (PullToRefreshLayout) inflater.inflate(R.layout.fragment_items, container, false);
        mListView = (ListView) mPullToRefreshLayout.findViewById(R.id.list_view_fragment_menu);
        mListView.setEmptyView(mPullToRefreshLayout.findViewById(R.id.progress_bar_fragment_menu));
        mListView.setOnItemClickListener(this);


        View footer =getActivity().getLayoutInflater().inflate(R.layout.view_item_footer,null,false);
        mListView.addFooterView(footer);
        return mPullToRefreshLayout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);
        getData();
    }

    public void setFilterFoodListener(CategoryListFragment.FilterFoodListener mFilterFoodListener) {
        this.mFilterFoodListener = mFilterFoodListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mFilterFoodListener = null;
    }

    @Override
    public void onRefreshStarted(View view) {
        getData();
    }


    private void getData() {
        mPullToRefreshLayout.setRefreshing(true);

        menuAdapter = new MenuAdapter(mFilterFoodListener.getFilterFood(getArguments().getInt(KEY_CATEGORY)));
        mListView.setAdapter(menuAdapter);
        mPullToRefreshLayout.setRefreshComplete();

//        APIEngine.getFoods(getArguments().getString(KEY_CATEGORY), new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
//                FoodObj itemsObj = new Gson().fromJson(responseBody, FoodObj.class);
//                menuAdapter = new MenuAdapter(itemsObj.data);
//                mListView.setAdapter(menuAdapter);
//                mPullToRefreshLayout.setRefreshComplete();
//                super.onSuccess(statusCode, headers, responseBody);
//            }
//        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        FoodObj.Food food = menuAdapter.getItem(position);
        FoodDetailFragment.newInstance(new Gson().toJson(food)).show(getFragmentManager(), "item_detail");

    }


    private class MenuAdapter extends BaseAdapter {
        FoodObj.Food[] mItems;

        private MenuAdapter(FoodObj.Food[] items) {
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public FoodObj.Food getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItemView item = (MenuItemView) convertView;
            if (item == null) {
                item = new MenuItemView(getActivity());
            }
            item.parse(mItems[position]);
            return item;
        }
    }
}
