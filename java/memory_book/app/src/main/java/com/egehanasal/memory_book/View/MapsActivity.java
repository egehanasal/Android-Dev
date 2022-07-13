package com.egehanasal.memory_book.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.egehanasal.memory_book.Model.Place;
import com.egehanasal.memory_book.databinding.ActivityMapsBinding;
import com.egehanasal.memory_book.R;
import com.egehanasal.memory_book.room_db.PlaceDao;
import com.egehanasal.memory_book.room_db.PlaceDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    /** YAPILACAKLAR
     *
     * room db oluştur bu activity'de
     * thread'leri düzenle
     */

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    LocationManager locationManager;
    LocationListener locationListener;
    private static boolean flag = true;
    ActivityResultLauncher<String> permissionLauncher;

    Place selectedPlace;

    double selectedLatitude;
    double selectedLongitude;

    PlaceDatabase db;
    PlaceDao placeDao;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerLauncher();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Database is created.
        db = Room.databaseBuilder(getApplicationContext(), PlaceDatabase.class, "PlacesDatabase").build();
        placeDao = db.placeDao();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if(info.equals("new")) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // Uygulama başlarken konumumuzdan başlayacak, sonra kamerayı istediğimiz gibi dolaştırabileceğiz
                    if(MapsActivity.flag){
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
                        MapsActivity.flag = false;
                    }
                    System.out.println("location: "+ location.toString());
                }
            };

            // İzin vermemişse izin iste
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Kullanıcıya göstermemiz gerekiyorsa
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(binding.getRoot(), "Permission needed for Maps", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // İzin iste
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                }
                else {
                    // Kullanıcıya göstermemiz gerekmiyorsa
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }
            // İzin verilmişse lokasyon al
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                // Artık izin olduğuna eminiz,
                mMap.setMyLocationEnabled(true);
            }
        }
        else if(info.equals("old")){
            mMap.clear();
            selectedPlace = (Place) intent.getSerializableExtra("place");
            LatLng latlng = new LatLng(selectedPlace.latitude, selectedPlace.longitude);
            mMap.addMarker(new MarkerOptions().position(latlng).title(selectedPlace.name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f));
            binding.placeNameText.setText(selectedPlace.name);
            binding.memoryText.setText(selectedPlace.memory);
        }
    }

    private void registerLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result) {
                    // İzin verildi ama tekrar kontrol etmek gerekiyor
                    if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener);
                    }
                }
                else {
                    // İzin verilmedi
                    Toast.makeText(MapsActivity.this, "Permission needed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));

        selectedLatitude = latLng.latitude;
        selectedLongitude = latLng.longitude;

        // Camera moves to marker location
        LatLng markerLocation = new LatLng(selectedLatitude, selectedLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, 15f));
    }

    public void save(View view) {
        Place place = new Place(binding.placeNameText.getText().toString(), binding.memoryText.getText().toString(),
                selectedLatitude, selectedLongitude);
        compositeDisposable.add(placeDao.insert(place).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this :: handleResponse));
    }

    public void handleResponse() {
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void delete(View view) {

    }
}