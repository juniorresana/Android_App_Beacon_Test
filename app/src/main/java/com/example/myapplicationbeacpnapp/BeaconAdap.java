package com.example.myapplicationbeacpnapp;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.AltBeaconParser;
import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconDevice;

public class BeaconAdap extends BaseAdapter {

    ArrayList<Beacon> modelArrayList = new ArrayList<>();
    Context context;
    ArrayList<Integer> keyList = new ArrayList<>();
    LinkedHashMap<Beacon, Integer> hashMap;

    public BeaconAdap(Context context, LinkedHashMap<Beacon, Integer> hashMap0) {
        this.hashMap = hashMap0;
        this.context = context;


    }

    @Override
    public void notifyDataSetChanged() {
        this.modelArrayList = new ArrayList<Beacon>(hashMap.keySet());
        this.keyList = new ArrayList<Integer>(hashMap.values());
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.beacon_list, null);

        Beacon beacon1 = modelArrayList.get(position);

        ((TextView) view.findViewById(R.id.uuid)).setText(" "+beacon1.getServiceUuid());
        ((TextView) view.findViewById(R.id.major)).setText(" "+beacon1.getId2());
        ((TextView) view.findViewById(R.id.rssi)).setText(" " + beacon1.getRssi());
        ((TextView) view.findViewById(R.id.minor)).setText(" "+beacon1.getId3());
        ((TextView) view.findViewById(R.id.distance)).setText(" "+beacon1.getDistance());

        return view;
    }
}
