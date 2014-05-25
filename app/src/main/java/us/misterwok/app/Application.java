package us.misterwok.app;

import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;

/**
 * Created by hoyin on 14/4/14.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initiImageLoader();
    }

    private void initiImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisc(true)
                .displayer(new FadeInBitmapDisplayer(200))
                .showImageOnLoading(R.drawable.empty_dish)
                .showImageForEmptyUri(R.drawable.empty_dish)
                .showImageOnFail(R.drawable.empty_dish)
                .resetViewBeforeLoading(true)
                .build();

        File cacheDir;
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            cacheDir = getCacheDir();
        }
        ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiscCache(cacheDir))
                .defaultDisplayImageOptions(options);

        if (BuildConfig.DEBUG) {
            configBuilder.writeDebugLogs();
        }

        ImageLoader.getInstance().init(configBuilder.build());
    }

}