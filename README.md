# HC-SR04-AndroidThings-Library
HC-SR04 Ultrasonic UserSensor library for Android Things

![](HC-SR04-Ultrasonic-Sensor.jpg?raw=true "Sensor")

This is WIP. Expect things to work in a different way or not to work at all. Pull requests for betterments and fixes are welcome.

##How to use the library

1. Clone the repository `git clone https://github.com/vishal-android-freak/HC-SR04-AndroidThings-Library.git`
2. Clone the sample project template `https://github.com/androidthings/new-project-template.git` for android things or create your own project in Android studio following https://developer.android.com/things.
3. In Android Studio, go to File > New > Import Module.
4. Browse for the **HC-SR04-AndroidThings-Library** cloned folder.
5. In the 'Module name' section type `:hcsr04`.
6. Right click on the **app** folder and select 'Open Module Settings'.
7. Go to the 'Dependencies' section and click on the '+' and select 'Module dependency'.
8. Select ':hcsr04' and click Ok. Done!

##Sample Usage

```java

import vaf.vishal.Hcsr04;

.....

// Access the environmental sensor directly:

Hcsr04 hcsr04;

try {
    hcsr04 = new Hcsr04("BCM23", "BCM25");
} catch(IOException e) {
  // couldn't configure the device...
}

//Read ultrasonic sensor values in centimeters.

try {
    float[] values = hcsr04.getProximityDistance();
    float distanceInCm = values[0];
} catch(IOException e) {
  //error reading values.
}

//close the ultrasonic sensor after finished.
try {
    hcsr04.close();
} catch(IOException e) {
  //error closing sensor.
}

```

If you need to read sensor values continuously, you can register the Hcsr04 with the system and listen for sensor values using the [Sensor APIs](https://developer.android.com/guide/topics/sensors/sensors_overview.html)

```java

SensorManager mSensorManager = getSystemService(Context.SENSOR_SERVICE);
SensorEventListener mListener = ...;
Hcsr04UltrasonicDriver mSensorDriver;

mSensorManager.registerDynamicSensorCallback(new SensorManager.DynamicSensorCallback() {
    @Override
    public void onDynamicSensorConnected(Sensor sensor) {
        if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
            mSensorManager.registerListener(mListener, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
});

try {
    mSensorDriver = new Hcsr04UltrasonicDriver("BCM23", "BCM25");
    mSensorDriver.register();
} catch (IOException e) {
    // Error configuring sensor
}

// Unregister and close the driver when finished:

mSensorManager.unregisterListener(mListener);
mSensorDriver.unregister();
try {
    mSensorDriver.close();
} catch (IOException e) {
    // error closing sensor
}

```

#License

MIT License

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
