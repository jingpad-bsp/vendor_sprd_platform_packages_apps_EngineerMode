
package com.sprd.engineermode.connectivity.BT;

public class BTRXResult {

    public String rssi;
    public String per;
    public String ber;

    public BTRXResult(String rssi, String per, String ber, boolean isOdd) {
        super();
        this.rssi = rssi;
        this.per = per;
        this.ber = ber;
    }
}
