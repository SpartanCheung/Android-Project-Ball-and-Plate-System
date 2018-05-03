package com.spartancheung.ballandplate;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements
        ThirdOverview.BleString, ThirdGraph.BlePosVal{
    private int[] bnpGoalPos = new int[2];

    private final static String LOG_TAG = "MainActivity";
    private BluetoothLeService mBleService;
    private boolean bound_status = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothLeService.bleBinder mBinder = (BluetoothLeService.bleBinder) service;
            mBleService = mBinder.getBle();
            bound_status = true;
            Log.v(LOG_TAG,"mBleService will not be a nullpointer");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound_status = false;
        }
    };


    public void settingBle(View view){
        Intent bleGo = new Intent(this,BluetoothSetting.class);
        startActivity(bleGo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        SectionPager sectionPager = new SectionPager(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(sectionPager);
        TabLayout tab = findViewById(R.id.mytab);
        tab.setupWithViewPager(viewPager);

        Intent intent = new Intent(this,BluetoothLeService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.v(LOG_TAG,"CREATED");
    }

    @Override
    protected void onStart() {
        super.onStart();
        askAllPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
        final Handler checkConnection = new Handler();
        checkConnection.post(new Runnable() {
            @Override
            public void run() {
                FloatingActionButton mFab = findViewById(R.id.mFab);
                if(mBleService == null){
                    Log.v(LOG_TAG,"Your service is not available");
                } else {
                    if( mBleService.mConnectionState==1 ){
                        mFab.setBackgroundTintList(getResources().getColorStateList(R.color.ble_blue));
                        mFab.setImageResource(R.drawable.ic_bluetooth_connected_white_24dp);
                        mFab.setSize(FloatingActionButton.SIZE_MINI);
                    } else {
                        mFab.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                        mFab.setImageResource(R.drawable.ic_bluetooth_disabled_white_24dp);
                        mFab.setSize(FloatingActionButton.SIZE_NORMAL);
                    }
                }
                checkConnection.postDelayed(this,10000);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.thanks_to:
                Intent intent = new Intent(this, ThanksTo.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private SecondOverview secondOverview = null;
    private SecondControl secondControl = null;
    private SecondGraph secondGraph = null;

    public class SectionPager extends FragmentPagerAdapter{
        private SectionPager (FragmentManager fm){
            super(fm);
        }

        public int getCount(){
            return 3;
        }

        public Fragment getItem(int positon){
            switch(positon){
                case 0:
                    if(secondOverview == null){
                        secondOverview = new SecondOverview();
                    }
                    return secondOverview;
                case 1:
                    if(secondControl == null){
                        secondControl = new SecondControl();
                    }
                    return secondControl;
                case 2:
                    if(secondGraph == null){
                        secondGraph = new SecondGraph();
                    }
                    return secondGraph;
            }
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position){
                case 0:
                    return getResources().getString(R.string.overview);
                case 1:
                    return getResources().getString(R.string.control_center);
                case 2:
                    return getResources().getString(R.string.graphy_center);
            }
            return super.getPageTitle(position);
        }
    }


    public void askAllPermission (){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1111);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH},1112);
        }
    }

    @Override
    public String readString() {
        if (mBleService != null && mBleService.mConnectionState == 1) {
            return mBleService.bleNotifyVal;
        } else {
            return null;
        }
    }

    @Override
    public int[] readVal() {
        int [] result = new int[4];
        if(mBleService != null && mBleService.mConnectionState == 1 &&
                mBleService.bleNotifyVal != null){
            String valStr = mBleService.bleNotifyVal;
            //Log.v(LOG_TAG,valStr);
            //int indexHead = valStr.indexOf("$");
            int indexXHead = valStr.indexOf("X");
            int indexXEnd = valStr.indexOf(",");
            int indexYHead = valStr.indexOf("Y");
            int indexEnd = valStr.indexOf("#");
            result[0]=Integer.parseInt(valStr.substring(indexXHead+1,indexXEnd));
            result[1]=Integer.parseInt(valStr.substring(indexYHead+1,indexEnd));
            bnpGoalPos[0] = 160;
            bnpGoalPos[1] = 100;
            result[2] = bnpGoalPos[0];
            result[3] = bnpGoalPos[1];
            return result;
        }
        bnpGoalPos[0] = 160;
        bnpGoalPos[1] = 100;
        result[0]=0;
        result[1]=0;
        result[2] = bnpGoalPos[0];
        result[3] = bnpGoalPos[1];

        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bound_status) {
            unbindService(mConnection);
            bound_status = false;
        }
    }
}
