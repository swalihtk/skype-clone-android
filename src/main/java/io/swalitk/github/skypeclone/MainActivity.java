package io.swalitk.github.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.budiyev.android.imageloader.ImageLoader;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BottomNavigationView navigationView;
    private ProgressDialog dialog;
    private FirebaseAuth userAuth;
    private DatabaseReference databaseReference;

    ArrayList<String> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.main_recycler_view);
        navigationView=findViewById(R.id.bottom_navigation_view);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        arrayList=new ArrayList<>();

        dialog=new ProgressDialog(this);
        navigationView.setOnNavigationItemSelectedListener(itemSelectedListener);

        All_Users all_users=new All_Users(this);
        all_users.getTotalUsers(new All_Users.Response_error_listener() {
            @Override
            public void onError(String err) {
                Toast.makeText(MainActivity.this, err.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccuss(ArrayList<All_users_list> users_lists) {
                customRecyclerView customRecyclerView=new customRecyclerView(MainActivity.this, users_lists);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(customRecyclerView);
                Toast.makeText(MainActivity.this, users_lists.get(0).getUsername(), Toast.LENGTH_SHORT).show();
                Log.d("myKey", users_lists.toString());
            }
        });

    }
    private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu_home:
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                    break;
                case R.id.menu_settings:
                    Intent newIntent=new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(newIntent);
                    break;
                case R.id.menu_notifications:
                    Toast.makeText(MainActivity.this, "Notification Activity", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.menu_logout:

                    logoutUser();
                    break;

            }
            return false;
        }
    };

    public void logoutUser(){

        AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
        adb.setTitle("Do you want to logout?");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressDialog dialog1=new ProgressDialog(MainActivity.this);
                dialog1.setTitle("Login out");
                dialog1.setMessage("Please wait");
                dialog1.show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                finish();
                dialog1.dismiss();
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        adb.show();
    }

    public class customUsersViewHolder extends RecyclerView.ViewHolder{

        TextView username_textView;
        ImageView dp_imageView;
        CardView userContainer;

        public customUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            username_textView=itemView.findViewById(R.id.card_username_textview);
            dp_imageView=itemView.findViewById(R.id.card_image_view);
            userContainer=itemView.findViewById(R.id.card_user_list_container);
        }
    }

    public  class customRecyclerView extends RecyclerView.Adapter<customUsersViewHolder>{
        @NonNull

        ArrayList<All_users_list> users_lists;
        Context context;

        customRecyclerView(Context context, ArrayList<All_users_list> users_lists){
            this.context=context;
            this.users_lists=users_lists;
        }

        @Override
        public customUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new customUsersViewHolder(LayoutInflater.from(context).inflate(R.layout.main_activity_design, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainActivity.customUsersViewHolder holder, int position) {
            String username=users_lists.get(position).getUsername();
            String status=users_lists.get(position).getStatus();
            String imageUrl=users_lists.get(position).getImageUrl();

            holder.username_textView.setText(username);
            ImageLoader.with(MainActivity.this).from(imageUrl).load(holder.dp_imageView);
            holder.userContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this, VideoCallActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return users_lists.size();
        }
    }


}