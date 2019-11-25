package com.example.mysocialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    TextView friendName, friendProfileName, friendStatus, friendCountry, friendGender, friendRela, friendDOB;
    CircleImageView friendProfileImage;
    Button btnSendRequest, btnDeclineRequest;

    DatabaseReference ProfileUserRef, UserRef;
    FirebaseAuth mAuth;
    String currentUserId, friendUserId, CURRENT_STATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Init();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        friendUserId = getIntent().getExtras().get("userFindDisplay").toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        UserRef.child(friendUserId).addValueEventListener(new ValueEventListener() {
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

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(friendProfileImage);

                    friendName.setText("@" + myUserName);
                    friendProfileName.setText(myProfileName);
                    friendStatus.setText(myProfileStatus);
                    friendCountry.setText("Country: " + myCountry);
                    friendGender.setText("Gender: " + myGender);
                    friendRela.setText("Relationship: " + myRelaStatus);
                    friendDOB.setText("Date of birth: " + myDOB);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnDeclineRequest.setVisibility(View.INVISIBLE);
        btnDeclineRequest.setEnabled(false);

        if(!currentUserId.equals(friendUserId)){
            btnSendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnSendRequest.setEnabled(false);
                }
            });
        }else{
            btnDeclineRequest.setVisibility(View.INVISIBLE);
            btnSendRequest.setVisibility(View.INVISIBLE);
        }
    }

    private void Init() {
        friendName = (TextView) findViewById(R.id.friend_fullname);
        friendProfileName = (TextView) findViewById(R.id.friend_username);
        friendStatus = (TextView) findViewById(R.id.friend_status);
        friendCountry = (TextView) findViewById(R.id.friend_country);
        friendGender = (TextView) findViewById(R.id.friend_gender);
        friendRela = (TextView) findViewById(R.id.friend_rela);
        friendDOB = (TextView) findViewById(R.id.friend_dob);
        btnSendRequest = (Button) findViewById(R.id.btn_send_friend_request);
        btnDeclineRequest = (Button) findViewById(R.id.btn_decline_friend_request);
        friendProfileImage = (CircleImageView) findViewById(R.id.friend_pic);

        CURRENT_STATE = "not friend";
    }
}
