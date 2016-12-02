package com.scanner.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by soeun on 2016. 11. 30..
 */

public class ImageActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        TextView textView = (TextView) findViewById(R.id.text);
        Intent intent = getIntent();
        String path = intent.getStringExtra("FILE_ROUTE");
        textView.setText(path);

        BitmapFactory.Options bo = new BitmapFactory.Options();
        bo.inSampleSize = 2;
        Bitmap bmp = BitmapFactory.decodeFile(path, bo);
//저장돼있던 비트맵 불러왔음
        ImageView imageView = (ImageView)findViewById(R.id.image_view);
        imageView.setImageBitmap(bmp);
//불러온 비트맵을 이미지뷰에 셋팅
    }
}