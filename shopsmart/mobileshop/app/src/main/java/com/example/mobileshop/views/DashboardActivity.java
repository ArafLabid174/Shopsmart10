package com.example.mobileshop.views;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobileshop.R;
import com.example.mobileshop.views.fragments.CartFragment;
import com.example.mobileshop.views.fragments.OffersFragment;
import com.example.mobileshop.views.fragments.ProfileFragment;
import com.example.mobileshop.views.fragments.ScanFragment;
import com.example.mobileshop.views.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the default fragment to ScanFragment
        loadFragment(new ScanFragment());

        // Set listener for bottom navigation item selection using if–else instead of switch-case
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();
                if (id == R.id.nav_offers) {
                    selectedFragment = new OffersFragment();
                } else if (id == R.id.nav_cart) {
                    selectedFragment = new CartFragment();
                } else if (id == R.id.nav_scan) {
                    selectedFragment = new ScanFragment();
                } else if (id == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (id == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                }
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });
    }

    // Method to load the selected fragment into the container
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
