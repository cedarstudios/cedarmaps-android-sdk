package com.cedarmaps.sdksampleapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.cedarstudios.cedarmapssdk.CedarMapsStyle;
import com.cedarstudios.cedarmapssdk.CedarMapsStyleConfigurator;
import com.cedarstudios.cedarmapssdk.MapView;
import com.cedarstudios.cedarmapssdk.listeners.ForwardGeocodeResultsListener;
import com.cedarstudios.cedarmapssdk.listeners.OnStyleConfigurationListener;
import com.cedarstudios.cedarmapssdk.model.geocoder.forward.ForwardGeocode;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.utils.ColorUtils;

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
    private CircleManager circleManager;

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
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;

            CedarMapsStyleConfigurator.configure(
                    CedarMapsStyle.RASTER_LIGHT, new OnStyleConfigurationListener() {
                        @Override
                        public void onSuccess(Style.Builder styleBuilder) {
                            mMapboxMap.setStyle(styleBuilder, style ->
                                    circleManager = new CircleManager(mMapView, mMapboxMap, style));
                        }

                        @Override
                        public void onFailure(@NonNull String errorMessage) {
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

            mMapboxMap.setMaxZoomPreference(16);
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
                circleManager.deleteAll();
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
                    circleManager.deleteAll();
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
        if (item.getItemId() == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showItemOnMap(final ForwardGeocode item) {
        setState(State.MAP_PIN);
        if (getActivity() == null || item.getLocation().getCenter() == null) {
            return;
        }

        circleManager.deleteAll();
        mSearchView.clearFocus();

        int color = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
        int strokeColor = ContextCompat.getColor(getActivity(), R.color.colorAccent);
        CircleOptions circleOptions = new CircleOptions()
                .withLatLng(item.getLocation().getCenter())
                .withCircleColor(ColorUtils.colorToRgbaString(color))
                .withCircleStrokeWidth(4f)
                .withCircleStrokeColor(ColorUtils.colorToRgbaString(strokeColor))
                .withCircleBlur(0.5f)
                .withCircleRadius(12f);
        circleManager.create(circleOptions);

        circleManager.addClickListener(circle -> {
            if (!TextUtils.isEmpty(item.getAddress())) {
                Toast.makeText(getContext(), item.getAddress(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), item.getName(), Toast.LENGTH_SHORT).show();
            }
        });

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
        if (circleManager != null) {
            circleManager.onDestroy();
        }
        mMapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mMapView = null;
    }
}
