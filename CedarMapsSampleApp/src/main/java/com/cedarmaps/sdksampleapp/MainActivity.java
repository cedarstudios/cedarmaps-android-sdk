package com.cedarmaps.sdksampleapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.cedarmaps.sdksampleapp.fragments.DirectionFragment;
import com.cedarmaps.sdksampleapp.fragments.ForwardGeocodeFragment;
import com.cedarmaps.sdksampleapp.fragments.MapFragment;
import com.cedarmaps.sdksampleapp.fragments.ReverseGeocodeFragment;
import com.cedarmaps.sdksampleapp.fragments.StaticMapFragment;
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.listeners.OnTilesConfigured;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private Integer currentlySelectedMenuID = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


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

            if (fragment != null && (currentlySelectedMenuID == null || currentlySelectedMenuID != item.getItemId())) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment).commit();
                currentlySelectedMenuID = item.getItemId();
                return true;
            }

            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CedarMaps.getInstance().setClientID(Constants.CLIENT_ID);
        CedarMaps.getInstance().setClientSecret(Constants.CLIENT_SECRET);
        CedarMaps.getInstance().setContext(this);

        CedarMaps.getInstance().prepareTiles(new OnTilesConfigured() {
            @Override
            public void onSuccess() {
                BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constants.PERMISSION_LOCATION_REQUEST_CODE:
                if (!(grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED)) {
                    Toast.makeText(this, "App needs location to function", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
