package projects.THU.jukify;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * QR Code scanner class
 */
public class QrScanner {
    /**
     * Button
     */
    private FloatingActionButton button;
    /**
     * Activity
     */
    private Activity activity;

    public QrScanner(FloatingActionButton button, Activity activity){
        this.button = button;
        this.activity = activity;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                scanCode();
            }
        });
    }

    /**
     * Scans QR Code
     */
    public void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this.activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan QrCode");
        integrator.setCaptureActivity(ScanCode.class);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

}