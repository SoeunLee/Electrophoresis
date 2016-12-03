package com.scanner.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by soeun on 2016. 12. 3..
 */

public class DetectActivity extends Activity {
    private ImageView procImage;
    private Button histButton;
    private Button originButton;
    private Bitmap bitmap, bitmapOrigin;
    private Mat mat, matOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        procImage = (ImageView) findViewById(R.id.proc_image);
        histButton = (Button) findViewById(R.id.hist_button);
        histButton.setOnClickListener(new histButtonOnClickListener());
        originButton = (Button) findViewById(R.id.origin_button);
        originButton.setOnClickListener(new originButtonOnClickListener());

        Intent intent = getIntent();
        String route = intent.getStringExtra("SCANNED_IMAGE");
        //BitmapFactory.Options bfo = new BitmapFactory.Options();
        //bfo.inSampleSize = 2;
        bitmap = BitmapFactory.decodeFile(route);
        bitmapOrigin = bitmap.copy(bitmap.getConfig(), true);
        //Bitmap resized = Bitmap.createScaledBitmap(bitmap, imgWidth, imgHeight, true);

        if(route!=null) procImage.setImageBitmap(bitmap);
        procImage.invalidate();
            Log.i("Soeun", route);

        mat = new Mat (bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
        matOrigin = mat.clone();
    }
    private class histButtonOnClickListener implements View.OnClickListener {
        private boolean isFirst;
        private int blockSize;
        private int constant;

        histButtonOnClickListener(){

            blockSize = 63;
            constant = 10;
        }
        @Override
        public void onClick(View v) {
            Imgproc.medianBlur(mat, mat, 9);
            Imgproc.blur(mat, mat, new Size(3, 3));
 //         Imgproc.equalizeHist(mat, mat);
            Imgproc.adaptiveThreshold(mat, mat, 255,
                    Imgproc.ADAPTIVE_THRESH_MEAN_C,
                    Imgproc.THRESH_BINARY, blockSize, constant);

            Utils.matToBitmap(mat, bitmap);
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            procImage.setImageBitmap(bitmap);
            procImage.invalidate();

        }
    }
    private class originButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            bitmap = bitmapOrigin.copy(bitmapOrigin.getConfig(), true);
            mat = matOrigin.clone();
            procImage.setImageBitmap(bitmap);
            procImage.invalidate();
        }
    }
}
