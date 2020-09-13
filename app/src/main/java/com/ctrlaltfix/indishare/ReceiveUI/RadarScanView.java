package com.ctrlaltfix.indishare.ReceiveUI;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.ctrlaltfix.indishare.R;

public class RadarScanView extends View
{
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;

    private int defaultWidth;
    private int defaultHeight;
    private int start;
    private int centerX;
    private int centerY;
    private int radarRadius;
    private int circleColor;
    private int radarColor;
    private int tailColor;

    private Paint mPaintCircle;
    private Paint mPaintRadar;
    private Matrix matrix;

    private Handler handler = new Handler();
    private Runnable run = new Runnable()
    {
        @Override
        public void run()
        {
            start += 2;
            matrix = new Matrix();
            matrix.postRotate(start, centerX, centerY);
            postInvalidate();
            handler.postDelayed(run, 10);
        }
    };

    public RadarScanView(Context context)
    {
        super(context);
        init(null, context);
    }

    public RadarScanView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, context);
    }

    public RadarScanView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    @TargetApi(21)
    public RadarScanView(Context context, AttributeSet attrs, int defStyleAttr,
                         int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radarRadius = Math.min(w, h);
    }

    private void init(AttributeSet attrs, Context context)
    {
        if (attrs != null)
        {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.RadarScanView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                circleColor = ta.getColor(R.styleable.RadarScanView_circleColor, getResources().getColor(R.color.circleColor,null));
                radarColor = ta.getColor(R.styleable.RadarScanView_radarColor, getResources().getColor(R.color.radarColor,null));
                tailColor = ta.getColor(R.styleable.RadarScanView_tailColor, getResources().getColor(R.color.tailColor,null));
            }else{
                circleColor = ta.getColor(R.styleable.RadarScanView_circleColor, getResources().getColor(R.color.circleColor));
                radarColor = ta.getColor(R.styleable.RadarScanView_radarColor, getResources().getColor(R.color.radarColor));
                tailColor = ta.getColor(R.styleable.RadarScanView_tailColor, getResources().getColor(R.color.tailColor));
            }
            ta.recycle();
        }

        initPaint();

        defaultWidth = dip2px(context, DEFAULT_WIDTH);
        defaultHeight = dip2px(context, DEFAULT_HEIGHT);

        matrix = new Matrix();
        handler.post(run);
    }

    private void initPaint()
    {
        mPaintCircle = new Paint();
        mPaintCircle.setColor(circleColor);
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setStrokeWidth(5);

        mPaintRadar = new Paint();
        mPaintRadar.setColor(radarColor);
        mPaintRadar.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int resultWidth = 0;
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);

        if (modeWidth == MeasureSpec.EXACTLY)
        {
            resultWidth = sizeWidth;
        }
        else
        {
            resultWidth = defaultWidth;
            if (modeWidth == MeasureSpec.AT_MOST)
            {
                resultWidth = Math.min(resultWidth, sizeWidth);
            }
        }

        int resultHeight = 0;
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (modeHeight == MeasureSpec.EXACTLY)
        {
            resultHeight = sizeHeight;
        }
        else
        {
            resultHeight = defaultHeight;
            if (modeHeight == MeasureSpec.AT_MOST)
            {
                resultHeight = Math.min(resultHeight, sizeHeight);
            }
        }

        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawCircle(centerX, centerY, radarRadius / 7, mPaintCircle);
        canvas.drawCircle(centerX, centerY, radarRadius / 4, mPaintCircle);
        canvas.drawCircle(centerX, centerY, radarRadius / 3, mPaintCircle);
        canvas.drawCircle(centerX, centerY, 3 * radarRadius / 7, mPaintCircle);

        Shader shader;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shader = new SweepGradient(centerX, centerY, getResources().getColor(R.color.sweeper1, null),
                    getResources().getColor(R.color.sweeper2, null));
        }else{
            shader = new SweepGradient(centerX, centerY, getResources().getColor(R.color.sweeper1),
                    getResources().getColor(R.color.sweeper2));
        }
        mPaintRadar.setShader(shader);
        canvas.concat(matrix);
        canvas.drawCircle(centerX, centerY, 3 * radarRadius / 7, mPaintRadar);
    }

    private int dip2px(Context context, float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
