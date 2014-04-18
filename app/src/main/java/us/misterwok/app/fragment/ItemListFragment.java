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
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import us.misterwok.app.R;
import us.misterwok.app.api.ItemClient;
import us.misterwok.app.api.obj.ItemsObj;
import us.misterwok.app.widget.MenuItemView;

/**
 * Created by hoyin on 14/4/14.
 */
public class ItemListFragment extends BaseFragment implements OnRefreshListener, AdapterView.OnItemClickListener {

    private static final String KEY_CATEGORY = "category_id";

    PullToRefreshLayout mPullToRefreshLayout;
    ListView mListView;
    MenuAdapter menuAdapter;

    public static ItemListFragment newInstance(String categoryId) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_CATEGORY, categoryId);
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPullToRefreshLayout = (PullToRefreshLayout) inflater.inflate(R.layout.fragment_items, container, false);
        mListView = (ListView) mPullToRefreshLayout.findViewById(R.id.list_view_fragment_menu);
        mListView.setEmptyView(mPullToRefreshLayout.findViewById(R.id.progress_bar_fragment_menu));
        mListView.setOnItemClickListener(this);
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

    @Override
    public void onRefreshStarted(View view) {
        getData();
    }


    private void getData() {
        mPullToRefreshLayout.setRefreshing(true);
        ItemClient.get(getArguments().getString(KEY_CATEGORY), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                ItemsObj itemsObj = new Gson().fromJson(responseBody, ItemsObj.class);
                menuAdapter = new MenuAdapter(itemsObj.items);
                mListView.setAdapter(menuAdapter);
                mPullToRefreshLayout.setRefreshComplete();
                super.onSuccess(statusCode, headers, responseBody);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        ItemsObj.Item item = menuAdapter.getItem(position);
        ItemDetailFragment.newInstance(new Gson().toJson(item)).show(getFragmentManager(), "item_detail");

    }


    private class MenuAdapter extends BaseAdapter {
        ItemsObj.Item[] mItems;

        private MenuAdapter(ItemsObj.Item[] items) {
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public ItemsObj.Item getItem(int position) {
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
