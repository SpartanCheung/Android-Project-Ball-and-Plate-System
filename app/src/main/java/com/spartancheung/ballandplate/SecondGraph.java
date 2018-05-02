package com.spartancheung.ballandplate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondGraph extends Fragment {


    public SecondGraph() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_graph, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ThirdGraph graph = new ThirdGraph();
        FragmentTransaction ft_graph = getChildFragmentManager().beginTransaction();
        ft_graph.add(R.id.graph_container,graph);
        ft_graph.addToBackStack(null);
        ft_graph.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft_graph.commit();
    }
}
