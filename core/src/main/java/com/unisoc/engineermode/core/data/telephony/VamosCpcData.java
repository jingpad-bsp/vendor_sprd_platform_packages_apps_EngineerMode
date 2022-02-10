package com.unisoc.engineermode.core.data.telephony;

public class VamosCpcData {
    private int vamos;
    private int cpc;

    public VamosCpcData() {
    }

    public VamosCpcData(int vamos, int cpc) {
        this.vamos = vamos;
        this.cpc = cpc;
    }

    public int getVamos() {
        return vamos;
    }

    public void setVamos(int vamos) {
        this.vamos = vamos;
    }

    public int getCpc() {
        return cpc;
    }

    public void setCpc(int cpc) {
        this.cpc = cpc;
    }

}
