package com.example.startuptourism.Activities.Owner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.startuptourism.Adapter.AccFormImgAdapter;
import com.example.startuptourism.Database.RoomDb.DbHelper;
import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;
import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.Helper.AddressHelp;
import com.example.startuptourism.Helper.DialogHelp;
import com.example.startuptourism.Helper.TextHelp;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.ActivityAccommodationFormBinding;
import com.example.startuptourism.databinding.DialogImageBinding;
import com.example.startuptourism.databinding.DialogLoadingBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AccommodationForm extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, AccFormImgAdapter.ImageClick {
    private ActivityAccommodationFormBinding root;
    private DbHelper dbHelper;
    private Handler handler;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private LatLng pinnedLocation;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia1, pickMultipleMedia2, pickMultipleMedia3, pickMultipleMedia4, pickMultipleMedia5;
    private final List<Uri> selectedImages = new ArrayList<>();
    private AccFormImgAdapter accFormImgAdapter;
    private String mode;
    private List<String> imgPathUploaded = new ArrayList<>();
    private User user;
    private StorageReference imgRef;
    private UploadTask uploadTask;
    private AlertDialog dialog;

    private void setDb() {
        dbHelper = new DbHelper(this);
        handler = new Handler(Looper.getMainLooper());
    }

    private void setBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                back();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityAccommodationFormBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        setBackPressed();
        // TODO: 10/05/2024 edit mode
        user = (User) getIntent().getSerializableExtra("user");
        mode = getIntent().getStringExtra("mode");
        if (mode.equalsIgnoreCase("add")) {
            root.btnAdd.setVisibility(View.VISIBLE);
            root.btnDelete.setVisibility(View.GONE);
            root.btnUpdate.setVisibility(View.GONE);
        } else if (mode.equalsIgnoreCase("edit")) {
            root.btnDelete.setVisibility(View.VISIBLE);
            root.btnUpdate.setVisibility(View.VISIBLE);
            root.btnAdd.setVisibility(View.GONE);
        }
        root.btnCancel.setOnClickListener(view -> back());
        root.btnBack.setOnClickListener(view -> back());
        setLocationRequest();
        setPhotoPickerRequest();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        root.edtType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.accom_type)));
        root.edtType.setThreshold(0);

        root.btnAdd.setOnClickListener(view -> validate());

        root.rvImages.setAdapter(accFormImgAdapter = new AccFormImgAdapter(this, new ArrayList<>(), this));
        root.btnAddImage.setOnClickListener(view -> {
            switch (5 - selectedImages.size()) {
                case 1:
                    pickMultipleMedia1.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                    break;
                case 2:
                    pickMultipleMedia2.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                    break;
                case 3:
                    pickMultipleMedia3.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                    break;
                case 4:
                    pickMultipleMedia4.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                    break;
                case 5:
                    pickMultipleMedia5.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                    break;
            }
        });
        root.sliderHours.addOnChangeListener(((slider, value, fromUser) -> {
            root.txtTimeHoursStay.setText(String.valueOf((int) value));
        }));
    }

    private void setPhotoPickerRequest() {
        pickMultipleMedia1 = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                List<Uri> imgsel = new ArrayList<>();
                imgsel.add(uri);
                afterSelectImage(imgsel);
            }
        });
        pickMultipleMedia2 = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(2), this::afterSelectImage);
        pickMultipleMedia3 = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(3), this::afterSelectImage);
        pickMultipleMedia4 = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(4), this::afterSelectImage);
        pickMultipleMedia5 = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5), this::afterSelectImage);
    }

    private void afterSelectImage(List<Uri> uris) {
        if (!uris.isEmpty()) {
            selectedImages.addAll(uris);
            if (selectedImages.size() >= 5)
                root.btnAddImage.setVisibility(View.GONE);
            else
                root.btnAddImage.setVisibility(View.VISIBLE);

            if (selectedImages.size() > 5) {
                for (int i = 4; i < selectedImages.size(); i++) {
                    selectedImages.remove(i);
                }
            }
            accFormImgAdapter.refreshList(selectedImages);
        }
    }

    private void validate() {
        // TODO: 10/05/2024 validate
        uploadImages();

    }

    private void uploadImages() {
        imgPathUploaded = new ArrayList<>();
        DialogLoadingBinding binding = DialogLoadingBinding.inflate(getLayoutInflater());
        dialog = DialogHelp.createDialog(this, "Posting Your Accommodation", null)
                .setCancelable(false)
                .setView(binding.getRoot())
                .create();
        dialog.show();
        // TODO: 10/05/2024 change waiting text
        binding.txtMessage.setText("Please wait...");
        Executors.newSingleThreadExecutor().submit(() -> {
            StorageReference accomFolder = dbHelper.getStorage().getReference().child("accomImages");
            //Image 1---------------------------------------------------------------------
            if (selectedImages.size() > 0) {
                imgRef = accomFolder.child(selectedImages.get(0).getLastPathSegment());
                uploadTask = imgRef.putFile(selectedImages.get(0));
                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        handler.post(this::imageUploadError);
                    }
                    return imgRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imgPathUploaded.add(task.getResult().toString());
                        //Image 2---------------------------------------------------------------------
                        if (selectedImages.size() > 1) {
                            imgRef = accomFolder.child(selectedImages.get(1).getLastPathSegment());
                            uploadTask = imgRef.putFile(selectedImages.get(1));
                            uploadTask.continueWithTask(task1 -> {
                                if (!task1.isSuccessful()) {
                                    handler.post(this::imageUploadError);
                                }
                                return imgRef.getDownloadUrl();
                            }).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    imgPathUploaded.add(task1.getResult().toString());


                                    //Image 3---------------------------------------------------------------------
                                    if (selectedImages.size() > 2) {
                                        imgRef = accomFolder.child(selectedImages.get(2).getLastPathSegment());
                                        uploadTask = imgRef.putFile(selectedImages.get(2));
                                        uploadTask.continueWithTask(task2 -> {
                                            if (!task2.isSuccessful()) {
                                                handler.post(this::imageUploadError);
                                            }
                                            return imgRef.getDownloadUrl();
                                        }).addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                imgPathUploaded.add(task2.getResult().toString());


                                                //Image 4---------------------------------------------------------------------
                                                if (selectedImages.size() > 3) {
                                                    imgRef = accomFolder.child(selectedImages.get(3).getLastPathSegment());
                                                    uploadTask = imgRef.putFile(selectedImages.get(3));
                                                    uploadTask.continueWithTask(task3 -> {
                                                        if (!task3.isSuccessful()) {
                                                            handler.post(this::imageUploadError);
                                                        }
                                                        return imgRef.getDownloadUrl();
                                                    }).addOnCompleteListener(task3 -> {
                                                        if (task3.isSuccessful()) {
                                                            imgPathUploaded.add(task3.getResult().toString());

                                                            //Image 5---------------------------------------------------------------------
                                                            if (selectedImages.size() > 4) {
                                                                imgRef = accomFolder.child(selectedImages.get(4).getLastPathSegment());
                                                                uploadTask = imgRef.putFile(selectedImages.get(4));
                                                                uploadTask.continueWithTask(task4 -> {
                                                                    if (!task4.isSuccessful()) {
                                                                        handler.post(this::imageUploadError);
                                                                    }
                                                                    return imgRef.getDownloadUrl();
                                                                }).addOnCompleteListener(task4 -> {
                                                                    if (task4.isSuccessful()) {
                                                                        imgPathUploaded.add(task4.getResult().toString());
                                                                        handler.post(this::imageUploadFinish);
                                                                    } else
                                                                        handler.post(this::imageUploadError);
                                                                });
                                                                return;
                                                            }
                                                            handler.post(this::imageUploadFinish);

                                                        } else
                                                            handler.post(this::imageUploadError);
                                                    });
                                                    return;
                                                }
                                                handler.post(this::imageUploadFinish);
                                            } else
                                                handler.post(this::imageUploadError);
                                        });
                                        return;
                                    }
                                    handler.post(this::imageUploadFinish);
                                } else
                                    handler.post(this::imageUploadError);
                            });
                            return;
                        }
                        handler.post(this::imageUploadFinish);
                    } else
                        handler.post(this::imageUploadError);
                });
            }
        });
    }

    private void imageUploadError() {
        dialog.dismiss();
        Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
    }

    private void imageUploadFinish() {
        DatabaseReference ref = dbHelper.getReference().child("accommodation").child(user.getUsername());
        DatabaseReference accommodationRef = ref.push();
        Accommodation postingAccom = retrieveAccomm();
        postingAccom.setAccomId(accommodationRef.getKey());
        accommodationRef.setValue(postingAccom).addOnSuccessListener(unused -> {
            dialog.dismiss();
            DialogHelp.createDialog(this, "Accommodation Posted", "Your accommodation was successfully posted. Tourists can now book for your accommodation")
                    .setPositiveButton("OKAY", null)
                    .setOnDismissListener((dialogInterface -> finish()))
                    .create().show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private Accommodation retrieveAccomm() {
        Accommodation accommodation = new Accommodation();
        accommodation.setAccomUsername(user.getUsername());
        if (!TextHelp.isEmpty(root.edtName))
            accommodation.setAccomName(TextHelp.getText(root.edtName));
        if (!TextHelp.isEmpty(root.edtType))
            accommodation.setAccomType(TextHelp.getText(root.edtType));
        if (!TextHelp.isEmpty(root.edtAddress))
            accommodation.setAccomAddress(TextHelp.getText(root.edtAddress));
        accommodation.setAccomLat(pinnedLocation.latitude);
        accommodation.setAccomLng(pinnedLocation.longitude);

        if (!TextHelp.isEmpty(root.edtCapacity))
            accommodation.setAccomPax(Integer.parseInt(TextHelp.getText(root.edtCapacity)));

        accommodation.setAccomMinHours((int) root.sliderHours.getValue());

        if (!TextHelp.isEmpty(root.edtRate))
            accommodation.setAccomHourRate(Double.parseDouble(TextHelp.getText(root.edtRate)));

        if (!TextHelp.isEmpty(root.edtDescription))
            accommodation.setAccomDesc(TextHelp.getText(root.edtDescription));
        if (mode.equalsIgnoreCase("add"))
            accommodation.setAccomVerified(0);


        if (imgPathUploaded.size() > 0) {
            accommodation.setImgPath1(imgPathUploaded.get(0));
            if (imgPathUploaded.size() > 1) {
                accommodation.setImgPath2(imgPathUploaded.get(1));
                if (imgPathUploaded.size() > 2) {
                    accommodation.setImgPath3(imgPathUploaded.get(2));
                    if (imgPathUploaded.size() > 3) {
                        accommodation.setImgPath4(imgPathUploaded.get(3));
                        if (imgPathUploaded.size() > 4) {
                            accommodation.setImgPath5(imgPathUploaded.get(4));
                        }
                    }
                }
            }
        }
        return accommodation;
    }

    private void setLocationRequest() {
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        getCurrentLoc();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        getCurrentLoc();
                    } else {
                        Toast.makeText(this, "Please allow location permission for more accurate reservation", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void back() {
        // TODO: 10/05/2024 validate back
        finish();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        getCurrentLoc();
    }

    private void getCurrentLoc() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            LatLng currentLoc = new LatLng(15.4861, 120.5862);
            if (location != null) {
                currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(this, "Current location is not detected", Toast.LENGTH_SHORT).show();
            }


            Geocoder geocoder = new Geocoder(this);
            try {
                Address address = geocoder.getFromLocation(currentLoc.latitude, currentLoc.longitude, 1).get(0);
                if (address == null) {
                    Toast.makeText(this, "No address retrieved", Toast.LENGTH_SHORT).show();
                    return;
                }
                root.edtAddress.setText(AddressHelp.getAddress(address));

            } catch (IOException e) {
                Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            mMap.addMarker(new MarkerOptions().position(currentLoc));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(20));
            pinnedLocation = currentLoc;
        });
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        pinnedLocation = latLng;
        Address address = AddressHelp.getAddress(this, pinnedLocation);
        if (address != null) {

        }
    }

    @Override
    public void onClick(Uri uri) {
        DialogImageBinding root = DialogImageBinding.inflate(getLayoutInflater());
        Picasso.get().load(uri).resize(500, 300).centerCrop().into(root.img);
        DialogHelp.createDialog(this, "", null)
                .setCustomTitle(null)
                .setView(root.getRoot())
                .create().show();
    }

    @Override
    public void onRemove(int position) {
        selectedImages.remove(position);
        accFormImgAdapter.refreshList(selectedImages);
        root.btnAddImage.setVisibility(View.VISIBLE);
    }
}