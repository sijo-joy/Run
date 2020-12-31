package com.example.run;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// class definition
public class CustomViewLive extends View {
    private ArrayList<Double> speed_list;
    int total_length;
    private static Double total_distance;
    private static int total_sec;
    private static HashMap<Integer, Double> hashmap_distance = new HashMap<Integer, Double>();
    private static HashMap<Integer, Long> hashmap_time = new HashMap<Integer, Long>();
    // default constructor for the class that takes in a context
    public CustomViewLive(Context c) {
        super(c);
        init();
    }

    // constructor that takes in a context and also a list of attributes
// that were set through XML
    public CustomViewLive(Context c, AttributeSet as) {
        super(c, as);
        init();
    }

    // constructor that take in a context, attribute set and also a default
// style in case the view is to be styled in a certian way
    public CustomViewLive(Context c, AttributeSet as, int default_style) {
        super(c, as, default_style);
        init();
    }

    // refactored init method as most of this code is shared by all the
// constructors
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = 0;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        // set the dimensions
        if (widthWithoutPadding > heigthWithoutPadding) {
            size = heigthWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }
        total_length = size + getPaddingLeft() + getPaddingRight() - 10;
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    public void init() {
        speed_list = new ArrayList<Double>();
    }

    // public method that needs to be overridden to draw the contents of this
// widget
    public void onDraw(Canvas canvas) {
// call the superclass method
        super.onDraw(canvas);
        MainActivity ac = new MainActivity();
        speed_list = ac.getDatas();
        total_distance = 0.0;
        total_distance = ac.getTotKm();
        hashmap_distance = ac.getDistanceHm();
        hashmap_time = ac.getTimeHm();

        total_sec = ac.getTotSec();
        total_sec = ac.getTotSec();
        total_sec = ac.getTotSec();
        Paint paint2 = new Paint();
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(1);
        paint2.setColor(Color.BLACK);
        paint2.setTextSize(40);

        Paint paint = new Paint();
        paint.setColor( Color.RED );
        paint.setStrokeWidth( 1.5f );
        paint.setStyle( Paint.Style.STROKE );
        paint.setTextSize(10);
        canvas.drawLine(50, total_length-50, total_length-50, total_length-50, paint);
        canvas.drawLine(50, total_length-50, 50, 50, paint);

        if (total_distance > 0.0 && total_sec > 10 && total_distance < 50.0){
            int tot_dis = Integer.valueOf(total_distance.intValue())+1;
            int sopt_to_mark = (total_length-100) / tot_dis;
            int a = (int) Math.round(sopt_to_mark);
            int to_add = a;
            for(int k = 0;k <= tot_dis;k++){
                canvas.drawCircle( a+50, total_length-50 ,10,paint2);
                canvas.drawText(Integer.toString(k+1), a+50, total_length-10 ,paint2);
                a += to_add;
            }
            int to_div = (total_sec+10) / 10;
            int sopt_to_mark_sec = (total_length-100) / (to_div);
            int to_add_sec = sopt_to_mark_sec;
            for(int j = 0; j <= (total_sec+10);j+=10){
                canvas.drawCircle( 50 , total_length -(sopt_to_mark_sec+50) ,10,paint2);
                canvas.drawText(Integer.toString(j+10), 50, total_length -(sopt_to_mark_sec+50) ,paint2);
                sopt_to_mark_sec += to_add_sec;
            }
            int fx = 50;
            int fy = total_length - 50;
            int sx = 0;
            int count_mul = 1;
            for(Map.Entry dis_map:hashmap_distance.entrySet()){
//
                long time_mili_sec = hashmap_time.get(dis_map.getKey());
                int  time_sec = ((int) time_mili_sec / 1000) ;
                Double ss = Double.valueOf(total_sec+10) / Double.valueOf(time_sec);
                Double asdad = (total_length-100) / (ss);
                int asa = Integer.valueOf(asdad.intValue());
                int sy = total_length -(asa+50);
                if((count_mul) == hashmap_distance.size()){
                    double per = ( (Double) dis_map.getValue() / 1.00 ) * 100;
                    double per_to_Add = (per/100) * to_add;
                    sx += per_to_Add + 50;
                }
                else if(count_mul == 1) {
                    sx = to_add + 50;
                }

                canvas.drawCircle( sx , sy ,10,paint2);
                canvas.drawLine(fx, fy, sx, sy, paint);

                fx = sx;
                fy = sy;
                if((count_mul+1) != hashmap_distance.size()){
                    sx = to_add+((to_add+50)*count_mul);
                }


                count_mul++;
            }
        }


    }


}