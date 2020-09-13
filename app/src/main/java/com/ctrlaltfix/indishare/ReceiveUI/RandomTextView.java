package com.ctrlaltfix.indishare.ReceiveUI;


import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.ctrlaltfix.indishare.R;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

public class RandomTextView extends FrameLayout
    implements
        ViewTreeObserver.OnGlobalLayoutListener
{

    private static final String tag = RandomTextView.class.getSimpleName();

    private static final int MAX = 5;
    private static final int IDX_X = 0;
    private static final int IDX_Y = 1;
    private static final int IDX_TXT_LENGTH = 2;
    private static final int IDX_DIS_Y = 3;
    private static final int TEXT_SIZE = 12;
    private static int i=0;
    private Random random;
    private Vector<WifiP2pDevice> vecKeywords;
    private int width;
    private int height;
    private int mode = RippleView.MODE_OUT;
    private int fontColor = R.color.fontColor;
    private int shadowColor = R.color.shadowColor;

    public interface OnRippleViewClickListener
    {
        void onRippleViewClicked(View view, WifiP2pDevice device);
    }

    private OnRippleViewClickListener onRippleOutViewClickListener;

    public RandomTextView(Context context)
    {
        super(context);
        init(null, context);
    }

    public RandomTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, context);
    }

    public RandomTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    @TargetApi(21)
    public RandomTextView(Context context, AttributeSet attrs, int defStyleAttr,
                          int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, context);
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public void setOnRippleViewClickListener(OnRippleViewClickListener listener)
    {
        onRippleOutViewClickListener = listener;
    }

    public void addDevice(WifiP2pDevice keyword)
    {
        if (!vecKeywords.contains(keyword))
            vecKeywords.add(keyword);
    }

    public Vector<WifiP2pDevice> getKeyWords()
    {
        return vecKeywords;
    }


    private void init(AttributeSet attrs, Context context)
    {
        random = new Random();
        vecKeywords = new Vector<WifiP2pDevice>(MAX);
        getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @Override
    public void onGlobalLayout()
    {
        int tmpW = getWidth();
        int tmpH = getHeight();
        if (width != tmpW || height != tmpH)
        {
            width = tmpW;
            height = tmpH;
            Log.d(tag, "RandomTextView width = " + width + "; height = " + height);
        }
    }

    public void show()
    {
        this.removeAllViews();

        if (width > 0 && height > 0 && vecKeywords != null && vecKeywords.size() > 0)
        {
            int xCenter = width >> 1;
            int yCenter = height >> 1;
            int size = vecKeywords.size();
            int xItem = width / (size + 1);
            int yItem = height / (size + 1);
            LinkedList<Integer> listX = new LinkedList<>();
            LinkedList<Integer> listY = new LinkedList<>();
            for (int i = 0; i < size; i++)
            {
                listX.add(i * xItem);
                listY.add(i * yItem + (yItem >> 2));
            }
            LinkedList<RippleView> listTxtTop = new LinkedList<>();
            LinkedList<RippleView> listTxtBottom = new LinkedList<>();

            for (i = 0; i < size; i++)
            {
                final WifiP2pDevice wifiP2pDevice = vecKeywords.get(i);
                final String keyword = wifiP2pDevice.deviceName;
                int ranColor = fontColor;
                int xy[] = randomXY(random, listX, listY, xItem);

                int txtSize = TEXT_SIZE;
                final RippleView txt = new RippleView(getContext());
                if (mode == RippleView.MODE_IN)
                {
                    txt.setMode(RippleView.MODE_IN);
                }
                else
                {
                    txt.setMode(RippleView.MODE_OUT);
                }

                txt.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (onRippleOutViewClickListener != null)
                            onRippleOutViewClickListener.onRippleViewClicked(view, wifiP2pDevice);
                    }
                });
                txt.setText(keyword);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    txt.setTextColor(getResources().getColor(R.color.fontColor, null));
                }else{
                    txt.setTextColor(getResources().getColor(R.color.fontColor));
                }
                txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtSize);
                txt.setShadowLayer(1, 1, 1, shadowColor);
                txt.setGravity(Gravity.CENTER);
                txt.startRippleAnimation();

                int strWidth = txt.getMeasuredWidth();
                xy[IDX_TXT_LENGTH] = strWidth;
                if (xy[IDX_X] + strWidth > width - (xItem/* >> 1 */))
                {
                    int baseX = width - strWidth;
                    xy[IDX_X] = baseX - xItem + random.nextInt(xItem >> 1);
                }
                else if (xy[IDX_X] == 0)
                {
                    xy[IDX_X] = Math.max(random.nextInt(xItem), xItem / 3);
                }
                xy[IDX_DIS_Y] = Math.abs(xy[IDX_Y] - yCenter);
                txt.setTag(xy);
                if (xy[IDX_Y] > yCenter)
                {
                    listTxtBottom.add(txt);
                }
                else
                {
                    listTxtTop.add(txt);
                }
            }

            attach2Screen(listTxtTop, xCenter, yCenter, yItem);
            attach2Screen(listTxtBottom, xCenter, yCenter, yItem);
        }
    }

    private void attach2Screen(LinkedList<RippleView> listTxt, int xCenter, int yCenter,
                               int yItem)
    {
        int size = listTxt.size();
        sortXYList(listTxt, size);
        for (int i = 0; i < size; i++)
        {
            RippleView txt = listTxt.get(i);
            int[] iXY = (int[]) txt.getTag();
            int yDistance = iXY[IDX_Y] - yCenter;

            int yMove = Math.abs(yDistance);
            inner : for (int k = i - 1; k >= 0; k--)
            {
                int[] kXY = (int[]) listTxt.get(k).getTag();
                int startX = kXY[IDX_X];
                int endX = startX + kXY[IDX_TXT_LENGTH];
                if (yDistance * (kXY[IDX_Y] - yCenter) > 0)
                {
                    if (isXMixed(startX, endX, iXY[IDX_X], iXY[IDX_X]
                        + iXY[IDX_TXT_LENGTH]))
                    {
                        int tmpMove = Math.abs(iXY[IDX_Y] - kXY[IDX_Y]);
                        if (tmpMove > yItem)
                        {
                            yMove = tmpMove;
                        }
                        else if (yMove > 0)
                        {
                            yMove = 0;
                        }
                        break inner;
                    }
                }
            }

            if (yMove > yItem)
            {
                int maxMove = yMove - yItem;
                int randomMove = random.nextInt(maxMove);
                int realMove = Math.max(randomMove, maxMove >> 1) * yDistance
                    / Math.abs(yDistance);
                iXY[IDX_Y] = iXY[IDX_Y] - realMove;
                iXY[IDX_DIS_Y] = Math.abs(iXY[IDX_Y] - yCenter);
                sortXYList(listTxt, i + 1);
            }
            LayoutParams layParams = new LayoutParams(
            /* FrameLayout.LayoutParams.WRAP_CONTENT */200,
            /* FrameLayout.LayoutParams.WRAP_CONTENT */200);
            layParams.gravity = Gravity.LEFT | Gravity.TOP;
            layParams.leftMargin = iXY[IDX_X];
            layParams.topMargin = iXY[IDX_Y];
            addView(txt, layParams);
        }
    }

    private int[] randomXY(Random ran, LinkedList<Integer> listX,
                           LinkedList<Integer> listY, int xItem)
    {
        int[] arr = new int[4];
        arr[IDX_X] = listX.remove(ran.nextInt(listX.size()));
        arr[IDX_Y] = listY.remove(ran.nextInt(listY.size()));
        return arr;
    }

    private boolean isXMixed(int startA, int endA, int startB, int endB)
    {
        boolean result = false;
        if (startB >= startA && startB <= endA)
        {
            result = true;
        }
        else if (endB >= startA && endB <= endA)
        {
            result = true;
        }
        else if (startA >= startB && startA <= endB)
        {
            result = true;
        }
        else if (endA >= startB && endA <= endB)
        {
            result = true;
        }
        return result;
    }

    private void sortXYList(LinkedList<RippleView> listTxt, int endIdx)
    {
        for (int i = 0; i < endIdx; i++)
        {
            for (int k = i + 1; k < endIdx; k++)
            {
                if (((int[]) listTxt.get(k).getTag())[IDX_DIS_Y] < ((int[]) listTxt
                        .get(i).getTag())[IDX_DIS_Y])
                {
                    RippleView iTmp = listTxt.get(i);
                    RippleView kTmp = listTxt.get(k);
                    listTxt.set(i, kTmp);
                    listTxt.set(k, iTmp);
                }
            }
        }
    }
}
