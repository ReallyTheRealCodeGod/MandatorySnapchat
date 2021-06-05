package com.example.mandatorysnapchat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
    //drawing path
    static Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    static int paintColor = 0xFFFF0000;
    //stroke width
    private  float STROKE_WIDTH = 5f;
    //canvas
    private Canvas drawCanvas;

    //canvas bitmap
    private Bitmap canvasBitmap;
    //eraser mode
    private boolean erase=false;

    private Bitmap bitmap;

    //constructor
    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
        setErase(erase);
    }

    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(STROKE_WIDTH);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setImageToDraw(Bitmap image) {

        Bitmap.Config bitmapConfig = image.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        image = image.copy(bitmapConfig, true);
        bitmap = image;
        drawCanvas.drawBitmap(bitmap, 0,0, null);

    }

    public Bitmap returnBitmap() {
        return canvasBitmap;
    }

    private Bitmap mergeBitmaps(Bitmap back, Bitmap front) {

        Bitmap result = Bitmap.createBitmap(back.getWidth(), back.getHeight(), back.getConfig());
        Canvas canvas = new Canvas(result);
        int widthBack = back.getWidth();
        int widthFront = front.getWidth();
        float move = (widthBack - widthFront) / 2;
        canvas.drawBitmap(back, 0f, 0f, null);
        canvas.drawBitmap(front, move, move, null);
        return result;
    }

    //*************************************** View assigned size  ****************************************************

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        Picture picture = new Picture();
//        Canvas canvas = picture.beginRecording(canvasBitmap.getWidth(), canvasBitmap.getHeight());
//        canvas.drawBitmap(canvasBitmap, null, new RectF(0f,0f, (float) canvasBitmap.getWidth(), (float) canvasBitmap.getHeight()), null);
//        picture.endRecording();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            canvasBitmap = Bitmap.cre (picture ,w, h, Bitmap.Config.ARGB_8888);
//        }

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        drawCanvas = new Canvas(canvasBitmap);
    }

    public void setErase(boolean isErase){
        erase=isErase;
        drawPaint = new Paint();
        if(erase) {
            setupDrawing();
            int srcColor= 0x00000000;

            PorterDuff.Mode mode = PorterDuff.Mode.CLEAR;
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(srcColor, mode);

            drawPaint.setColorFilter(porterDuffColorFilter);

            drawPaint.setColor(srcColor);
            drawPaint.setXfermode(new PorterDuffXfermode(mode));

        }
        else {

            setupDrawing();

        }
    }

    //************************************   draw view  *************************************************************

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    //***************************   respond to touch interaction   **************************************************

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        canvasPaint.setColor(paintColor);
        float touchX = event.getX();
        float touchY = event.getY();
        //respond to down, move and up events

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        //redraw
        invalidate();
        return true;
    }

    //***********************************   return current alpha   ***********************************************
    public int getPaintAlpha(){
        return Math.round((float)STROKE_WIDTH/255*100);
    }

    //**************************************  set alpha   ******************************************************
    public void setPaintAlpha(int newAlpha){
        STROKE_WIDTH=Math.round((float)newAlpha/100*255);
        drawPaint.setStrokeWidth(newAlpha);
    }
}

