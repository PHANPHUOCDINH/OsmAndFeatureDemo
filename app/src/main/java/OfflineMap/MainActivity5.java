package OfflineMap;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.iboism.gpxrecorder.R;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import egolabsapps.basicodemine.offlinemap.Interfaces.GeoPointListener;
import egolabsapps.basicodemine.offlinemap.Utils.MapUtils;
import egolabsapps.basicodemine.offlinemap.Views.OfflineMapView;

public class MainActivity5 extends AppCompatActivity implements MapListener, GeoPointListener, egolabsapps.basicodemine.offlinemap.Interfaces.MapListener {

    OfflineMapView offlineMapView;
    MapUtils mapUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        offlineMapView = findViewById(R.id.map);
        offlineMapView.init(this, this);
    }


    @Override
    public void mapLoadSuccess(MapView mapView, MapUtils mapUtils) {

        // GeoPoint belongs to ISTANBUL heart of the world :)
        this.mapUtils = mapUtils;
     //   offlineMapView.setInitialPositionAndZoom(new GeoPoint(10.806415259053074, 106.6340004719055), 14.5);

        // 41.025135, 28.974101 Galata Tower

    //    Marker marker = new Marker(mapView);
   //     marker.setPosition(new GeoPoint(10.806415259053074, 106.6340004719055));
    //    marker.setIcon(getResources().getDrawable(R.drawable.galata_tower));

        // marker.setImage(drawable);

    //    marker.setTitle("Hello Istanbul");
    //    marker.showInfoWindow();
     //   mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    @Override
    public void mapLoadFailed(String ex) {
        Log.e("ex:", ex);
    }

    @Override
    public void onGeoPointRecieved(GeoPoint geoPoint) {

        //Selected GeoPoint Returns Here

        Toast.makeText(this, geoPoint.toDoubleString(), Toast.LENGTH_SHORT).show();
    }

    public void activateAnimatePicker(View view) {
        if (mapUtils != null)
            offlineMapView.setAnimatedLocationPicker(true, this, mapUtils);
    }


    @Override
    public boolean onScroll(ScrollEvent event) {
        return false;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}