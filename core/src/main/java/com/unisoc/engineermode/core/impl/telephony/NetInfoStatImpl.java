package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;

import com.unisoc.engineermode.core.data.telephony.netinfostat.NetInfoStatData;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

import java.text.NumberFormat;


class NetInfoStatImpl implements ITelephonyApi.INetInfoStat {
    private static final String TAG = "NetInfoStat";

    private static final String[] reselect_lte_desc = {
            "3G->3G",
            "3G->2G",
            "2G->2G",
            "2G->3G",
            "3G->4G",
            "2G->4G",
            "4G->4G",
            "4G->2G",
            "4G->3G",
    };

    private static final String[] reselect_desc = {
            "3G->3G",
            "3G->2G",
            "2G->2G",
            "2G->3G",
    };

    private static final String[] handover_desc = {
            "3G->3G HO",
            "3G->2G HO",
            "2G->2G HO",
            "2G->3G HO",
            "3G->2G CCO",
            "2G->2G CCO",
            "2G->3G CCO",
    };

    private static final String[] handover_lte_desc = {
            "3G->3G HO",
            "3G->2G HO",
            "2G->2G HO",
            "2G->3G HO",
            "3G->2G CCO",
            "2G->2G CCO",
            "2G->3G CCO",
            "2G->4G CCO",
            "4G->2G CCO",
            "2G->4G HO",
            "3G->4G HO",
            "4G->4G HO",
            "4G->3G HO",
            "4G->2G HO",
            "SRVCC 2G",
            "SRVCC 3G",
    };

    /*
            "time on 2g",
            "time on 3g",
            "time on unknown",
            "time all",
            "time on lte",
            "time on volte",
            "time on lte ca",
            "count of lte ca",
            "count of non-lte ca"
     */
    private static final String[] attachtime_desc = {
            "Time on 2G",
            "Time on 3G",
            "Time on Unknown",
            "All the Time",
    };

    private static final String[] attachtime_lte_desc = {
        "Time on 2g",
        "Time on 3g",
        "Time on unknown",
        "Time all",
        "Time on lte",
        "Time on volte",
        "Time on lte ca",
        "Count of lte ca",
        "Count of non-lte ca"
    };

    private static final String[] dropcount_desc = {
            "Drop times on 2G",
            "Drop times on 3G",
    };

    private static final String[] dropcount_lte_desc = {
            "Drop times on 2G",
            "Drop times on 3G",
            "Drop times on 4G",
    };

    @Override
    public NetInfoStatData getReselectInfo(int simIdx) throws Exception {
        String atRsp = IATUtils.sendATCmd("AT+SPENGMD=0,7,1", simIdx);
        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new Exception("AT reponse error");
        }

        int itemCount;
        //int fieldCount = 5;
        String[] desc;
        if (TelephonyApiImpl.getInstance().telephonyInfo().isSupportLte()) {
            desc = reselect_lte_desc;
            itemCount = reselect_lte_desc.length;
        } else {
            desc = reselect_desc;
            itemCount = reselect_desc.length;
        }

        atRsp = atRsp.replaceAll("--", "-");
        String[] str = atRsp.split("-");
        int array_len = str.length;
        Log.d(TAG, "atRSP.length = " + array_len);
        for (int i = 0; i < array_len; i++) {
            str[i] = str[i].replaceAll("\r|\n", "");
        }
        str[array_len - 1] = str[array_len - 1].substring(0,
                str[array_len - 1].length() - 2);
        Log.d(TAG, "split str len: " + str.length);
        int temp = 0;


        NetInfoStatData.Item[] items = new NetInfoStatData.Item[itemCount];
        for (int i = 0; i < itemCount; i++) {

            /*
            for (int k = 1; k < COL - 1; k++) {
                mTextValue[j][k] = str[temp++];
                if (temp == 12) {
                    temp = temp + 2;
                } // str[12],str[13] is useless,need to delete
            }
            */
            Log.d(TAG, "current item : " + i);
            items[i] = new NetInfoStatData.Item();
            items[i].desc = desc[i];
            items[i].success = str[temp++];
            items[i].fail = str[temp++];
            items[i].ratio = convertPercent(str[temp++], items[i].success);
            //items[i].delay = str[temp++];
            if (temp == 12) {
                temp = temp + 2;
            } // str[12],str[13] is useless,need to delete
        }
        /*
        for (int z = 1; z < ROW; z++) {
            try {
                if (Integer.parseInt(mTextValue[z][3]) == 0) {
                    format.setMinimumFractionDigits(2);
                    mTextValue[z][3] = format.format(1.0);
                } else {
                    format.setMinimumFractionDigits(2);
                    mTextValue[z][3] = format.format((double) Integer
                            .parseInt(mTextValue[z][1])
                            / Integer.parseInt(mTextValue[z][3]));
                }
            } catch (RuntimeException ex) {
                Log.d(TAG, "shengyi parse at response fail");
            }
        }
        */

        /*
        if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG) {
            firstcol = mContext.getResources().getStringArray(
                    R.array.Reselect_WG);
        } else {
            firstcol = mContext.getResources().getStringArray(
                    R.array.Reselect_LTE);
        }
        for (int index = 0; index < firstcol.length; index++) {
            mTextValue[index][0] = firstcol[index];
        }
        */

        /* reselect delay according to "at+spengmd =0,7,1" response */
        /*
        if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG) {
            mTextValue[1][COL - 1] = str[32];
            mTextValue[2][COL - 1] = str[33];
            mTextValue[3][COL - 1] = str[29];
            mTextValue[4][COL - 1] = str[30];
        } else {
            mTextValue[1][COL - 1] = str[32];
            mTextValue[2][COL - 1] = str[33];
            mTextValue[3][COL - 1] = str[29];
            mTextValue[4][COL - 1] = str[30];
            mTextValue[5][COL - 1] = str[34];
            mTextValue[6][COL - 1] = str[31];
            mTextValue[7][COL - 1] = str[35];
            mTextValue[8][COL - 1] = str[36];
            mTextValue[9][COL - 1] = str[37];
        }
        */

        if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG
            || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WW) {
            items[0].delay = str[32];
            items[1].delay = str[33];
            items[2].delay = str[29];
            items[3].delay = str[30];
        } else {
            items[0].delay = str[32];
            items[1].delay = str[33];
            items[2].delay = str[29];
            items[3].delay = str[30];
            items[4].delay = str[34];
            items[5].delay = str[31];
            items[6].delay = str[35];
            items[7].delay = str[36];
            items[8].delay = str[37];
        }
        return new NetInfoStatData(items);
    }

    @Override
    public NetInfoStatData getHandOverInfo(int simIdx) throws Exception {
        String atRsp = IATUtils.sendATCmd("AT+SPENGMD=0,7,2", simIdx);
        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new Exception("AT reponse error");
        }

        int itemCount;
        String[] desc;
        if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG
            || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WW) {
            desc = handover_desc;
            itemCount = handover_desc.length;
        } else {
            desc = handover_lte_desc;
            itemCount = handover_lte_desc.length;
        }

        String[] str = atRsp.split("-");
        int array_len = str.length;
        for (int i = 0; i < str.length; i++) {
            str[i] = str[i].replaceAll("\r|\n", "");
        }
        str[array_len - 1] = str[array_len - 1].substring(0,
                str[array_len - 1].length() - 2);
        int temp = 0;

        NetInfoStatData.Item[] items = new NetInfoStatData.Item[itemCount];
        for (int i = 0; i < itemCount-2; i++) {
            Log.d(TAG, "current item : " + i);
            items[i] = new NetInfoStatData.Item();
            items[i].desc = desc[i];
            items[i].success = str[temp++];
            items[i].fail = str[temp++];
            items[i].ratio = convertPercent(str[temp++], items[i].success);
            Log.d(TAG, items[i].desc + ": " + items[i].success + "," + items[i].fail);
        }

       /*
        for (int j = 1; j < ROW-2; j++) {
                for (int k = 1; k < COL - 1; k++) {
                    mTextValue[j][k] = str[temp++];
                }

            }
       */

        if(str.length>=68){
            temp+=18;
            for (int i = itemCount-2; i < itemCount; i++) {
                items[i] = new NetInfoStatData.Item();
                items[i].desc = desc[i];
                items[i].success = str[temp++];
                items[i].fail = str[temp++];
                items[i].ratio = convertPercent(str[temp++], items[i].success);
            }
        } else {
            for (int i = itemCount-2; i < itemCount; i++) {
                items[i] = new NetInfoStatData.Item();
                items[i].desc = desc[i];
                items[i].success = "NA";
                items[i].fail = "NA";
                items[i].ratio = "NA";
            }
        }
            /*
            for (int z = 1; z < ROW; z++) {
                try {
                    if(mTextValue[z][1].equals("NA")){
                        break;
                    }
                    if (Integer.parseInt(mTextValue[z][3]) == 0) {
                        format.setMinimumFractionDigits(2);
                        mTextValue[z][3] = format.format(1.0);
                    } else {
                        format.setMinimumFractionDigits(2);
                        mTextValue[z][3] = format.format((double) Integer
                                .parseInt(mTextValue[z][1])
                                / Integer.parseInt(mTextValue[z][3]));
                    }
                } catch (RuntimeException ex) {
                    Log.d(TAG, "parse at response fail");
                }
            }

            if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG) {
                mFirstcol = getActivity().getApplicationContext()
                        .getResources().getStringArray(R.array.Handover_WG);
            }else {
                mFirstcol = getActivity().getApplicationContext()
                        .getResources().getStringArray(R.array.Handover_LTE);
            }
            for (int index = 0; index < mFirstcol.length; index++) {
                mTextValue[index][0] = mFirstcol[index];
            }
            */
            /* handover delay according to "AT+SPENGMD=0,7,2" response */
            if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG
                || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WW) {
                items[0].delay = str[48];
                items[1].delay = str[49];
                items[2].delay = str[42];
                items[3].delay = str[44];
                items[4].delay = str[50];
                items[5].delay = str[43];
                items[6].delay = str[45];

            }else {

                items[0].delay = str[48];
                items[1].delay = str[49];
                items[2].delay = str[42];
                items[3].delay = str[44];
                items[4].delay = str[50];
                items[5].delay = str[43];
                items[6].delay = str[45];
                items[7].delay = str[47];
                items[8].delay = str[54];
                items[9].delay = str[46];
                items[10].delay = str[51];
                items[11].delay = str[52];
                items[12].delay = str[55];
                items[13].delay = str[53];

                if(str.length>=68){
                    items[14].delay = str[66];
                    items[15].delay = str[67];
                }else{
                    items[14].delay = "NA";
                    items[15].delay = "NA";
                }
            }

        return new NetInfoStatData(items);
    }

    @Override
    public DataItem[] getAttachTime(int simIdx) throws Exception {

        String atRsp = IATUtils.sendATCmd("AT+SPENGMD=0,7,7", simIdx);

        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new Exception("AT reponse error");
        }

            Log.d(TAG, "parseNetStatistic atRSP =" + atRsp);
            String[] str = atRsp.split("-");
            int array_len = str.length;
            for (int i = 0; i < str.length; i++) {
                str[i] = str[i].replaceAll("\r|\n", "");
                Log.d(TAG, "parseNetStatistic str =" + str[i]);
            }
            str[array_len - 1] = str[array_len - 1].substring(0,
                    str[array_len - 1].length() - 2);

            int itemCount;
            String[] desc;
            if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG
                || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WW) {
                desc = attachtime_desc;
                itemCount = attachtime_desc.length;
            } else {
                desc = attachtime_lte_desc;
                itemCount = attachtime_lte_desc.length;
            }

        DataItem[] items = new DataItem[itemCount];

        for (int i = 0; i < itemCount; i++) {
            items[i] = new DataItem();
            items[i].desc = desc[i];
            items[i].data = str[i];
            Log.d(TAG, i + " => " + items[i].desc + ": " + items[i].data);
        }

        return items;

    }

    @Override
    public DataItem[] getDropCount(int simIdx) throws Exception {

        String atRsp = IATUtils.sendATCmd("AT+SPENGMD=0,7,0", simIdx);
        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new Exception("AT reponse error");
        }

            String[] str = atRsp.split("-");
            int array_len = str.length;
            for (int i = 0; i < str.length; i++) {
                str[i] = str[i].replaceAll("\r|\n", "");
            }
            str[array_len - 1] = str[array_len - 1].substring(0,
                    str[array_len - 1].length() - 2);

        int itemCount;
        String[] desc;
        if (TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WG
            || TelephonyManagerSprd.getRadioCapbility() == TelephonyManagerSprd.RadioCapbility.WW) {
            desc = dropcount_desc;
            itemCount = dropcount_desc.length;
        } else {
            desc = dropcount_lte_desc;
            itemCount = dropcount_lte_desc.length;
        }
        DataItem[] items = new DataItem[itemCount];

        for (int i = 0; i < itemCount; i++) {
            items[i] = new DataItem();
            items[i].desc = desc[i];
            items[i].data = str[i];
            Log.d(TAG, i + " => " + items[i].desc + ": " + items[i].data);
        }

        return items;

    }

    private static String convertPercent(String total, String pass) {
        NumberFormat format = NumberFormat.getPercentInstance();
        try {
            if (Integer.parseInt(total) == 0) {
                format.setMinimumFractionDigits(2);
                return format.format(1.0);
            } else {
                format.setMinimumFractionDigits(2);
                return format.format((double) Integer.parseInt(pass) / Integer.parseInt(total));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "NA";
        }
    }
}
