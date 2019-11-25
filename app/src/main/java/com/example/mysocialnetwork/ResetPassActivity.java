package com.example.mysocialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ResetPassActivity extends AppCompatActivity {

    Button btnSendEmail;
    EditText inputEmail;

    Toolbar mToolbar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        btnSendEmail = (Button)findViewById(R.id.btn_sendEmail);
        inputEmail = (EditText)findViewById(R.id.input_email);

        mToolbar = (Toolbar)findViewById(R.id.forget_pass_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset password");

        mAuth = FirebaseAuth.getInstance();

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = inputEmail.getText().toString();

                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ResetPassActivity.this, "Please input your email", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPassActivity.this, "Please check your email account", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassActivity.this, LoginActivity.class));
                            }else{
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPassActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
