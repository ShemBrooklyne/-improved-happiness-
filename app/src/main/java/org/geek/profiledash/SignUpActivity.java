package org.geek.profiledash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.loginTxt)
    TextView loginAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        loginAcc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == loginAcc) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }
}