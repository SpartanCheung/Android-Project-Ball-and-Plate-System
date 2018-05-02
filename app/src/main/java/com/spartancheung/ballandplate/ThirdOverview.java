package com.spartancheung.ballandplate;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdOverview extends Fragment {
    interface BleString {
        String readString ();
    }

    private BleString bleString;
    private String mBleInString;

    /*View view = getView();
    final TextView mView = view.findViewById(R.id.test_display);

    Handler readHandler;
    Runnable testRun = new Runnable() {
        @Override
        public void run() {
            mView.setText(bleString.readString());
            readHandler.postDelayed(this,100);
        }
    };*/

    public ThirdOverview() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third_overview, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.bleString = (BleString)context;
    }

    @Override
    public void onResume() {
        super.onResume();
        //readHandler = new Handler();
        //readHandler.post(testRun);
    }

    @Override
    public void onPause() {
        super.onPause();
        //readHandler.removeCallbacksAndMessages(testRun);
    }
}
