package ad35988.sakugabooru;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by andrew on 11/29/16.
 */
public class TagList extends AppCompatActivity implements TagAdapter.TagSearch {
    private RequestController mRequestController;
    private RecyclerView mTags;
    private TagAdapter mTagAdapter;
    private Context mTagListContext;
    protected LinearLayoutManager linearLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.DKGRAY, null));
        }
        getSupportActionBar().hide();

        mTagListContext = getApplicationContext();
        mRequestController = RequestController.getInstance(mTagListContext);
        mTags = (RecyclerView) findViewById(R.id.tagList);
        mTagAdapter = new TagAdapter(mTagListContext, this);
        linearLayoutManager = new LinearLayoutManager(this);
        mTags.setLayoutManager(linearLayoutManager);
        mTags.setAdapter(mTagAdapter);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mTagAdapter.getItemCount() == 0) {
            mRequestController.queueTagListRequest(mTagAdapter);
        }
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
