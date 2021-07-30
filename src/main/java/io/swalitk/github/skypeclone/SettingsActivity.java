package io.swalitk.github.skypeclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.budiyev.android.imageloader.ImageLoader;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private EditText et_username, et_status;
    private ImageView img_profile;
    private Button btn_save;

    private int GALLERY_INTENT=1;
    private Uri profileUri;

    ProgressDialog dialog;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseAuth userAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        et_username=findViewById(R.id.settings_username);
        et_status=findViewById(R.id.settings_status);
        img_profile=findViewById(R.id.settings_profile_image);
        btn_save=findViewById(R.id.settings_save_button);

        dialog=new ProgressDialog(this);
        userAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference= FirebaseStorage.getInstance().getReference().child("ProfileImages");

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profileUri==null){
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("imageUrl")){
                                saveUserInfoOnly();
                            }else{
                                Toast.makeText(SettingsActivity.this, "Please select image first", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    saveProfileToDatabase();
                }
            }
        });
        retriveUserInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_INTENT){
            profileUri=data.getData();
            img_profile.setImageURI(profileUri);
        }
    }
    private void saveProfileToDatabase(){
        final String username_text=et_username.getText().toString();
        final String status_text=et_status.getText().toString();
        if(username_text.equals("") && status_text.equals("")){
            Toast.makeText(this, "Username and status are mandatory!!", Toast.LENGTH_SHORT).show();
        }else{
            dialog.setTitle("Saving...");
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            StorageReference path=storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            UploadTask task=path.putFile(profileUri);
            task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return path.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        String imageUrl=task.getResult().toString();
                        HashMap<String, Object> userData=new HashMap<>();
                        userData.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userData.put("username", username_text);
                        userData.put("status", status_text);
                        userData.put("imageUrl", imageUrl);
                        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    dialog.dismiss();
                                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                                    finish();
                                }else{
                                    dialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Error happened while saving", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(SettingsActivity.this, "Error while competing", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public void saveUserInfoOnly(){
        final String username_text=et_username.getText().toString();
        final String status_text=et_status.getText().toString();
        if(username_text.equals("") && status_text.equals("")){
            Toast.makeText(this, "Username and status are mandatory!!", Toast.LENGTH_SHORT).show();
        }else{
            dialog.setTitle("Saving...");
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            HashMap<String, Object> userData=new HashMap<>();
            userData.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            userData.put("username", username_text);
            userData.put("status", status_text);

            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        dialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                        finish();
                    }else{
                        dialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error happened while saving", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public void retriveUserInfo(){
        dialog.setTitle("Settings");
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    String username=snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").getValue().toString();
                    String status=snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").getValue().toString();
                    String imageUrl=snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imageUrl").getValue().toString();
                    et_username.setText(username);
                    et_status.setText(status);
                    ImageLoader.with(SettingsActivity.this).from(imageUrl).load(img_profile);
                    dialog.dismiss();
                }else{
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}