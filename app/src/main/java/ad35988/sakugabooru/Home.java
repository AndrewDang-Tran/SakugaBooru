package ad35988.sakugabooru;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater; import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Activity for the homepage where the SakugaBooru posts will be displayed
 * SakugaBooru API documentation https://sakugabooru.com/help/api
 */
public class Home extends AppCompatActivity implements PostAdapter.StartMedia, SearchView.OnQueryTextListener, TagAdapter.TagSearch {

    private RequestController mRequestController;
    private RecyclerView mPosts;
    protected LinearLayoutManager linearLayoutManager;
    private PostAdapter mPostAdapter;
    private Context mHomeContext;
    private PostAdapter.StartMedia mShowVideo;
    private HttpProxyCacheServer mVideoCache;
    private int mPreviousFirstVisibleIndex;

    private int mPostsPage;
    private String mSearch;
    private boolean mScrollEndLoading;
    private int mPreviousTotalItemCount;
    private SortFragment.Sortable sortBy;
    private SortFragment.SortDirection sortDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().getDecorView().setBackgroundColor(Color.DKGRAY);

        mHomeContext = getApplicationContext();
        mVideoCache = VideoCache.getInstance(mHomeContext);
        mShowVideo = this;
        mRequestController = RequestController.getInstance(mHomeContext);
        mPosts = (RecyclerView) findViewById(R.id.posts_view);
        mPostAdapter = new PostAdapter(this, mShowVideo);
        linearLayoutManager = new LinearLayoutManager(this);
        mPosts.setLayoutManager(linearLayoutManager);
        mPosts.setAdapter(mPostAdapter);

        mScrollEndLoading = true;
        mPreviousFirstVisibleIndex = Integer.MIN_VALUE;
        mPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
           @Override
           public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);
               //pre-fetching videos
               int firstVisibleIndex = linearLayoutManager.findFirstVisibleItemPosition();
               if(firstVisibleIndex != mPreviousFirstVisibleIndex) {
                   mPreviousFirstVisibleIndex = firstVisibleIndex;
                   for (int i = -2; i <= 2; i++) {
                       int index = firstVisibleIndex + i;
                       if (index >= 0 && index < mPostAdapter.list.size()) {
                           Post p = mPostAdapter.list.get(index);
                           if (p.mediaType == Post.MediaType.MP4 || p.mediaType == Post.MediaType.WEBM) {
                               String fileUrlToCache = p.videoUrl.toString();
                               if (!mVideoCache.isCached(fileUrlToCache)) {
                                   new StartVideoCaching(mHomeContext, fileUrlToCache).execute("");
                               }
                           }
                       }
                   }
               }

               if (dy > 0) {
                   int visibleItemCount = linearLayoutManager.getChildCount();
                   int totalItemCount = linearLayoutManager.getItemCount();
                   int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                   if (mScrollEndLoading && visibleItemCount + pastVisibleItems >= totalItemCount) {
                       mScrollEndLoading = false;
                       mPreviousTotalItemCount = totalItemCount;
                       mPostsPage++;
                       mRequestController.queuePostsRequest(mPostAdapter, mPostsPage, mSearch);
                       sort();
                   } else if (!mScrollEndLoading && totalItemCount > mPreviousTotalItemCount) {
                        mScrollEndLoading = true;
                   }
               }
           }
        });
        mPostsPage = 1;

        Intent callingIntent = getIntent();
        Bundle callingBundle = callingIntent.getExtras();
        if (callingBundle != null && callingBundle.containsKey("tagSearch"))
            mSearch = callingBundle.getString("tagSearch");
        else
            mSearch = "";

        sortBy = SortFragment.Sortable.NEW;
        sortDirection = SortFragment.SortDirection.DESCENDING;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        // Associate searchable configuration with the SearchView
        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectId = item.getItemId();
        if (selectId == R.id.sort) {
            showSortFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortFragment() {
        SortFragment sortFragment = new SortFragment();
        Bundle b = new Bundle();
        b.putSerializable("sortBy", sortBy);
        b.putSerializable("sortDirection", sortDirection);
        sortFragment.setArguments(b);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("sortFragment");
        ft.add(android.R.id.content, sortFragment).commit();
    }

    @Override
    public void showVideo(int i) {
        Post post = PostAdapter.list.get(i);
        VideoFragment video = new VideoFragment();
        Bundle b = new Bundle();
        b.putString("videoUrl", post.videoUrl.toString());
        b.putInt("index", i);
        video.setArguments(b);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("videoFragment");
        ft.add(android.R.id.content, video).commit();
    }

    @Override
    public void showImage(int i) {
        Post post = PostAdapter.list.get(i);
        Intent intent = new Intent();
        intent.setClass(this, FullscreenImageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("index", i);
        bundle.putString("imageUrl", post.videoUrl.toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mPostAdapter.clearData();
        mSearch = query.replace(" ", "_");
        mPostsPage = 1;
        mRequestController.queuePostsRequest(mPostAdapter, mPostsPage, mSearch);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        this.getCurrentFocus().clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.equals("")){
            this.onQueryTextSubmit("");
        }
        return true;
    }

    @Override
    public void startTagSearch(String tagSearch) {
        onQueryTextSubmit(tagSearch);
    }

    private void sort() {
        mPostAdapter.setSortBy(sortBy);
        mPostAdapter.sort();
    }

    public void sortBy(SortFragment.Sortable sortable) {
        sortBy = sortable;
        sort();
        mPosts.smoothScrollToPosition(0);
    }

    public SortFragment.SortDirection sortDirection() {
        sortDirection = mPostAdapter.reverseSortDirection();
        mPostAdapter.sort();
        mPosts.smoothScrollToPosition(0);
        return sortDirection;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPostAdapter.isEmpty()) {
            mRequestController.queuePostsRequest(mPostAdapter, mPostsPage, mSearch);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        linearLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable("postsState"));
        mSearch = savedInstanceState.getString("search");
        mPreviousFirstVisibleIndex = savedInstanceState.getInt("previousFirstVisibleIndex");
        mPreviousTotalItemCount = savedInstanceState.getInt("previousTotalItemCount");
        sortDirection = (SortFragment.SortDirection) savedInstanceState.getSerializable("sortDirection");
        sortBy = (SortFragment.Sortable) savedInstanceState.getSerializable("sortBy");
        mPostAdapter.setSortBy(sortBy);
        mPostAdapter.setSortDirection(sortDirection);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable layoutManagerState = linearLayoutManager.onSaveInstanceState();
        outState.putParcelable("postsState", layoutManagerState);
        outState.putString("search", mSearch);
        outState.putInt("previousTotalItemCount", mPreviousTotalItemCount);
        outState.putInt("previousFirstVisibleIndex", mPreviousFirstVisibleIndex);
        outState.putSerializable("sortDirection", sortDirection);
        outState.putSerializable("sortBy", sortBy);
    }
}
