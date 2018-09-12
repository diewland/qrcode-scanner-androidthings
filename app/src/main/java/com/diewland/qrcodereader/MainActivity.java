package com.diewland.qrcodereader;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.ht16k33.Ht16k33;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import java.io.IOException;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {

    private String TAG = "DOOR";
    private boolean process_lock;

    private LinearLayout resultLayout;
    private QRCodeReaderView qrCodeReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        process_lock = false;

        resultLayout = (LinearLayout)findViewById(R.id.ll_result);
        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(false);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        if(!process_lock){
            // verify success
            process_lock = true;

            try {
                playAnimation();
            } catch (IOException e) {
                e.printStackTrace();
            }
            resultLayout.setBackgroundColor(Color.GREEN);
            Log.d(TAG, text);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        stopAnimation();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    resultLayout.setBackgroundColor(Color.BLACK);
                    process_lock =  false;
                }
            }, 1*1000);
        }
        else {
            Log.d(TAG, "process is locked.");
        }

    }

    private void playAnimation() throws IOException {
        // text
        AlphanumericDisplay segment = RainbowHat.openDisplay();
        segment.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX);
        segment.display("OPEN");
        segment.setEnabled(true);
        segment.close();

        // rainbow lights
        /*
        Apa102 ledstrip = RainbowHat.openLedStrip();
        ledstrip.setBrightness(31);
        int[] rainbow = new int[RainbowHat.LEDSTRIP_LENGTH];
        for (int i = 0; i < rainbow.length; i++) {
            rainbow[i] = Color.HSVToColor(255, new float[]{i * 360.f / rainbow.length, 1.0f, 1.0f});
        }
        ledstrip.write(rainbow);
        ledstrip.close();
        */
    }

    private void stopAnimation() throws IOException {
        // text
        AlphanumericDisplay segment = RainbowHat.openDisplay();
        segment.setBrightness(Ht16k33.HT16K33_BRIGHTNESS_MAX);
        segment.display("");
        segment.setEnabled(true);
        segment.close();

        // rainbow lights
        /*
        Apa102 ledstrip = RainbowHat.openLedStrip();
        ledstrip.setBrightness(0);
        int[] rainbow = new int[RainbowHat.LEDSTRIP_LENGTH];
        for (int i = 0; i < rainbow.length; i++) {
            rainbow[i] = Color.HSVToColor(255, new float[]{i * 360.f / rainbow.length, 1.0f, 1.0f});
        }
        ledstrip.write(rainbow);
        ledstrip.close();
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

}
