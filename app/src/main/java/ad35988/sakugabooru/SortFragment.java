package ad35988.sakugabooru;

import android.content.Context;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by andrew on 4/29/17.
 */

public class SortFragment extends Fragment {

    public enum SortDirection {
        ASCENDING,
        DESCENDING
    }

    public enum Sortable {
        NEW(0, R.id.newLayout, R.id.newImage, R.drawable.new_teal, R.drawable.new_lightgrey),
        SCORE(1, R.id.scoreLayout, R.id.scoreImage, R.drawable.heart_teal, R.drawable.heart_lightgrey);

        private final int index;
        private final int layoutId;
        private final int imageId;
        private final int onDrawable;
        private final int offDrawable;

        Sortable(int v, int i, int im, int on, int off) {
            index = v;
            layoutId = i;
            imageId = im;
            onDrawable = on;
            offDrawable = off;
        }

        public int getIndex() { return index; }
        public int getLayoutId() { return layoutId; }
        public int getImageId() { return imageId; }
        public int getOnDrawableId() { return onDrawable; }
        public int getOffDrawableId() { return offDrawable; }
    }

    private static final int NUM_SORTABLE = 2;
    private LinearLayout[] sortLayouts;
    private ImageView[] sortImages;
    private PercentRelativeLayout restOfFragment;
    private ImageView ascDescImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_sort_layout, container, false);

        sortLayouts = new LinearLayout[NUM_SORTABLE];
        sortImages = new ImageView[NUM_SORTABLE];

        Context context = getActivity();
        final Home homeContext;
        if (context instanceof Home) {
            homeContext = (Home) context;
        } else {
            Log.v("Error", "SortFragment not used in the Home context");
            return rootView;
        }

        restOfFragment = (PercentRelativeLayout) rootView.findViewById(R.id.sortFragmentLayout);
        ascDescImage = (ImageView) rootView.findViewById(R.id.ascDescButton);

        restOfFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeContext.onBackPressed();
            }
        });

        ascDescImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortDirection sd =  homeContext.sortDirection();
                showActiveSortDirection(sd);
            }
        });

        int index;
        for(final Sortable s : Sortable.values()) {
            index = s.getIndex();
            sortLayouts[index] = (LinearLayout) rootView.findViewById(s.getLayoutId());
            sortImages[index] = (ImageView) rootView.findViewById(s.getImageId());
            LinearLayout sortLayout = sortLayouts[index];
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeContext.sortBy(s);
                    showActiveSortImage(s);
                    homeContext.onBackPressed();
                }
            };
            sortLayout.setOnClickListener(clickListener);
        }

        Bundle b = getArguments();
        Sortable startingSort = (Sortable) b.getSerializable("sortBy");
        SortDirection startingDirection = (SortDirection) b.getSerializable("sortDirection");
        showActiveSortImage(startingSort);
        showActiveSortDirection(startingDirection);
        return rootView;
    }

    private void showActiveSortImage(Sortable currentSortable) {
        for (Sortable s : Sortable.values()) {
            ImageView image = sortImages[s.getIndex()];
            boolean isCurrentSortable = currentSortable.getImageId() == s.getImageId();
            if (isCurrentSortable) {
                image.setImageResource(s.getOnDrawableId());
            } else {
                image.setImageResource(s.getOffDrawableId());
            }
        }
    }

    private void showActiveSortDirection(SortDirection sd) {
        if (sd ==  SortDirection.DESCENDING) {
            ascDescImage.setImageResource(R.drawable.arrowhead_down_teal);
        } else {
            ascDescImage.setImageResource(R.drawable.arrowhead_up_teal);
        }
    }
}
