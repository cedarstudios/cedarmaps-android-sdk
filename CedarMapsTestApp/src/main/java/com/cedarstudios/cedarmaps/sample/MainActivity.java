package com.cedarstudios.cedarmaps.sample;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cedarstudios.cedarmaps.sample.fragment.APIAdvancedGeocodeTestFragment;
import com.cedarstudios.cedarmaps.sample.fragment.APIGeocodeTestFragment;
import com.cedarstudios.cedarmaps.sample.fragment.APIReverseGeocodeTestFragment;
import com.cedarstudios.cedarmaps.sample.fragment.ItemizedIconOverlayTestFragment;
import com.cedarstudios.cedarmaps.sample.fragment.MainTestFragment;
import com.cedarstudios.cedarmaps.sample.fragment.MarkersTestFragment;
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.CedarMapsException;
import com.cedarstudios.cedarmapssdk.CedarMapsFactory;
import com.cedarstudios.cedarmapssdk.auth.OAuth2Token;
import com.cedarstudios.cedarmapssdk.config.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final String PREF_NAME = "pref";

    public static final String PREF_ID_ACCESS_TOKEN = "access_token";

    public static final String PREF_ID_TOKEN_TYPE = "token_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        new CedarMapsAuthenticateTask().execute();

        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment;

        switch (item.getItemId()) {
            case R.id.mainTestMap:
                fragment = new MainTestFragment();
                break;
            case R.id.markersTestMap:
                fragment = new MarkersTestFragment();
                break;
            case R.id.itemizedOverlayTestMap:
                fragment = new ItemizedIconOverlayTestFragment();
                break;
            case R.id.searchAPI:
                fragment = new APIGeocodeTestFragment();
                break;
            case R.id.streetSearchAPIAdvanced:
                fragment = new APIAdvancedGeocodeTestFragment();
                break;
            case R.id.reverseGeocode:
                fragment = new APIReverseGeocodeTestFragment();
                break;
            default:
                fragment = new MainTestFragment();
                break;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        setTitle(item.getTitle());
        return true;
    }

    class CedarMapsAuthenticateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            com.cedarstudios.cedarmapssdk.config.Configuration
                    configuration = new ConfigurationBuilder()
                    .setClientId(Constants.CLIENT_ID)
                    .setClientSecret(Constants.CLIENT_SECRET)
                    .build();

            CedarMapsFactory factory = new CedarMapsFactory(configuration);
            CedarMaps cedarMaps = factory.getInstance();
            try {
                OAuth2Token oAuth2Token = cedarMaps.getOAuth2Token();

                SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(PREF_ID_ACCESS_TOKEN, oAuth2Token.getAccessToken());
                editor.putString(PREF_ID_TOKEN_TYPE, oAuth2Token.getTokenType());
                editor.commit();

            } catch (CedarMapsException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
