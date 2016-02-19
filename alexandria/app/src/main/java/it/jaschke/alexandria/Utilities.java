package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by brandnewpeterson on 2/15/16.
 */
public class Utilities {

    public static BarcodeDetector setupDetector(Context context){
        BarcodeDetector detector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        return detector;
    }

    public static CameraSource setupCameraSource(Context context, BarcodeDetector detector){

        CameraSource cameraSource = new CameraSource.Builder(context, detector)
                .setAutoFocusEnabled(true)
                        //.setAutoFocusEnabled() //This method does not work well (uses CONTINOUS_VIDEO_MODE)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(6.0f)
                .build();

        return cameraSource;
    }

    public static void hideKeyboard(Activity activity) {
        //activity.getWindow().setSoftInputMode(
        //        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        //);

        InputMethodManager inputManager =
                (InputMethodManager) activity.
                        getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null){
            inputManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    public static boolean isConnected(Activity activity) throws InterruptedException, IOException
    {
        ConnectivityManager cm =
                (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static String validateISBNInput(String isbn) {//Method to evaluate all conceivable error cases for ISBN input.

        String result = null;

        if (isbn.length() == 10 && !isbn.startsWith("978")) { //Special case 9-digit ISBNs, which are allowed
            isbn = "978" + isbn;
        }

        if (isbn != null && isbn.length() == 13 && isbn.matches("[0-9]+")) { //Make sure input or padded result is 13 chars, all numeric.

            result = isbn;

        }

        return result;
    }


}
