package ad35988.sakugabooru;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by andrew on 11/29/16.
 */

public class NavigationBarFragment extends Fragment {

    private ImageView mHomeNavigation;
    private ImageView mTagsNavigation;
    private ImageView mRandomNavigation;
    private Context mContext;
    private String mActivityName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_navigation_bar_layout, container, false);
        mContext = getActivity();
        mActivityName = mContext.getClass().getSimpleName();
        mHomeNavigation = (ImageView) rootView.findViewById(R.id.homeNavigation);
        if (mActivityName.equals(Home.class.getSimpleName())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mHomeNavigation.setImageDrawable(getResources().getDrawable(R.drawable.home_teal, null));
            } else {
                mHomeNavigation.setImageDrawable(getResources().getDrawable(R.drawable.home_teal));
            }
        } else {
            mHomeNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent homePageIntent = new Intent();
                    homePageIntent.setClass(mContext, Home.class);
                    startActivity(homePageIntent);
                }
            });
        }

        mTagsNavigation = (ImageView) rootView.findViewById(R.id.tagsNavigation);
        if (mActivityName.equals(TagList.class.getSimpleName())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTagsNavigation.setImageDrawable(getResources().getDrawable(R.drawable.hashtag_teal, null));
            } else {
                mTagsNavigation.setImageDrawable(getResources().getDrawable(R.drawable.hashtag_teal));
            }
        } else {
            mTagsNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tagsPageIntent = new Intent();
                    tagsPageIntent.setClass(mContext, TagList.class);
                    startActivity(tagsPageIntent);
                }
            });
        }

        mRandomNavigation = (ImageView) rootView.findViewById(R.id.randomNavigation);
        if (mActivityName.equals(RandomPost.class.getSimpleName())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mRandomNavigation.setImageDrawable(getResources().getDrawable(R.drawable.dice_teal, null));
            } else {
                mRandomNavigation.setImageDrawable(getResources().getDrawable(R.drawable.dice_teal));
            }
        } else {
            mRandomNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent randomPostPageIntent = new Intent();
                    randomPostPageIntent.setClass(mContext, RandomPost.class);
                    startActivity(randomPostPageIntent);
                }
            });
        }
        return rootView;
    }
}

