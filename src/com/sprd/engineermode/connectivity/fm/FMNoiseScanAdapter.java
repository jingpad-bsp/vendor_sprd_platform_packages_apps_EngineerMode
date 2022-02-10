package com.sprd.engineermode.connectivity.fm;

import java.util.ArrayList;
import java.util.Collections;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.BaseAdapter;
import com.sprd.engineermode.R;

public class FMNoiseScanAdapter  extends BaseAdapter{

    private LayoutInflater inflater = null;
    protected ArrayList<NoiseScanResult> result = null;

    public FMNoiseScanAdapter(Context context, ArrayList<NoiseScanResult> result) {
        inflater = LayoutInflater.from(context);
        this.result = result;
    }

    @Override
    public int getCount() {
        return (result != null) ? result.size() : 0;
    }

    @Override
    public Object getItem(int p) {
        if (result != null && p >= 0 && p < result.size()) {
            return result.get(p);
        }
        return null;
    }

    @Override
    public long getItemId(int p) {
        return p;
    }

    class ViewHolder {
        TextView freq;
        TextView rssi;
        TextView snr;
    }

    @Override
    public View getView(int p, View convert, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convert == null) {
            convert = inflater.inflate(R.layout.noise_scan_list_row, parent, false);
            holder = new ViewHolder();
            holder.freq = (TextView) convert.findViewById(R.id.freq);
            holder.rssi = (TextView) convert.findViewById(R.id.rssi);
            holder.snr = (TextView) convert.findViewById(R.id.snr);
            convert.setTag(holder);
        } else {
            holder = (ViewHolder) convert.getTag();
        }

        holder.freq.setText(result.get(p).freq);
        holder.rssi.setText(result.get(p).rssi);
        holder.snr.setText(result.get(p).snr);
        return convert;
    }
}
