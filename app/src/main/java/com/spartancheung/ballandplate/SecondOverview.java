package com.spartancheung.ballandplate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondOverview extends Fragment {


    public SecondOverview() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_overview, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ThirdOverview overview = new ThirdOverview();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.overview_container,overview);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        //setTextView("mmmmmmm");
    }
}
