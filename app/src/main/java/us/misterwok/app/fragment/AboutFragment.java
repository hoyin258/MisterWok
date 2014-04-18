package us.misterwok.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import us.misterwok.app.R;

/**
 * Created by hoyin on 18/4/14.
 * Yintro.com
 */
public class AboutFragment extends BaseFragment {

    private GoogleMap mMap;

    public static Fragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        TextView mAddress = (TextView) rootView.findViewById(R.id.text_view_address);
        TextView mPhone = (TextView) rootView.findViewById(R.id.text_view_phone);

        mAddress.setText(getString(R.string.address_for_display, getString(R.string.store_address)));
        mPhone.setText(getString(R.string.phone_number_for_display, getString(R.string.store_phone_number)));

        LatLng latLng = new LatLng(
                Double.parseDouble(getString(R.string.store_address_lat)),
                Double.parseDouble(getString(R.string.store_address_lng)));
        mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.support_map_fragment)).getMap();
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.app_name)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        return rootView;
    }
}
