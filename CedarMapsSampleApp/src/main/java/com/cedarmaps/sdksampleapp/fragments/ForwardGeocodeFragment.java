package com.cedarmaps.sdksampleapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cedarmaps.sdksampleapp.R;
import com.cedarmaps.sdksampleapp.SearchViewAdapter;
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.mapbox.MapView;
import com.cedarstudios.cedarmapssdk.listeners.ForwardGeocodeResultsListener;
import com.cedarstudios.cedarmapssdk.model.geocoder.forward.ForwardGeocode;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.List;

public class ForwardGeocodeFragment extends Fragment {

    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private SearchView mSearchView;
    private SearchViewAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayout;
    private State state = State.MAP;

    private enum State {
        MAP,
        MAP_PIN,
        SEARCHING,
        RESULTS
    }

    private void setState(State state) {
        this.state = state;
        switch (state) {
            case MAP:
            case MAP_PIN:
                mLinearLayout.setVisibility(View.GONE);
                break;
            case SEARCHING:
                mLinearLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case RESULTS:
                mLinearLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_forward_geocode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = view.findViewById(R.id.mapView);
//        mMapView.setStyleUrl("https://api.cedarmaps.com/v1/tiles/light.json");
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;

            mMapboxMap.setMaxZoomPreference(17);
            mMapboxMap.setMinZoomPreference(6);
        });

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mLinearLayout = view.findViewById(R.id.search_results_linear_layout);
        mProgressBar = view.findViewById(R.id.search_progress_bar);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && state == State.MAP_PIN) {
                setState(State.RESULTS);
                return true;
            }
            return false;
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_view_menu_item, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (menu == null || menu.findItem(R.id.action_search) == null) {
            return;
        }
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.search_in_places));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView = searchView;

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && state == State.MAP_PIN) {
                mMapboxMap.clear();
                if (!TextUtils.isEmpty(searchView.getQuery())) {
                    setState(State.RESULTS);
                } else {
                    setState(State.MAP);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                if (TextUtils.isEmpty(newText)) {
                    mMapboxMap.clear();
                    setState(State.MAP);
                } else {
                    setState(State.SEARCHING);
                    CedarMaps.getInstance().forwardGeocode(newText, new ForwardGeocodeResultsListener() {
                        @Override
                        public void onSuccess(@NonNull List<ForwardGeocode> results) {
                            setState(State.RESULTS);
                            if (results.size() > 0 && newText.equals(mSearchView.getQuery().toString())) {
                                mRecyclerAdapter = new SearchViewAdapter(results);
                                mRecyclerView.setAdapter(mRecyclerAdapter);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull String errorMessage) {
                            setState(State.RESULTS);
                            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                return false;
            }
        });

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search :
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showItemOnMap(final ForwardGeocode item) {
        setState(State.MAP_PIN);
        mMapboxMap.clear();

        mSearchView.clearFocus();

        final Marker marker = mMapboxMap.addMarker(new MarkerOptions()
                .position(item.getLocation().getCenter())
                .title(item.getName())
                .snippet(item.getAddress())
        );
        mMapboxMap.selectMarker(marker);

        if (item.getLocation().getCenter() != null) {
            mMapboxMap.easeCamera(CameraUpdateFactory.newLatLng(item.getLocation().getCenter()), 1000);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mMapView = null;
    }
}
