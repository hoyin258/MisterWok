package us.misterwok.app.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import us.misterwok.app.R;
import us.misterwok.app.api.obj.ItemsObj;
import us.misterwok.app.db.CartItemSQLiteHelper;

/**
 * Created by hoyin on 15/4/14.
 */
public class ItemDetailFragment extends BaseDialogFragment {

    public static final String KEY_ITEM_DETAIL = "item";

    private ListView mListView;
    private EditText mUnit;
    private ItemsObj.Item item;

    public static ItemDetailFragment newInstance(String itemInJson) {
        ItemDetailFragment itemDetailFragment = new ItemDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_ITEM_DETAIL, itemInJson);
        itemDetailFragment.setArguments(arguments);
        return itemDetailFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String itemInString = getArguments().getString(KEY_ITEM_DETAIL);
        item = new Gson().fromJson(itemInString, ItemsObj.Item.class);

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_item_detail, null);
        mUnit = (EditText) rootView.findViewById(R.id.edit_text_unit);
        mListView = (ListView) rootView.findViewById(R.id.list_view_item_detail_type);
        TypeAdapter typeAdapter = new TypeAdapter(getActivity(), android.R.layout.simple_list_item_single_choice);
        typeAdapter.addAll(item.types);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setAdapter(typeAdapter);
        mListView.setItemChecked(typeAdapter.getCount() - 1, true);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_item_detail_title)
                .setPositiveButton(R.string.dialog_item_detail_confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(CartItemSQLiteHelper.KEY_ITEM_ID, item.id);
                                contentValues.put(CartItemSQLiteHelper.KEY_NAME, item.menu_id + " " + item.name);
                                contentValues.put(CartItemSQLiteHelper.KEY_UNIT, mUnit.getText().toString());
                                contentValues.put(CartItemSQLiteHelper.KEY_PRICE, item.types[mListView.getCheckedItemPosition()].price);
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

    private class TypeAdapter extends ArrayAdapter<ItemsObj.Type> {
        int resourceId;

        public TypeAdapter(Context context, int resource) {
            super(context, resource);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity(), resourceId, null);
                CheckedTextView textView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
                textView.setText(getItem(position).unit + ": " + getItem(position).price);
            }
            return convertView;
        }
    }
}
