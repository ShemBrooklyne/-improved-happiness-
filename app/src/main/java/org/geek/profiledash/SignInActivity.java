package org.geek.profiledash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.createTxt)
    TextView createAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        createAcc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == createAcc) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
    }
}