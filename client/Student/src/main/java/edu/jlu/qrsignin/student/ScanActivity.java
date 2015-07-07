package edu.jlu.qrsignin.student;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;

/**
 * @author xubowen92@gmail.com
 */
public class ScanActivity extends Activity {

  public static final int REQ_CODE_CAPTURE = 1;

  public static final String EXTRA_RESULT = "extra.result";

  private SurfaceView cameraView;

  private Camera camera;

  private int displayRotation;

  private int captureSize;

  private final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
      int previewWidth = camera.getParameters().getPreviewSize().width;
      int previewHeight = camera.getParameters().getPreviewSize().height;

      PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, previewWidth,
          previewHeight, (previewWidth - captureSize) / 2, (previewHeight - captureSize) / 2,
          captureSize, captureSize, false);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      Reader reader = new QRCodeReader();
      Result result;
      try {
        result = reader.decode(bitmap);
        String text = result.getText();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, text);
        setResult(REQ_CODE_CAPTURE, intent);
        finish();
      } catch (Exception e) {
        // ignore
      }
    }
  };

  private final SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
      if (camera != null) {
        try {
          camera.setPreviewDisplay(holder);
        } catch (IOException e) {
          releaseCamera();
        }
      }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      if (camera != null) {
        Camera.Parameters params = camera.getParameters();
        params.setPictureFormat(ImageFormat.JPEG);
        switch (displayRotation) {
          case Surface.ROTATION_0:
            params.setPreviewSize(height, width);
            camera.setDisplayOrientation(90);
            break;
          case Surface.ROTATION_90:
            params.setPreviewSize(width, height);
            camera.setDisplayOrientation(0);
            break;
          case Surface.ROTATION_180:
            params.setPreviewSize(height, width);
            camera.setDisplayOrientation(270);
            break;
          case Surface.ROTATION_270:
            params.setPreviewSize(width, height);
            camera.setDisplayOrientation(180);
            break;
        }
        Camera.Size size = params.getSupportedPreviewSizes().get(0);
        params.setPreviewSize(size.width, size.height);
        camera.setPreviewCallback(previewCallback);
        camera.setParameters(params);
        camera.startPreview();
      }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
      if (camera != null) {
        camera.stopPreview();
        releaseCamera();
      }
    }
  };

  public static void launchForResult(Activity activity) {
    Intent intent = new Intent(activity, ScanActivity.class);
    activity.startActivityForResult(intent, REQ_CODE_CAPTURE);
  }

  public static void launchForResult(Fragment fragment) {
    Intent intent = new Intent(fragment.getActivity(), ScanActivity.class);
    fragment.startActivityForResult(intent, REQ_CODE_CAPTURE);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scan);
    cameraView = (SurfaceView) findViewById(R.id.camera_view);
    cameraView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (camera != null) {
          // 点击相机的时候自动对焦
          camera.autoFocus(null);
        }
      }
    });
    captureSize = getResources().getDimensionPixelSize(R.dimen.capture_area_size);
    // 创建一个相机成像的展示区域
    SurfaceHolder holder = cameraView.getHolder();
    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    holder.addCallback(callback);
  }

  @Override
  protected void onResume() {
    super.onResume();
    camera = Camera.open();
  }

  @Override
  protected void onPause() {
    super.onPause();
    releaseCamera();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    displayRotation = getWindowManager().getDefaultDisplay().getRotation();
  }

  private void releaseCamera() {
    if (camera != null) {
      camera.setPreviewCallback(null);
      camera.release();
      camera = null;
    }
  }
}
