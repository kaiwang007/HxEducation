package com.lefeee.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;


import com.lefeee.util.CamParaUtil;
import com.lefeee.util.FileUtil;
import com.lefeee.util.ImageUtil;

import java.io.IOException;
import java.util.List;

public class CameraInterface {
    private static final String TAG = "cw_cam";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private float mPreviwRate = -1f;
    private static CameraInterface mCameraInterface;
    private boolean isOpening = false;

    public interface CamOpenOverCallback {
        public void cameraHasOpened();
    }

    private CameraInterface() {

    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    /**
     * 打开Camera
     * @param callback
     */
    public Camera doOpenCamera(CamOpenOverCallback callback) {
//		Log.i(TAG, "Camera open....");
        mCamera = Camera.open();
//		Log.i(TAG, "Camera open over....");
        if (callback != null)
            callback.cameraHasOpened();
        isOpening = true;
        return mCamera;
    }


    /**
     * 开始预览
     * @param holder
     * @param previewRate
     */
    public void doStartPreview(SurfaceHolder holder, float previewRate) {
//		Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);
            mParams.setPreviewFormat(ImageFormat.NV21);
            CamParaUtil.getInstance().printSupportPictureSize(mParams);
            CamParaUtil.getInstance().printSupportPreviewSize(mParams);

            Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(mParams.getSupportedPictureSizes(),
                    previewRate, 800);
            mParams.setPictureSize(pictureSize.width, pictureSize.height);

            Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(mParams.getSupportedPreviewSizes(),
                    previewRate, 800);
            mParams.setPreviewSize(previewSize.width, previewSize.height);

            mCamera.setDisplayOrientation(90);

            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-picture")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (focusModes.contains("auto")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            } else if (focusModes.contains("macro")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
            } else if (focusModes.contains("infinity ")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            } else if (focusModes.contains("fixed ")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            } else if (focusModes.contains("edof"))
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_EDOF);
            //	mCamera.cancelAutoFocus();
            mCamera.setParameters(mParams);

            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                mCamera.cancelAutoFocus();
                // mCamera.setPreviewCallback(this);
                // Camera.setOneShotPreviewCallback(this)
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            isPreviewing = true;
            mPreviwRate = previewRate;

            mParams = mCamera.getParameters();
        }
    }

    /**
     * ֹ停止Camera
     */
    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mPreviwRate = -1f;
            mCamera.release();
            isOpening = false;
            mCamera = null;
        }

    }

    // 获取Camera预览状态
    public boolean getPreviewingState() {
        if (isPreviewing)
            return true;
        else
            return false;
    }

    //
    public boolean IsOpen() {
        if (isOpening)
            return true;
        else
            return false;
    }

    /**
     * 拍照
     */
    public void doTakePicture() {
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    /*Ϊ��ʵ�����յĿ������������ձ�����Ƭ��Ҫ���������ص�����*/
    ShutterCallback mShutterCallback = new ShutterCallback()
            //���Ű��µĻص������������ǿ����������Ʋ��š����ꡱ��֮��Ĳ�����Ĭ�ϵľ������ꡣ
    {
        public void onShutter() {
            // TODO Auto-generated method stub
//			Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    PictureCallback mRawCallback = new PictureCallback()
            // �����δѹ��ԭ���ݵĻص�,����Ϊnull
    {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
//			Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };

    PictureCallback mJpegPictureCallback = new PictureCallback()
            //��jpegͼ�����ݵĻص�,����Ҫ��һ���ص�
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
//			Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data���ֽ����ݣ����������λͼ
                mCamera.stopPreview();
                isPreviewing = false;
            }
            //����ͼƬ��sdcard
            if (null != b) {
                //����FOCUS_MODE_CONTINUOUS_VIDEO)֮��myParam.set("rotation", 90)ʧЧ��
                //ͼƬ��Ȼ������ת�ˣ�������Ҫ��ת��
                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                FileUtil.saveBitmap(rotaBitmap);
            }
            //�ٴν���Ԥ��
            mCamera.startPreview();
            isPreviewing = true;
        }
    };

}
