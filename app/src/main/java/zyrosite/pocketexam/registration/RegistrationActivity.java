package zyrosite.pocketexam.registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import zyrosite.pocketexam.R;

public class RegistrationActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        frameLayout = findViewById(R.id.registration_frameLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_new_round);
        setFragment(new PhoneNumberFragment());
    }

    private void setFragment(Fragment fragment) {
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        /*
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                String BackStackList = "";
                for (int i=0; i<fm.getBackStackEntryCount(); i++) {
                    BackStackList = BackStackList+"\n"+fm.getBackStackEntryAt(i);
                }
                Toast.makeText(RegistrationActivity.this, BackStackList, Toast.LENGTH_SHORT).show();
            }
        });
        */
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() < 2) {
            super.onBackPressed();
        }else {
            fm.popBackStack("OTP", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
