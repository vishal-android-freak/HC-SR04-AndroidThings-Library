package vaf.vishal.hcsr04;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

/**
 * Created by vishal on 18/12/16.
 * Driver for HC-SRC04 Ultrasonic sensor.
 */

public class Hcsr04 implements AutoCloseable {

    private static final String TAG = Hcsr04.class.getSimpleName();
    private Gpio trigGpio, echoGpio;
    private Handler handler = new Handler();

    private long startTime, ellapsedTime;

    public Hcsr04(String trigPin, String echoPin) throws IOException {
        try {
            PeripheralManagerService service = new PeripheralManagerService();
            trigGpio = service.openGpio(trigPin);
            echoGpio = service.openGpio(echoPin);
            configureGpio(trigGpio, echoGpio);
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public void close() throws Exception {
        handler.removeCallbacks(startTrigger);
        echoGpio.unregisterGpioCallback(gpioCallback);
        try {
            trigGpio.close();
            echoGpio.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureGpio(Gpio trigGpio, Gpio echoGpio) throws IOException {
        try {
            trigGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            echoGpio.setDirection(Gpio.DIRECTION_IN);

            trigGpio.setActiveType(Gpio.ACTIVE_HIGH);
            echoGpio.setActiveType(Gpio.ACTIVE_HIGH);
            echoGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            echoGpio.registerGpioCallback(gpioCallback);
            handler.post(startTrigger);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable startTrigger = new Runnable() {
        @Override
        public void run() {
            try {
                trigGpio.setValue(!trigGpio.getValue());

                handler.postDelayed(startTrigger, (long)0.001);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


    public float[] getProximityDistance() {
        return new float[] { (float)(17.150 * ellapsedTime) };
    }

    private GpioCallback gpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                if (!gpio.getValue()) {
                    startTime = SystemClock.elapsedRealtime();
                } else {
                    ellapsedTime = SystemClock.elapsedRealtime() - startTime;
                    getProximityDistance();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.d("ERROR", "GPIO CALLBACK ERROR");
        }
    };
}
