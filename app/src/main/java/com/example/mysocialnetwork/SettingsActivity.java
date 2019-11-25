package com.example.mysocialnetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    Toolbar mToolbar;
    EditText userName, userProfileName, userStatus, userCountry, userGender, userRela, userDOB;
    Button btnUpdateAccountSettings;
    CircleImageView userProfileImage;

    DatabaseReference SettingsUserRef, UserRef;
    FirebaseAuth mAuth;
    StorageReference UserProfileImageRef;

    String currentUserId, downloadImageProfileUrl;

    final static int Gallery_Pick = 1;
    Uri imageUri;
    Uri resultUri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
//        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.settings_username);
        userProfileName = (EditText) findViewById(R.id.settings_profile_full_name);
        userStatus = (EditText) findViewById(R.id.settings_status);
        userCountry = (EditText) findViewById(R.id.settings_country);
        userGender = (EditText) findViewById(R.id.settings_gender);
        userRela = (EditText) findViewById(R.id.settings_rela_status);
        userDOB = (EditText) findViewById(R.id.settings_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.settings_profile_image);
        btnUpdateAccountSettings = (Button) findViewById(R.id.btn_updateAccountSettings);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Image");

        SavingSettingsInfoToFirebase();

    }

    private void SavingSettingsInfoToFirebase() {
        SettingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("userName").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullName").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("statis").getValue().toString();
                    String myDOB = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelaStatus = dataSnapshot.child("relationship").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

                    userName.setText(myUserName);
                    userProfileName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userCountry.setText(myCountry);
                    userGender.setText(myGender);
                    userRela.setText(myRelaStatus);
                    userDOB.setText(myDOB);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnUpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

//            resultUri = imageUri;

            userProfileImage.setImageURI(resultUri);

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                progressDialog.setTitle("Profile Image...");
                progressDialog.setMessage("Please wait ,whle we update ...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();


                Uri resultUri1 = result.getUri();


                final StorageReference filePath = UserProfileImageRef.child(imageUri.getLastPathSegment() + ".jpg").child(imageUri.getLastPathSegment() + ".jpg");
                final UploadTask uploadTask = filePath.putFile(resultUri1);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = e.toString();
                        Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SettingsActivity.this, "Profileimage upload sucessfully...", Toast.LENGTH_SHORT).show();

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                downloadImageProfileUrl = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadImageProfileUrl = task.getResult().toString();
                                    Toast.makeText(SettingsActivity.this, "DOWNLOADIMAGEPROFILEURL: " + downloadImageProfileUrl, Toast.LENGTH_SHORT).show();

                                    Toast.makeText(SettingsActivity.this, "Profileimage stored successfully to firebasestorage", Toast.LENGTH_SHORT).show();

                                    progressDialog.dismiss();
                                    SettingsUserRef.child("profileimage").setValue(downloadImageProfileUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SettingsActivity.this, "Profile image stored Firebase database successfully", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }

                                                }
                                            });
                                }
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(this, "Error: Image can not be cropped, try again", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String profilename = userProfileName.getText().toString();
        String status = userStatus.getText().toString();
        String dob = userDOB.getText().toString();
        String country = userCountry.getText().toString();
        String gender = userGender.getText().toString();
        String relastatus = userRela.getText().toString();

        if (TextUtils.isEmpty(username))
            Toast.makeText(this, "Please input your username", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(profilename))
            Toast.makeText(this, "Please input your profilename", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(status))
            Toast.makeText(this, "Please input your status", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(dob))
            Toast.makeText(this, "Please input your date of birth", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(country))
            Toast.makeText(this, "Please input your country", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(gender))
            Toast.makeText(this, "Please input your gender", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(relastatus))
            Toast.makeText(this, "Please input your relastatus", Toast.LENGTH_SHORT).show();
        else {
            progressDialog.setTitle("Profile Image...");
            progressDialog.setMessage("Please wait ,whle we update ...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            UpdateAccountInfo(username, profilename, status, dob, country, gender, relastatus);
        }

    }

    private void UpdateAccountInfo(String username, String profilename, String status, String dob, String country, String gender, String relastatus) {
        HashMap userMap = new HashMap();
        userMap.put("userName", username);
        userMap.put("fullName", profilename);
        userMap.put("statis", status);
        userMap.put("dob", dob);
        userMap.put("country", country);
        userMap.put("gender", gender);
        userMap.put("relationship", relastatus);

        SettingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    SendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Account Settings Updated Successfully...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(SettingsActivity.this, "Error, while update your account", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }


}
