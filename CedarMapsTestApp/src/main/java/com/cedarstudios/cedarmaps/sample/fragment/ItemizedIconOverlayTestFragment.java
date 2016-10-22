package com.cedarstudios.cedarmaps.sample.fragment;

import android.widget.Toast;

import com.cedarstudios.cedarmaps.sample.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class ItemizedIconOverlayTestFragment extends MainTestFragment {
    @Override
    protected void onMapLoaded() {
        mMapView.getController().setZoom(15);
        mMapView.getController().setCenter(new GeoPoint(35.762734, 51.432126));

        final ArrayList<OverlayItem> items = new ArrayList<>();
        items.add(new OverlayItem(getString(R.string.haghani_metro), "", new GeoPoint(35.759926, 51.432512)));
        items.add(new OverlayItem(getString(R.string.third_street), "", new GeoPoint(35.762329, 51.429722)));
        items.add(new OverlayItem(getString(R.string.haghani_way), "", new GeoPoint(35.759055, 51.427362)));
        items.add(new OverlayItem(getString(R.string.tabrizian), "", new GeoPoint(35.762538, 51.435173)));

			/* OnTapListener for the Markers, shows a simple Toast. */
        ItemizedOverlay<OverlayItem> mMyLocationOverlay = new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Toast.makeText(getActivity(), "Item '" + item.getTitle() + "' (index=" + index
                                + ") got single tapped up", Toast.LENGTH_LONG).show();
                        return true; // We 'handled' this event.
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Toast.makeText(getActivity(), "Item '" + item.getTitle() + "' (index=" + index
                                + ") got long pressed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }, getContext());
        mMapView.getOverlays().add(mMyLocationOverlay);


    }

}
