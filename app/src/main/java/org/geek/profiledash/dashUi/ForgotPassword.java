package org.geek.profiledash.dashUi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.geek.profiledash.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private final Handler handler = new Handler();
    private final Runnable updatedTimeTask = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(ForgotPassword.this, SignInActivity.class);
            startActivity(intent);
        }
    };
    @BindView(R.id.emailAddress)
    EditText emailEdit;
    @BindView(R.id.resetBtn)
    Button reset;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog authProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        reset.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        createAuthProcessDialog();
    }

    private void createAuthProcessDialog() {
        authProgressDialog = new ProgressDialog(this);
        authProgressDialog.setTitle("Loading...");
        authProgressDialog.setMessage("Please wait \uD83D\uDE0A.");
        authProgressDialog.setCancelable(false);
    }

    private void resetPassword() {
        String email = emailEdit.getText().toString().trim();
        if (email.equals("")) {
            emailEdit.setError("Email is required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError("Please provide a valid email");
            return;
        }

        authProgressDialog.show();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                authProgressDialog.dismiss();
                if (task.isSuccessful()) {
                    DynamicToast.makeSuccess(ForgotPassword.this, "Reset link has been sent to your email!").show();
                } else {
                    DynamicToast.makeWarning(ForgotPassword.this, "Try again! Something went wrong!").show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == reset) {
            resetPassword();
        }
        handler.postDelayed(updatedTimeTask, 850);
    }

}