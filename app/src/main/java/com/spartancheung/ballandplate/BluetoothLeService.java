package com.spartancheung.ballandplate;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class BluetoothLeService extends Service {
    public String bleNotifyVal;

    private final bleBinder mBleBinder = new bleBinder();
    public BluetoothAdapter mBluetoothAdapter; // used in activity
    private BluetoothDevice mDevice;
    public int mConnectionState = 0;
    private BluetoothGattService mBleService;
    private BluetoothGattCharacteristic mBleChar;

    private static final int BLE_CONNECTED = 1;
    private static final int BLE_DISCONNECTED = 0;
    private boolean isDevicePicked = false;
    public boolean serviceDiscovered = false;

    private static final String LOG_TAG = "BluetoothLeService";

    Handler mHandler = new Handler();
    Handler getserviceHandler = new Handler();

    boolean mScanning = false;
    List<String> mDeviceNameList = new ArrayList<String>();
    List<BluetoothDevice> mBleDevices = new ArrayList<>();
    List<Integer> mNamedDeviceIndex = new ArrayList<>();

    List<BluetoothGattService> bleGattServices; // = new ArrayList<>();
    List<BluetoothGattCharacteristic> bleGattChar; // = new ArrayList<>();
    List<BluetoothGattDescriptor> bleGattDes;

    private BluetoothGatt mBleGatt;
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            //super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.v(LOG_TAG,getResources().getString(R.string.ble_connected));
                mConnectionState = BLE_CONNECTED;
                //Log.v(LOG_TAG,"dddddd");
                getserviceHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gatt.discoverServices();
                        Log.v(LOG_TAG, "dddddd");
                    }
                },600);
            } else{
                Log.v(LOG_TAG,getResources().getString(R.string.ble_disconnected));
                mConnectionState = BLE_DISCONNECTED;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //super.onServicesDiscovered(gatt, status);
            /*for (BluetoothGattService service : bleGattServices){
                Log.v(LOG_TAG,service.getUuid().toString());
            }*/
            if (status == BluetoothGatt.GATT_SUCCESS){
                bleGattServices = gatt.getServices();
                Log.v(LOG_TAG,Integer.toString(bleGattServices.size()));
                for (BluetoothGattService service : bleGattServices){
                    Log.v(LOG_TAG,service.getUuid().toString());
                }
                serviceDiscovered = true;
                Log.v(LOG_TAG,"services founded!");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //super.onCharacteristicRead(gatt, characteristic, status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                byte [] value = characteristic.getValue();
                StringBuilder sb = new StringBuilder();
                for (byte bt:value){
                    sb.append(String.format("%02X",bt));
                }
                String valueStr = sb.toString();
                Log.v(LOG_TAG,valueStr);
            } else{
                Log.v(LOG_TAG,"Characteristic Read failed");
            }
            Log.v(LOG_TAG,"Characteristic Read failed");

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if(mBleGatt.setCharacteristicNotification(mBleChar,true)){
                BluetoothGattDescriptor clientConfig = mBleChar.getDescriptor(descriptor.getUuid());
                clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBleGatt.writeDescriptor(clientConfig);
                Log.v(LOG_TAG,"descriptor setting is done");
            }
            Log.v(LOG_TAG,"onDescriptorRead get called");
            Log.v(LOG_TAG,"the descriptor"+descriptor.getUuid().toString());
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte [] value = characteristic.getValue();
            String valueStr = new String(value);
            bleNotifyVal = valueStr;
            //Log.v(LOG_TAG,valueStr);
        }
    };



    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!mBleDevices.contains(device)){
                        mBleDevices.add(device);
                    }
                }
            }).start();
        }
    };

    private static final long SCAN_PERIOD = 5000;


    public class bleBinder extends Binder {
        BluetoothLeService getBle (){
            return BluetoothLeService.this;
        }
    }


    public BluetoothLeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBleBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        Log.v(LOG_TAG,"Adapter is all set!");
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    if(mBleDevices.size()>0){
                        for (BluetoothDevice device:mBleDevices) {
                            if(device.getName()!=null) {
                                mDeviceNameList.add(device.getName());
                                mNamedDeviceIndex.add(mBleDevices.indexOf(device));
                            }
                        }
                    }
                    Log.v(LOG_TAG,"Device Scan Done!");
                    Log.v(LOG_TAG,Integer.toString(mDeviceNameList.size()) + " devices found");

                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    public void setmDevice (int index) {
        mDevice = mBleDevices.get(index);
        if (mDevice != null){
            isDevicePicked = true;
        } else {
            Log.v(LOG_TAG,"setmDevice failed!");
        }
    }

    public String connectDevice () {
        if(isDevicePicked){
            mBleGatt = mDevice.connectGatt(this,false, mGattCallback);
            mConnectionState = BLE_CONNECTED;
            Log.v(LOG_TAG,getResources().getString(R.string.ble_connecting));
            return ""; //
        } else {
            return ""; // user did not choose a ble device name, no valid device!
        }
    }

    public void disconnectDevice(){
        if(mBleGatt == null){
            return;
        }
        mBleGatt.close();
        mBleGatt = null;
        mConnectionState = BLE_DISCONNECTED;
    }

    /*  the user calls the following method to get a list of Service UUID (which is simplified to 16 bits - 4 HEX from the original 128 bits UUID, from index 4 to 7)
        the bluetooth activity will adapt the list into a spinner for user to choose which service should be used.
        the index of user picks in spinner is corresponding to the index in the bleGattServices here */
    public List<String> getServiceUuidList () {
        List<String> serviceUuidList_16bits = new ArrayList<>();
        if (serviceDiscovered && bleGattServices.size()>0){
            for (BluetoothGattService service : bleGattServices){
                serviceUuidList_16bits.add(service.getUuid().toString().substring(4,8).toUpperCase());
            }
            Log.v(LOG_TAG,"service uuid RETURNED!");
            return serviceUuidList_16bits;
        } else {
            return null;
        }
    }

    public void setmBleService(long id){
        mBleService = bleGattServices.get((int)id);
        Log.v(LOG_TAG,"The user picked service :" + mBleService.getUuid().toString());
        bleGattChar = mBleService.getCharacteristics();
        /*if(bleGattChar.size() == 0){
            Log.v(LOG_TAG,"Characteristics fail");
        } else {
            Log.v(LOG_TAG,"Characteristics succeed");
        }*/
        //mBleChar = bleGattChar.get(0);
    }

    public List<String> getBleCharUuids(){
        if (bleGattChar.size()>0){
            List<String> bleCharList = new ArrayList<>();
            for (BluetoothGattCharacteristic characteristic: bleGattChar){
                bleCharList.add(characteristic.getUuid().toString().substring(4,8).toUpperCase());
            }
            return bleCharList;
        } else{
            return null;
        }
    }

    public void setBleChar(long id){
        mBleChar = bleGattChar.get((int)id);
        bleGattDes = mBleChar.getDescriptors();
        mBleGatt.readDescriptor(bleGattDes.get(0)); // set the default client descriptor to uuid:2901
    }
}
