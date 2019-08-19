package com.lefeee.hxeducation;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lefeee.camera.CameraInterface;
import com.lefeee.camera.preview.CameraSurfaceView;
import com.lefeee.util.DisplayUtil;
import com.lefeee.util.FileUtil;
import com.lefeee.util.ImageUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ScanActivity extends AppCompatActivity implements CameraInterface.CamOpenOverCallback, Camera.PreviewCallback {
    private String TAG = "ScanLog";
    CameraSurfaceView surfaceView = null;
    ImageButton shutterBtn;

    LayoutInflater inflater;
    View layout;
    TextView text;
    Toast toast;
    float previewRate = -1f;
    ImageView mQrLineView;
    ScaleAnimation animation;

    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;

    Camera mCamera;
    Boolean ScanState = false;
    RecTask mRecTask;
    Boolean isRecing = false;

    Thread myScanThread;

    private void initUI() {
        surfaceView = (CameraSurfaceView) findViewById(R.id.camera_surfaceview);
        shutterBtn = (ImageButton) findViewById(R.id.btn_shutter);

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        // 添加扫描动画
        mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
        mQrLineView.setVisibility(View.INVISIBLE);
        // 扫描线
        animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f); // 创建动画效果
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(3000); // 扫描一次动画的时间
        //mQrLineView.setAnimation(animation);
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void initViewParams() {
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this); //
        surfaceView.setLayoutParams(params);

        ViewGroup.LayoutParams p2 = shutterBtn.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);

        shutterBtn.setLayoutParams(p2);

        inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
        text = (TextView) layout.findViewById(R.id.text);

        toast = new Toast(getApplicationContext());
        // 设置Toast的位置
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);

//        schoolid = UserUtil.getSchoolid();
//        Intent intent = getIntent();
//        gradeid = intent.getStringExtra("gradeid");
//        classid = intent.getStringExtra("classid");

    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Log.d(TAG, "onPreviewFrame");
        if (null != mRecTask) {
            switch (mRecTask.getStatus()) {
                case RUNNING:
                    return;
                case PENDING:
                    mRecTask.cancel(true);
                    break;
                default:
                    break;
            }
        }
        isRecing = true;
        mRecTask = new RecTask(bytes);
        mRecTask.execute((Void) null);
    }

    @Override
    public void cameraHasOpened() {
        // TODO Auto-generated method stub
        Log.d(TAG, "cameraHasOpened 1");
        SurfaceHolder holder = surfaceView.getSurfaceHolder();

        Log.d("0729", "cameraHasOpened 2");
        CameraInterface.getInstance().doStartPreview(holder, previewRate);

        Log.d("0729", "cameraHasOpened 3");
        // mCamera.setPreviewCallback(this);
    }


    /*开启一个线程分析数据 */
    private class RecTask extends AsyncTask<Void, Void, Void> {

        private byte[] mData;

        // 构造函数
        RecTask(byte[] data) {
            this.mData = data;
        }

        @Override
        protected Void doInBackground(Void... params) {
            isRecing = true;
            //TODO Auto-generated method stub

            Camera.Size size = mCamera.getParameters().getPreviewSize(); //获取预览大小
            final int w = size.width; //宽度
            final int h = size.height;

            Log.d(TAG ,"raw length: " + mData.length);

            final YuvImage image = new YuvImage(mData, ImageFormat.NV21, w, h, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);
            if (!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
                return null;
            }
            byte[] tmp = os.toByteArray();
            Log.d(TAG ,"jpeg length: " + tmp.length);
            Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
            Bitmap rotaBitmap = ImageUtil.getRotateBitmap(bmp, 90.0f);
            FileUtil.saveBitmap(rotaBitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rotaBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            String strBase64 = Base64.encodeToString(data, Base64.DEFAULT);
            Log.d(TAG ,"strBase64 length: " + strBase64.length());

            ApiCall apiCall = new ApiCall();
//            apiCall.ImagePost("hehehehe");
            apiCall.ImagePost(strBase64);

            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
                bmp = null;
            }

            if (rotaBitmap != null && !rotaBitmap.isRecycled()) {
                rotaBitmap.recycle();
                rotaBitmap = null;
            }

            System.gc();

            isRecing = false;
            return null;
        }
    }


    private class BtnListeners implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_shutter:
                    if (ScanState) {
                        mQrLineView.setVisibility(View.INVISIBLE);
                        mQrLineView.clearAnimation();
                        ScanState = false;
                        shutterBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_camera_all_click));
                    } else {
                        mQrLineView.setVisibility(View.VISIBLE);
                        mQrLineView.setAnimation(animation);
                        shutterBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_camera_all));
                        ScanState = true;
                    }

                    break;
                default:
                    break;
            }
        }

    }

    // 扫描线程,2s一次
    class ScanThread implements Runnable {
        public void run() {
            Log.d(TAG, "ScanThread");
            // TODO Auto-generated method stub

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (null != mCamera && CameraInterface.getInstance().getPreviewingState() && ScanState
                            && !isRecing) {
                        // myCamera.autoFocus(myAutoFocusCallback);
                        mCamera.setOneShotPreviewCallback(ScanActivity.this);
                        // Log.i(tag, "setOneShotPreview...");
                    }
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initUI();
        initViewParams();

        //open the camera
        Thread openThread = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                CameraInterface.getInstance().doOpenCamera(null);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                CameraInterface.getInstance().doStopCamera();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mCamera = CameraInterface.getInstance().doOpenCamera(ScanActivity.this);
            }
        };
        openThread.start();

        shutterBtn.setOnClickListener(new BtnListeners());

        myScanThread = new Thread(new ScanThread());
        myScanThread.start();

    }
}
