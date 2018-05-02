package com.spartancheung.ballandplate;

import android.bluetooth.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class BluetoothSetting extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = "BluetoothSetting";

    private Handler getData = new Handler();
    private BluetoothLeService bleService;
    private boolean bound = false;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothLeService.bleBinder mBinder = (BluetoothLeService.bleBinder) service;
            bleService = mBinder.getBle();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };
    //final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    /**public BluetoothAdapter mBluetoothAdapter;
     * Handler mHandler = new Handler();
    boolean mScanning = false;
    List<String> deviceNameList = new ArrayList<String>();
    ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    List<Integer> namedDeviceIndex = new ArrayList<>(); //document the index of device with a name

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {

                public void run() {
                    if (!bluetoothDevices.contains(device)){
                        bluetoothDevices.add(device);
                    }
                }
            });
        }
    };

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 5000;


    //boolean isBleOpen = false;
    //boolean isSearchDone = false;*/
    String statusText = " ";
    private static final int REQUEST_ENABLE_BT = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_setting);

        Toolbar bleToolbar = findViewById(R.id.toolBar_ble);
        setSupportActionBar(bleToolbar);

        ActionBar bleActionBar = getSupportActionBar();
        bleActionBar.setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void onStart() {
        super.onStart();
        //final TextView statusBar = findViewById(R.id.ble_status_display);
        final Spinner nameSpinner = findViewById(R.id.spinner);
        nameSpinner.setOnItemSelectedListener(BluetoothSetting.this);

        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
        updateStatusScroll(getResources().getString(R.string.ble_activity_open));



        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, R.string.ble_not_supported ,Toast.LENGTH_SHORT).show();
            updateStatusScroll(getResources().getString(R.string.ble_not_supported));

        } else {
            Toast.makeText(this, R.string.ble_supported,Toast.LENGTH_SHORT).show();
            updateStatusScroll(getResources().getString(R.string.ble_supported));



            final Switch bleSwitch = findViewById(R.id.bleSwitch);
            bleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (bleService.mBluetoothAdapter == null || !bleService.mBluetoothAdapter.isEnabled()){
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
                        } else {
                            updateStatusScroll(getResources().getString(R.string.ble_enabled));
                        }
                    } else {
                        //code for disable bluetooth

                    }
                }
            });

            Switch searchSwitch = findViewById(R.id.switch2);
            searchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        //
                        bleService.mDeviceNameList.clear();
                        bleService.mBleDevices.clear();
                        updateStatusScroll(getResources().getString(R.string.ble_scanning));
                        bleService.scanLeDevice(true);
                        Handler wait = new Handler();
                        wait.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (bleService.mDeviceNameList.size()>0) {
                                    ArrayAdapter<String> deviceNameAdapter = new ArrayAdapter<>(BluetoothSetting.this, android.R.layout.simple_spinner_item, bleService.mDeviceNameList);
                                    deviceNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    nameSpinner.setAdapter(deviceNameAdapter);
                                    updateStatusScroll(getResources().getString(R.string.ble_device_found)+bleService.mDeviceNameList.size());
                                    Log.v(LOG_TAG,"Spinner set done...");
                                } else {
                                    updateStatusScroll(getResources().getString(R.string.ble_device_not_found));
                                }
                            }
                        },6000);
                    } else {
                        //
                        bleService.mDeviceNameList.clear();
                        bleService.mBleDevices.clear();
                        bleService.scanLeDevice(false);
                    }
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,deviceList);
        Spinner deviceNames = findViewById(R.id.spinner);
        deviceNames.setAdapter(arrayAdapter);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { // start form 0
        switch(parent.getId()) {
            case R.id.spinner:
                //TextView statusBar = findViewById(R.id.ble_status_display);
                TextView macAddr = findViewById(R.id.macAddr);
                int indexInDevice = bleService.mNamedDeviceIndex.get((int)id); // remember the index of list begin form 0 !
                bleService.setmDevice(indexInDevice);  // set the choosen device.
                String addr = bleService.mBleDevices.get(indexInDevice).getAddress();
                String name = bleService.mDeviceNameList.get((int)id);
                updateStatusScroll(getResources().getString(R.string.ble_name_selected)+name);
                updateStatusScroll(getResources().getString(R.string.ble_addr_selected)+addr);
                macAddr.setText(addr);
                break;
            case R.id.spinner2:
                bleService.setmBleService(id);
                List<String> bleCharList = bleService.getBleCharUuids();
                Spinner bleCharSpinner = findViewById(R.id.spinner3);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(BluetoothSetting.this,android.R.layout.simple_spinner_item,bleCharList);
                bleCharSpinner.setAdapter(arrayAdapter);
                bleCharSpinner.setOnItemSelectedListener(BluetoothSetting.this);
                break;
            case R.id.spinner3:
                bleService.setBleChar(id);
                getData.post(new Runnable() {
                    @Override
                    public void run() {
                        updateStatusScroll(bleService.bleNotifyVal);
                        if(bleService.mConnectionState == 1) {
                            getData.postDelayed(this, 5);
                        }
                    }
                });
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    if(bluetoothDevices.size()>0){
                        for (BluetoothDevice device :bluetoothDevices) {
                            if(device.getName()!=null) {
                                deviceNameList.add(device.getName());
                                namedDeviceIndex.add(bluetoothDevices.indexOf(device));
                            }
                        }
                        ArrayAdapter<String> deviceNameAdapter = new ArrayAdapter<>(BluetoothSetting.this,android.R.layout.simple_spinner_item,deviceNameList);
                        deviceNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner nameSpinner = findViewById(R.id.spinner);
                        Log.v("ble",Integer.toString(deviceNameList.size()) + " devices found");
                        nameSpinner.setAdapter(deviceNameAdapter);
                    }
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }*/



    private void updateStatusScroll (String text){
        statusText += "\n"+text;
        TextView statusBar = findViewById(R.id.ble_status_display);
        statusBar.setMovementMethod(new ScrollingMovementMethod());
        statusBar.setText(statusText);
        int offset = statusBar.getLineCount()*statusBar.getLineHeight();
        statusBar.scrollTo(0,offset-statusBar.getHeight()); // set the view to always display the newest info.
    }

    public void connect (View view){
        String connectResult = bleService.connectDevice();
        Log.v(LOG_TAG,connectResult);
        new Handler().postDelayed(new Runnable() {  // get the services of the ble device
            @Override
            public void run() {
                Spinner serviceSpinner = findViewById(R.id.spinner2);
                serviceSpinner.setOnItemSelectedListener(BluetoothSetting.this);
                if(bleService.serviceDiscovered){
                    ArrayAdapter<String> serviceUuidList = new ArrayAdapter<>(BluetoothSetting.this,android.R.layout.simple_spinner_item,bleService.getServiceUuidList());
                    serviceSpinner.setAdapter(serviceUuidList); // display the services in the spinner
                }
            }
        },1000); //MUST GREATER THAN 600( ensure the time for service discovery)

    }

    public void disconnect(View view){
        bleService.disconnectDevice();
    }
}

//Switch bleSwitch = findViewById(R.id.bleSwitch);
//bleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//   @Override
//   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//       if (isChecked) {
//         //mBluetoothAdapter = bluetoothManager.getAdapter();
//     }
//  }
// });

/**@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
super.onRequestPermissionsResult(requestCode, permissions, grantResults);
TextView statusBar = findViewById(R.id.ble_status_display);
switch (requestCode){
case REQUEST_ENABLE_BT:{
if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
updateStatusScroll(getResources().getString(R.string.ble_enabled), statusBar);
} else{
updateStatusScroll(getResources().getString(R.string.ble_enabled_fail), statusBar);
}
}
}
}*/


