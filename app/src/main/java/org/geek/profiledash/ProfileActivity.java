package org.geek.profiledash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_name)
    TextView profileName;
    @BindView(R.id.profile_about)
    TextView profileAbout;
    @BindView(R.id.profile_email)
    TextView profileEmail;
    @BindView(R.id.profile_phone)
    TextView profilePhone;

    public static final String TAG = "Result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readDbData();
    }

    public void readDbData() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    Log.d(TAG, "User Name: " + user.getUser_name() + ", User Email: " + user.getUser_email() + ", User About: " + user.getUser_about() + ", Phone Number: " + user.getPhone_number());

                    profileName.setText(user.getUser_name());
                    profileAbout.setText(user.getUser_about());
                    profilePhone.setText(user.getPhone_number());
                    profileEmail.setText(user.getUser_email());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Canceled", error.getMessage());
                DynamicToast.makeError(ProfileActivity.this, "Failed loading profile");
            }
        };
        reference.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}