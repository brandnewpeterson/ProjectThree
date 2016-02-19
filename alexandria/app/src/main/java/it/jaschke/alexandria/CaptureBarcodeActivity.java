package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by brandnewpeterson on 2/14/16.
 */
public class CaptureBarcodeActivity extends ActionBarActivity {

    private SurfaceView cameraView;
    private BarcodeDetector barDetector;
    private CameraSource camSource;
    private TextView barCodeInfo;
    private RelativeLayout rl;
    private ProgressBar scanPB1;

    @Override
    protected void onPause() {
        super.onPause();

        if (camSource != null) {
            camSource.stop();
            camSource.release();
            camSource = null;
        }

        if(cameraView != null){
            cameraView.setVisibility(View.GONE);
        }


        if (barDetector != null) {
            barDetector.release();
            barDetector = null;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_barcode);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();  //Make sure you are extending ActionBarActivity
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.scan_actionbar_title));

        barCodeInfo = (TextView) findViewById(R.id.barCodeScannerText);
        scanPB1 = (ProgressBar) findViewById(R.id.scanProgressBar);
        scanPB1.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);


    }

    @Override
    protected void onResume() {
        super.onResume();

        barCodeInfo.setText(getResources().getString(R.string.scan_scanning_text));

        barDetector = Utilities.setupDetector(getApplicationContext());
        camSource = Utilities.setupCameraSource(getApplicationContext(), barDetector);

        cameraView = (SurfaceView)findViewById(R.id.camera_view);
        cameraView.setVisibility(View.VISIBLE);


        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    camSource.start(cameraView.getHolder());

                    //Set the proper auto focus mode
                    Camera cam = getCamera(camSource);
                    List<String> supportedFocusModes = cam.getParameters().getSupportedFocusModes();
                    boolean hasBestAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    boolean hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);

                    Camera.Parameters params = cam.getParameters();
                    if (hasBestAutoFocus) {
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    } else if (hasAutoFocus) {
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    }
                    cam.setParameters(params);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });


        barDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    String isbn = barcodes.valueAt(0).displayValue;

                    scanPB1.setVisibility(View.INVISIBLE);
                    barCodeInfo.setText(isbn);
                    barCodeInfo.setTypeface(null, Typeface.BOLD);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", isbn);
                    setResult(Activity.RESULT_OK, returnIntent);

                    finish();

                }
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Method copied from here: https://gist.github.com/Gericop/364dd12b105fdc28a0b6
    //Standard autofocus mode supported by camera source (FOCUS_MODE_CONTINUOUS_VIDEO does not work well

    public static Camera getCamera(@NonNull CameraSource cameraSource) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        return camera;
                    }

                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

        return null;
    }
}
