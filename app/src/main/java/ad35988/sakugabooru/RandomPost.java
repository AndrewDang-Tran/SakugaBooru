package ad35988.sakugabooru;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by andrew on 11/30/16.
 */

public class RandomPost extends AppCompatActivity implements TagAdapter.TagSearch {

    public static final int MAX_POST_PAGES = 1202;
    private Post mPost;
    private TextView likes;
    private ImageView mThumbnail;
    private TextView artistLabel;
    private TextView mArtist;
    private RecyclerView mTags;
    private TagAdapter tagAdapter;
    protected PostTagsLinearLayoutManager linearLayoutManager;
    private FloatingActionButton mRandomButton;
    private HttpProxyCacheServer mVideoCache;

    private Context mRandomPostContext;
    private RequestController mRequestController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        getWindow().getDecorView().setBackgroundColor(Color.DKGRAY);

        mRandomPostContext = getApplicationContext();
        mVideoCache = VideoCache.getInstance(mRandomPostContext);
        mRequestController = RequestController.getInstance(mRandomPostContext);
        likes = (TextView) findViewById(R.id.likes);
        mThumbnail = (ImageView) findViewById(R.id.thumbnail);
        artistLabel = (TextView) findViewById(R.id.artistLabel);
        mArtist = (TextView) findViewById(R.id.artist);
        mTags = (RecyclerView) findViewById(R.id.tags);
        linearLayoutManager = new PostTagsLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        TagAdapter.TagSearch tagSearch = this;
        tagAdapter = new TagAdapter(this, tagSearch);
        mTags.setLayoutManager(linearLayoutManager);
        mTags.setAdapter(tagAdapter);
        mRandomButton = (FloatingActionButton) findViewById(R.id.randomButton);
        final RandomPost th = this;
        mRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequestController.queueRandomPostRequest(th, MAX_POST_PAGES);
            }
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        mRequestController.queueRandomPostRequest(this, MAX_POST_PAGES);
    }

    public void assignPost(Post p) {
        mPost = p;
        tagAdapter.populateTags(mPost.tags);
    }

    public void render() {
        if (mPost != null) {
            likes.setText(mPost.score + " likes");

            String thumbnailUrlString = mPost.previewUrl.toString();
            Glide.with(mRandomPostContext).load(thumbnailUrlString).signature(new StringSignature(String.valueOf(thumbnailUrlString))).into(mThumbnail);
            mThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPost.mediaType == Post.MediaType.MP4 || mPost.mediaType == Post.MediaType.WEBM)
                        showVideo();
                    else if (mPost.mediaType == Post.MediaType.JPG ||
                            mPost.mediaType == Post.MediaType.PNG ||
                            mPost.mediaType == Post.MediaType.GIF)
                        showImage();
                }
            });

            if(mPost.artist == null)
                mPost.findArtist(PostAdapter.artistsMap);
            if (mPost.artist == null)
                mArtist.setText("artist_unknown");
            else
                mArtist.setText(mPost.artist);

            String fileUrlToCache = mPost.videoUrl.toString();
            if (!mVideoCache.isCached(fileUrlToCache)) {
                new StartVideoCaching(mRandomPostContext, fileUrlToCache).execute("");
            }
        }
    }

    private void showVideo() {
        VideoFragment video = new VideoFragment();
        Bundle b = new Bundle();
        b.putString("videoUrl", mPost.videoUrl.toString());
        video.setArguments(b);
        getSupportActionBar().hide();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("videoFragment");
        ft.add(android.R.id.content, video).commit();
    }

    private void showImage() {
        Intent intent = new Intent();
        intent.setClass(this, FullscreenImageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl", mPost.videoUrl.toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void startTagSearch(String tagSearch) {
        Intent homeIntent = new Intent();
        homeIntent.setClass(this, Home.class);
        Bundle b = new Bundle();
        b.putString("tagSearch", tagSearch);
        homeIntent.putExtras(b);
        startActivity(homeIntent);
    }
}
