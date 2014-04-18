package us.misterwok.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoyin on 15/4/14.
 */
public class CartItemSQLiteHelper extends SQLiteOpenHelper {

    public static final String KEY_ID = "id";
    public static final String KEY_ITEM_ID = "item_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PRICE = "price";
    public static final String KEY_UNIT = "unit";
    private static final String[] COLUMNS = {KEY_ID, KEY_NAME, KEY_PRICE, KEY_UNIT};
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CartItemDB";
    private static final String TABLE_CART_ITEMS = "CartItems";

    public CartItemSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE CartItems ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "item_id TEXT, " +
                "name TEXT, " +
                "price TEXT, " +
                "unit TEXT )";
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS books");
        this.onCreate(db);
    }

    public void addCartItem(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CART_ITEMS,
                null,
                contentValues);
        db.close();
    }

    public ContentValues getCartItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_CART_ITEMS,
                        COLUMNS,
                        " id = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null,
                        null);
        ContentValues map = null;
        if (cursor.moveToFirst()) {
            map = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, map);
        }
        cursor.close();
        return map;
    }

    public int getCartItemCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_CART_ITEMS, null, null);
        return (int) count;
    }

    public double getTotal() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_CART_ITEMS,
                        COLUMNS,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
        double mTotal = 0;
        try {
            if (cursor.moveToFirst()) {
                do {
                    int unit = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_UNIT)));
                    double price = Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_PRICE)));
                    mTotal += unit * price;
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mTotal = -1;
        }
        cursor.close();
        return mTotal;
    }

    public List<ContentValues> getAllCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_CART_ITEMS,
                        COLUMNS,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
        ArrayList<ContentValues> retVal = new ArrayList<ContentValues>();
        ContentValues map;
        if (cursor.moveToFirst()) {
            do {
                map = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, map);
                retVal.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return retVal;
    }

    public int updateCartItem(ContentValues contentValues) {

        assert contentValues.get(KEY_ID) != null;
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.update(TABLE_CART_ITEMS,
                contentValues,
                KEY_ID + " = ?",
                new String[]{String.valueOf(contentValues.get(KEY_ID))});
        db.close();
        return i;

    }

    public int deleteCartItem(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(TABLE_CART_ITEMS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(contentValues.get(KEY_ID))});
        db.close();
        return i;
    }

    public int deleteAllCartItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(TABLE_CART_ITEMS,
                null,
                null);
        db.close();
        return i;
    }

}
