package com.example.mysocialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    Toolbar mToolbar;
    ImageButton btn_Search;
    EditText searchInputText;

    RecyclerView searchResultList;

    DatabaseReference allUsersDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar) findViewById(R.id.find_friends_bar_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        searchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        btn_Search = (ImageButton) findViewById(R.id.btn_search_people_friends);
        searchInputText = (EditText) findViewById(R.id.search_box_input);

        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = searchInputText.getText().toString();
//                SearchPeopleFindFriends(searchBoxInput);
            }
        });
    }

//    private void SearchPeopleFindFriends(String searchBoxInput) {
//
//        Toast.makeText(this, "Search...", Toast.LENGTH_LONG).show();
//
//        Query searchPeopleandFriendsQuery = allUsersDatabaseRef.orderByChild("fullName")
//                .startAt(searchBoxInput).endAt(searchBoxInput + "uf8ff");
//
//
//        FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter
//                = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>
//                (
//                        FindFriends.class,
//                        R.layout.all_users_dislay_layout,
//                        FindFriendsViewHolder.class,
//                        searchPeopleandFriendsQuery
//                ) {
//            @Override
//            protected void populateViewHolder(FindFriendsViewHolder viewHolder, FindFriends model, final int i) {
//                viewHolder.setFullName(model.getFullName());
//                viewHolder.setStatus(model.getStatus());
//                viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
//
//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String userFindDisplay = getRef(i).getKey();
//                        Intent friendIntent = new Intent(FindFriendsActivity.this, FriendsActivity.class);
//                        friendIntent.putExtra("userFindDisplay", userFindDisplay);
//                        startActivity(friendIntent);
//                    }
//                });
//            }
//        };
//
//        searchResultList.setAdapter(firebaseRecyclerAdapter);
//    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FindFriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileimage(Context context, String profileimage) {
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }

        public void setFullName(String fullname) {
            TextView myName = (TextView) mView.findViewById(R.id.all_users_profile_full_name);
            myName.setText(fullname);
        }

        public void setStatus(String status) {
            TextView myStatus = (TextView) mView.findViewById(R.id.all_users_status);
            myStatus.setText(status);
        }

    }
}
