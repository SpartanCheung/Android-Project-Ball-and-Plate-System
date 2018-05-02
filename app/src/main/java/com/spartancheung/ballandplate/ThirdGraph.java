package com.spartancheung.ballandplate;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.Plot;
import com.androidplot.SeriesRegistry;
import com.androidplot.util.PlotStatistics;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdGraph extends Fragment {
    private static final String LOG_TAG = "ThirdGraph";
    private static final int HISTORY_SIZE = 100;

    private XYPlot xDirPlot;
    private XYPlot yDirPlot;
    private SimpleXYSeries xDirSeries;
    private SimpleXYSeries yDirSeries;

    private Redrawer redrawer;

    interface BlePosVal {
        int readVal ();
    }
    private BlePosVal blePosVal;
    private int xVal;
    private int yVal;

    Handler valReadHandler;
    Runnable updateData = new Runnable() {
        @Override
        public void run() {
            xVal = blePosVal.readVal();
            yVal = xVal;
            if (xDirSeries.size()>HISTORY_SIZE){
                xDirSeries.removeFirst();
                yDirSeries.removeFirst();
            }
            xDirSeries.addLast(null,xVal);
            yDirSeries.addLast(null,yVal);
            //Log.v(LOG_TAG,Integer.toString(xVal));
            valReadHandler.postDelayed(this,50);
        }
    };


    public ThirdGraph() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third_graph, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        Log.v(LOG_TAG,"View founded!");

        xDirPlot = (XYPlot) view.findViewById(R.id.xDirectionPlot);
        xDirSeries = new SimpleXYSeries("real-time value");
        xDirSeries.useImplicitXVals();
        xDirPlot.addSeries(xDirSeries,new LineAndPointFormatter(Color.rgb(0,0,255),null,null,null));
        xDirPlot.setDomainStepMode(StepMode.INCREMENT_BY_VAL);
        xDirPlot.setDomainStepValue(HISTORY_SIZE/10);
        xDirPlot.setDomainBoundaries(0,HISTORY_SIZE,BoundaryMode.FIXED);
        xDirPlot.setRangeBoundaries(0,300, BoundaryMode.FIXED);
        xDirPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("#"));
        final PlotStatistics xPlotStats = new PlotStatistics(1000,false);
        xDirPlot.setLayerType(View.LAYER_TYPE_NONE,null);
        xPlotStats.setAnnotatePlotEnabled(true);


        yDirSeries = new SimpleXYSeries("real-time value");
        yDirSeries.useImplicitXVals();
        yDirPlot = (XYPlot) view.findViewById(R.id.yDirectionPlot);
        yDirPlot.addSeries(yDirSeries,new LineAndPointFormatter(Color.rgb(255,64,129),null,null,null));
        yDirPlot.setDomainStepMode(StepMode.INCREMENT_BY_VAL);
        yDirPlot.setDomainStepValue(HISTORY_SIZE/10);
        yDirPlot.setDomainBoundaries(0,HISTORY_SIZE,BoundaryMode.FIXED);
        yDirPlot.setRangeBoundaries(0,200,BoundaryMode.FIXED);
        yDirPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("#"));
        final PlotStatistics yPlotStats = new PlotStatistics(1000,false);
        yDirPlot.setLayerType(View.LAYER_TYPE_NONE,null);
        yPlotStats.setAnnotatePlotEnabled(true);
        //Log.v(LOG_TAG,"All plot set is done...");
        valReadHandler = new Handler();

        valReadHandler.post(updateData);

        redrawer = new Redrawer(Arrays.asList(new Plot[]{xDirPlot,yDirPlot}),
                10,true);
    }

    @Override
    public void onPause() {
        super.onPause();
        redrawer.pause();
        valReadHandler.removeCallbacksAndMessages(updateData);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.blePosVal = (BlePosVal) context;
        //Log.v(LOG_TAG,"Third Graph attached!");
    }
}
