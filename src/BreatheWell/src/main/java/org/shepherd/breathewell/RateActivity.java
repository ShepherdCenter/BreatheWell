package org.shepherd.breathewell;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.google.android.glass.widget.Slider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class RateActivity extends Activity {

    /**
     * {@link CardScrollView} to use as the main content view.
     */
    private static final String LOG_TAG = RateActivity.class.getName();
    private static final int START_BREATHING = 0;

    private TextView rateStress;
    private ImageView imageView;
    private int sliderPositions = 7;
    private int currentStress = 3;
    private TextToSpeech mSpeech;
    boolean beforeExercise = true;
    ObjectAnimator animation = null;
    FrameLayout layout;

    private GestureDetector mGestureDetector;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ((App) RateActivity.this.getApplication()).trackView("rate-prebreathing");
        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                mSpeech.speak("Please rate your level of stress. Swipe to change.", TextToSpeech.QUEUE_ADD, null);
                refreshView();
            }}
                );
        mGestureDetector = createGestureDetector(this);



    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    private void buildView() {

        //CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TITLE);
        String text = "";
        int textColor = Color.WHITE;
        int drawable = R.drawable.stressscreen;
        int color;


        imageView.setBackgroundResource(drawable);
        //this.getViewView v = card.getView();
        //mSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.body_layout);

        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.arrow);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(28, 60);
        params.leftMargin = 5+ (currentStress-1)*100;
        params.topMargin = 300;
        rl.addView(iv, params);
        layout.setBackgroundColor(Color.WHITE);
    }

    private void refreshView() {
        if (rateStress == null) {
            setContentView(R.layout.ratestressimage);
            //rateStress = (TextView)this.findViewById(R.id.stressLabel);
            layout = (FrameLayout)this.findViewById(R.id.layout);
            imageView = (ImageView)this.findViewById(R.id.imageView);

        }
        buildView();


    }

    private void handleTap() {
        if (beforeExercise) {
            beforeExercise = false;
            Intent intent = new Intent(this, BreatheActivity.class);
            startActivityForResult(intent, START_BREATHING);
        } else {
            // final stress
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == START_BREATHING) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // show rating again
                ((App) RateActivity.this.getApplication()).trackView("rate-postbreathing");
                mSpeech.speak("Please rate your level of stress after the exercise.", TextToSpeech.QUEUE_ADD, null);
                refreshView();
            } else {
                // cancelled?
                finish();
            }
        }
    }


    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                Log.i(LOG_TAG, gesture.name());
                if ((gesture == Gesture.TAP) || (gesture == Gesture.LONG_PRESS)) {
                    // do something on tap
                    handleTap();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    // do something on two finger tap
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    // do something on right (forward) swipe
                    currentStress++;
                    if (currentStress > sliderPositions) {
                        currentStress = sliderPositions;
                    }
                    refreshView();
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    currentStress--;
                    if (currentStress < 1) {
                        currentStress = 1;
                    }
                    refreshView();
                    return true;
                }
                return false;
            }
        });
        return gestureDetector;
    }

    @Override
    protected void onDestroy()
    {
        mSpeech.shutdown();
        mSpeech = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }


}
