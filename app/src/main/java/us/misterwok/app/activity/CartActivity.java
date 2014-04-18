package us.misterwok.app.activity;

import android.os.Bundle;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import us.misterwok.app.R;
import us.misterwok.app.fragment.CartItemListFragment;

/**
 * Created by hoyin on 18/4/14.
 */
public class CartActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CartItemListFragment.newInstance())
                    .commit();
        }
    }
}
