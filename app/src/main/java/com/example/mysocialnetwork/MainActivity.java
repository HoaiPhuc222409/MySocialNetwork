package com.example.mysocialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ImageButton newPostButton;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostsRef, LikesRef;

    String currentUserID;
    Boolean likeCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");


        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
//        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SNOP");

        newPostButton = (ImageButton) findViewById(R.id.btn_newPost);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);


        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);


        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);


        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("fullName")) {
                        String fullname = dataSnapshot.child("fullName").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }
                    // link profileimage trong database
                    if (dataSnapshot.hasChild("profileimage")) {

                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Toast.makeText(MainActivity.this, "image: " + image, Toast.LENGTH_SHORT).show();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    } else {
                        Toast.makeText(MainActivity.this, "Profile name does not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPostActivity();
            }
        });

        DisplayAllUsersPosts();
    }

    private void DisplayAllUsersPosts() {
        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(PostsRef, Posts.class).build();
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {
                holder.username.setText(model.getFullName());
                holder.time.setText(" " + model.getTime());
                holder.date.setText(" " + model.getDate());
                holder.description.setText(model.getDescription());
                Picasso.get().load(model.getProfileimage()).into(holder.user_post_image);
                Picasso.get().load(model.getPostimage()).into(holder.postImage);

                final String postKey = getRef(position).getKey();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(MainActivity.this, ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey", postKey);
                        startActivity(clickPostIntent);
                    }
                });

                holder.setBtnLikeStatus(postKey);

                holder.btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeCheck = true;

                        LikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(likeCheck.equals(true)){
                                           if (dataSnapshot.child(postKey).hasChild(currentUserID)) {
                                               LikesRef.child(postKey).child(currentUserID).removeValue();
                                               likeCheck = false;
                                           } else {
                                               LikesRef.child(postKey).child(currentUserID).setValue(true);
                                               likeCheck = false;
                                           }
                                       }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                holder.btnComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent commentPostIntent = new Intent(MainActivity.this, CommentActivity.class);
                                commentPostIntent.putExtra("PostKey", postKey);
                                startActivity(commentPostIntent);
                            }
                        });

            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                PostsViewHolder viewHolder = new PostsViewHolder(view);
                return viewHolder;
            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
//    public static class PostsViewHolder extends RecyclerView.ViewHolder{
//        TextView username,date,time,description;
//        CircleImageView user_post_image;
//        ImageView postImage;
//
//        View mView;
//        public PostsViewHolder(View itemView) {
//            super(itemView);
//            mView = itemView;
//
//            username = (TextView)mView.findViewById(R.id.post_user_name);
//            date = (TextView)mView.findViewById(R.id.post_date);
//            time = (TextView)mView.findViewById(R.id.post_time);
//            description = (TextView)mView.findViewById(R.id.post_description);
//            user_post_image = (CircleImageView)mView.findViewById(R.id.post_profile_image);
//            postImage = (ImageView)mView.findViewById(R.id.post_image);
//        }
//    }

//    private void DisplayAllUsersPosts() {
////
//
//        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
//                        (
//                                Posts.class,
//                                R.layout.all_posts_layout,
//                                PostsViewHolder.class,
//                                PostsRef
//                        ) {
//                    @Override
//                    protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int i) {
//                        final String postKey = getRef(i).getKey();
//
//                        viewHolder.setFullName(model.getFullName());
//                        viewHolder.setDate(model.getDate());
//                        viewHolder.setTime(model.getTime());
//                        viewHolder.setDescription(model.getDescription());
//                        viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
//                        viewHolder.setPostimage(getApplicationContext(), model.getPostimage());
//
//                        viewHolder.setBtnLikeStatus(postKey);
//
//
//                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent clickPostIntent = new Intent(MainActivity.this, ClickPostActivity.class);
//                                clickPostIntent.putExtra("PostKey", postKey);
//                                startActivity(clickPostIntent);
//                            }
//                        });
//
//                        viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent commentPostIntent = new Intent(MainActivity.this, CommentActivity.class);
//                                commentPostIntent.putExtra("PostKey", postKey);
//                                startActivity(commentPostIntent);
//                            }
//                        });
//
////                        viewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View v) {
////                                likeCheck = true;
////
////                                LikesRef.addValueEventListener(new ValueEventListener() {
////                                    @Override
////                                    public void onDataChange(DataSnapshot dataSnapshot) {
////                                       if(likeCheck.equals(true)){
////                                           if (dataSnapshot.child(postKey).hasChild(currentUserID)) {
////                                               LikesRef.child(postKey).child(currentUserID).removeValue();
////                                               likeCheck = false;
////                                           } else {
////                                               LikesRef.child(postKey).child(currentUserID).setValue(true);
////                                               likeCheck = false;
////                                           }
////                                       }
////                                    }
////
////                                    @Override
////                                    public void onCancelled(DatabaseError databaseError) {
////
////                                    }
////                                });
////                            }
////                        });
//
//                    }
//                };
//        postList.setAdapter(firebaseRecyclerAdapter);
//
//    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        TextView username, date, time, description;
        CircleImageView user_post_image;
        ImageView postImage;

        ImageButton btnLike, btnComment;
        TextView amountOfLike;
        int countLikes;
        String currentUserId;
        DatabaseReference LikesRef;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            username = (TextView) mView.findViewById(R.id.post_user_name);
            date = (TextView) mView.findViewById(R.id.post_date);
            time = (TextView) mView.findViewById(R.id.post_time);
            description = (TextView) mView.findViewById(R.id.post_description);
            user_post_image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            postImage = (ImageView) mView.findViewById(R.id.post_image);

            btnLike = (ImageButton) mView.findViewById(R.id.btn_like);
            btnComment = (ImageButton) mView.findViewById(R.id.btn_comment);
            amountOfLike = (TextView) mView.findViewById(R.id.display_amount_of_like);

            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setBtnLikeStatus(final String postKey) {
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        btnLike.setImageResource(R.drawable.profile);
                        amountOfLike.setText((Integer.toString(countLikes)) + "Likes");
                    } else {
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        btnLike.setImageResource(R.drawable.quyen);
                        amountOfLike.setText((Integer.toString(countLikes)) + "Likes");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



//        public void setFullName(String fullName) {
//            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
//            username.setText(fullName);
//        }
//
//        public void setProfileimage(Context applicationContext, String profileimage) {
//            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
//            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(image);
//        }
//
//        public void setTime(String time) {
//            TextView postTime = (TextView) mView.findViewById(R.id.post_time);
//            postTime.setText(" " + time);
//        }
//
//        public void setDate(String date) {
//            TextView postDate = (TextView) mView.findViewById(R.id.post_date);
//            postDate.setText(" " + date);
//        }
//
//        public void setDescription(String description) {
//            TextView postDescription = (TextView) mView.findViewById(R.id.post_description);
//            postDescription.setText(description);
//        }
//
//        public void setPostimage(Context applicationContext, String postimage) {
//            ImageView postImage = (ImageView) mView.findViewById(R.id.post_image);
//            Picasso.get().load(postimage).placeholder(R.drawable.profile).into(postImage);
//        }
    }


    private void SendUserToPostActivity() {
        Intent postIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(postIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            SendUserToLoginActivity();
            Toast.makeText(this, "NULL USERS", Toast.LENGTH_SHORT).show();
        } else {
            checkUserExistence();
        }
    }

    private void checkUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(current_user_id)) {
                    Toast.makeText(MainActivity.this, "Please setup your information", Toast.LENGTH_SHORT).show();
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String message = databaseError.getMessage();
                Toast.makeText(MainActivity.this, "FAILLLLL" + message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToProfileActivity() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void SendUserToFindFriendsActivity() {
        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case (R.id.nav_post):
                SendUserToPostActivity();
                break;

            case (R.id.nav_Profile):
                SendUserToProfileActivity();
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case (R.id.nav_home):
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case (R.id.nav_friends):
                Toast.makeText(this, "Friend List", Toast.LENGTH_SHORT).show();
                break;

            case (R.id.nav_find_friends):
                SendUserToFindFriendsActivity();
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;

            case (R.id.nav_messages):
                Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
                break;

            case (R.id.nav_settings):
                SendUserToSettingsActivity();
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

            case (R.id.nav_logout):
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }

    }
}
