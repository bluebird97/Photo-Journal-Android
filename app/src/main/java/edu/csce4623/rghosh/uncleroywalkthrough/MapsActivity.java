package edu.csce4623.rghosh.uncleroywalkthrough;

// database imports

import edu.csce4623.rghosh.uncleroywalkthrough.data.PhotoEntry;
import edu.csce4623.rghosh.uncleroywalkthrough.data.PhotoEntryDao;
import edu.csce4623.rghosh.uncleroywalkthrough.data.PhotoEntryDatabase;
import edu.csce4623.rghosh.uncleroywalkthrough.data.PhotoEntryRepository;
import edu.csce4623.rghosh.uncleroywalkthrough.data.PhotoEntryDataSource;
import util.AppExecutors;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FloatingActionButton addButton;
    private FusedLocationProviderClient mFusedLocationClient;
    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;
    String timeTaken;
    PhotoEntry photo;

    private PhotoEntryDao photoEntryDao;
    private PhotoEntryRepository mPhotoEntryRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        addButton = (FloatingActionButton) findViewById(R.id.add);
//        myImageView = findViewById(R.id.ivMyImageView);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Main activity", "clicked button");
                dispatchTakePictureIntent();
//                getLocationAndLog();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
        }
        // fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mapFragment != null) {
            transaction.add(R.id.mapFrame, mapFragment);
            transaction.commit();
        }

        mPhotoEntryRepository = PhotoEntryRepository.getInstance(new AppExecutors(), getApplicationContext());
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPhotoEntries();
    }

    public void loadPhotoEntries() {
        Log.d("ToDoListPresenter", "Loading ToDoItems");
        mPhotoEntryRepository.getPhotoEntries(new PhotoEntryDataSource.LoadPhotoEntriesCallback() {
            @Override
            public void onPhotoEntriesLoaded(List<PhotoEntry> photoEntries) {
                Log.d("PRESENTER", "Loaded");
                PhotoEntry photo;
                LatLng thisLocation = null;
                for (int i = 0; i < photoEntries.size(); i++) {
                    photo = photoEntries.get(i);
                    Log.d("IN LOAD", "made it");
                    thisLocation = new LatLng(photo.getLatitude(), photo.getLongitude());
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(thisLocation);
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    mMap.addMarker(marker).setTag(photo);
                }
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("PRESENTER", "Not Loaded");
            }
        });
    }

    // pictures stuff
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "currently do nothing");
            getLocationAndLog();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d("CREATERD IMAGE FILE", "CURRENT PHOTO PATH");
        timeTaken = timeStamp;
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("MainActivity", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "edu.csce4623.rghosh.uncleroywalkthrough.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void getLocationAndLog() {
        if (mFusedLocationClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MapsActivity", "No Location Permission");
                return;
            }
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d("MapsActivity", "" + location.getLatitude() + ":" + location.getLongitude());
                        final LatLng thisLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d("IN SETTING PHOTO", currentPhotoPath);
                        photo = new PhotoEntry();
                        photo.setPathname(currentPhotoPath);
                        photo.setLongitude(location.getLongitude());
                        photo.setLatitude(location.getLatitude());
                        photo.setTime(timeTaken);
                        Log.d("ID IN PRESENTER", photo.getPathname());
                        mPhotoEntryRepository.createPhotoEntry(photo, new PhotoEntryDataSource.CreatePhotoEntryCallback() {
                            @Override
                            public void onCreatePhotoEntry(long id, PhotoEntry photo) {
                                Log.d("ID IN PRESENTER", String.valueOf(id));
                                MarkerOptions marker = new MarkerOptions();
                                marker.position(thisLocation);
                                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                mMap.addMarker(marker).setTag(photo);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(thisLocation, 20));
                            }

                            @Override
                            public void onDataNotAvailable() {
                                Log.d("PRESENTER", "Error creating item");
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    Log.d("INSIDE GET LAST LOC", "MADE IT");
                                    LatLng thisLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(thisLocation, 20));
                                }
                            }
                        });


            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
            mMap.setOnMarkerClickListener(this);
            // Enable ur location
            enableMyLocation();
            loadPhotoEntries();
        }
    }
    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        PhotoEntry photo = (PhotoEntry) marker.getTag();
        Log.d("on marker click:", photo.getPathname());

        // Check if a click count was set, then display the click count.
        if (photo != null) {
            Log.d("on marker click:", photo.getPathname());
            Fragment fragment;
            fragment = new PhotoFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.popBackStack();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.mapFrame, fragment).addToBackStack("photo");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            Bundle data = new Bundle();
            data.putSerializable("PhotoLocation", photo);
            fragment.setArguments(data);
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}