package com.library.custom.marqueeview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by IT114 on 10-08-2017.
 */

public class MarqueeText extends HorizontalScrollView {
    private ArrayList<TextListener> textListenerArrayList ;
    private Context context;
    private int size;
    private int width;
    private int threadTime;
    static boolean suspendThread ;
    private static Thread thread;
    LinearLayout.LayoutParams lp;
    static LinearLayout relativeLayout;
    public MarqueeText(Context context) {
        super(context);
        this.context = context;
        textListenerArrayList = new ArrayList<>();
        initComponent();
    }


    public MarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initComponent();

    }

    public MarqueeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initComponent();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setMarqueeStopTime(int threadTime) {
        this.threadTime = threadTime;
    }

    public void setMarqueeText(String marqueeText ,OnClickListener onClickListener,int color) {
        TextListener textListener = new TextListener(marqueeText,onClickListener,color);
        textListenerArrayList.add(textListener);
    }


    public void startMarquee(){
        if(getChildCount()==0) {
            relativeLayout = new LinearLayout(context);
            relativeLayout.setOrientation(LinearLayout.HORIZONTAL);
            relativeLayout.setLayoutParams(lp);
            relativeLayout.setGravity(Gravity.CENTER_VERTICAL);
            TextView text = new TextView(context);
            text.setTextSize(size);
            for (int j = 0; j < (width / (text.getTextSize() / 2)) * 2; j++) {
                TextView textView = new TextView(context);
                textView.setTextSize(size);
                textView.setLayoutParams(lp);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setText(" ");
                relativeLayout.addView(textView);
            }
            for (TextListener textListener : textListenerArrayList) {
                TextView textView = new TextView(context);
                textView.setTextSize(size);
                textView.setText(textListener.string);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                if (textListener.onClickListener != null) {
                    textView.setOnClickListener(textListener.onClickListener);
                }
                textView.setTextColor(textListener.color);
                relativeLayout.addView(textView);
            }
            for (int j = 0; j < (width / (text.getTextSize() / 2)) * 2; j++) {
                TextView textView = new TextView(context);
                textView.setTextSize(size);
                textView.setText(" ");
                textView.setGravity(Gravity.CENTER_VERTICAL);
                relativeLayout.addView(textView);
            }
            addView(relativeLayout);
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (true) {
                        if (i == relativeLayout.getWidth() - width) {
                            i = 0;
                        }
                        if (i == 0) {
                            scrollTo(-(relativeLayout.getWidth()), 0);
                        }
                        try {

                            final int finalI = i;
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollTo(finalI, 0);
                                }
                            });
                            Thread.sleep(threadTime);
                            synchronized (thread) {
                                while (suspendThread) {
                                    thread.wait();
                                    System.out.println("wait");
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            synchronized (thread) {
                                while (suspendThread) {
                                    thread.wait();
                                    System.out.println("wait");
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        i++;
                    }
                }
            });
            if (!thread.isAlive()) {
                thread.start();
            }
        }
//        thread.start();
    }
    public void pause(){
        suspendThread = true;
    }
    public void resume(){
        suspendThread = false;
        if(thread!=null) {
            synchronized (thread) {
                System.out.println("notify");
                thread.notify();
            }
        }
    }


    public void destroy(){
        if(thread!=null){
            Toast.makeText(context, "Marquee Destroy", Toast.LENGTH_SHORT).show();
            thread.interrupt();
        }
    }
    class TextListener{
        String string;
        OnClickListener onClickListener;
        int color;
        TextListener(String string, OnClickListener onClickListener , int color){
            this.string = string;
            this.onClickListener = onClickListener;
            this.color = color;
        }
    }

    void initComponent(){
        textListenerArrayList = new ArrayList<>();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        size = 12;
        threadTime = 10;
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);
        setHorizontalScrollBarEnabled(false);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    pause();
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    resume();
                }
                return false;
            }
        });

    }





}
