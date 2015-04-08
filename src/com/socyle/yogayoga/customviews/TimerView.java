package com.socyle.yogayoga.customviews;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;

import com.socyle.yogayoga.R;

public class TimerView extends View {
	private Paint mPaint;
	private Bitmap imageRing;
	private Bitmap imageMoga;
	private RectF circle;
	private int progress;
	private int startAngle;
	private int sweepAngle;

	public TimerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		imageRing = BitmapFactory.decodeResource(getResources(),
				R.drawable.timer_circlering);
		imageMoga = BitmapFactory.decodeResource(getResources(),
				R.drawable.timer_moga1);

		circle = new RectF();

		progress = 10;
		System.out.println("ANGLE: " + Math.toDegrees(Math.atan2(25, 50)));
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// calc x, y, width and height
		int xRing = (canvas.getWidth() - imageRing.getWidth()) / 2;
		int yRing = (canvas.getHeight() - imageRing.getHeight()) / 2;
		int borderWidth = (int)((44 / 824.0) * imageRing.getWidth()) + 4;
		int circleLeft = xRing + borderWidth - 2;
		int circleTop = yRing + borderWidth;
		int circleRight = circleLeft + imageRing.getWidth() - (borderWidth * 2) + 4;
		int circleBottom = circleTop + imageRing.getHeight() - (borderWidth * 2) + 3;
		int circleWidth = circleRight - circleLeft;
		int circleHeight = circleBottom - circleTop;
		int circleRadius = circleWidth / 2;
//		int emptyHeight = circleHeight - (int)((progress / 100.0) * circleHeight);
		int emptyHeight = (circleHeight/2) - (int)((progress / 100.0) * (circleHeight/2));
		int mogaWidth = (int)((progress / 50.0) * imageMoga.getWidth());
		int xMoga = circleLeft + ((circleWidth - mogaWidth) / 2);
		int yMoga = circleTop + (circleHeight - (int)((progress / 100.0) * circleHeight)) - (imageMoga.getHeight()/2);
		double mo7et = 2 * Math.PI * circleRadius;
		double arcLenght = (progress / 100) * mo7et;
		
		circle.set(circleLeft, circleTop, circleRight, circleBottom);
		
		// draw background circle
		mPaint.setColor(getResources().getColor(R.color.timer_circle_bg));
		canvas.drawOval(circle, mPaint);

		// draw progress
//		startAngle = 90 - (int) ((progress / 50.0) * 90);
//		startAngle -= ((1/3.0) * startAngle);
		
//		startAngle = (int)Math.toDegrees(Math.atan2(emptyHeight, circleRadius));
		
		startAngle =(int)((progress / 100.0) * 2 * Math.PI);
		sweepAngle = 180 - (startAngle * 2);
		mPaint.setColor(getResources().getColor(R.color.timer_circle_fill1));
		canvas.drawArc(circle, startAngle, sweepAngle, false, mPaint);
		
		// draw moga
//		canvas.drawBitmap(imageMoga, xMoga, yMoga, mPaint);
		canvas.drawLine(0, circleTop + (circleHeight - (int)((progress / 100.0) * circleHeight)), canvas.getHeight(), circleTop + (circleHeight - (int)((progress / 100.0) * circleHeight)), mPaint);

		// draw background ring
		canvas.drawBitmap(imageRing, xRing, yRing, mPaint);

		invalidate();
	}
	
	/**
	 * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
	 * more memory that there is already allocated.
	 * 
	 * @param imgIn - Source image. It will be released, and should not be used more
	 * @return a copy of imgIn, but muttable.
	 */
	public Bitmap convertToMutable(Bitmap imgIn) {
	    try {
	        //this is the file going to use temporally to save the bytes. 
	        // This file will not be a image, it will store the raw image data.
	        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

	        //Open an RandomAccessFile
	        //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
	        //into AndroidManifest.xml file
	        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

	        // get the width and height of the source bitmap.
	        int width = imgIn.getWidth();
	        int height = imgIn.getHeight();
	        Config type = imgIn.getConfig();

	        //Copy the byte to the file
	        //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
	        FileChannel channel = randomAccessFile.getChannel();
	        MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
	        imgIn.copyPixelsToBuffer(map);
	        //recycle the source bitmap, this will be no longer used.
	        imgIn.recycle();
	        System.gc();// try to force the bytes from the imgIn to be released

	        //Create a new bitmap to load the bitmap again. Probably the memory will be available. 
	        imgIn = Bitmap.createBitmap(width, height, type);
	        map.position(0);
	        //load it back from temporary 
	        imgIn.copyPixelsFromBuffer(map);
	        //close the temporary file and channel , then delete that also
	        channel.close();
	        randomAccessFile.close();

	        // delete the temp file
	        file.delete();

	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } 

	    return imgIn;
	}

}

