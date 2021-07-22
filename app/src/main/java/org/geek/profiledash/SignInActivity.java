package org.geek.profiledash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.createTxt)
    TextView createAcc;
    @BindView(R.id.emailAddress)
    EditText lgnEmail;
    @BindView(R.id.lgnPassword)
    EditText lgnPassword;
    @BindView(R.id.loginBtn)
    Button login;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog authProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Intent intent = new Intent(SignInActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        createAcc.setOnClickListener(this);
        login.setOnClickListener(this);

        createAuthProcessDialog();

    }

    private void createAuthProcessDialog() {
        authProgressDialog = new ProgressDialog(this);
        authProgressDialog.setTitle("Loading...");
        authProgressDialog.setMessage("Please wait \uD83D\uDE0A.");
        authProgressDialog.setCancelable(false);
    }

    private void loginWithPassword() {
        String email = lgnEmail.getText().toString().trim();
        String password = lgnPassword.getText().toString().trim();
        if (email.equals("")) {
            lgnEmail.setError("Please enter your email");
            return;
        }
        if (password.equals("")) {
            lgnPassword.setError("Password cannot be blank");
            return;
        }
        authProgressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        authProgressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            DynamicToast.makeError(SignInActivity.this, "Login failed").show();
                        } else {
                            DynamicToast.makeSuccess(SignInActivity.this, "Login Success").show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == createAcc) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }

        if (v == login) {
            loginWithPassword();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}