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

Copyright 2016 Vishal Dubey.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
