package ad35988.sakugabooru;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by andrew on 10/25/16.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    public interface StartMedia {
        void showVideo(int i);
        void showImage(int i);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public Post row;
        public ImageView thumbnail;
        public TextView likes;
        public TextView artistLabel;
        public TextView artist;
        public RecyclerView tags;
        public View divider;
        public View container;
        public PostViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            likes = (TextView) itemView.findViewById(R.id.likes);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            artistLabel = (TextView) itemView.findViewById(R.id.artistLabel);
            artist = (TextView) itemView.findViewById(R.id.artist);
            tags = (RecyclerView) itemView.findViewById(R.id.tags);
            divider = (View) itemView.findViewById(R.id.divider);
        }
    }

    public static ArrayList<Post> list;
    private Context mContext;
    private StartMedia mStartMedia;
    public static HashMap<String, String> artistsMap;
    private long timeLastClicked;
    private SortFragment.Sortable sortBy;
    private SortFragment.SortDirection sortDirection = SortFragment.SortDirection.DESCENDING;

    public PostAdapter(Context c, StartMedia sv) {
        list = new ArrayList<Post>();
        mContext = c;
        mStartMedia = sv;
        artistsMap = new HashMap<String, String>();
        sortBy = SortFragment.Sortable.NEW;
        RequestController.getInstance(c).getArtistsMap(artistsMap, 0, this);
    }

    public void addPosts(ArrayList<Post> otherPosts) {
       list.addAll(otherPosts);
    }

    public void addPost(Post p) {
        list.add(p);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, final int position) {
        final Post post = list.get(position);

        if(post.artist == null)
            post.findArtist(artistsMap);

        holder.row = post;
        String thumbnailUrlString = post.previewUrl.toString();
        Glide.with(mContext).load(thumbnailUrlString).signature(new StringSignature(String.valueOf(thumbnailUrlString))).into(holder.thumbnail);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long timeClicked = SystemClock.uptimeMillis();
                long elapsedTime = timeClicked - timeLastClicked;
                if (elapsedTime <= GestureConstants.MIN_CLICK_INTERVAL) {
                   return;
                } timeLastClicked = timeClicked;
                if (post.mediaType == Post.MediaType.MP4 || post.mediaType == Post.MediaType.WEBM)
                    mStartMedia.showVideo(position);
                else if (post.mediaType == Post.MediaType.JPG ||
                        post.mediaType == Post.MediaType.PNG ||
                        post.mediaType == Post.MediaType.GIF)
                    mStartMedia.showImage(position);

            }
        });
        holder.likes.setText(Integer.toString(post.score) + " likes");
        if (post.artist == null)
           holder.artist.setText("artist_unknown");
        else
            holder.artist.setText(post.artist);

        PostTagsLinearLayoutManager linearLayoutManager = new PostTagsLinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        TagAdapter.TagSearch tagSearch = (TagAdapter.TagSearch) mContext;
        TagAdapter tagAdapter = new TagAdapter(mContext, post.tags, tagSearch);
        holder.tags.setLayoutManager(linearLayoutManager);
        holder.tags.setAdapter(tagAdapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clearData() {
        list.clear();
    }

    public void setSortBy(SortFragment.Sortable s) { sortBy = s; }

    public void setSortDirection(SortFragment.SortDirection s) { sortDirection = s; }

    public SortFragment.SortDirection reverseSortDirection() {
        if (sortDirection == SortFragment.SortDirection.ASCENDING) {
            sortDirection = SortFragment.SortDirection.DESCENDING;
        } else {
            sortDirection = SortFragment.SortDirection.ASCENDING;
        }
        return sortDirection;
    }

    public void sort() {
        if (sortBy == SortFragment.Sortable.NEW) {
            Collections.sort(list, Post.sortByNew);
        } else if (sortBy == SortFragment.Sortable.SCORE) {
            Collections.sort(list, Post.sortByScore);
        }
        if (sortDirection == SortFragment.SortDirection.ASCENDING) {
            Collections.reverse(list);
        }
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}

