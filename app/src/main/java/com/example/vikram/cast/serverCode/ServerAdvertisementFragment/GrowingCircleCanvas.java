package com.example.vikram.cast.serverCode.ServerAdvertisementFragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.vikram.cast.R;

public class GrowingCircleCanvas extends View implements ValueAnimator.AnimatorUpdateListener{
    private int circleCentreX;
    private int circleCentreY;
    private int currentRadius;
    private int endRadius;
    private Canvas canvas;
    private Paint paint;

    public GrowingCircleCanvas(Context context,AttributeSet attrs) {
        super(context,attrs);
        endRadius=getResources().getDisplayMetrics().widthPixels;

        canvas = new Canvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(getResources().getColor(R.color.colorAccent));

        ValueAnimator animator = ValueAnimator.ofInt(160,endRadius);
        animator.setDuration(4000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(this);
        animator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        circleCentreX=w/2;
        circleCentreY=h/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(circleCentreX,circleCentreY,currentRadius,paint);
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        currentRadius = (int)animation.getAnimatedValue();
        invalidate();
    }
}
