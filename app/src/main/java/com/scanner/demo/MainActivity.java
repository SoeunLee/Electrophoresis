package com.scanner.demo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 99;
    private ImageButton scanButton;
    private ImageButton detectButton;
    private byte[] imgBytes;
//    private ImageButton cameraButton;
//    private ImageButton mediaButton;
    private ImageView scannedImageView;
    private Context mContext = this;
    private File pictureFile;
    private String fileRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public void onPause(){
        super.onPause();
        // 새로 추가된 이미지를 갤러리에 새로고침 시킨다.
        if (imgBytes!=null) {
//			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//					Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mediaMountUri));


            //this.
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));
        }

    }

    private void init() {
        scanButton = (ImageButton) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new ScanButtonClickListener());
        detectButton = (ImageButton) findViewById(R.id.detectButton);
        detectButton.setOnClickListener(new DetectButtonClickListener());
//        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
//        cameraButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA));
//        mediaButton = (ImageButton) findViewById(R.id.mediaButton);
//        mediaButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_MEDIA));
        scannedImageView = (ImageView) findViewById(R.id.scannedImage);
    }

    private class ScanButtonClickListener implements View.OnClickListener {

        private int preference;

        public ScanButtonClickListener(int preference) {
            this.preference = preference;
        }

        public ScanButtonClickListener() {
        }

        @Override
        public void onClick(View v) {
            startScan(preference);
        }
    }

    protected void startScan(int preference) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private class DetectButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(imgBytes != null) {
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));
                Intent intent = new Intent(MainActivity.this, DetectActivity.class);
                intent.putExtra("SCANNED_IMAGE", fileRoute);////////

                startActivity(intent);
            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                scannedImageView.setImageBitmap(bitmap);
                imgBytes = bitmapToByteArray(bitmap);
                new ImageSaveTask().execute(imgBytes);
//                SystemClock.sleep(50);
//                Log.e("Sleep", "50");
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] bitmapToByteArray( Bitmap bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }

    private Bitmap convertByteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ImageSaveTask extends AsyncTask<byte[],Void,Boolean> {

        /* 실제 작업을 하는 메소드 */
        @Override
        protected Boolean doInBackground(byte[]... data) {

			/*
			 * 이미지에 필터를 입히거나 특정 작업을 처리를 한다
			 */

            // 처리가 끝나면 이미지를 파일로 저장한다
            pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return false;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data[0]);
                fos.close();
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        /* 작업이 끝난후 알림 */
        @Override
        protected void onPostExecute(Boolean isDone){
            if(isDone){
                Toast.makeText(mContext, "Image saved!", Toast.LENGTH_SHORT).show();
                Log.e("CAMERA", fileRoute);
//                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
//                intent.putExtra("FILE_ROUTE", fileRoute);
//                startActivity(intent);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));
            }
        }

        /** 이미지를 저장할 파일 객체를 생성합니다 */
        private File getOutputMediaFile(){
            // SD카드가 마운트 되어있는지 먼저 확인해야합니다
            // Environment.getExternalStorageState() 로 마운트 상태 확인 가능합니다

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "ELCTRPRSS");
            //Toast.makeText(getApplicationContext(), fileRoute, Toast.LENGTH_SHORT).show();
            // 굳이 이 경로로 하지 않아도 되지만 가장 안전한 경로이므로 추천함.

            // 없는 경로라면 따로 생성한다.
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Log.d("Soeun", "failed to create directory");
                    return null;
                }
            }

            // 파일명을 적당히 생성. 여기선 시간으로 파일명 중복을 피한다.
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
            fileRoute = mediaFile.getAbsolutePath();
            Log.i("Soeun", "Saved at"+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));

            addImageToGallery(mediaFile.toString(), mContext);
            return mediaFile;
        }
    }
    public static void addImageToGallery(final String file, final Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, file);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}
