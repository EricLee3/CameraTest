package com.example.ios.cameratest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = null;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private ImageView imageview;
    private boolean inProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        imageview = (ImageView) findViewById(R.id.imageView1);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceListener);
        //surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        findViewById(R.id.btn).setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (camera != null && inProgress == false) {
                            camera.takePicture(null, null, takePicture);
                            inProgress = true;
                        }
                    }
                }
        );

    }


    private Camera.PictureCallback takePicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "샷다 누름 확인");

            if (data != null)
                Log.i(TAG, "JPEG 사진 찍었음!");
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            try {
                String imageSaveUri = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "store picture", "picture stored");
                Uri uri = Uri.parse(imageSaveUri);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            } catch(Exception e)  {
                Log.e("store picture", "storing failure", e);
            }
            imageview.setImageBitmap(bitmap);
            camera.startPreview();
            inProgress = false;
        }
    };

    private SurfaceHolder.Callback surfaceListener = new SurfaceHolder.Callback() {
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            camera.release();
            camera = null;
            Log.i(TAG, "카메라 기능 해제");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            camera = Camera.open();
            Log.i(TAG, "카메라 미리보기 활성");

            try {
                camera.setPreviewDisplay(holder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
            camera.startPreview();
            Log.i(TAG, "카메라 미리보기 활성");
        }
    };
}
