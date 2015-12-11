package org.shepherd.breathewell;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import java.util.Arrays;


public class CustomProgress extends TextView {

    private static final String LOG_TAG = CustomProgress.class.getName();
    private final static int SHAPE_RECTANGLE = 0;
    private final static int SHAPE_ROUNDED_RECTANGLE = 1;
    private final static int DEFAULT_TEXT_MARGIN = 10;

    private ShapeDrawable progressDrawable;
    private TextView textView;
    private float width = 0;
    private int calculatedWidth = 0;
    private int maxHeight = 0;
    private int progressColor;
    private int progressBackgroundColor;
    private int progressShape = SHAPE_RECTANGLE;
    private float maximumPercentage = 0.0f;
    private float cornerRadius = 25.0f;
    private boolean showingPercentage = false;

    private float increaseRate = 5.0f;
    private float deceaseRate = 5.0f;
    private boolean isIncreasing = true;
    //Constructor

    public CustomProgress(Context context) {
        super(context);
        setDefaultValue();
    }

    public CustomProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultValue();
    }

    public CustomProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDefaultValue();
    }

    private void setDefaultValue() {
        // default color
        progressColor = getResources().getColor(R.color.red_500);
        progressBackgroundColor = getResources().getColor(R.color.breathe_background);
    }

    public void refresh() {
        //this.initView();
        this.invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed) {
            initView();
        }
    }

    public void snapToPercent(float percent) {


        float newWidth  = (this.getWidth() * percent);
        if (((Math.abs(newWidth - width) > 10)) || (percent == 100 || percent ==0)) {
            width = newWidth;
            invalidate();
        }
        //width = calculatedWidth;

    }

    private float calculateRate(float seconds) {
     // use width of this component and # of seconds to find out how much it should increase
        int width = this.getWidth();

        int fps = 50; // decreasing this makes it go faster
        float rate = width / seconds / fps;

        Log.i(LOG_TAG, String.format("width:%d seconds:%f  rate:%f", width, seconds, rate));

        return rate; //Math.round(rate);
        //this.increaseRate = width / seconds;
    }
    public void setIncreaseRate(float seconds) {
        this.increaseRate = this.calculateRate(seconds);
    }
    public void setDecreaseRate(float seconds) {
        this.deceaseRate = this.calculateRate(seconds);
    }

    public void reverse() {
        this.isIncreasing = !this.isIncreasing;
        if (!this.isIncreasing) {
            this.setTargetPercentage(0.0F);
        }
    }
    public void setIncreasing(boolean bIncrease) {
        this.isIncreasing = bIncrease;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        progressDrawable.setBounds(0, 0, Math.round(width), this.getHeight());
        progressDrawable.draw(canvas);
        if(isShowingPercentage()) {
            this.setText(getCurrentPercentage()+"%");
        }
        if (isIncreasing) {
            if(width< this.getWidth()) {
                width+=this.increaseRate;
                //Log.i(LOG_TAG, String.format("width:%d ", width));
                invalidate();
            }
        }
        else {
            if (width > 0) {
                width -= this.deceaseRate;
                invalidate();
            }
        }

    }

    /**
     * Initialize the view before it will be drawn
     */
    private void initView() {
        Shape progressShapeDrawable = null;
        Shape backgroundProgressShapeDrawable = null;
        switch (progressShape) {
            case SHAPE_RECTANGLE:
                progressShapeDrawable = new RectShape();
                backgroundProgressShapeDrawable = new RectShape();
                break;
            case SHAPE_ROUNDED_RECTANGLE:
                float[] outerRadius = new float[8];
                Arrays.fill(outerRadius, cornerRadius);
                progressShapeDrawable = new RoundRectShape(outerRadius, null, null);
                backgroundProgressShapeDrawable = new RoundRectShape(outerRadius, null, null);
                break;
        }

        //Progress
        progressDrawable = new ShapeDrawable(progressShapeDrawable);
        progressDrawable.getPaint().setColor(progressColor);
        //if((this.getText().length() > 0) || isShowingPercentage()) {
            progressDrawable.setAlpha(100);
        //}

        //Background
        ShapeDrawable backgroundDrawable = new ShapeDrawable(backgroundProgressShapeDrawable);
        backgroundDrawable.getPaint().setColor(progressBackgroundColor);
        backgroundDrawable.setBounds(0, 0, this.getWidth(), this.getHeight());
        this.setBackground(backgroundDrawable);

        this.calculatedWidth = (int) (this.getWidth() * maximumPercentage);

        //Percentage
        //if(isShowingPercentage()) {
            this.setTextSize(20);
            this.setTextColor(Color.WHITE);
            this.setGravity(Gravity.CENTER);
        //}
    }

    //Helper

    /**
     * Set the progress color
     * @param color
     */
    public void setProgressColor(int color) {
        this.progressColor = color;
    }

    /**
     * Set the background color
     * @param color
     */
    public void setProgressBackgroundColor(int color) {
        this.progressBackgroundColor = color;
    }

    /**
     * Reset the progress to 0
     */
    public void resetWidth() {
        width = 0;
    }

    /**
     * Set the maximum percentage for the progress
     * @param percentage
     */
    public void setTargetPercentage(float percentage) {
        this.maximumPercentage = percentage;
    }

    /**
     * Get current percentage based on current width
     * @return
     */
    public int getCurrentPercentage() {
        return (int) Math.ceil((width/(calculatedWidth *1.0f))*100);
    }

    /**
     * Set the shape of custom progress to rectangle
     */
    public void useRectangleShape() {
        this.progressShape = SHAPE_RECTANGLE;
    }

    /**
     * Set the shape of custom progress to rounded rectangle
     * @param cornerRadius radius of the corner
     */
    public void useRoundedRectangleShape(float cornerRadius) {
        this.progressShape = SHAPE_ROUNDED_RECTANGLE;
        this.cornerRadius = cornerRadius;
    }

    /**
     * If this returns true the custom progress
     * will show progress based on getCurrentPercentage()
     * @return true for showing percentage false for not showing anything
     */
    public boolean isShowingPercentage() {
        return showingPercentage;
    }

    /**
     * Set if the custom progress will show percentage or not
     * @param showingPercentage true for showing percentage false for not showing anything
     */
    public void setShowingPercentage(boolean showingPercentage) {
        this.showingPercentage = showingPercentage;
    }
}
