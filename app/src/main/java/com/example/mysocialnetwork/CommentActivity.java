package com.example.mysocialnetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import org.w3c.dom.Comment;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    ImageButton btnPostComment;
    EditText commentInput;
    RecyclerView commentList;
    CircleImageView CommentImage;
    Uri imageUri;

    String Post_Key, currentUserId, downloadImageUrl;

    StorageReference PostsImageReference;
    DatabaseReference UsersRef, PostsRef, CommentsRef, SettingsUserRef;
    FirebaseAuth mAuth;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        CommentImage = (CircleImageView) findViewById(R.id.comment_image);
        btnPostComment = (ImageButton) findViewById(R.id.btn_postComment);
        commentInput = (EditText) findViewById(R.id.comments_input);
        commentList = (RecyclerView) findViewById(R.id.commentList);
        commentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentList.setLayoutManager(linearLayoutManager);

        Post_Key = getIntent().getExtras().get("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");
        CommentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("profileimage");
        PostsImageReference = FirebaseStorage.getInstance().getReference().child("Profile Image");
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
///////////////////////////////////////////////////////////////////////////////////////////////////
//        CommentsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    String commentImage = dataSnapshot.child("profileimage").getValue().toString();
//                    Picasso.get().load(commentImage).placeholder(R.drawable.profile).into(CommentImage);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



        DisplayAllComments();
////////////////////////////////////////////////////////////////////////////////////////////////

    }

///////////////////////////////////////////////////////////////////////////////////////


    private void DisplayAllComments() {
        FirebaseRecyclerOptions<Comments> options = new FirebaseRecyclerOptions.Builder<Comments>().setQuery(FirebaseDatabase.getInstance().getReference().child("Posts"), Comments.class).build();
        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comments model) {
                holder.myUserName.setText(model.getUserName());
                holder.myComment.setText(model.getComment());
                holder.myDate.setText(model.getDate());
                holder.myTime.setText(model.getTime());
            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comment_layout, parent, false);
                CommentsViewHolder viewHolder = new CommentsViewHolder(view);
                return viewHolder;
            }
        };

        commentList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    //    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter
//                = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>
//                (
//                        Comments.class,
//                        R.layout.all_comment_layout,
//                        CommentsViewHolder.class,
//                        PostsRef
//                ) {
//            @Override
//            protected void populateViewHolder(CommentsViewHolder viewHolder, Comments model, int i) {
//                viewHolder.setUserName(model.getUserName());
//                viewHolder.setComment(model.getComment());
//                viewHolder.setDate(model.getDate());
//                viewHolder.setTime(model.getTime());
//            }
//        };
//        commentList.setAdapter(firebaseRecyclerAdapter);
//    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView myUserName, myComment, myDate, myTime;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            myUserName = (TextView) mView.findViewById(R.id.comment_username);
            myComment = (TextView) mView.findViewById(R.id.comment_text);
            myDate = (TextView) mView.findViewById(R.id.comment_date);
            myTime = (TextView) mView.findViewById(R.id.comment_time);
        }


//        public void setUserName(String userName) {
//            TextView myUserName = (TextView) mView.findViewById(R.id.comment_username);
//            myUserName.setText(userName);
//        }
//
//        public void setComment(String comment) {
//            TextView myComment = (TextView) mView.findViewById(R.id.comment_text);
//            myComment.setText(comment);
//        }
//
//        public void setDate(String date) {
//            TextView myDate = (TextView) mView.findViewById(R.id.comment_date);
//            myDate.setText("Date: " + date);
//        }
//
//        public void setTime(String time) {
//            TextView myTime = (TextView) mView.findViewById(R.id.comment_time);
//            myTime.setText("Time: " + time);
//        }
    }

    private void ValidateComment(String userName) {
        String commentText = commentInput.getText().toString();
        if (TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "Please write your comment", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("FF:mm:ss");
            final String saveCurrentTime = currentTime.format(calFordTime.getTime());

            final String RandomKey = currentUserId + saveCurrentDate + saveCurrentTime;

            HashMap CommentMap = new HashMap();
            CommentMap.put("uid", currentUserId);
            CommentMap.put("comment", commentText);
            CommentMap.put("date", saveCurrentDate);
            CommentMap.put("time", saveCurrentTime);
            CommentMap.put("userName", userName);

            PostsRef.child(RandomKey).updateChildren(CommentMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CommentActivity.this, "You have comment successfully...", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CommentActivity.this, "Error Occured: try again...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
