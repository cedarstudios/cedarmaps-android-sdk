package com.cedarmaps.sdksampleapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cedarmaps.sdksampleapp.fragments.DirectionFragment;
import com.cedarmaps.sdksampleapp.fragments.ForwardGeocodeFragment;
import com.cedarmaps.sdksampleapp.fragments.MapFragment;
import com.cedarmaps.sdksampleapp.fragments.ReverseGeocodeFragment;
import com.cedarmaps.sdksampleapp.fragments.StaticMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.android.core.permissions.PermissionsListener;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(MainActivity.this);
        navigation.setSelectedItemId(R.id.navigation_map);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_map:
                setTitle(R.string.title_map);
                fragment = new MapFragment();
                break;
            case R.id.navigation_reverse:
                setTitle(R.string.title_reverse);
                fragment = new ReverseGeocodeFragment();
                break;
            case R.id.navigation_forward:
                setTitle("");
                fragment = new ForwardGeocodeFragment();
                break;
            case R.id.navigation_direction:
                setTitle(R.string.title_direction);
                fragment = new DirectionFragment();
                break;
            case R.id.navigation_static:
                setTitle(R.string.title_static);
                fragment = new StaticMapFragment();
                break;
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (getSupportFragmentManager().getFragments().isEmpty()) {
                transaction.add(R.id.content, fragment, String.format(Locale.US, "item: %d", item.getItemId())).commit();
            } else {
                transaction.replace(R.id.content, fragment, String.format(Locale.US, "item: %d", item.getItemId())).commit();
                invalidateOptionsMenu();
            }
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        List allFragments = getSupportFragmentManager().getFragments();
        if (allFragments.isEmpty()) {
            return;
        }
        Fragment currentFragment = (Fragment) allFragments.get(allFragments.size() - 1);
        if (currentFragment instanceof PermissionsListener) {
            currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
