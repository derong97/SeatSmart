package com.example.seatsmart;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.database.DatabaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity implements View.OnClickListener{

    // TAG reference for logging
    private static final String TAG = "Admin";

    // UI reference
    private EditText edtEmail;
    private EditText edtPassword;
    private LinearLayout llLogin;
    private PopupWindow puContactAdmin;
    private ProgressDialog progressDialog;

    // Firebase reference
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Variable Declaration
    private DatabaseManager model = DatabaseManager.getModel();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        // Initialize UI
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        llLogin = findViewById(R.id.llLogin);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // AddValueEventListener to each seat at login page
        model.addValueEventListener();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Checks if there is any login activity
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.i(TAG, "User is signed in");
                    startActivity(new Intent(LoginPage.this, Level1.class));
                } else {
                    // User is signed out
                    Log.i(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // Cancels email and password fields quickly if user types wrongly
        findViewById(R.id.btnCancelEmail).setOnClickListener(this);
        findViewById(R.id.btnCancelPassword).setOnClickListener(this);

        // Login through firebase authentication
        findViewById(R.id.btnLogin).setOnClickListener(this);

        // Informs user that admin has been contacted
        findViewById(R.id.tvContactAdmin).setOnClickListener(this);
    }

    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnCancelEmail) {
            edtEmail.setText("");
        } else if(i == R.id.btnCancelPassword){
            edtPassword.setText("");
        } else if (i == R.id.btnLogin){
            progressDialog = new ProgressDialog(LoginPage.this);
            progressDialog.setMessage("Processing your request, please wait...");
            progressDialog.show();
            startLogin();
        } else if (i == R.id.tvContactAdmin){
            popUpContactAdmin();
            puContactAdmin.showAtLocation(llLogin, Gravity.CENTER,0,0);
        } else if (i == R.id.btnClose){
            puContactAdmin.dismiss();
        }
    }

    private void startLogin(){
        final String email = edtEmail.getText().toString();
        final String password = edtPassword.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            toastMessage("Please key in all the fields");
            progressDialog.dismiss();
        } else{
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(!task.isSuccessful()){
                        Log.d(TAG,"Incorrect authentication: entered email is :" + email + " ," +
                                " entered password is: " + password);
                        toastMessage("Incorrect email or password");
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void popUpContactAdmin(){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup_screen,null);

        puContactAdmin = new PopupWindow(
                customView,
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT
        );

        if(Build.VERSION.SDK_INT>=21){
            puContactAdmin.setElevation(5.0f);
        }

        // Close pop-up
        customView.findViewById(R.id.btnClose).setOnClickListener(this);
    }
}
