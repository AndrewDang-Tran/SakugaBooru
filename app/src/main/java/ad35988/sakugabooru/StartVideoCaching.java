package ad35988.sakugabooru;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by andrew on 11/30/16.
 */

public class StartVideoCaching extends AsyncTask<String, String, String> {
    private HttpProxyCacheServer mVideoCache;
    private String mVideoUrlString;
    private InputStream is;

    public StartVideoCaching(Context context, String videoUrlString) {
        mVideoUrlString = videoUrlString;
        mVideoCache = VideoCache.getInstance(context);
    }

    @Override
    protected String doInBackground(String... f_url) {
        try {
            URL cacheUrl = new URL(mVideoCache.getProxyUrl(mVideoUrlString));
            is = cacheUrl.openStream();
            byte[] byteChunk = IOUtils.toByteArray(is);
        } catch (Exception e) {
            if (e != null)
                Log.v("pre caching failed" , "what is happening? " + e.getMessage());
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }
        return null;
    }
}
