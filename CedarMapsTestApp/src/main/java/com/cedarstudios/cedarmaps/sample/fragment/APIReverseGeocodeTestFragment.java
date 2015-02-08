package com.cedarstudios.cedarmaps.sample.fragment;

import com.cedarstudios.cedarmaps.sample.Constants;
import com.cedarstudios.cedarmaps.sample.MainActivity;
import com.cedarstudios.cedarmaps.sample.R;
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.CedarMapsException;
import com.cedarstudios.cedarmapssdk.CedarMapsFactory;
import com.cedarstudios.cedarmapssdk.config.Configuration;
import com.cedarstudios.cedarmapssdk.config.ConfigurationBuilder;
import com.cedarstudios.cedarmapssdk.tileprovider.CedarMapsTileLayer;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class APIReverseGeocodeTestFragment extends Fragment implements View.OnClickListener {

    private EditText mSearchEditText;

    private MapView mapView;

    private ArrayList<Marker> mMarkers = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        mapView = (MapView) view.findViewById(R.id.mapView);

        SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.PREF_NAME,
                Context.MODE_PRIVATE);
        String accessToken = pref.getString(MainActivity.PREF_ID_ACCESS_TOKEN, "");
        mapView.setAccessToken(accessToken);

        CedarMapsTileLayer cedarMapsTileLayer = new CedarMapsTileLayer(Constants.MAPID_CEDARMAPS_STREETS);
        mapView.setTileSource(cedarMapsTileLayer);

        mapView.setCenter(new LatLng(35.6961, 51.4231)); // center of tehran
        mapView.setZoom(12);

        view.findViewById(R.id.search).setOnClickListener(this);
        mSearchEditText = (EditText) view.findViewById(R.id.term);
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    view.findViewById(R.id.search).performClick();
                    return true;
                }
                return false;
            }
        });

        mSearchEditText.setText("35.759926, 51.432512");

        return view;
    }

    @Override
    public void onClick(View v) {
        if (!TextUtils.isEmpty(mSearchEditText.getText().toString())) {
            InputMethodManager inputManager = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            clearMarkers();

            new ReverseGeocodeAsyncTask().execute(mSearchEditText.getText().toString().trim());
        }
    }

    private void clearMarkers() {
        for (Marker marker : mMarkers) {
            mapView.removeMarker(marker);
        }
        mapView.setZoom(12);
        mapView.setCenter(new LatLng(35.6961, 51.4231)); // center of tehran

        mMarkers.clear();
        mapView.clear();
    }

    class ReverseGeocodeAsyncTask extends AsyncTask<String, Void, JSONObject> {

        private ProgressDialog mProgress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgress = new ProgressDialog(getActivity());
            mProgress.setMessage(getString(R.string.searching));
            mProgress.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject searchResult = null;
            try {
                SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.PREF_NAME,
                        Context.MODE_PRIVATE);

                Configuration configuration = new ConfigurationBuilder()
                        .setOAuth2AccessToken(pref.getString(MainActivity.PREF_ID_ACCESS_TOKEN, ""))
                        .setOAuth2TokenType(pref.getString(MainActivity.PREF_ID_TOKEN_TYPE, ""))
                        .setMapId(Constants.MAPID_CEDARMAPS_STREETS)
                        .build();

                CedarMaps cedarMaps = new CedarMapsFactory(configuration).getInstance();
                String[] latlng = params[0].split(",");

                searchResult = cedarMaps.geocode(Double.parseDouble(latlng[0]),
                        Double.parseDouble(latlng[1]));
            } catch (CedarMapsException e) {
                e.printStackTrace();
            }
            return searchResult;
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            mProgress.dismiss();

            try {
                String status = jsonObject.getString("status");
                if (status.equals("OK")) {
                    String address = jsonObject
                            .getJSONObject("result")
                            .getString("address");

                    String[] latlng = mSearchEditText.getText().toString().trim().split(",");
                    Marker marker = new Marker(mapView, address, "",
                            new LatLng(Double.parseDouble(latlng[0]),
                                    Double.parseDouble(latlng[1])));
                    marker.setIcon(
                            new Icon(getActivity(), Icon.Size.MEDIUM, "marker-stroked",
                                    "000FFF"));
                    mapView.addMarker(marker);
                    Toast.makeText(getActivity(), address,
                            Toast.LENGTH_LONG)
                            .show();
                } else if (status.equals("ZERO_RESULTS")) {
                    Toast.makeText(getActivity(), getString(R.string.no_results),
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.unkonown_error),
                            Toast.LENGTH_LONG)
                            .show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.parse_error),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
