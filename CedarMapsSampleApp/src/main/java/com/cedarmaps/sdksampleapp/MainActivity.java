package com.cedarmaps.sdksampleapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cedarmaps.sdksampleapp.fragments.DirectionFragment;
import com.cedarmaps.sdksampleapp.fragments.ForwardGeocodeFragment;
import com.cedarmaps.sdksampleapp.fragments.MapFragment;
import com.cedarmaps.sdksampleapp.fragments.ReverseGeocodeFragment;
import com.cedarmaps.sdksampleapp.fragments.StaticMapFragment;
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.listeners.OnTilesConfigured;

public class MainActivity extends AppCompatActivity {

    private Integer currentlySelectedMenuID = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
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

                if (fragment != null && (currentlySelectedMenuID == null || currentlySelectedMenuID != item.getItemId())) {
                    invalidateOptionsMenu();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    currentlySelectedMenuID = item.getItemId();
                    transaction.add(R.id.content, fragment).commit();
                    return true;
                }

                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CedarMaps.getInstance().prepareTiles(new OnTilesConfigured() {
            @Override
            public void onSuccess() {
                BottomNavigationView navigation = findViewById(R.id.navigation);
                navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                navigation.setSelectedItemId(R.id.navigation_map);
                currentlySelectedMenuID = navigation.getSelectedItemId();
            }

            @Override
            public void onFailure(@NonNull String error) {
                Log.e("MainActivity", error);
            }
        });
    }
}
