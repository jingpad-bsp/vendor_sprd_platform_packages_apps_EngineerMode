package com.unisoc.engineermode.core.data.telephony.netinfostat;

public class NetInfoStatData {
    public static class Item {
        public String desc;

        public String success;
        public String fail;
        public String ratio;
        public String delay;

        public Item() {
            success = "NA";
            fail = "NA";
            ratio = "NA";
            delay = "NA";
        }

        public String getDesc() {
            return desc;
        }

        public String getSuccess() {
            return success;
        }

        public String getFail() {
            return fail;
        }

        public String getRatio() {
            return ratio;
        }

        public String getDelay() {
            return delay;
        }
    }

    private Item data[];

    public NetInfoStatData(Item[] data) {
       this.data = data;
    }

    public Item[] getData() {
        return data;
    }

    public void setData(Item[] data) {
        this.data = data;
    }


}
