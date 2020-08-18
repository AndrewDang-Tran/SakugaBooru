package ad35988.sakugabooru;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew on 11/29/16.
 */
public class TagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface TagSearch {
        void startTagSearch(String tagSearch);
    }

    private enum TagListTypes {
        POST_TAGS(0),
        ALL_TAGS(1);

        private final int value;

        TagListTypes(int v) { value = v; }

        public int getValue() { return value; }
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        public Tag row;
        public View container;
        public TextView tagName;
        public TextView tagCount;
        public View rowView;
        public TagViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            tagName = (TextView) itemView.findViewById(R.id.tagName);
            tagCount = (TextView) itemView.findViewById(R.id.tagCount);
            rowView = (View) itemView.findViewById(R.id.tagRow);
        }
    }

    public static class PostTagsViewHolder extends RecyclerView.ViewHolder {
        public Tag row;
        public View container;
        public TextView tagName;
        public View rowView;
        public PostTagsViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            tagName = (TextView) itemView.findViewById(R.id.tagName);
            rowView = (View) itemView.findViewById(R.id.tagRow);
        }
    }

    public ArrayList<Tag> list;
    private Context mContext;
    private TagSearch mTagSearch;
    private Trie tagTrie;

    public TagAdapter(Context c, TagSearch ts) {
        list = new ArrayList<Tag>();
        tagTrie = new Trie();
        mContext = c;
        mTagSearch = ts;

    }

    public TagAdapter(Context c, HashMap<String, String> data, TagSearch ts) {
        list = new ArrayList<Tag>();
        tagTrie = new Trie();
        populateTags(data);
        mContext = c;
        mTagSearch = ts;
    }

    public void addTags(ArrayList<Tag> otherTags) {
        list.addAll(otherTags);
    }

    public void populateTags(HashMap<String, String> data) {
        list.clear();
        for(Map.Entry<String, String> pair : data.entrySet()) {
            String tagName = pair.getValue();
            Tag newTag = new Tag(-1, tagName, -1, -1);
            list.add(newTag);
            tagTrie.insert(tagName);
        }
    }

    @Override
    public int getItemViewType(int position) {
        boolean isHome = mContext instanceof Home;
        boolean isRandom = mContext instanceof RandomPost;
        boolean isTagList = mContext instanceof TagList;
        if (isHome || isRandom) {
            return TagListTypes.POST_TAGS.getValue();
        } else {
            return TagListTypes.ALL_TAGS.getValue();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == TagListTypes.POST_TAGS.getValue()) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_column_home, parent, false);
            return new PostTagsViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_row, parent, false);
            return new TagViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int viewType = holder.getItemViewType();
        if (viewType == TagListTypes.POST_TAGS.getValue()) {
            PostTagsViewHolder ptvh = (PostTagsViewHolder) holder;
            final Tag tag = list.get(position);
            ptvh.row = tag;
            ptvh.tagName.setText("#" + ptvh.row.name);
            ptvh.tagName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTagSearch.startTagSearch(tag.name);
                }
            });
        } else {
            TagViewHolder tvh = (TagViewHolder) holder;
            final Tag tag = list.get(position);
            tvh.row = tag;
            tvh.tagName.setText("#" + tvh.row.name);
            tvh.tagCount.setText(String.valueOf(tvh.row.count));
            tvh.rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTagSearch.startTagSearch(tag.name);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
