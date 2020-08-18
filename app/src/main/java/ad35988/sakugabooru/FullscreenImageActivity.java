package ad35988.sakugabooru;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.signature.StringSignature;

/**
 * Created by andrew on 11/8/16.
 */

public class FullscreenImageActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private ImageView mImage;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        getSupportActionBar().hide();
        mGestureDetector = new GestureDetector(this, this);
        String imageUrlString = getIntent().getExtras().getString("imageUrl");
        mImage = (ImageView) findViewById(R.id.fullscreenImage);
        mImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
        GlideDrawableImageViewTarget glideImage = new GlideDrawableImageViewTarget(mImage);
        Glide.with(this).load(imageUrlString).signature(new StringSignature(String.valueOf(imageUrlString))).into(glideImage);
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
            if (diffX > diffY) {
            }
            if (Math.abs(diffY) > GestureConstants.MIN_FLING_DISTANCE && Math.abs(velocityY) > GestureConstants.MIN_FLING_VELOCITY) {
                if (diffY != 0) {
                    //fling top to bottom or bottom to top
                    onBackPressed();
                }
            }
            result = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
}
