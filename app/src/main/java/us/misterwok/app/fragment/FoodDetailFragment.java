package us.misterwok.app.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import us.misterwok.app.R;
import us.misterwok.app.api.obj.FoodObj;
import us.misterwok.app.db.CartItemSQLiteHelper;

/**
 * Created by hoyin on 15/4/14.
 */
public class FoodDetailFragment extends DialogFragment {

    public static final String KEY_ITEM_DETAIL = "item";

    private ListView mListView;
    private EditText mUnit;
    private FoodObj.Food food;

    public static FoodDetailFragment newInstance(String foodInJson) {
        FoodDetailFragment itemDetailFragment = new FoodDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_ITEM_DETAIL, foodInJson);
        itemDetailFragment.setArguments(arguments);
        return itemDetailFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String itemInString = getArguments().getString(KEY_ITEM_DETAIL);
        food = new Gson().fromJson(itemInString, FoodObj.Food.class);

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_item_detail, null);
        mUnit = (EditText) rootView.findViewById(R.id.edit_text_unit);
        mListView = (ListView) rootView.findViewById(R.id.list_view_item_detail_type);
        SizeAdapter typeAdapter = new SizeAdapter(getActivity(), android.R.layout.simple_list_item_single_choice);
        typeAdapter.addAll(food.items);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setAdapter(typeAdapter);
        mListView.setItemChecked(typeAdapter.getCount() - 1, true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_item_detail_title)
                .setPositiveButton(R.string.dialog_item_detail_confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(CartItemSQLiteHelper.KEY_ITEM_ID, food.items[whichButton-1].size.id);
                                contentValues.put(CartItemSQLiteHelper.KEY_NAME, food.menu_number + " " + food.name);
                                contentValues.put(CartItemSQLiteHelper.KEY_UNIT, mUnit.getText().toString());
                                contentValues.put(CartItemSQLiteHelper.KEY_PRICE, food.items[mListView.getCheckedItemPosition()].price);
                                new CartItemSQLiteHelper(getActivity()).addCartItem(contentValues);
                                Intent intent = new Intent(CategoryFragment.KEY_CART_BROADCAST_RECEIVER);
                                getActivity().sendBroadcast(intent);

                                Toast.makeText(getActivity(), getString(R.string.toast_item_detail_added), Toast.LENGTH_SHORT).show();
                            }
                        }
                )
                .setNegativeButton(R.string.dialog_item_detail_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                );
        builder.setView(rootView);
        return builder.create();
    }

    private class SizeAdapter extends ArrayAdapter<FoodObj.Item> {
        int resourceId;

        public SizeAdapter(Context context, int resource) {
            super(context, resource);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity(), resourceId, null);
                CheckedTextView textView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
                textView.setText(getItem(position).size.name + ": " + getItem(position).price);
            }
            return convertView;
        }
    }
}
