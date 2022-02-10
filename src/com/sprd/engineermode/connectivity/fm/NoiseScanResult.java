package com.sprd.engineermode.connectivity.fm;

public class NoiseScanResult {

    public String freq;
    public String rssi;
    public String snr;

    public NoiseScanResult(String freq,String rssi,String snr){
        super();
        this.freq=freq;
        this.rssi=rssi;
        this.snr=snr;
    }
}
