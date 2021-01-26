package FavoritePlace;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.iboism.gpxrecorder.R;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MainActivity3 extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener, SettingDialogFragment.OnSwitchListener {
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private MapboxMap mapboxMap;
    ImageButton button;
    public static boolean isFavorite = false;
    List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
    List<LatLng> latLngList=new ArrayList<>();
    Button btnAdd;
    SettingDialogFragment dialog;
    FavoriteInfoDialogFragment dialog2;
    Marker selecting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main3);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        btnAdd=findViewById(R.id.btnAdd);
        latLngList.add(new LatLng(10.806415259053074,106.6340004719055));
//        symbolLayerIconFeatureList.add(Feature.fromGeometry(
//                Point.fromLngLat(106.6340004719055, 10.806415259053074)));
//        symbolLayerIconFeatureList.add(Feature.fromGeometry(
//                Point.fromLngLat(106.64492242701414, 10.803696294722014)));
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {

            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                addDestinationIconSymbolLayer(style);


                mapboxMap.addOnMapClickListener(MainActivity3.this);
                button = findViewById(R.id.menuButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog = new SettingDialogFragment(MainActivity3.this);
                        dialog.show(getSupportFragmentManager(), "setting dialog");
                    }
                });
            }

        });
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @Override
    public boolean onMapClick(@NonNull final LatLng point) {
        if(!isFavorite) {
            Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
            GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    latLngList.add(new LatLng(point.getLatitude(), point.getLongitude()));
                    btnAdd.setVisibility(View.INVISIBLE);
                    mapboxMap.clear();
                    dialog2 = new FavoriteInfoDialogFragment();
                    dialog2.show(getSupportFragmentManager(), "setting2 dialog");
                }
            });
            if (source != null) {
                source.setGeoJson(Feature.fromGeometry(destinationPoint));
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Activate the MapboxMap LocationComponent to show user location
// Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "This app needs location permissions to show its functionality.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, "You didn't grant location permissions.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();

    }

    void showFavoriteLocation() {


//            mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
//
//// Add the SymbolLayer icon image to the map style
//                    .withImage("ICON_ID", BitmapFactory.decodeResource(
//                            MainActivity.this.getResources(), R.drawable.icon_favorite))
//
//// Adding a GeoJson source for the SymbolLayer icons.
//                    .withSource(new GeoJsonSource("SOURCE_ID",
//                            FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
//
//// Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
//// marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
//// the coordinate point. This is offset is not always needed and is dependent on the image
//// that you use for the SymbolLayer icon.
//                    .withLayer(new SymbolLayer("LAYER_ID", "SOURCE_ID")
//                            .withProperties(
//                                    iconImage("ICON_ID"),
//                                    iconAllowOverlap(true),
//                                    iconIgnorePlacement(true)
//                            )
//                    ), new Style.OnStyleLoaded() {
//                @Override
//                public void onStyleLoaded(@NonNull Style style) {
//
//// Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.
//
//
//                }
//            });

        IconFactory iconFactory = IconFactory.getInstance(MainActivity3.this);

        Icon icon = iconFactory.fromResource(R.drawable.icon_favorite);

        for(LatLng l:latLngList) {
            mapboxMap.addMarker(new MarkerOptions().setIcon(icon)
                    .position(l)
                    .title("Eiffel Tower")).setId(System.currentTimeMillis());
        }
        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                finish();
                return true;
            }
        });
    }

     void hideFavoriteLocation()
     {
//         mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
//
//             @Override
//             public void onStyleLoaded(@NonNull Style style) {
//                 enableLocationComponent(style);
//                 addDestinationIconSymbolLayer(style);
//
//                 mapboxMap.addOnMapClickListener(MainActivity.this);
//                 button = findViewById(R.id.menuButton);
//                 button.setOnClickListener(new View.OnClickListener() {
//                     @Override
//                     public void onClick(View v) {
//                         SettingDialogFragment dialog = new SettingDialogFragment(MainActivity.this);
//                         dialog.show(getSupportFragmentManager(), "setting dialog");
//                     }
//                 });
//             }
//
//         });
         mapboxMap.clear();
     }

    @Override
    public void onOn() {
        showFavoriteLocation();
        dialog.dismiss();
        isFavorite=true;
    }

    @Override
    public void onOff() {
        hideFavoriteLocation();
        btnAdd.setVisibility(View.INVISIBLE);
        dialog.dismiss();
        isFavorite=false;
    }
}