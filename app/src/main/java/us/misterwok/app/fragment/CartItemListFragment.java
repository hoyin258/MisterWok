package us.misterwok.app.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import us.misterwok.app.R;
import us.misterwok.app.db.CartItemSQLiteHelper;

/**
 * Created by hoyin on 15/4/14.
 */
public class CartItemListFragment extends BaseFragment implements
        OnRefreshListener,
        LoaderManager.LoaderCallbacks<List<ContentValues>> {

    private PullToRefreshLayout mPullToRefreshLayout;
    private ListView mListView;
    private ArrayAdapter mCartItemAdapter;

    private TextView mTotal;
    private Button mConfirm;
    private TextView mTax;


    public static CartItemListFragment newInstance() {
        CartItemListFragment fragment = new CartItemListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart_items, container, false);
        mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        mListView = (ListView) mPullToRefreshLayout.findViewById(R.id.list_view_fragment_menu);
        mListView.setEmptyView(mPullToRefreshLayout.findViewById(R.id.text_view_fragment_cart_empty));

        mCartItemAdapter = new CartItemAdapter(getActivity(), R.layout.view_cart_item);
        mListView.setAdapter(mCartItemAdapter);

        mTotal = (TextView) rootView.findViewById(R.id.text_view_total);
        mConfirm = (Button) rootView.findViewById(R.id.button_confirm);
        mTax = (TextView) rootView.findViewById(R.id.text_view_tax);

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText mPhoneNumber = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                mPhoneNumber.setLayoutParams(lp);
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog_phone_title)
                        .setMessage(R.string.dialog_phone_message)
                        .setView(mPhoneNumber)
                        .setPositiveButton(R.string.dialog_phone_confirm,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String phoneNumber = mPhoneNumber.getText().toString();
                                        if (!TextUtils.isEmpty(phoneNumber)) {
                                            submitCartItems();
                                            dialog.dismiss();
                                        }
                                    }
                                }
                        )
                        .setNegativeButton(R.string.dialog_phone_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);
        onRefreshStarted(null);
    }

    @Override
    public void onRefreshStarted(View view) {
        mPullToRefreshLayout.setRefreshing(true);
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public Loader<List<ContentValues>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<ContentValues>>(getActivity()) {
            @Override
            public List<ContentValues> loadInBackground() {
                return new CartItemSQLiteHelper(getContext()).getAllCartItems();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<ContentValues>> loader, List<ContentValues> data) {
        mCartItemAdapter.clear();
        for (ContentValues contentValues : data) {
            mCartItemAdapter.add(contentValues);
        }
        mCartItemAdapter.notifyDataSetChanged();
        calculateTotal();
        mPullToRefreshLayout.setRefreshComplete();
    }

    @Override
    public void onLoaderReset(Loader<List<ContentValues>> loader) {
        mCartItemAdapter.clear();
    }

    private void calculatePrice(ContentValues item, EditText mUnit, TextView mPrice) {
        try {
            int unit = Integer.parseInt(mUnit.getText().toString());
            double unitPrice = Double.parseDouble(item.getAsString(CartItemSQLiteHelper.KEY_PRICE));
            double price = unit * unitPrice;
            mPrice.setText(String.format("$ %.2f", price));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void calculateTotal() {
        double itemTotal = new CartItemSQLiteHelper(getActivity()).getTotal();
        double tax = itemTotal * 0.03;
        double totalRounded = new BigDecimal(itemTotal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double taxRounded = new BigDecimal(tax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        mTax.setText(String.format("$ %s", taxRounded));
        mTotal.setText(String.format("$ %s", totalRounded));
    }

    private void submitCartItems() {


//                RequestParams requestParams = null;
//                CartItemClient.post(requestParams, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, String responseBody) {
////                        new CartItemSQLiteHelper().deleteCartItem()
//                        Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
//                        super.onSuccess(statusCode, headers, responseBody);
//                    }
//                });
        new CartItemSQLiteHelper(getActivity()).deleteAllCartItems();
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_thank_you_title))
                .setMessage(getString(R.string.dialog_thank_you_message))
                .setPositiveButton(getString(R.string.dialog_thank_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        getActivity().onBackPressed();
                    }
                }).create().show();

    }

    private class CartItemAdapter extends ArrayAdapter<ContentValues> {
        int resourceId;

        public CartItemAdapter(Context context, int resource) {
            super(context, resource);
            resourceId = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity(), resourceId, null);
                final TextView mName = (TextView) convertView.findViewById(R.id.text_view_name);
                final EditText mUnit = (EditText) convertView.findViewById(R.id.edit_text_unit);
                final TextView mPrice = (TextView) convertView.findViewById(R.id.text_view_price);
                final Button mRemove = (Button) convertView.findViewById(R.id.button_remove);
                final ContentValues item = getItem(position);

                mName.setText(item.getAsString(CartItemSQLiteHelper.KEY_NAME) + " :");
                mUnit.setText(item.getAsString(CartItemSQLiteHelper.KEY_UNIT));

                calculatePrice(item, mUnit, mPrice);

                mUnit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        try {
                            if (!TextUtils.isEmpty(mUnit.getText().toString())) {
                                item.put(CartItemSQLiteHelper.KEY_UNIT, mUnit.getText().toString());
                                new CartItemSQLiteHelper(getActivity()).updateCartItem(item);
                                calculatePrice(item, mUnit, mPrice);
                                calculateTotal();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                mRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new CartItemSQLiteHelper(getActivity()).deleteCartItem(item);
                        onRefreshStarted(null);
                    }
                });
            }
            return convertView;
        }
    }
}
