package zyrosite.pocketexam.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import zyrosite.pocketexam.R;
import zyrosite.pocketexam.registration.RegistrationActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static List<String> PREFERRED_EXAMS = new ArrayList<>();
    private String Email, DisplayName, ID, PhotoUrl;
    private FirebaseAuth mAuth;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView profilePhoto;
    private TextView profileName;
    private ProgressBar profileProgressBar;
    private FragmentManager fragmentManager;
    private boolean isHomePage = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        Email = intent.getStringExtra("Email");
        DisplayName = intent.getStringExtra("DisplayName");
        ID = intent.getStringExtra("ID");
        PhotoUrl = intent.getStringExtra("PhotoUrl");
        PREFERRED_EXAMS = intent.getStringArrayListExtra("EXAMS");

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();
        View nav_header_view = navigationView.getHeaderView(0);
        profileName = nav_header_view.findViewById(R.id.main_full_name);
        profilePhoto = nav_header_view.findViewById(R.id.main_profile_image);
        profileProgressBar = nav_header_view.findViewById(R.id.profileProgressBar);
        profileProgressBar.setVisibility(View.VISIBLE);
        setProfile();
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment(this), "Home").commit();
    }

    private void setProfile() {
        profileProgressBar.setVisibility(View.INVISIBLE);
        profileName.setText(DisplayName);
        if (!PhotoUrl.isEmpty())
            Glide.with(this).load(PhotoUrl).apply(new RequestOptions().placeholder(R.drawable.profile_placeholder)).into(profilePhoto);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else if (!isHomePage) {
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment,new HomeFragment(this)).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            isHomePage = true;
        }else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                fragmentTransaction.replace(R.id.nav_host_fragment, new HomeFragment(this)).commit();
                break;
            case R.id.nav_saved_notes:
                isHomePage = false;
                break;
            case R.id.nav_exam_preferences:
                fragmentTransaction.replace(R.id.nav_host_fragment, new ExamPreferencesFragment(this)).commit();
                isHomePage = false;
                break;
            case R.id.nav_sign_out:
                mAuth.signOut();
                finish();
                intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(MainActivity.this, "Unexpected value: " + menuItem.getItemId(), Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START, true);
        return true;
    }
    public static String convertDuration(int millisUntilFinished){
        int min;
        millisUntilFinished /= 1000;
        millisUntilFinished /= 60;
        min = millisUntilFinished % 60;
        millisUntilFinished /= 60;
        int hour = millisUntilFinished % 12;
        return hour>0 ? hour+"H : "+min+"M" : min+" Min";
    }
}