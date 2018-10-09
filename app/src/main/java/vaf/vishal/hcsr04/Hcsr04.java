/** MIT License

Copyright (c) 2017 Vishal Dubey

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

**/


package vaf.vishal.hcsr04;

import android.os.Handler;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by vishal on 18/12/16.
 * Driver for HC-SRC04 Ultrasonic sensor.
 */

public class Hcsr04 implements AutoCloseable {

    private static final String TAG = Hcsr04.class.getSimpleName();
    private Gpio trigGpio, echoGpio;
    private Handler handler = new Handler();
    private static final int pauseInMicro = 10;

    private long startTime, ellapsedTime;
    private float distanceInCm;

    public Hcsr04(String trigPin, String echoPin) throws IOException {
        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            trigGpio = manager.openGpio(trigPin);
            echoGpio = manager.openGpio(echoPin);
            configureGpio(trigGpio, echoGpio);
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public void close() throws Exception {
        handler.removeCallbacks(startTrigger);
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
                busyWaitMicros(pauseInMicro);
                trigGpio.setValue(!trigGpio.getValue());
                while (!echoGpio.getValue())
                    startTime = System.nanoTime();
                while (echoGpio.getValue())
                    ellapsedTime = System.nanoTime() - startTime;
                ellapsedTime = TimeUnit.NANOSECONDS.toMicros(ellapsedTime);
                distanceInCm = ellapsedTime / 58;
                handler.postDelayed(startTrigger, 60);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


    public float[] getProximityDistance() {
        return new float[] {distanceInCm};
    }

    public static void busyWaitMicros(long micros){
        long waitUntil = System.nanoTime() + (micros * 1_000);
        while(waitUntil > System.nanoTime()){
            ;
        }
    }

}
