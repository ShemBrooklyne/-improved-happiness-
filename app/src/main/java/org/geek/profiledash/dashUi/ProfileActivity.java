package org.geek.profiledash.dashUi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUriExposedException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.geek.profiledash.R;
import org.geek.profiledash.model.User;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "Result";
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    @BindView(R.id.profile_name)
    TextView profileName;
    @BindView(R.id.profile_about)
    TextView profileAbout;
    @BindView(R.id.profile_email)
    TextView profileEmail;
    @BindView(R.id.profile_phone)
    TextView profilePhone;
    @BindView(R.id.profile_image)
    ImageView profilePic;
    @BindView(R.id.post_profile)
    ImageView uploadProfile;
    // firebase storage and reference instance
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uploadProfile.setOnClickListener(this);
        profilePic.setOnClickListener(this);

        // Firebase storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        readDbData();
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image from here..."), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore
                        .Images.Media.getBitmap(getContentResolver(), filePath);
                profilePic.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImage() throws FileUriExposedException {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //child storage ref
            StorageReference reference = FirebaseStorage.getInstance().getReference()
                    .child("profileImages")
                    .child(userId + ".jpeg");
            //upload listeners
            //listens to failure of image
            reference.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                progressDialog.dismiss();
                getDownloadUrl(reference);
                DynamicToast.makeSuccess(ProfileActivity.this, "Profile image uploaded!");
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                DynamicToast.makeError(ProfileActivity.this, "Profile upload failed!");
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }

    public void getDownloadUrl(StorageReference storageReference) {
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(TAG, "onSuccess: " + uri);
            setUserProfileUrl(uri);
        });
    }

    public void setUserProfileUrl(Uri uri) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri).build();
        assert firebaseUser != null;
        firebaseUser.updateProfile(request)
                .addOnSuccessListener(unused -> DynamicToast.makeSuccess(ProfileActivity.this, "Profile image set!"))
                .addOnFailureListener(e -> DynamicToast.makeError(ProfileActivity.this, "Profile image failed!"));
    }

    public void readDbData() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        assert firebaseUser != null;
        String userID = firebaseUser.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Log.d(TAG, "User Name: " + user.getUser_name() + ", User Email: " + user.getUser_email() + ", User About: " + user.getUser_about() + ", Phone Number: " + user.getPhone_number());

                    profileName.setText(user.getUser_name());
                    profileAbout.setText(user.getUser_about());
                    profilePhone.setText(user.getPhone_number());
                    profileEmail.setText(user.getUser_email());
                }
                if (firebaseUser.getPhotoUrl() != null) {
                    Glide.with(ProfileActivity.this)
                            .load(firebaseUser.getPhotoUrl()).into(profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Canceled", error.getMessage());
                DynamicToast.makeError(ProfileActivity.this, "Failed loading profile");

            }
        });

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

    @Override
    public void onClick(View v) {
        // done dirty
        if (v == uploadProfile) {
            uploadImage();
        }

        if (v == profilePic) {
            selectImage();
        }
    }
}