package io.mapwize.mapwizeuicomponents;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import net.crowdconnected.androidcolocator.CoLocator;
import net.crowdconnected.androidcolocator.LocationCallback;
import net.crowdconnected.androidcolocator.connector.LocationResponse;
import net.crowdconnected.androidcolocator.messaging.ClientMessagingProtocol;

import java.util.List;

import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.core.IndoorLocationProvider;

import static android.content.Context.SENSOR_SERVICE;

public class ColocatorIndoorLocationProvider extends IndoorLocationProvider {
    private boolean isStarted = false;
    private Double headingOffset;
    private double lat, lng, error;
    private boolean hadLocation = false;
    private Float bearing;
    private SensorManager sensorManager;
    private SensorCallback sensorCallback;
    private Activity activity;

    public ColocatorIndoorLocationProvider(Activity activity) {
        this.activity = activity;
    }

    public void setIndoorLocation(IndoorLocation indoorLocation) {
        this.dispatchIndoorLocationChange(indoorLocation);
    }

    public boolean supportsFloor() {
        return true;
    }

    public void start() {
        this.sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        sensorCallback = new ColocatorIndoorLocationProvider.SensorCallback();
        sensorManager.registerListener(sensorCallback, sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
        CoLocator.instance().registerLocationListener(new LocationCallback() {
            @Override
            public void onLocationReceived(LocationResponse clientLocationResponse) {

            }

            @Override
            public void onLocationsReceived(List<LocationResponse> list) {
                System.out.println("Messages Received");
                for (LocationResponse response : list) {
                    hadLocation = true;
                    System.out.println(response.getLatitude() + "," + response.getLongitude() + "," + response.getError());
                    if (response.getHeadingOffset() != 0) {
                        headingOffset = response.getHeadingOffset();
                    } else {
                        headingOffset = null;
                        bearing = null;
                    }
                    lat = response.getLatitude();
                    lng = response.getLongitude();
                    error = response.getError();
                    Location location = new Location("");
                    location.setLatitude(lat);
                    location.setLongitude(lng);
                    location.setAccuracy((float) error);
                    if (bearing != null) {
                        location.setBearing(bearing);
                    }
                    dispatchIndoorLocationChange(new IndoorLocation(location, 0D));
                }
            }
        });
        this.isStarted = true;
    }

    public void stop() {
        this.isStarted = false;
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    private class SensorCallback implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            double x = sensorEvent.values[0];
            double y = sensorEvent.values[1];
            double z = sensorEvent.values[2];
            double cos = sensorEvent.values[3];
            double u = 1.0 - 2.0 * (y * y + z * z);
            double v = 2.0 * (cos * z + x * y);
            double azimuth_rad = Math.atan2(v, u);
            double azimuth_deg = azimuth_rad * 180.0 / Math.PI;
            if (hadLocation && headingOffset != null) {
                bearing = (float) ((azimuth_deg + headingOffset - 90.0) * -1.0);
                Location location = new Location("");
                location.setLatitude(lat);
                location.setLongitude(lng);
                location.setAccuracy((float) error);
                location.setBearing(bearing);
                dispatchIndoorLocationChange(new IndoorLocation(location, 0D));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}
