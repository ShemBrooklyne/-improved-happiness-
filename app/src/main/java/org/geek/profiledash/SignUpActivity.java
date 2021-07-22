package org.geek.profiledash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.loginTxt)
    TextView loginAcc;
    @BindView(R.id.name)
    EditText userName;
    @BindView(R.id.createEmail)
    EditText creEmail;
    @BindView(R.id.createPassword)
    EditText crePassword;
    @BindView(R.id.confirmPassword)
    EditText confPassword;
    @BindView(R.id.create)
    Button create;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog authProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        createAuthStateListener();
        createAuthProgressDialog();

        loginAcc.setOnClickListener(this);
        create.setOnClickListener(this);
    }

    private void createAuthProgressDialog() {
        authProgressDialog = new ProgressDialog(this);
        authProgressDialog.setTitle("Loading...");
        authProgressDialog.setMessage("Please wait \uD83D\uDE42...");
        authProgressDialog.setCancelable(true);
    }

    private void createAuthStateListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    public void createNewUser() {
        final String email = creEmail.getText().toString().trim();
        String password = crePassword.getText().toString().trim();
        boolean validEmail = isValidEmail(email);
        boolean validPassword = isValidPassword(password);
        if (!validEmail || !validPassword) return;

        authProgressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                authProgressDialog.dismiss();
                if (task.isSuccessful()) {
                    createFirebaseUserProfile(task.getResult().getUser());
                    DynamicToast.makeSuccess(SignUpActivity.this, "Profile Created Successfully").show();
                } else {
                    DynamicToast.makeError(SignUpActivity.this, "Ooops! Something went wrong \uD83D\uDE10").show();

                }
            }
        });
    }

    private void createFirebaseUserProfile(final FirebaseUser firebaseUser) {

    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            crePassword.setError("Password is too short. Try a minimum of 8 digits");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            creEmail.setError("Please enter a valid email address");
            return false;
        }
        return isGoodEmail;
    }

    @Override
    public void onClick(View v) {
        if (v == loginAcc) {
            Intent intent = new Intent(this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        if (v == create) {
            createNewUser();
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