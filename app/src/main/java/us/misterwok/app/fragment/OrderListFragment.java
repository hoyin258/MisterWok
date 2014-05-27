package us.misterwok.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import us.misterwok.app.Constants;
import us.misterwok.app.R;
import us.misterwok.app.activity.AdminLoginActivity;
import us.misterwok.app.api.APIEngine;
import us.misterwok.app.api.obj.OrderObj;
import us.misterwok.app.widget.OrderItemView;

/**
 * Created by hoyin on 25/5/14.
 * Yintro.com
 */
public class OrderListFragment extends BaseFragment implements OnRefreshListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    PullToRefreshLayout mPullToRefreshLayout;
    ListView mListView;
    OrderAdapter mOrderAdapter;

    int page;
    int per_page = 10;

    boolean mLastItemVisible;

    public static OrderListFragment newInstance() {
        return new OrderListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        page = 1;

        mPullToRefreshLayout = (PullToRefreshLayout) inflater.inflate(R.layout.fragment_orders, container, false);
        mListView = (ListView) mPullToRefreshLayout.findViewById(R.id.list_view_fragment_order);
        mListView.setEmptyView(mPullToRefreshLayout.findViewById(R.id.progress_bar_fragment_order));
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

        mOrderAdapter = new OrderAdapter();
        mListView.setAdapter(mOrderAdapter);

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
        page=1;
        mOrderAdapter.clear();
        getData();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.admin, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_admin_login:
                Intent intent = new Intent(getActivity(), AdminLoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getData() {

        mPullToRefreshLayout.setRefreshing(true);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                getActivity().getPackageName(), Activity.MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.PREFERENCE_ADMIN_EMAIL, "");
        String password = sharedPreferences.getString(Constants.PREFERENCE_ADMIN_PASSWORD, "");

        RequestParams requestParams = new RequestParams();
        requestParams.put("email", email);
        requestParams.put("password", password);
        requestParams.put("page", page + "");
        requestParams.put("per_page", per_page + "");

        APIEngine.getOrder(getString(R.string.store_id), requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {

                OrderObj orderObj = new Gson().fromJson(responseBody, OrderObj.class);
                mOrderAdapter.addOrder(orderObj.data);
                mPullToRefreshLayout.setRefreshComplete();
                super.onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                super.onFailure(e, errorResponse);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int state) {
        if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLastItemVisible) {
            if (mPullToRefreshLayout.isRefreshing() == false) {
                page += 1;
                getData();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
    }


    private class OrderAdapter extends BaseAdapter {

        ArrayList<OrderObj.Order> mOrders;

        private OrderAdapter() {
            mOrders = new ArrayList<OrderObj.Order>();
        }

        public void clear(){
            mOrders.clear();
            notifyDataSetChanged();
        }

        public void addOrder(OrderObj.Order[] orders) {
            mOrders.addAll(Arrays.asList(orders));
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mOrders.size();
        }

        @Override
        public OrderObj.Order getItem(int position) {
            return mOrders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OrderItemView orderItem = (OrderItemView) convertView;
            if (orderItem == null) {
                orderItem = new OrderItemView(getActivity());
            }
            orderItem.parse(getItem(position));
            return orderItem;
        }
    }
}
