package org.geek.profiledash.dashUi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.geek.profiledash.R;
import org.geek.profiledash.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.loginTxt)
    TextView loginAcc;
    @BindView(R.id.name)
    EditText userName;
    @BindView(R.id.createEmail)
    EditText creEmail;
    @BindView(R.id.createAbout)
    EditText creAbout;
    @BindView(R.id.phone)
    EditText phoneNumber;
    @BindView(R.id.createPassword)
    EditText crePassword;
    @BindView(R.id.create)
    Button create;
    FirebaseDatabase database;
    DatabaseReference reference;
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
        String user_email = creEmail.getText().toString().trim();
        String user_password = crePassword.getText().toString().trim();
        String user_name = userName.getText().toString().trim();
        String phone_number = phoneNumber.getText().toString().trim();
        String user_about = creAbout.getText().toString().trim();
        boolean validEmail = isValidEmail(user_email);
        boolean validPassword = isValidPassword(user_password);
        if (!validEmail || !validPassword) return;
        if (user_name.isEmpty()) {
            userName.setError("Full name is required!");
            userName.requestFocus();
            return;
        }

        if (phone_number.isEmpty()) {
            phoneNumber.setError("Phone number is required!");
            phoneNumber.requestFocus();
            return;
        }

        if (user_about.isEmpty()) {
            creAbout.setError("Small text about yourself");
            creAbout.requestFocus();
            return;
        }

        authProgressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                authProgressDialog.dismiss();
                if (task.isSuccessful()) {

                    User user = new User(user_name, phone_number, user_about, user_email);

                    FirebaseStorage.getInstance().getReference("images");
                    FirebaseDatabase.getInstance().getReference("Users")

                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

                    createFirebaseUserProfile(task.getResult().getUser());
                    DynamicToast.makeSuccess(SignUpActivity.this, "Profile Created Successfully").show();
                } else {
                    DynamicToast.makeError(SignUpActivity.this, "Ooops! Something went wrong \uD83D\uDE10").show();

                }
            }
        });
    }

    private void createFirebaseUserProfile(FirebaseUser firebaseUser) {
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 6) {
            crePassword.setError("Password is too short. Try a minimum of 6 digits");
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
        return true;
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