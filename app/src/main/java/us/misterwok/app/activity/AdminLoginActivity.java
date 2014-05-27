package us.misterwok.app.activity;

import android.os.Bundle;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import us.misterwok.app.R;
import us.misterwok.app.fragment.AdminLoginFragment;

/**
 * Created by hoyin on 25/5/14.
 * Yintro.com
 */
public class AdminLoginActivity extends SwipeBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, AdminLoginFragment.newInstance())
                    .commit();
        }
    }
}
