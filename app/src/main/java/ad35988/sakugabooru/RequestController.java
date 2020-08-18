package ad35988.sakugabooru;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.LruCache;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by andrew on 10/25/16.
 * Code based on the singleton pattern for Volley shown in the documentation
 * https://developer.android.com/training/volley/requestqueue.html
 */
public class RequestController extends Application {
    private RequestQueue mRequestQueue;
    private static RequestController sInstance;
    private ImageLoader mImageLoader;
    private static Context sContext;
    private final static String NOT_CONNECTED_TO_INTERNET = "Not Connected to the Internet";

    /**
     * Constructor
     * @param context
     */
    private RequestController(Context context) {
        sContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,
            new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap>
                        cache = new LruCache<String, Bitmap>(20);

                @Override
                public Bitmap getBitmap(String url) {
                                                          return cache.get(url);
                                                                                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                                                                       cache.put(url, bitmap);
                                                                                              }
            });
    }

    /**
     * Way to retrieve the RequestController singleton
     * @param c
     * @return
     */
    public static synchronized RequestController getInstance(Context c) {
        if(sInstance == null) {
            sInstance = new RequestController(c);
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(sContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * Queue the requests which will populate the RecyclerView
     * @param postAdapter
     * @param pageNumber
     * @param search
     */
    public void queuePostsRequest(final PostAdapter postAdapter, int pageNumber, String search) {
        if(!checkConnection())
            return;
        String postsUrlString = "https://sakugabooru.com/post.xml?page=" + pageNumber + "&tags=" + search;
        final Context context = sContext;
        StringRequest postsRequest = new StringRequest(Request.Method.GET, postsUrlString,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    ArrayList<Post> newPosts = RequestParser.parsePostsRequest(response);
                    postAdapter.addPosts(newPosts);
                    postAdapter.sort();
                    postAdapter.notifyDataSetChanged();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "postsRequest failed", Toast.LENGTH_LONG);
                }
            }
        );
        addToRequestQueue(postsRequest);
    }

    public void getArtistsMap(final HashMap<String, String> artists, int pageNumber, final PostAdapter adapter) {
        if(!checkConnection())
            return;
        String artistsUrlString = "https://sakugabooru.com/artist.xml?page=";
        final Context context = sContext;
        final int[] pn = {pageNumber};
        StringRequest artistsRequest = new StringRequest(Request.Method.GET, artistsUrlString + pageNumber,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    HashMap<String, String> moreArtists = RequestParser.parseArtistsRequest(response);
                    if (moreArtists.size() > 0) {
                        artists.putAll(moreArtists);
                        pn[0]++;
                        getArtistsMap(artists, pn[0], adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "artistRequest failed with pn = " + pn[0], Toast.LENGTH_LONG);
                }
            }
        );
        addToRequestQueue(artistsRequest);
    }

    public void queueTagListRequest(final TagAdapter tagAdapter) {
        if(!checkConnection())
            return;
        String tagListUrlString = "https://sakugabooru.com/tag.xml?limit=0";
        StringRequest tagListRequest = new StringRequest(Request.Method.GET, tagListUrlString,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    ArrayList<Tag> newTags = RequestParser.parseTagListRequest(response);
                    tagAdapter.addTags(newTags);
                    tagAdapter.notifyDataSetChanged();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
        );
        addToRequestQueue(tagListRequest);
    }

    public void queueRandomPostRequest(final RandomPost randomPostContext, int totalPostPages) {
        if(!checkConnection())
            return;
        Random random = new Random();
        int pageRandom = random.nextInt(totalPostPages);
        String randomPostUrlString = "https://sakugabooru.com/post.xml?page=" + pageRandom;
        StringRequest randomPostRequest = new StringRequest(Request.Method.GET, randomPostUrlString,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Post randomPost = RequestParser.parseRandomPostRequest(response);
                    randomPostContext.assignPost(randomPost);
                    randomPostContext.render();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
        );
        addToRequestQueue(randomPostRequest);
    }

    private boolean connectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean checkConnection() {
        boolean isConnected = connectedToInternet();
        if (!isConnected) {
            Toast notConnectedToast = Toast.makeText(sContext, NOT_CONNECTED_TO_INTERNET, Toast.LENGTH_SHORT);
            notConnectedToast.setGravity(Gravity.CENTER, 0, 0);
            notConnectedToast.show();
        }
        return isConnected;
    }
}
