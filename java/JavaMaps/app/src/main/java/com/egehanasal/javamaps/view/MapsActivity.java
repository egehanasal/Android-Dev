package com.egehanasal.javamaps.view;

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
import android.view.View;
import android.widget.Toast;

import com.egehanasal.javamaps.R;
import com.egehanasal.javamaps.databinding.ActivityMapsBinding;
import com.egehanasal.javamaps.model.Place;
import com.egehanasal.javamaps.roomdb.PlaceDao;
import com.egehanasal.javamaps.roomdb.PlaceDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    LocationManager locationManager;
    LocationListener locationListener;

    ActivityResultLauncher <String> permissionLauncher;

    private static boolean flag = true;

    PlaceDatabase db;
    PlaceDao placeDao;

    double selectedLatitude;
    double selectedLongitude;

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

        db = Room.databaseBuilder(getApplicationContext(), PlaceDatabase.class, "PlacesDatabase").build();
        placeDao = db.placeDao();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                // Uygulama ba??larken konumumuzdan ba??layacak, sonra kameray?? istedi??imiz gibi dola??t??rabilece??iz
                if(MapsActivity.flag){
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
                    MapsActivity.flag = false;
                }
                System.out.println("location: "+ location.toString());
            }
        };

        // ??zin vermemi??se izin iste
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Kullan??c??ya g??stermemiz gerekiyorsa
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(binding.getRoot(), "Permission needed for Maps", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ??zin iste
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();
            }
            else {
                // Kullan??c??ya g??stermemiz gerekmiyorsa
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
        // ??zin verilmi??se lokasyon al
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // Art??k izin oldu??una eminiz,
            mMap.setMyLocationEnabled(true);

            /*
            // Opsiyonel (Uygulama kullan??l??rkenki son konumdan ba??layacak harita, kullan??c?? offline oldu??unda uygulamay??
            // a??arsa i??e yarayabilecek bir feature)
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastLocation != null) {
                LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f));
            }
             */
        }
    }

    private void registerLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result) {
                    // ??zin verildi ama tekrar kontrol etmek gerekiyor
                    if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener);

                        /*
                        // Opsiyonel (Uygulama kullan??l??rkenki son konumdan ba??layacak harita, kullan??c?? offline oldu??unda uygulamay??
                        // a??arsa i??e yarayabilecek bir feature)
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(lastLocation != null) {
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f));
                        }
                         */
                    }
                }
                else {
                    // ??zin verilmedi
                    Toast.makeText(MapsActivity.this, "Permission needed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

        // ??nceden koyulan marker'?? kald??r??yor yeni marker koymak istedi??imizde
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));

        selectedLatitude = latLng.latitude;
        selectedLongitude = latLng.longitude;

    }

    public void save(View view) {
        Place place = new Place(binding.placeNameText.getText().toString(), selectedLatitude, selectedLongitude);

        /**
         * THREADS
         * Main (UI): A????r i??lemler yap??l??rsa kullan??c?? aray??z??n?? bloklayabilir uygulamay?? ????kertebilir
         * Default: (CPU Intensive): Arka planda ??al????an yo??un i??lemler burada yap??l??r
         * IO (network, database)
         */

        // placeDao.insert(place).subscribeOn(Schedulers.io()).subscribe();
        // Disposable kullanmak memory'i rahatlatmaya yard??mc?? olacak. ????lem bittikten sonra ????pe at??yoruz
        // O y??zden ??stteki kodu comment'leyip alttakini yaz??yorum.
        compositeDisposable.add(placeDao.insert(place).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this :: handleResponse)); // handleResponse methoduna referans veriliyor.
        // IO thread'inde ??al????acak fakat Main thread'de g??zlemleyece??iz. O y??zden observeOn methodu kullan??ld??.
        // Save'e bast??ktan sonra intent, fonskiyonun devam??na yaz??labilirdi fakat subscribe'??n i??inde handleResponse demek de
        // alternatif bir ????z??m.
    }

    private void handleResponse() {
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // B??t??n aktiviteleri kapat??p ??yle git
        startActivity(intent);
    }

    public void delete(View view) {
        /*
        compositeDisposable.add(placeDao.delete().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this :: handleResponse));
         */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}