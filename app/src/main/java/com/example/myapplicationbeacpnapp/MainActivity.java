package com.example.myapplicationbeacpnapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconDataNotifier;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.scanner.NonBeaconLeScanCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.BluetoothService;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private BeaconManager beaconManager = null;

    ListView listView;
    Handler handler;
    public static int e = 0;

    public static Thread threadScanAll = new Thread();
    public static Thread threadScanBeacon = new Thread();


    public static boolean onlyBeacon = false;
    BluetoothAdapter bluetoothAdapter;
    public static Adap modelAdapter;
    public static BeaconAdap beaconAdap;
    LinkedHashMap<BluetoothDevice, Integer> stringHashMap = new LinkedHashMap<>();
    LinkedHashMap<BluetoothDevice, Integer> stringHashMapBuffer = new LinkedHashMap<>();
    LinkedHashMap<Beacon, Integer> stringHashMapIBeacondevice = new LinkedHashMap<>();

    public static Timer timerMain = new Timer();


    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listView = findViewById(R.id.list);
        modelAdapter = new Adap(MainActivity.this, stringHashMap);
        beaconAdap = new BeaconAdap(MainActivity.this, stringHashMapIBeacondevice);
        listView.setAdapter(modelAdapter);


        registerReceiver(receiverStart, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(receiverStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(receiverStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));


        if (!bluetoothAdapter.isEnabled()) {
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }


        startScanner();

        tabLayout = findViewById(R.id.tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equals("all")) {
                    Log.d("tag111", "onTabSelected:all true" + tab.getText().toString());
                    onlyBeacon = false;
                    listView.setAdapter(modelAdapter);
                } else if (tab.getText().toString().equals("Beacon")) {
                    Log.d("tag111", "onTabSelected:beacon true" + tab.getText().toString());
                    onlyBeacon = true;
                    listView.setAdapter(beaconAdap);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    public void startScanner() {
        listView.setAdapter(modelAdapter);
        timerMain.schedule(new TimerTask() {
            @Override
            public void run() {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothAdapter.startDiscovery();


                } else {
                    bluetoothAdapter.startDiscovery();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        beaconAdap.notifyDataSetChanged();
                    }
                });
            }
        }, 0, 3000);


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("tag111", "run: handler" + e++);
                bluetoothAdapter.startLeScan(leScanCallback);
            }
        }, 0);
    }


    BroadcastReceiver receiverStart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {

            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

            }

            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Log.d("tag111", "onReceive: started");

            }


            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Log.d("tag111", "onReceive: finished");

                stringHashMap.clear();
                stringHashMap.putAll(sortMap(stringHashMapBuffer));
                stringHashMapBuffer.clear();
                modelAdapter.notifyDataSetChanged();

            }

            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {

                String extraNAme = (intent.getParcelableExtra(BluetoothDevice.EXTRA_NAME));
                int rssi = (intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                if (onlyBeacon == false) {
                    stringHashMapBuffer.put(((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))
                            , ((int) (intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE))));

                }


            }


        }
    };


    private final BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            final BluetoothLeDevice bluetoothLeDevice = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());


                stringHashMapBuffer.put(device, rssi);




        }
    };


    public static LinkedHashMap<BluetoothDevice, Integer> res = new LinkedHashMap<BluetoothDevice, Integer>();

    public LinkedHashMap<BluetoothDevice, Integer> sortMap(LinkedHashMap h) {

        res.clear();
        List<Map.Entry<BluetoothDevice, Integer>> list = new ArrayList<Map.Entry<BluetoothDevice, Integer>>(h.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<BluetoothDevice, Integer>>() {
            public int compare(Map.Entry<BluetoothDevice, Integer> o1, Map.Entry<BluetoothDevice, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());

            }
        });

        for (Map.Entry<BluetoothDevice, Integer> entry : list) {
            res.put(entry.getKey(), entry.getValue());
        }

        return res;

    }


    @Override
    protected void onDestroy() {
        timerMain.cancel();
        unregisterReceiver(receiverStart);
        bluetoothAdapter.stopLeScan(leScanCallback);
        beaconManager.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i("tag", "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");

                    for (Beacon beacon : beacons){
                        stringHashMapIBeacondevice.put(beacon, beacon.getRssi());
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }
    }