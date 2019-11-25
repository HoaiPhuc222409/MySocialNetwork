package com.example.mysocialnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText UserEmail, UserPass, UserConfirmPass;
    private Button CreateRegisterButton;

    private FirebaseAuth mAuth;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        UserEmail = (EditText) findViewById(R.id.email_register);
        UserPass = (EditText) findViewById(R.id.pass_register);
        UserConfirmPass = (EditText) findViewById(R.id.confirmPass_register);
        CreateRegisterButton = (Button) findViewById(R.id.btn_createRegis);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);

        CreateRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(RegisterActivity.this, "great", Toast.LENGTH_SHORT).show();
                CreateNewAccout();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            SendUserToMainActiity();
        }
    }

    private void SendUserToMainActiity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void CreateNewAccout() {

        String email = UserEmail.getText().toString();
        String pass = UserPass.getText().toString();
        String confirmPass = UserConfirmPass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please input your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Please input your password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(confirmPass)) {
            Toast.makeText(this, "Your password don't match with your confirmpassword", Toast.LENGTH_SHORT).show();
        }

        progressBar.setVisibility(View.VISIBLE);

        if(pass.equals(confirmPass)){

            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {
                                SendUserToSetupActivity();
//                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                Toast.makeText(RegisterActivity.this, "SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(RegisterActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

}
