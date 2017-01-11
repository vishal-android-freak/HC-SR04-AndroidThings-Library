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

import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.google.android.things.userdriver.UserDriverManager;
import com.google.android.things.userdriver.UserSensor;
import com.google.android.things.userdriver.UserSensorDriver;
import com.google.android.things.userdriver.UserSensorReading;

import java.io.IOException;

/**
 * Created by vishal on 18/12/16.
 */

public class Hcsr04UltrasonicDriver implements AutoCloseable {

    private static final String TAG = Hcsr04UltrasonicDriver.class.getSimpleName();
    private static final int DRIVER_VERSION = 1;
    private static final String DRIVER_NAME = "HC-SR04 Ultrasonic Sensor";

    private UserSensor userSensor;
    private Hcsr04 device;

    public Hcsr04UltrasonicDriver(String trigPin, String echoPin) throws IOException {
        device = new Hcsr04(trigPin, echoPin);
    }

    @Override
    public void close() throws Exception {
        unregister();
        if (device != null) {
            try {
                device.close();
            } finally {
                device = null;
            }
        }
    }

    public void register() {
        if (device == null) {
            throw new IllegalStateException("cannot registered closed driver");
        }
        if (userSensor == null) {
            userSensor = build(device);
            UserDriverManager.getManager().registerSensor(userSensor);
        }
    }

    public void unregister() {
        if (userSensor != null) {
            UserDriverManager.getManager().unregisterSensor(userSensor);
            userSensor = null;
        }
    }

    private static UserSensor build(final Hcsr04 hcsr04) {
        return UserSensor.builder()
                .setName(DRIVER_NAME)
                .setVersion(DRIVER_VERSION)
                .setType(Sensor.TYPE_PROXIMITY)
                .setDriver(new UserSensorDriver() {
                    @Override
                    public UserSensorReading read() throws IOException {
                        float[] distance = hcsr04.getProximityDistance();
                        return new UserSensorReading(distance);
                    }
                })
                .build();
    }
}
