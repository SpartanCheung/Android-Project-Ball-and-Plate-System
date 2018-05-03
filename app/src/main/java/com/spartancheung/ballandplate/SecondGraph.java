package com.spartancheung.ballandplate;


import android.os.Bundle;
import android.support.annotation.Nullable;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        System.out.println("oncreate2222.....................................");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        System.out.println("oncreateview2222.....................................");
        return inflater.inflate(R.layout.fragment_second_graph, container, false);
    }

    private ThirdGraph thirdGraph = null;

    @Override
    public void onStart() {
        super.onStart();
        if(thirdGraph == null) {
            thirdGraph = new ThirdGraph();
            FragmentTransaction ft_graph = getChildFragmentManager().beginTransaction();
            ft_graph.add(R.id.graph_container, thirdGraph);
            ft_graph.addToBackStack(null);
            ft_graph.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft_graph.commit();
        }
//        System.out.println("onstart2222.....................................");
    }

    @Override
    public void onStop() {
        super.onStop();
//        System.out.println("onstop2222.....................................");
    }

    @Override
    public void onResume() {
        super.onResume();
//        System.out.println("onresume2222.....................................");
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(redrawer != null){
//            redrawer.pause();
//            redrawer = null;
//        }
//
//        if(getDataThread != null){
//            getDataThread.interrupt();
//            getDataThread = null;
//        }
//        System.out.println("onpause2222.....................................");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        System.out.println("ondestroy2222.....................................");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        System.out.println("ondestroyview2222.....................................");
    }
}
