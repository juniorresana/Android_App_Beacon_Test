package com.example.myapplicationbeacpnapp;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Adap extends BaseAdapter {
    ArrayList<BluetoothDevice> modelArrayList = new ArrayList<>();
    Context context;
    ArrayList<Integer> keyList = new ArrayList<>();
    LinkedHashMap<BluetoothDevice, Integer> hashMap;

    public Adap(Context context, LinkedHashMap<BluetoothDevice, Integer> hashMap0) {
        this.hashMap = hashMap0;
        this.context = context;


    }

    @Override
    public void notifyDataSetChanged() {
        this.modelArrayList = new ArrayList<BluetoothDevice>(hashMap.keySet());
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

        View view = LayoutInflater.from(context).inflate(R.layout.beacon_model, null);

        BluetoothDevice bluetoothDevice = modelArrayList.get(position);

        ((TextView) view.findViewById(R.id.positiontextview)).setText("" + position);
        ((TextView) view.findViewById(R.id.tMacc)).setText(bluetoothDevice.getAddress());
        ((TextView) view.findViewById(R.id.tRSSi)).setText("" + keyList.get(position));

        return view;
    }


}

