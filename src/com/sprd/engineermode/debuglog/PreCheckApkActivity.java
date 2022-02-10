package com.sprd.engineermode.debuglog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;
import com.unisoc.engineermode.core.utils.SocketUtils;
import com.sprd.engineermode.utils.TeleManagerUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Build;
import android.os.Message;
import android.net.LocalSocketAddress;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.bluetooth.BluetoothAdapter;
import android.text.TextUtils;
import android.os.Handler;

import java.io.FileOutputStream;

import android.app.ActionBar;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager.NameNotFoundException;

import java.io.DataInputStream;
import java.io.FileInputStream;

import com.sprd.engineermode.R;
import java.util.Collections;

import com.unisoc.engineermode.core.common.Const;

public class PreCheckApkActivity extends Activity implements OnItemClickListener {

    private static final String TAG = "PreCheckActivity";
    private ListView mListView;
    private CheckItemAdapter mAdapter;
    private ArrayList<String> mCheckItems;
    private TeleManagerUtil mTeleManagerUtil;
    private static int mPhoneCount;
    private WifiManager mWifiManager;
    private String mUnavailable;
    private Resources mResources;
    private BandData mBandData;
    private BluetoothAdapter mBluetoothAdapter = null;

    private static final int WRITE_SUCC = 20;
    private static final int WRITE_FAIL = 21;
    private static final String FILEPATH = "infolog.txt";
    private PreHandler mPreHandler = new PreHandler();

    private static final String GPS_CSR_CONFIG_FILE = "/data/cg/supl/supl.xml";
    private static final String GPS_CSR_CONFIG_FILE2 = "/data/gnss/config/config.xml";
    private static final String VIEW_ITEM_VERSION = "com.engineermode.debuglog.itemversion";
    private static final int GET_CP0_VERSION = 0;
    private static final int GET_WCN_VERSION = 1;

    private static final String ADC_PATH = "/productinfo/adc.bin";
    private static final int ADCBYTES = 56;
    private DataInputStream mInputStream = null;

    private String mFilePath;

    private static final String SOCKET_NAME = "hidl_common_socket";
    private static final String PROCESS_NAME = "wcnd ";

    private static final int AP_VERSION = 0;
    private static final int WCN_VERSION = 1;
    private static final int MODEM_VERSION = 2;
    private static final int GPS_VERSION = -1;
    private static final int DSP_VERSION = 3;
    private static final int USER_DEBUG_VERSION = 4;
    private static final int LTE_VERSION = 5;
    private static final int CP_CONFIG_VERSION = 6;
    private static final int AP_CONFIG_VERSION = 7;
    private static final int IMEI = 8;
    private static final int BT_MAC = 9;
    private static final int WIFI_MAC = 10;
    private static final int SUPPORT_BAND = 11;
    private static final int CALL_FORWARDING = 12;
    private static final int VOLTE = 13;
    private static final int MOS_MODE = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_check_activity);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    , ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        setTitle(R.string.precheck_title);
        init();
    }

    private void init() {
        mResources = getResources();
        parseXml(R.array.CheckItems);
        mListView = (ListView) findViewById(R.id.itemlist);
        if (mAdapter == null) {
            mAdapter = new CheckItemAdapter(mCheckItems, this);
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mTeleManagerUtil = TeleManagerUtil.getInstance(this);
        mPhoneCount = mTeleManagerUtil.getPhoneCount();
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        mUnavailable = getResources().getString(R.string.status_unavailable);
    }

    private void parseXml(int arrayId) {
        mCheckItems = new ArrayList<String>();
        for (String s : mResources.getStringArray(arrayId)) {
            mCheckItems.add(s);
        }
    }

    private String getApVersion() {
        String ApVersion = null ;
        if(Const.isUser()) {
            ApVersion = SystemPropertiesProxy.get("ro.build.description");
        } else {
            ApVersion = Build.DISPLAY;
        }
        return ApVersion;
    }

    private String getWcnVersion() {
        String result = SocketUtils.sendCmdAndRecResult(SOCKET_NAME,
                    LocalSocketAddress.Namespace.ABSTRACT, PROCESS_NAME + "wcn at+spatgetcp2info");
        Log.d(TAG, "WCN Version is " + result);
        if (result != null) {
            return recombinResult(GET_WCN_VERSION, result);
        } else {
            return "UNKNOWN";
        }
    }

    private String getModemVersion() {
        String result = IATUtils.sendAt("AT+CGMR", "atchannel1");
        //String result = IATUtils.sendATCmd("AT+CGMR", "atchannel1");
        Log.d(TAG, "CP0 Version is " + result);
        if (result.contains(IATUtils.AT_OK)) {
            return recombinResult(GET_CP0_VERSION,result);
        } else {
            return "UNKNOWN";
        }
    }

    private String getImei() {
        String ImeiResult = IATUtils.sendAt("AT","atchannel0");
        if (!ImeiResult.contains(IATUtils.AT_OK)) {
            return "ERROR(modem asserted)";
        }
        String result;
        StringBuilder strbuild = new StringBuilder();
        for (int i = 0; i < mPhoneCount; i++) {
            strbuild.append("IMEI" + (i+1) + ":");
            result = IATUtils.sendAt("AT+SPIMEI?", "atchannel" + i);
            //result = IATUtils.sendATCmd("AT+SPIMEI?", "atchannel" + i);
            result = result.substring(0, 15);
            strbuild.append(result+"\n");
        }
        return strbuild.toString();
    }

    private String getGpsVersion() {
        String gpsVersion = getGpsVersion(GPS_CSR_CONFIG_FILE,"PROPERTY", "VERSION");
        if("UNKNOWN".equals(gpsVersion)) {
            gpsVersion = getGpsVersion(GPS_CSR_CONFIG_FILE2,"PROPERTY", "GE2-VERSION");
        }
        return gpsVersion;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.d(TAG, "onItemClick:arg2="+arg2+", arg3="+arg3);
        String version = null;
        switch(arg2) {
        case AP_VERSION :
            version = getApVersion();
            break;
        case WCN_VERSION :
            version = getWcnVersion();
            break;
        case MODEM_VERSION :
            version = getModemVersion();
            break;
        case GPS_VERSION :
            version = getGpsVersion();
            break;
        case IMEI :
            String ImeiResult = IATUtils.sendAt("AT","atchannel0");
            if (!ImeiResult.contains(IATUtils.AT_OK)) {
                Toast.makeText(this, "sorry, the modem is not alive!", Toast.LENGTH_LONG).show();
                return;
            }
            version = getImei();
            break;
        case BT_MAC :
            version = getBtStatus();
            break;
        case WIFI_MAC :
            version = getWifiStatus();
            break;
        case SUPPORT_BAND :
            String supportband = IATUtils.sendAt("AT","atchannel0");
            if (!supportband.contains(IATUtils.AT_OK)) {
                Toast.makeText(this, "sorry, the modem is not alive!", Toast.LENGTH_LONG).show();
                return;
            }
            version = getSupportBand();
            break;
        case CALL_FORWARDING :
            version = getCallforwarding();
            break;
        case USER_DEBUG_VERSION :
            version = getVersion();
            break;
        case LTE_VERSION :
            version = getLteVersion();
            break;
        case CP_CONFIG_VERSION :
            version = getCpConfigurationVersion();
            break;
        case AP_CONFIG_VERSION :
            version = getApConfigurationVersion();
            break;
        case VOLTE :
            version = getVolte();
            break;
        case MOS_MODE :
            version = getMosMode();
            break;
        case DSP_VERSION :
            version = getDspVersion();
            break;
        default :
            break;
        }
        Intent intent = new Intent(VIEW_ITEM_VERSION);
        intent.putExtra(ViewItemVersionActivity.VERSION, version);
        intent.putExtra(ViewItemVersionActivity.TITLE, mCheckItems.get(arg2).split("\\.")[1]);
        startActivity(intent);
    }

    private String getDspVersion() {
        String[] str;
        String[] str1;
        String mVersion = "";
        //GSM DSP Version
        String dspVersionResult = "GSM DSP Version Info:" + "\n";
        String result = IATUtils.sendAt("AT+SPDSPVERSION="+"0", "atchannel1");
        //String result = IATUtils.sendATCmd("AT+SPDSPVERSION="+"0", "atchannel1");

        str = result.split("\n");
        if (result.contains("OK")) {
            str1 = str[0].split("\\,");
            mVersion = dspVersionResult + str1[1]+"\n\n";
        } else {
            mVersion = dspVersionResult + str[0]+"\n\n";
        }
        Log.d(TAG,"DSPVersion GSM is "+result);

        //TD DSP Version
        dspVersionResult = "TD DSP Version Info:" + "\n";
        result = IATUtils.sendAt("AT+SPDSPVERSION="+"1", "atchannel1");
        //result = IATUtils.sendATCmd("AT+SPDSPVERSION="+"1", "atchannel1");
        str = result.split("\n");
        if (result.contains("OK")) {
            str1 = str[0].split("\\,");
            mVersion = mVersion + dspVersionResult + str1[1]+"\n\n";
        } else {
            mVersion = mVersion + dspVersionResult + str[0]+"\n\n";
        }
        Log.d(TAG,"DSPVersion TD is "+result);
                    //WCDMA DSP Version
        dspVersionResult = "WCDMA DSP Version Info:" + "\n";
        result = IATUtils.sendAt("AT+SPDSPVERSION="+"2", "atchannel1");
        //result = IATUtils.sendATCmd("AT+SPDSPVERSION="+"2", "atchannel1");
        str = result.split("\n");
        if (result.contains("OK")) {
            str1 = str[0].split("\\,");
            mVersion = mVersion + dspVersionResult + str1[1]+"\n\n";
        } else {
            mVersion = mVersion + dspVersionResult + str[0]+"\n\n";
        }
        Log.d(TAG,"DSPVersion WCDMA is "+result);

        //LTE DSP Version
        dspVersionResult = "LTE DSP Version Info:" + "\n";
        result = IATUtils.sendAt("AT+SPDSPVERSION="+"3", "atchannel1");
        //result = IATUtils.sendATCmd("AT+SPDSPVERSION="+"3", "atchannel1");
        str = result.split("\n");
        if (result.contains("OK")) {
            str1 = str[0].split("\\,");
            mVersion = mVersion + dspVersionResult + str1[1] + "\n\n";
        } else {
            /* Bug 828236 precheck -- DSP Version Info show +CME ERROR:4 @ { */
            mVersion =  mVersion + dspVersionResult + this.getString(R.string.feature_not_support) + "\n\n";
            /* }@ */
        }
        Log.d(TAG,"DSPVersion LTE is "+result);

        return mVersion;
    }

    private String getMosMode() {
        String version = "UNKNOWN";
        String result = IATUtils.sendAt("AT+SPCAPABILITY=45,0", "atchannel0");
        //String result = IATUtils.sendATCmd("AT+SPCAPABILITY=45,0", "atchannel0");
        Log.d(TAG, "result="+result);
        if (result.contains(IATUtils.AT_OK)) {
            String[] splits = result.split(",");
            int mode = Integer.valueOf(splits[2].substring(0, 1));
            Log.d(TAG, "mode="+mode);
            switch(mode) {
            case 0 :
                version = "not mos test mode";
                break;
            case 1 :
                version = "mos test mode";
                break;
            }
        }

        return version;
    }

    private String getApConfigurationVersion() {
        String result = "Open Market";
        if (SystemPropertiesProxy.get("ro.operator").contains("cmcc")) {
            result = "CMCC";
        } else if (SystemPropertiesProxy.get("ro.operator").contains("cucc")) {
            result = "CUCC";
        }

        return result;
    }

    private String getVolte() {
       String result;
       if (SystemPropertiesProxy.get("persist.vendor.sys.volte.enable").equals("true")) {
           result = "Volte was launched";
       } else {
           result = "Volte was not launched";
       }
       return result;
    }

    public boolean readFile() {
        try {
            File adcFile = new File(ADC_PATH);
            int count = 0;
            if (!adcFile.exists()) {
                Log.d(TAG, "adcFile do not exists");
                return false;
            }
            mInputStream = new DataInputStream(new FileInputStream(adcFile));
            byte[] buffer = new byte[ADCBYTES];
            if (mInputStream != null) {
                count = mInputStream.read(buffer, 0, ADCBYTES);
            }
            if (buffer == null || buffer.length <= 0) {
                Log.d(TAG, "buffer == null or buffer.length <= 0");
                return false;
            }
            Log.d(TAG, "count = " + count + " size = " + buffer.length);
            int adcBit = buffer.length - 4;
            int adcResult = buffer[adcBit] | 0xFFFFFFFC;
            Log.d(TAG, "adcBit = " + adcBit + " buffer[" + adcBit + "] = 0x"
                    + Integer.toHexString(buffer[adcBit]) + " adcResult = 0x"
                    + Integer.toHexString(adcResult));
            if (adcResult == 0xFFFFFFFF) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed get outputStream: " + e);
            e.printStackTrace();
        } finally {
            try {
                mInputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Failed get outputStream: " + e);
            }
        }
        return false;
    }

    private String getLteVersion() {
        String nowModeName =  SystemPropertiesProxy.get("persist.radio.ssda.mode");
        Log.d(TAG, "nowModeName="+nowModeName);
        String value = "Unknown";
        if (("tdd-csfb").equals(nowModeName)) {
            value = "3 Mode";
        } else if (("fdd-csfb").equals(nowModeName)) {
            value = "4 Mode";
        } else if (("csfb").equals(nowModeName)) {
            value = "5 Mode";
        }
        return value;
    }

    private String getCpConfigurationVersion() {
        String version = "UNKNOWN";
        String result = IATUtils.sendAt("AT+SPCAPABILITY=32,0", "atchannel0");
        //String result = IATUtils.sendATCmd("AT+SPCAPABILITY=32,0", "atchannel0");
        if (result.contains(IATUtils.AT_OK)) {
            String[] splits = result.split(",");
            int mode = Integer.valueOf(splits[2].substring(0, 1));
            Log.d(TAG, "mode="+mode);
            switch(mode) {
            case 0 :
                version = "Open Market";
                break;
            case 1 :
                version = "CMCC";
                break;
            case 2 :
                version = "CUCC";
                break;
            }
        }
        return version;
    }

    private String getVersion() {
        String result;
        if (Build.DISPLAY.contains("userdebug") || Build.DISPLAY.contains("test-keys")) {
            result = "userdebug";
        } else {
            result = "user";
        }
        return result;
    }

    private String getCallforwarding() {
         String result = "0";
         result = SystemPropertiesProxy.get("persist.sys.callforwarding");
         return TextUtils.isEmpty(result) ? "0" : result;
    }

    private String recombinResult (int cmdType, String res){
        String result = null;
        String str[] = res.split("\n");
        for(int i = 0; i < str.length - 1; i++) {
            if (cmdType == GET_CP0_VERSION && str[i].contains("BASE  Version")) {
                String str1[] = str[i].split("\\:");
                result = "PS Version:"+"\n"+str1[1]+"\n"+"Release Date:"+"\n"+str[str.length - 2]+"\n";
            }
        }
        if (cmdType == GET_WCN_VERSION) {
            if (res.startsWith("marlin")){
                result = res.trim();
            } else {
                res = res.replaceAll("\\s+", "");
                if (res.startsWith("Platform")) {
                    final String PROC_VERSION_REGEX =
                            "PlatformVersion:(\\S+)" + "ProjectVersion:(\\S+)" + "HWVersion:(\\S+)";
                    Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(res);
                    if (!m.matches()) {
                        Log.e(TAG, "Regex did not match on cp2 version: ");
                    } else {
                        String dateTime = m.group(3);
                        String modem = "modem";
                        int endIndex = dateTime.indexOf(modem) + modem.length();
                        String subString1 = dateTime.substring(0, endIndex);
                        String subString2 = dateTime.substring(endIndex);
                        int index = subString2.lastIndexOf(":");
                        if (index > 0) {
                            subString2 = subString2.substring(0, index + 3);
                        } else {
                            if(subString1 != null && subString1.length() > 2) {
                                subString1 = subString1.substring(0, subString1.length() -2);
                            }
                            subString2 = null;
                        }
                        String time = subString2 == null ? "" : subString2.substring(10);
                        String date = subString2 == null ? "" : subString2.substring(0, 10);
                        result = m.group(1) + "|" + m.group(2) + "|" + subString1 + "|" + date + " "
                                + time;
                    }
                /* SPRD Bug 853862:Parse cp2 version information in Precheck for marlin3 phone. @{ */
                } else if (res.startsWith("WCN_VER")) {
                    //For example:WCN_VER:Platform Version:Marlin3_Trunk_W18.09.1~Project Version:sc2355_marlin3~02-26-2018 13:48:37~...
                    String str1[] = str[0].split("\\~");
                    String strPlatformVersion[] = str1[0].split("\\:");
                    String strProjectVersion[] = str1[1].split("\\:");
                    result = strPlatformVersion[2] + "|" + strProjectVersion[1] + "|" + str1[2];
                } else {
                    Log.e(TAG, "cp2 version is error");
                }
                /* @} */
            }
        }
        return result;
    }

    private String getGpsVersion(String path, String elementName,String key) {
        String gpsVersion = "UNKNOWN";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File gpsConfig = new File(path);
            Document doc = db.parse(gpsConfig);

            NodeList nodeList = doc.getElementsByTagName(elementName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                if (element.getAttribute("NAME").equals(key)) {
                    gpsVersion = element.getAttribute("VALUE");
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception->" + e);
            e.printStackTrace();
        }
        Log.d(TAG,"GPS Version is "+gpsVersion);
        return gpsVersion;
    }

    private String getBtStatus() {
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth != null) {
            String address = bluetooth.isEnabled() ? bluetooth.getAddress() : null;
            if (!TextUtils.isEmpty(address)) {
                return address.toLowerCase();
            }
        }
        return mUnavailable;
    }

    private String getWifiStatus() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String macAddress = wifiInfo == null ? null : wifiInfo.getMacAddress();
        return (!TextUtils.isEmpty(macAddress) ? macAddress : mUnavailable);
    }

    private String getSupportBand() {
        String supportband = IATUtils.sendAt("AT","atchannel0");
        if (!supportband.contains(IATUtils.AT_OK)) {
            Toast.makeText(this, "sorry, the modem is not alive!", Toast.LENGTH_LONG).show();
            return "ERROR(modem asserted)";
        }
        StringBuilder bandResult = new StringBuilder();
        for (int i = 0; i < mPhoneCount; i++) {
            if (mPhoneCount > 1) {
                bandResult.append("Sim Card ").append(i + 1).append(":\n");
            }

            bandResult.append(getLteBands(i));
            bandResult.append(getWcdmaBands(i));
            bandResult.append(getTdBands(i));
        }
        return bandResult.toString();
    }

    private String getLteBands(int phoneId) {
        /*
        if(bandData.initprecheckout()) {
            sb.append(bandData.getSupportBands());
            sb.append("\n");
        }
        */

        LTEBandData bandData = LTEBandData.getInstance(phoneId, this);
        ITelephonyApi.IBand.LteBand supportedBands = bandData.getSupportBands();
        if (supportedBands == null) {
            return "";
        }


        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            if (supportedBands.tddBands[i] == 1) {
                sb.append(LTEBandData.KEY_TDD_LTE).append(i + 33);
                sb.append("\n");
            }
        }
        sb.append("\n");

        for (int i = 0; i < 32; i++) {
            if (supportedBands.fddBands[i] == 1) {
                sb.append(LTEBandData.KEY_FDD_LTE).append(i + 1);
                sb.append("\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    private String getWcdmaBands(int phoneId) {
        WCDMABandData bandData = new WCDMABandData(this, phoneId);
        List<ITelephonyApi.IBand.WcdmaBand> supportedBands = bandData.getSupportBands();

        if (supportedBands.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (ITelephonyApi.IBand.WcdmaBand band : supportedBands) {
            sb.append(band.name());
            sb.append("\n");
        }
        sb.append("\n");

        return sb.toString();
    }

    private String getTdBands(int phoneId) {
        TDBandData bandData = new TDBandData(this, phoneId);
        ITelephonyApi.IBand.TdBand supportedBands = bandData.getSupportBands();

        if (supportedBands == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        if (supportedBands.band_a) {
            sb.append("TD_SCDMA A Band");
            sb.append("\n");
        }
        if (supportedBands.band_f) {
            sb.append("TD_SCDMA F Band");
            sb.append("\n");
        }
        sb.append("\n");

        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pre_check, menu);
        MenuItem about = menu.findItem(R.id.action_about);
        about.setOnMenuItemClickListener(new OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem arg0) {
            Dialog dialog = new AlertDialog.Builder(PreCheckApkActivity.this).
            setTitle("apk info").setMessage(getApkVersion()).create();
            dialog.show();
            return true;
        }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.action_save :
            saveAllInfoToFiles();
            return true;
        case android.R.id.home:
            finish();
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private String getApkVersion() {
        PackageManager pm = getPackageManager();
        String apkVersionName = "unknown";
        try {
            String packagename = getPackageName();
            Log.d(TAG, "packagename="+packagename);
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            apkVersionName = pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String result = "Apk Version: " + apkVersionName + "\n" +
            "Feature:  key parameters check\n" +
            "Copyright: UNISOC";
        return result;
    }

    private void saveAllInfoToFiles() {
        mPreHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run...");
                StringBuffer fileResult = new StringBuffer();
                String number, version, result;
                fileResult.append("INFO LIST:\n");
                String[] items = mResources.getStringArray(R.array.CheckItems);
                number = items[AP_VERSION];
                version = getApVersion();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[WCN_VERSION];
                version = getWcnVersion();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[MODEM_VERSION];
                version = getModemVersion();
                /* SPRD Bug 907474: Save key parameters cause no responding. @{ */
                if (version != null && version.equals("UNKNOWN")) {
                    Log.d(TAG, "getModemVersion UNKNOWN");
                    Toast.makeText(PreCheckApkActivity.this, "sorry, the modem is not alive!", Toast.LENGTH_LONG).show();
                    return;
                }
                /* @} */
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                /*number = items[GPS_VERSION];
                version = getGpsVersion();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");*/
                //======================
                number = items[DSP_VERSION];
                version = getDspVersion();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[USER_DEBUG_VERSION];
                version = getVersion();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[LTE_VERSION];
                version = getLteVersion();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[CP_CONFIG_VERSION];
                version = getCpConfigurationVersion();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[AP_CONFIG_VERSION];
                version = getApConfigurationVersion();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[IMEI];
                version = getImei();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[BT_MAC];
                version = getBtStatus();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[WIFI_MAC];
                version = getWifiStatus();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[SUPPORT_BAND];
                version = getSupportBand();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[CALL_FORWARDING];
                version = getCallforwarding();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[VOLTE];
                version = getVolte();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================
                number = items[MOS_MODE];
                version = getMosMode();
                fileResult.append(number+":\n");
                fileResult.append(version+"\n\n");
                //======================

                boolean saveSucc = saveInfo(FILEPATH, fileResult.toString());
                Message msg = new Message();
                msg.arg1 = saveSucc ? WRITE_SUCC : WRITE_FAIL;
                mPreHandler.sendMessage(msg);
            }
        });
    }

    class PreHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage...");
            switch(msg.arg1) {
            case WRITE_SUCC :
                Toast.makeText(PreCheckApkActivity.this, "file saved in " + mFilePath, Toast.LENGTH_SHORT).show();
                break;
            case WRITE_FAIL :
                Toast.makeText(PreCheckApkActivity.this, "save info fail", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    private boolean saveInfo(String filename, String infos) {
        Log.d(TAG, "saveInfo infos:"+infos.length());
        /* SPRD Bug 927025: Save key parameters to SD card fail. @{ */
        File secondStorage = null;
        final List<File> allStorages = new ArrayList<File>();
        Collections.addAll(allStorages, getApplicationContext().getExternalFilesDirs(null));
        for(File file : allStorages){
            if (file != null) {
                Log.d(TAG, "file = " + file.getAbsolutePath());
                if(file.getAbsolutePath().startsWith("/storage/emulated/0")){
                    Log.d(TAG, "File is internal storage!");
                    secondStorage = file;
                    break;
                }
                secondStorage = file;
            }
        }
        if (secondStorage != null) {
            Log.d(TAG, "secondStorage = " + secondStorage.getAbsolutePath());
            mFilePath = secondStorage.getAbsolutePath() + "/" + filename;
            /* @} */
            try {
                File saveFile = new File(mFilePath);
                if(!saveFile.exists()) saveFile.createNewFile();
                FileOutputStream out = new FileOutputStream(saveFile);
                out.write(infos.getBytes());
                out.close();
                return true;
            } catch (IOException e) {
                Log.d(TAG, "IOException:"+Log.getStackTraceString(e));
            }
        }
        return false;
    }
}


