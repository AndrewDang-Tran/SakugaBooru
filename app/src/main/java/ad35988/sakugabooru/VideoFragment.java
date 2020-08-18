package ad35988.sakugabooru;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by andrew on 10/26/16.
 */

public class VideoFragment extends Fragment implements GestureDetector.OnGestureListener {

    private VideoView mVideoView;
    private GestureDetector mGestureDetector;
    private ProgressBar loadingProgress;

    public static VideoFragment newInstance(int index) {
        VideoFragment f = new VideoFragment();
        // Bundles are used to pass data using a key "index" and a value
        Bundle args = new Bundle();
        args.putInt("index", index);
        // Assign key value to the fragment
        f.setArguments(args);
        return f;
    }

    public int getShownIndex() {
        // Returns the index assigned
        return getArguments().getInt("index", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_video_layout, container, false);

        loadingProgress = (ProgressBar) rootView.findViewById(R.id.loadingProgress);
        loadingProgress.setVisibility(View.VISIBLE);

        mGestureDetector = new GestureDetector(getContext(), this);
        AppCompatActivity context = (AppCompatActivity) getActivity();
        context.getSupportActionBar().hide();
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        String videoUrlString = getArguments().getString("videoUrl");
        mVideoView = (VideoView) rootView.findViewById(R.id.videoView);
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 return mGestureDetector.onTouchEvent(event);
             }
         });
        HttpProxyCacheServer videoCache = VideoCache.getInstance(getActivity());
        //Uri videoUri = Uri.parse(videoUrlString);
        //mVideoView.setVideoURI(videoUri);
        String proxyUrl = videoCache.getProxyUrl(videoUrlString);
        mVideoView.setVideoPath(proxyUrl);
        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
                mp.setLooping(true);
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                   int arg2) {
                        // TODO Auto-generated method stub
                        loadingProgress.setVisibility(View.GONE);
                        mp.start();
                    }
                });
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        AppCompatActivity context = (AppCompatActivity) getActivity();
        context.getSupportActionBar().show();
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onDestroy();
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (diffX > diffY) {}
            if (Math.abs(diffY) > GestureConstants.MIN_FLING_DISTANCE && Math.abs(velocityY) > GestureConstants.MIN_FLING_VELOCITY) {
                if (diffY != 0) {
                    //fling top to bottom or bottom to top
                    getActivity().onBackPressed();
                }
            }
            result = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
}
