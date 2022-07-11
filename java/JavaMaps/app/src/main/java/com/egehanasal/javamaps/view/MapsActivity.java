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

            /*
            // Opsiyonel (Uygulama kullanılırkenki son konumdan başlayacak harita, kullanıcı offline olduğunda uygulamayı
            // açarsa işe yarayabilecek bir feature)
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
                    // İzin verildi ama tekrar kontrol etmek gerekiyor
                    if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener);

                        /*
                        // Opsiyonel (Uygulama kullanılırkenki son konumdan başlayacak harita, kullanıcı offline olduğunda uygulamayı
                        // açarsa işe yarayabilecek bir feature)
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(lastLocation != null) {
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f));
                        }
                         */
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

        // Önceden koyulan marker'ı kaldırıyor yeni marker koymak istediğimizde
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));

        selectedLatitude = latLng.latitude;
        selectedLongitude = latLng.longitude;

    }

    public void save(View view) {
        Place place = new Place(binding.placeNameText.getText().toString(), selectedLatitude, selectedLongitude);

        /**
         * THREADS
         * Main (UI): Ağır işlemler yapılırsa kullanıcı arayüzünü bloklayabilir uygulamayı çökertebilir
         * Default: (CPU Intensive): Arka planda çalışan yoğun işlemler burada yapılır
         * IO (network, database)
         */

        // placeDao.insert(place).subscribeOn(Schedulers.io()).subscribe();
        // Disposable kullanmak memory'i rahatlatmaya yardımcı olacak. İşlem bittikten sonra çöpe atıyoruz
        // O yüzden üstteki kodu comment'leyip alttakini yazıyorum.
        compositeDisposable.add(placeDao.insert(place).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this :: handleResponse)); // handleResponse methoduna referans veriliyor.
        // IO thread'inde çalışacak fakat Main thread'de gözlemleyeceğiz. O yüzden observeOn methodu kullanıldı.
        // Save'e bastıktan sonra intent, fonskiyonun devamına yazılabilirdi fakat subscribe'ın içinde handleResponse demek de
        // alternatif bir çözüm.
    }

    private void handleResponse() {
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Bütün aktiviteleri kapatıp öyle git
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