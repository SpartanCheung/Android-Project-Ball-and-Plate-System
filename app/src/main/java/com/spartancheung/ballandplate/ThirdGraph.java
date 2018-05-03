package com.spartancheung.ballandplate;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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


    private final static int MSG_DRAW = 1;

    private XYPlot xDirPlot;
    private XYPlot yDirPlot;
    private SimpleXYSeries xDirGoal;
    private SimpleXYSeries yDirGoal;
    private SimpleXYSeries xDirSeries;
    private SimpleXYSeries yDirSeries;

//    private Redrawer redrawer;

    interface BlePosVal {
        int[] readVal ();
    }
    private BlePosVal blePosVal;
    private int xVal;
    private int yVal;
    private int xGoalVal;
    private int yGoalVal;

    private class XY {
        public int x;
        public int y;
        public int xGoal;
        public int yGoal;
    }

    Handler valReadHandler;

    private static int threadCount = 0;

    private class GetDataThread extends Thread{
        private int threadNo;
        public GetDataThread(){
            threadNo = threadCount++;
        }
        @Override
        public void run() {
            threadCount++;
            while (true) {
                if(isInterrupted()){
                    break;
                }
                int [] vals = blePosVal.readVal();
                xVal = vals[0];
                yVal = vals[1];
                xGoalVal = vals[2];
                yGoalVal = vals[3];
                XY xy = new XY();
                xy.x = xVal;
                xy.y = yVal;
                xy.xGoal = xGoalVal;
                xy.yGoal = yGoalVal;
                Message msg = new Message();
                msg.what = MSG_DRAW;
                msg.obj = xy;
                valReadHandler.sendMessage(msg);

                try {
                    Thread.sleep(45);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                System.out.println("读取数据 " + threadNo);
            }
        }
    }

    private static int count = 0;

    public ThirdGraph() {
        System.out.println("构造thirdgraph" + count++);
        // Required empty public constructor
        xDirSeries = new SimpleXYSeries("real-time value");
        xDirSeries.useImplicitXVals();

        yDirSeries = new SimpleXYSeries("real-time value");
        yDirSeries.useImplicitXVals();

        xDirGoal = new SimpleXYSeries("Goal");
        xDirGoal.useImplicitXVals();

        yDirGoal = new SimpleXYSeries("Goal");
        yDirGoal.useImplicitXVals();

        valReadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_DRAW: {
//                        System.out.println("收到数据");
                        XY xy = (XY)msg.obj;
                        xVal = xy.x;
                        yVal = xy.y;
                        xGoalVal = xy.xGoal;
                        yGoalVal = xy.yGoal;
                        if (xDirSeries.size()>HISTORY_SIZE){
                            xDirSeries.removeFirst();
                            yDirSeries.removeFirst();
                            xDirGoal.removeFirst();
                            yDirGoal.removeFirst();
                        }
                        xDirSeries.addLast(null,xVal);
                        yDirSeries.addLast(null,yVal);
                        xDirGoal.addLast(null,xGoalVal);
                        yDirGoal.addLast(null,yGoalVal);


                        if(xDirPlot != null && yDirPlot != null) {
                            xDirPlot.redraw();
                            yDirPlot.redraw();
                        }

                        break;
                    }
                    default:
                        break;
                }
            }
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third_graph, container, false);

        xDirPlot = (XYPlot) view.findViewById(R.id.xDirectionPlot);
        xDirPlot.addSeries(xDirGoal,new LineAndPointFormatter(Color.rgb(0,255,0),null,null,null));
        xDirPlot.addSeries(xDirSeries,new LineAndPointFormatter(Color.rgb(0,0,255),null,null,null));
        xDirPlot.setDomainStepMode(StepMode.INCREMENT_BY_VAL);
        xDirPlot.setDomainStepValue(HISTORY_SIZE/10);
        xDirPlot.setDomainBoundaries(0,HISTORY_SIZE,BoundaryMode.FIXED);
        xDirPlot.setRangeBoundaries(0,320, BoundaryMode.FIXED);
        xDirPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("#"));
//        final PlotStatistics xPlotStats = new PlotStatistics(1000,false);
        xDirPlot.setLayerType(View.
                LAYER_TYPE_NONE,null);
//        xPlotStats.setAnnotatePlotEnabled(true);

        yDirPlot = (XYPlot) view.findViewById(R.id.yDirectionPlot);
        yDirPlot.addSeries(yDirGoal,new LineAndPointFormatter(Color.rgb(0,255,0),null,null,null));
        yDirPlot.addSeries(yDirSeries,new LineAndPointFormatter(Color.rgb(255,64,129),null,null,null));
        yDirPlot.setDomainStepMode(StepMode.INCREMENT_BY_VAL);
        yDirPlot.setDomainStepValue(HISTORY_SIZE/10);
        yDirPlot.setDomainBoundaries(0,HISTORY_SIZE,BoundaryMode.FIXED);
        yDirPlot.setRangeBoundaries(0,250,BoundaryMode.FIXED);
//        yDirPlot.setRangeStepMode(StepMode.INCREMENT_BY_VAL);
        yDirPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new DecimalFormat("#"));
//        final PlotStatistics yPlotStats = new PlotStatistics(1000,false);
        yDirPlot.setLayerType(View.LAYER_TYPE_NONE,null);
//        yPlotStats.setAnnotatePlotEnabled(true);
        //Log.v(LOG_TAG,"All plot set is done...");



        System.out.println("oncreateview.....................................");


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("oncreate.....................................");
    }

    private GetDataThread getDataThread = null;

    @Override
    public void onStart() {
        super.onStart();

        if(getDataThread == null){
            getDataThread = new GetDataThread();
            getDataThread.start();
        }
        System.out.println("onstart.....................................");

    }

    @Override
    public void onStop() {
        super.onStop();
        if(getDataThread != null){
            getDataThread.interrupt();
            getDataThread = null;
        }
        System.out.println("onstop.....................................");
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(redrawer == null) {
//            redrawer = new Redrawer(Arrays.asList(new Plot[]{xDirPlot, yDirPlot}),
//                    40, true);
//        }


        if(getDataThread == null){
            getDataThread = new GetDataThread();
            getDataThread.start();
        }
        System.out.println("onresume.....................................");
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(redrawer != null){
//            redrawer.finish();
//            redrawer = null;
//        }

        if(getDataThread != null){
            getDataThread.interrupt();
            getDataThread = null;
        }
        System.out.println("onpause.....................................");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("ondestroy.....................................");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("ondestroyview.....................................");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.blePosVal = (BlePosVal) context;
        //Log.v(LOG_TAG,"Third Graph attached!");
    }
}
