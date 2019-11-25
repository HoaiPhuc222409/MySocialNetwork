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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    final static int Gallery_Pick = 1;

    private EditText UserName, FullName, Country;
    private Button SaveButton;
    private CircleImageView ProfileImage;
    private ProgressDialog progressDialog;
    private Uri imageUri;

    private StorageReference UserProfileImageRef;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    private String currentUserID, downloadImageProfileUrl;

    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Image");

        UserName = (EditText) findViewById(R.id.userName_setup);
        FullName = (EditText) findViewById(R.id.fullName_setup);
        Country = (EditText) findViewById(R.id.country_setup);
        SaveButton = (Button) findViewById(R.id.btn_saveSetup);
        ProfileImage = (CircleImageView) findViewById(R.id.profile_setup);
        progressDialog = new ProgressDialog(this);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("profileimage")) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(ProfileImage);
                    } else {
//                        Toast.makeText(SetupActivity.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

//            resultUri = imageUri;

            ProfileImage.setImageURI(resultUri);

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
//
//                final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                final StorageReference filePath = UserProfileImageRef.child(imageUri.getLastPathSegment() + ".jpg").child(imageUri.getLastPathSegment() + ".jpg");
                final UploadTask uploadTask = filePath.putFile(resultUri1);

//                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(SetupActivity.this, "Profile image stored successfully to firebasestorage", Toast.LENGTH_SHORT).show();
//                            //LOAD URL WRONG
//                            final String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
//                            Toast.makeText(SetupActivity.this, "downloadUrl: " + downloadUrl, Toast.LENGTH_SHORT).show();
//                            UsersRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Intent selfActivity = new Intent(SetupActivity.this, SetupActivity.class);
//                                        startActivity(selfActivity);
//                                        Toast.makeText(SetupActivity.this, "Profile image stored Firebase database successfully", Toast.LENGTH_SHORT).show();
//                                        progressDialog.dismiss();
//
//                                    } else {
//                                        String message = task.getException().getMessage();
//                                        Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
//                                        progressDialog.dismiss();
//                                    }
//                                }
//                            });
//                        }
//                    }
//                });

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = e.toString();
                        Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SetupActivity.this, "Profileimage upload sucessfully...", Toast.LENGTH_SHORT).show();

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                downloadImageProfileUrl = filePath.getDownloadUrl().toString();
//                                Toast.makeText(SetupActivity.this, "DOWNLOADIMAGEPROFILEURL: "+ downloadImageProfileUrl, Toast.LENGTH_SHORT).show();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadImageProfileUrl = task.getResult().toString();
                                    Toast.makeText(SetupActivity.this, "DOWNLOADIMAGEPROFILEURL: "+ downloadImageProfileUrl, Toast.LENGTH_SHORT).show();

                                    Toast.makeText(SetupActivity.this, "Profileimage stored successfully to firebasestorage", Toast.LENGTH_SHORT).show();

                                    progressDialog.dismiss();
                                    UsersRef.child("profileimage").setValue(downloadImageProfileUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent selfActivity = new Intent(SetupActivity.this, SetupActivity.class);
                                                        startActivity(selfActivity);
                                                        Toast.makeText(SetupActivity.this, "Profile image stored Firebase database successfully", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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

    private void SaveAccountSetupInformation() {
        String userName = UserName.getText().toString();
        String fullName = FullName.getText().toString();
        String conuntry = Country.getText().toString();


        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Please input your username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Please input your full name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(conuntry)) {
            Toast.makeText(this, "Please input your country", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.setTitle("Setup your account...");
            progressDialog.setMessage("Please wait for setup ...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("userName", userName);
            userMap.put("fullName", fullName);
            userMap.put("country", conuntry);
            userMap.put("statis", "Hey Quyen,..");
            userMap.put("gender", "none");
            userMap.put("dob", "none");
            userMap.put("relationship", "none");

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SetupActivity.this, "Your account is update successfully", Toast.LENGTH_LONG).show();
                        SendUserToMainActivity();
                        progressDialog.dismiss();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
//            UsersRef.child(currentUserID).updateChildren(userMap)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
////                                Intent selfActivity = new Intent(SetupActivity.this, SetupActivity.class);
////                                startActivity(selfActivity);
//                                SendUserToMainActivity();
//                                Toast.makeText(SetupActivity.this, "Profile image stored Firebase database successfully", Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();
//                            } else {
//                                String message = task.getException().getMessage();
//                                Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();
//                            }
//
//                        }
//                    });
        }
    }

    private void SendUserToRegisterActivity() {
        Intent regisIntent = new Intent(SetupActivity.this, RegisterActivity.class);
        regisIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(regisIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(SetupActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
