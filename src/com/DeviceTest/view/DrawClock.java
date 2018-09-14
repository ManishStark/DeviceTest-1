
package com.DeviceTest.view;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class DrawClock extends View {

    public DrawClock(Context context) {
        super(context);
    }

    public void onDraw(Canvas canvas) {

        canvas.drawColor(Color.BLACK);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setTextSize(16);
        drawClockPandle(canvas, paint); // 画制时钟的底盘

        drawClockPointer(canvas, paint); // 画制时钟的指针
    }

    // ------时钟底盘画制函数--------------
    void drawClockPandle(Canvas canvas, Paint paint)
    {
        int px = getMeasuredWidth();
        int py = getMeasuredWidth();

        canvas.drawCircle(px / 2, py / 2, py / 2 - 1, paint);
        canvas.drawCircle(px / 2, py / 2, py / 40, paint);

        Path path = new Path();
        path.moveTo(1, py / 2);
        path.lineTo(py / 16, py / 2);
        canvas.drawPath(path, paint);
        canvas.drawText("11", py / 16, py / 2, paint);
        
        path.moveTo(px / 2, 1);
        path.lineTo(px / 2, py / 16);
        canvas.drawPath(path, paint);
        canvas.drawText("12", px / 2,py / 16, paint);
        
        path.moveTo(px - 1, py / 2);
        path.lineTo(px - px / 16, py / 2);
        canvas.drawPath(path, paint);
        canvas.drawText("3",px - px / 16, py / 2, paint);
        
        path.moveTo(px / 2, py - 1);
        path.lineTo(px / 2, py - py / 16);
        canvas.drawPath(path, paint);
        canvas.drawText("6", px / 2, py - py / 16, paint);
        
        canvas.save();
        canvas.rotate(30, px / 2, py / 2);
        Path path8 = new Path();
        path8.moveTo(1, py / 2);
        path8.lineTo(px / 30, py / 2);
        canvas.drawPath(path8, paint);
        canvas.drawText("10",px / 30, py / 2, paint);
        
        path8.moveTo(px / 2, 1);
        path8.lineTo(px / 2, py / 30);
        canvas.drawPath(path8, paint);
        canvas.drawText("1",px / 2, py / 30, paint);
        
        path8.moveTo(px - 1, py / 2);
        path8.lineTo(px - px / 30, py / 2);
        canvas.drawPath(path8, paint);
        canvas.drawText("4", px - px / 30, py / 2, paint);
        
        path8.moveTo(px / 2, py - 1);
        path8.lineTo(px / 2, py - py / 30);
        canvas.drawPath(path8, paint);
        canvas.drawText("7", px / 2, py - py / 30, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(60, px / 2, py / 2);
        Path path9 = new Path();
        path9.moveTo(1, py / 2);
        path9.lineTo(px / 30, py / 2);
        canvas.drawPath(path9, paint);
        canvas.drawText("11", px / 30, py / 2, paint);

        path9.moveTo(px / 2, 1);
        path9.lineTo(px / 2, py / 30);
        canvas.drawPath(path9, paint);
        canvas.drawText("2",px / 2,  py / 30, paint);
        
        path9.moveTo(px - 1, py / 2);
        path9.lineTo(px - px / 30, py / 2);
        canvas.drawPath(path9, paint);
        canvas.drawText("5", px - px / 30, py / 2, paint);
        
        path9.moveTo(px / 2, py - 1);
        path9.lineTo(px / 2, py - py / 30);
        canvas.drawPath(path9, paint);
        canvas.drawText("8",px / 2,py - py / 30, paint);
        canvas.restore();

    }

    // ---------时钟指针画制函数---------------------
    void drawClockPointer(Canvas canvas, Paint paint)
    {
        int px = getMeasuredWidth();
        int py = getMeasuredWidth();

        /*-------------------------获得当前时间小时和分钟数---------------------*/
        int mHour;
        int mMinutes;
        int mSeconds;
        long time = System.currentTimeMillis();
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        mHour = mCalendar.get(Calendar.HOUR);
        mMinutes = mCalendar.get(Calendar.MINUTE);
        mSeconds = mCalendar.get(Calendar.SECOND);
        /*-------------------------获得当前时间---------------------*/

        float hDegree = ((mHour + (float) mMinutes / 60) / 12) * 360;
        float mDegree = ((mMinutes + (float) mSeconds / 60) / 60) * 360;
        float sDegree = ((float) mSeconds / 60) * 360;

        // 分针－－－－－－－－－－－
        paint.setColor(Color.GREEN);
        canvas.save();
        canvas.rotate(mDegree, px / 2, py / 2);
        Path path1 = new Path();
        path1.moveTo(px / 2, py / 2);
        path1.lineTo(px / 2, py / 4);
        canvas.drawPath(path1, paint);
        canvas.restore();

        // 时针－－－－－－－－－－－－－－－－－－
        paint.setColor(Color.WHITE);
        canvas.save();
        canvas.rotate(hDegree, px / 2, py / 2);
        Path path2 = new Path();
        path2.moveTo(px / 2, py / 2);
        path2.lineTo(px / 2, py / 3);
        canvas.drawPath(path2, paint);
        canvas.restore();

        // 秒针－－－－－－－－－－－－－－－－－－－－－
        paint.setColor(Color.YELLOW);
        canvas.save();
        canvas.rotate(sDegree, px / 2, py / 2);
        Path path3 = new Path();
        path3.moveTo(px / 2, py / 2);
        path3.lineTo(px / 2, py / 8);
        canvas.drawPath(path3, paint);
        canvas.restore();

    }

}
