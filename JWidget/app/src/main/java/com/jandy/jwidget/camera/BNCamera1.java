package com.benew.ntt.jreading.arch.widget.vtstory;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.ntt.core.nlogger.NLogger;
import com.visiontalk.vtbrsdk.base.AbstractVTCameraCtrl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import kotlinx.coroutines.GlobalScope;

public class BNCamera1 implements Camera.PreviewCallback {

    private static final String TAG = BNCamera1.class.getSimpleName();

    public static final int MAGIC_TEXTURE_ID = 10;

    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private int mCameraId;
    private SurfaceTexture mSurfaceTexture;
    private AbstractVTCameraCtrl.ICameraPreviewCallback mICameraPreviewCb;
    private CameraHandlerThread mCameraThread;
    private byte[] mPreviewBuff;
    private Camera.Parameters param;

    public BNCamera1(Context context) {
        this.mContext = context;
    }

    public boolean openCamera(int cameraId, int previewWidth, int previewHeight,
                              AbstractVTCameraCtrl.ICameraPreviewCallback previewCallback,
                              SurfaceTexture surfaceTexture) {
        mICameraPreviewCb = previewCallback;
        mSurfaceTexture = surfaceTexture;

        return open(cameraId, previewWidth, previewHeight);
    }

    public boolean openCamera(int cameraId, int previewWidth, int previewHeight,
                              AbstractVTCameraCtrl.ICameraPreviewCallback previewCallback,
                              SurfaceHolder surfaceHolder) {
        mICameraPreviewCb = previewCallback;
        mSurfaceHolder = surfaceHolder;

        return open(cameraId, previewWidth, previewHeight);
    }

    private boolean open(int cameraId, int previewWidth, int previewHeight) {
        mCameraThread = new CameraHandlerThread("Camera open thread");
        synchronized (mCameraThread) {
            long time = System.currentTimeMillis();
            int cameranum = Camera.getNumberOfCameras();
            if (cameranum == 1) {
                cameraId = 0;
            }
            NLogger.d(TAG, "camera size: " + cameranum + " camera id=" + cameraId);
            mCameraId = cameraId;
            NLogger.d(TAG, "cameraId = " + cameraId
                    + ", width=" + previewWidth + ", height=" + previewHeight);

            mCameraThread.openCamera(cameraId);
            NLogger.d(TAG, "openCamera time=" + (System.currentTimeMillis() - time));
        }

        boolean result = initializeCamera(previewWidth, previewHeight, this);
        return result;
    }

    public void closeCamera() {
        NLogger.w(TAG, "");
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mCameraThread != null) {
            mCameraThread.quitSafely();
            mCameraThread = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] frame, Camera camera) {

        if (mICameraPreviewCb != null)
            mICameraPreviewCb.onPreview(frame, this.param.getPreviewSize().width, this.param.getPreviewSize().height);

        if (mCamera != null)
            mCamera.addCallbackBuffer(mPreviewBuff);
    }

    private class CameraHandlerThread extends HandlerThread {
        private Handler mHandler;

        public CameraHandlerThread(String name) {
            super(name);
            start();
            mHandler = new Handler(getLooper());
        }

        private synchronized void notifyCameraOpened() {
            notify();
        }

        void openCamera(int cameraId) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    Log.e(TAG, "####################cameraId = " + cameraId);

                    mCamera = Camera.open(cameraId);
                    notifyCameraOpened();
                }
            });
            try {
                wait();
            } catch (InterruptedException e) {
                NLogger.w(TAG, "wait was interrupted");
            }
        }
    }

    private boolean initializeCamera(int previewWidth, int previewHeight,
                                     Camera.PreviewCallback previewCallback) {

        if (mCamera == null) {
            return false;
        }
        /* Set camera parameters */
        Camera.Parameters params = mCamera.getParameters();
        printPreviewSizes(params);
//        getPreviewSize(params);
        params.setPreviewSize(previewWidth, previewHeight);
        params.setPreviewFormat(ImageFormat.NV21);
//        params.getSupportedPreviewFormats().forEach(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) {
//                NLogger.d(TAG,"getSupportedPreviewFormats = "+integer);
//            }
//        });
//        params.setPreviewFpsRange(4, 12);
//        params.set("jpeg-quality", 90);
        List<String> FocusModes = params.getSupportedFocusModes();
        if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        this.param = params;
        mCamera.setParameters(params);

        NLogger.d(TAG, "initializeCamera --> "
                + "width:" + mCamera.getParameters().getPreviewSize().width
                + ", height:" + mCamera.getParameters().getPreviewSize().height);

        mPreviewBuff = new byte[previewWidth * previewHeight * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8];
        try {
            if (mSurfaceHolder != null) {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } else {
                mCamera.setPreviewTexture(mSurfaceTexture);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


//        if (mSurfaceHolder != null) {
//            mCamera.setPreviewCallback(previewCallback);
//        } else {
//            mCamera.addCallbackBuffer(mPreviewBuff);
//            mCamera.setPreviewCallbackWithBuffer(previewCallback);
//        }

        /* Finally we are ready to start the preview */
        NLogger.d(TAG, "startPreview");
        setCameraDisplayOrientation((Activity) mContext, mCameraId, mCamera);

        mCamera.setPreviewCallback(previewCallback);
        mCamera.addCallbackBuffer(mPreviewBuff);
//        mCamera.setPreviewCallbackWithBuffer(previewCallback);

        mCamera.startPreview();

        return true;
    }

    private void printPreviewSizes(Camera.Parameters params) {
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        int i = 0;
        for (Camera.Size s :
                previewSizes) {
            NLogger.d(TAG, "printPreviewSizes" + i + "(): width=" + s.width + "\theight=" + s.height);
        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

//        Configuration mConfiguration = activity.getResources().getConfiguration(); //获取设置的配置信息
//        int ori = mConfiguration.orientation; //获取屏幕方向

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        NLogger.d(TAG, "result=" + result);
        camera.setDisplayOrientation(result);
    }

    private void getPreviewSize(Camera.Parameters params, int defaultWidth, int defaultHeight) {

        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        double[] a = new double[previewSizes.size()];

        for (int i = 0; i < previewSizes.size(); i++) {
            Camera.Size s = previewSizes.get(i);
            int supportWidth = s.width;
            int supportheight = s.height;

            a[i] = Math.pow(defaultWidth - supportWidth, 2) + Math.pow(defaultHeight - supportheight, 2);
        }

        int minIndex = 0;
        double minA = a[0];
        for (int j = 0; j < a.length; j++) {
            if (a[j] <= minA) {
                minIndex = j;
                minA = a[j];
            }
        }

        int width = previewSizes.get(minIndex).width;
        int height = previewSizes.get(minIndex).height;
        NLogger.e(TAG, "SetPreviewSize: (width) " + width + " x (height) " + height);
        params.setPreviewSize(width, height);
    }
}
