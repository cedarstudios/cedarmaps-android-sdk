package com.cedarstudios.cedarmaps.sample;

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

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private ListView mDrawerList;

    private ArrayList<String> testFragmentNames;

    private int selectedFragmentIndex = 0;

    public static final String PREF_NAME = "pref";

    public static final String PREF_ID_ACCESS_TOKEN = "access_token";

    public static final String PREF_ID_TOKEN_TYPE = "token_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
           MapView.setDebugMode(true); //make sure to call this before the view is created!
           */
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        testFragmentNames = new ArrayList<>();
        testFragmentNames.add(getString(R.string.mainTestMap));
        testFragmentNames.add(getString(R.string.markersTestMap));
        testFragmentNames.add(getString(R.string.itemizedOverlayTestMap));
        testFragmentNames.add(getString(R.string.searchAPI));
        testFragmentNames.add(getString(R.string.streetSearchAPIAdvanced));
        testFragmentNames.add(getString(R.string.reverseGeocode));

        mDrawerList
                .setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, testFragmentNames));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.mipmap.ic_drawer, R.string.drawerOpen, R.string.drawerClose) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(testFragmentNames.get(selectedFragmentIndex));
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set MainTestFragment
        selectItem(0);

        new CedarMapsAuthenticateTask().execute();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        selectedFragmentIndex = position;
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = new MainTestFragment();
                break;
            case 1:
                fragment = new MarkersTestFragment();
                break;
            case 2:
                fragment = new ItemizedIconOverlayTestFragment();
                break;
            case 3:
                fragment = new APIGeocodeTestFragment();
                break;
            case 4:
                fragment = new APIAdvancedGeocodeTestFragment();
                break;
            case 5:
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

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(testFragmentNames.get(position));
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
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
