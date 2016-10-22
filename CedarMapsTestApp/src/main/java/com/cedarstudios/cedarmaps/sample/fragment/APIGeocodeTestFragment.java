package com.cedarstudios.cedarmaps.sample.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.cedarstudios.cedarmaps.sample.Constants;
import com.cedarstudios.cedarmaps.sample.MainActivity;
import com.cedarstudios.cedarmaps.sample.R;
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.CedarMapsException;
import com.cedarstudios.cedarmapssdk.CedarMapsFactory;
import com.cedarstudios.cedarmapssdk.config.Configuration;
import com.cedarstudios.cedarmapssdk.config.ConfigurationBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class APIGeocodeTestFragment extends MainTestFragment implements View.OnClickListener {

    private EditText mSearchEditText;

    private ArrayList<Marker> mMarkers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    }

    @Override
    public void onClick(View v) {
        if (!TextUtils.isEmpty(mSearchEditText.getText().toString())) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            clearMarkers();

            new SearchAsyncTask().execute(mSearchEditText.getText().toString().trim());
        }
    }

    private void clearMarkers() {
        mMapView.getOverlays().clear();
        mMapView.invalidate();
        mMapView.getController().setZoom(12);
        mMapView.getController().animateTo(new GeoPoint(35.6961, 51.4231)); // center of tehran

        mMarkers.clear();
    }

    class SearchAsyncTask extends AsyncTask<String, Void, JSONObject> {

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
                SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);

                Configuration configuration = new ConfigurationBuilder()
                        .setOAuth2AccessToken(pref.getString(MainActivity.PREF_ID_ACCESS_TOKEN, ""))
                        .setOAuth2TokenType(pref.getString(MainActivity.PREF_ID_TOKEN_TYPE, ""))
                        .setMapId(Constants.MAPID_CEDARMAPS_STREETS)
                        .build();

                CedarMaps cedarMaps = new CedarMapsFactory(configuration).getInstance();
                searchResult = cedarMaps.geocode(params[0]);
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
                    JSONArray array = jsonObject.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.getJSONObject(i);
                        String[] location = item.getJSONObject("location").getString("center").split(",");
                        GeoPoint latLng = new GeoPoint(Double.parseDouble(location[0]), Double.parseDouble(location[1]));
                        Marker marker = new Marker(mMapView);
                        marker.setPosition(latLng);
                        marker.setTitle(item.getString("name"));
                        mMapView.getOverlays().add(marker);
                    }
                } else if (status.equals("ZERO_RESULTS")) {
                    Toast.makeText(getActivity(), getString(R.string.no_results), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.unkonown_error), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.parse_error), Toast.LENGTH_LONG).show();
            }
        }
    }
}
