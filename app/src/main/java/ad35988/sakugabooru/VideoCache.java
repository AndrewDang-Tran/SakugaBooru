package ad35988.sakugabooru;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by andrew on 11/30/16.
 */

public class VideoCache {
    private static HttpProxyCacheServer mCache;

    private VideoCache() {}

    public static HttpProxyCacheServer getInstance(Context context) {
        if (mCache == null) {
           mCache = new HttpProxyCacheServer(context);
        }
        return mCache;
    }
}
