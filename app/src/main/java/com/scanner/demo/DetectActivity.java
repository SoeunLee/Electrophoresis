package com.scanner.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Created by soeun on 2016. 12. 3..
 */

public class DetectActivity extends Activity {
    private ImageView procImage;
    private Bitmap bitmap;
    private Mat matDst, matSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        procImage = (ImageView) findViewById(R.id.proc_image);

        Intent intent = getIntent();
        String route = intent.getStringExtra("SCANNED_IMAGE");
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        bitmap = BitmapFactory.decodeFile(route, bfo);
        //Bitmap resized = Bitmap.createScaledBitmap(bitmap, imgWidth, imgHeight, true);

        if(route!=null) procImage.setImageBitmap(bitmap);
            Log.e("Soeun", route);

        matSrc = new Mat();
        Utils.bitmapToMat(bitmap, matSrc);
        matDst = matSrc.clone();
        Utils.adaptiveThreshold();
    }
}
