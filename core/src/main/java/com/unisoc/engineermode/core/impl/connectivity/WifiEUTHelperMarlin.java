
package com.unisoc.engineermode.core.impl.connectivity;

import android.text.TextUtils;
import android.util.Log;

import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.IConnectivityApi;
import com.unisoc.engineermode.core.utils.HidlUtils;
import com.unisoc.engineermode.core.common.Const;

public class WifiEUTHelperMarlin implements IConnectivityApi.IWifiEut {

    private static final String TAG = "WifiEUTHelperMarlin";
    private static final String WCND_ENG_SOCKET_NAME = "wcnd_eng ";
    private static final String WCND_SOCKET_NAME = "wcnd ";

    private static final String CMD_SUCCESS = "OK";

    // iwnpi wlan0 ifaceup is sended by iwnpi, so equal to ifconfig wlan0 up,
    // iwnpi wlan0 ifacedown is sended by iwnpi, so equal to ifconfig wlan0 down
    private static final String WIFI_DRIVER_INSMOD = "iwnpi wlan0 insmod";
    private static final String WIFI_DRIVER_RMMOD = "iwnpi wlan0 rmmod";

    private static final String SET_EUT_UP = "iwnpi wlan0 ifaceup";
//    private static final String SET_EUT_DOWN = "iwnpi wlan0 ifacedown";
    private static final String SET_EUT_START = "iwnpi wlan0 start";
    private static final String SET_EUT_STOP = "iwnpi wlan0 stop";
    private static final String SET_EUT_SET_CHANNEL = "iwnpi wlan0 set_channel ";
    private static final String SET_EUT_SET_RATE = "iwnpi wlan0 set_rate ";

    // for tx
    private static final String SET_EUT_TX_START = "iwnpi wlan0 tx_start";
    private static final String SET_EUT_TX_STOP = "iwnpi wlan0 tx_stop";
    private static final String SET_EUT_CW_START = "iwnpi wlan0 sin_wave";
    private static final String SET_EUT_SET_POWER = "iwnpi wlan0 set_tx_power ";
    private static final String SET_EUT_SET_LENGTH = "iwnpi wlan0 set_pkt_len ";
    private static final String SET_EUT_SET_COUNT = "iwnpi wlan0 set_tx_count ";
    private static final String SET_EUT_SET_PREAMBLE = "iwnpi wlan0 set_preamble ";
//    private static final String SET_EUT_BANDWIDTH = "iwnpi wlan0 set_bandwidth ";
    private static final String SET_EUT_GUARDINTERVAL = "iwnpi wlan0 set_gi ";
    private static final String SET_EUT_PATH = "iwnpi wlan0 set_chain ";
    private static final String SET_EUT_CBW = "iwnpi wlan0 set_cbw ";
    private static final String SET_EUT_SBW = "iwnpi wlan0 set_sbw ";

    // for rx
    private static final String SET_EUT_RX_START = "iwnpi wlan0 rx_start";
    private static final String SET_EUT_RX_STOP = "iwnpi wlan0 rx_stop";
    private static final String SET_EUT_GET_RXOK = "iwnpi wlan0 get_rx_ok";

    // for reg_wr
    private static final String SET_EUT_READ = "iwnpi wlan0 get_reg ";
    private static final String SET_EUT_WRITE = "iwnpi wlan0 set_reg ";

//    private static final String WIFI_RECONNECTIONCOUNT = "iwnpi wlan0 get_reconnect";
//    private static final String WIFI_RSSISIGNALSTRENGTH = "iwnpi wlan0 get_rssi";

    private static final String CMD_ENABLED_POWER_SAVE = "iwnpi wlan0 lna_on";
    private static final String CMD_DISABLED_POWER_SAVE = "iwnpi wlan0 lna_off";
    private static final String CMD_GET_POWER_SAVE_STATUS = "iwnpi wlan0 lna_status";

    private static final String SET_ADAPTIVE_ON = "iwnpi wlan0 set_eng_mode 1 1";
    private static final String SET_ADAPTIVE_OFF = "iwnpi wlan0 set_eng_mode 1 0";
    private static final String GET_ADAPTIVE_STATUS = "iwnpi wlan0 set_eng_mode 2";

    private static final String SET_SCAN_ON = "iwnpi wlan0 set_eng_mode 3 1";
    private static final String SET_SCAN_OFF = "iwnpi wlan0 set_eng_mode 3 0";
    private static final String GET_SCAN_STATUS = "iwnpi wlan0 set_eng_mode 4";

    private static final String GET_BEAMFORMING_STATUS = "iwnpi wlan0 get_beamf_status";
    private static final String SET_BEAMFORMING_ON = "iwnpi wlan0 set_beamf_status 1";
    private static final String SET_BEAMFORMING_OFF = "iwnpi wlan0 set_beamf_status 0";

    private static final String GET_STBC_RX_STATUS = "iwnpi wlan0 get_rxstbc_status";
    private static final String SET_STBC_RX_ON = "iwnpi wlan0 set_rxstbc_status 1";
    private static final String SET_STBC_RX_OFF = "iwnpi wlan0 set_rxstbc_status 0";

    private int tempPower = 0;
    private String chainVaule = "1";

    public static class WifiEutMarlinImplHolder {
        static final IConnectivityApi.IWifiEut INSTANCE = new WifiEUTHelperMarlin();
    }

    /**
     * creat wcnd socket and send iwnpi wlan0 ifaceup because this cmd is the first cmd, so creat
     * socket here but the socket is not closed here, and close it when wifiDown(), cause the
     * "iwnpi wlan0 ifacedown" cmd is the last cmd sended by the wcnd socket
     *
     * @return true if success
     */
    @Override
    public boolean up() {
        return sendCmdEng(SET_EUT_UP);
    }

    /**
     * send iwnpi wlan0 start
     *
     * @return the cmd result, true if success
     */
    @Override
    public boolean start() {
        return sendCmdEng(SET_EUT_START);
    }

    /**
     * send cmd(iwnpi wlan0 ifacedown) and close wcnd socket
     *
     * @return true if success
     */
    @Override
    public boolean down() {
        return sendCmdEng(SET_EUT_STOP);
    }

    /**
     * send iwnpi wlan0 stop
     *
     * @return true if success
     */
    @Override
    public boolean stop() {
        return sendCmdEng(SET_EUT_STOP);
    }

    /**
     * This is for TX EX: mode 802.11 pkt iwnpi wlan0 set_channel xx iwnpi wlan0 set_pkt_length xx
     * iwnpi wlan0 set_tx_count xx iwnpi wlan0 set_tx_power xx iwnpi wlan0 set_rate xx iwnpi wlan0
     * set_preamble xx iwnpi wlan0 set_bandwidth xx iwnpi wlan0 set_guard_interval iwnpi wlan0
     * tx_start mode Sin Wave iwnpi wlan0 set_channel xx iwnpi wlan0 set_pkt_length xx iwnpi wlan0
     * set_tx_count xx iwnpi wlan0 set_tx_power xx iwnpi wlan0 set_rate xx iwnpi wlan0 set_preamble
     * xx iwnpi wlan0 set_bandwidth xx iwnpi wlan0 set_guard_interval iwnpi wlan0 sin_wave
     *
     */
    @Override
    public boolean txGo(WifiTX tx) {
        getChainValue(tx);
        tempPower = Integer.parseInt(tx.powerlevel, 10);

        if (sendCmdEng(SET_EUT_SET_LENGTH + tx.pktlength)
            && sendCmdEng(SET_EUT_SET_COUNT + tx.pktcnt)
            && sendCmdEng(SET_EUT_SET_POWER + tempPower)
            && sendCmdEng(SET_EUT_SET_PREAMBLE + tx.preamble)) {
            if (!Const.isMarlin2()) {
                return sendCmdEng(SET_EUT_PATH + chainVaule)
                    && sendCmdEng(SET_EUT_CBW + tx.bandwidth)
                    && sendCmdEng(SET_EUT_SBW + tx.bandsbw)
                    && sendCmdEng(SET_EUT_SET_CHANNEL + tx.channel)
                    && sendCmdEng(SET_EUT_SET_RATE + tx.rate)
                    && sendCmdEng(SET_EUT_GUARDINTERVAL + tx.guardinterval)
                    && sendCmdEng(SET_EUT_TX_START);
            } else {
                return sendCmdEng(SET_EUT_CBW + tx.bandwidth)
                    && sendCmdEng(SET_EUT_SET_CHANNEL + tx.channel)
                    && sendCmdEng(SET_EUT_SET_RATE + tx.rate)
                    && sendCmdEng(SET_EUT_GUARDINTERVAL + tx.guardinterval)
                    && sendCmdEng(SET_EUT_TX_START);
            }
        } else {
            sendCmdEng(SET_EUT_TX_START);
            return false;
        }
    }

    private void getChainValue(WifiTX tx) {
        if (TextUtils.isEmpty(tx.powerlevel)) {
            tx.powerlevel = "0";
        }
        if (tx.path != null) {
            if (tx.path.equals("MIMO")) {
                chainVaule = "3";
            } else if (tx.path.equals("Diversity")) {
                chainVaule = "2";
            } else {
                chainVaule = "1";
            }
        }
        Log.d(TAG, "tx.path: " + tx.path);
        Log.d(TAG, "tx.rate: " + tx.rate);
        Log.d(TAG, "tx.pktlength: " + tx.pktlength);
        Log.d(TAG, "tx.pktcnt: " + tx.pktcnt);
        Log.d(TAG, "tx.preamble: " + tx.preamble);
        Log.d(TAG, "tx.bandwidth: " + tx.bandwidth);
        Log.d(TAG, "tx.bandsbw: " + tx.bandsbw);
        Log.d(TAG, "tx.channel: " + tx.channel);
    }

    /**
     * This is for TX EX: iwnpi wlan0 tx_stop
     *
     */
    @Override
    public boolean txStop() {
        return sendCmdEng(SET_EUT_TX_STOP);
    }

    /**
     * This is for TX EX: iwnpi wlan0 set_channel xx iwnpi wlan0 tx_start
     *
     */
    @Override
    public boolean txCw(WifiTX tx) {
        getChainValue(tx);
        if (sendCmdEng(SET_EUT_SET_LENGTH + tx.pktlength )
            && sendCmdEng(SET_EUT_SET_COUNT + tx.pktcnt)
            && sendCmdEng(SET_EUT_SET_POWER + tempPower)
            && sendCmdEng(SET_EUT_SET_PREAMBLE + tx.preamble)) {
            if (!Const.isMarlin2()) {
                return sendCmdEng(SET_EUT_PATH + chainVaule)
                    && sendCmdEng(SET_EUT_CBW + tx.bandwidth)
                    && sendCmdEng(SET_EUT_SBW + tx.bandsbw)
                    && sendCmdEng(SET_EUT_SET_CHANNEL + tx.channel)
                    && sendCmdEng(SET_EUT_SET_RATE + tx.rate)
                    && sendCmdEng(SET_EUT_GUARDINTERVAL + tx.guardinterval)
                    && sendCmdEng(SET_EUT_CW_START);
            } else {
                return sendCmdEng(SET_EUT_CBW + tx.bandwidth)
                    && sendCmdEng(SET_EUT_SET_CHANNEL + tx.channel)
                    && sendCmdEng(SET_EUT_SET_RATE + tx.rate)
                    && sendCmdEng(SET_EUT_GUARDINTERVAL + tx.guardinterval)
                    && sendCmdEng(SET_EUT_CW_START);
            }
        } else {
            sendCmdEng(SET_EUT_CW_START);
            return false;
        }
    }

    /**
     * This is for RX EX: iwnpi wlan0 set_channel xx iwnpi wlan0 rx_start
     *
     */
    @Override
    public boolean rxStart(WifiRX rx) {
        String chain = "1";
        if (rx.path != null) {
            if (rx.path.equals("MIMO")) {
                chain = "3";
            } else if (rx.path.equals("Diversity")) {
                chain = "2";
            } else {
                chain = "1";
            }
        }
        if (sendCmdEng(SET_EUT_SET_CHANNEL + rx.channel)) {
            if (!Const.isMarlin2()) {
                return sendCmdEng(SET_EUT_PATH + chain)
                    && sendCmdEng(SET_EUT_CBW + rx.bandcbw)
                    && sendCmdEng(SET_EUT_SBW + rx.bandsbw)
                    && sendCmdEng(SET_EUT_RX_START);
            }
            return sendCmdEng(SET_EUT_RX_START);
        }
        return false;
    }

    /**
     * This is for RX EX: iwnpi wlan0 get_rx_ok
     *
     */
    @Override
    public String rxResult() {
        return sendCmdEngWithResult(SET_EUT_GET_RXOK);
    }

    /**
     * This is for RX EX: iwnpi wlan0 rx_stop
     *
     */
    @Override
    public boolean rxStop() {
        return sendCmdEng(SET_EUT_RX_STOP);
    }

    /**
     * This is for REG_R EX: iwnpi wlan0 get_reg %s(type) %x(Addr) %x(Length)
     *
     */
    @Override
    public String regr(WifiREG reg) {
        return sendCmdEngWithResult(SET_EUT_READ + reg.type + " " + reg.addr + " " + reg.length);
    }


    /**
     * This is for REG_W EX: iwnpi wlan0 set_reg %s(type) %x(Addr) %x(Vlaue)
     *
     */
    @Override
    public String regw(WifiREG reg) {
        return sendCmdEngWithResult(SET_EUT_WRITE + reg.type + " " + reg.addr + " " + reg.value);
    }

    @Override
    public boolean getPowerSaveState() {
        String ret = sendCmdEngWithResult(CMD_GET_POWER_SAVE_STATUS);
        checkResult(ret);

        String[] str = ret.split("\\:");
        String status = str[1].trim();
        return "1".equals(status);
    }

    @Override
    public void enablePowerSave() {
        if (!sendCmdEng(CMD_ENABLED_POWER_SAVE)) {
            fail();
        }
    }

    @Override
    public void disablePowerSave() {
        if (!sendCmdEng(CMD_DISABLED_POWER_SAVE)) {
            fail();
        }
    }

    /**
     * we should load wifi driver and start wifi before testing TX/RX
     *
     * @return true if loading driver success, false if fail
     */
    @Override
    public boolean insMod() {
        return sendCmdEng(WIFI_DRIVER_INSMOD);
    }

    /**
     * we shoule reload the wifi driver when finish WifiTXActivity/WifiRXActivity
     *
     * @return true if reload success, false if fail
     */
    @Override
    public boolean removeMod() {
        return sendCmdEng(WIFI_DRIVER_RMMOD);
    }

    @Override
    public void setTxPower(int power) {
        if (!sendCmdEng(SET_EUT_SET_POWER + power)) {
            fail();
        }
    }

    @Override
    public void enableAdaptive() {
        if (!sendCmd(SET_ADAPTIVE_ON)) {
            fail();
        }
    }

    @Override
    public void disableAdaptive() {
        if (!sendCmd(SET_ADAPTIVE_OFF)) {
            fail();
        }
    }

    @Override
    public boolean getAdaptiveState() {
        String ret = sendCmdWithResult(GET_ADAPTIVE_STATUS);
        return ret.contains("status: 1");
    }

    @Override
    public void enableSleepless() {
        if (!sendCmd(CMD_DISABLED_POWER_SAVE)) {
            fail();
        }
    }

    @Override
    public void disableSleepless() {
        if (!sendCmd(CMD_ENABLED_POWER_SAVE)) {
            fail();
        }
    }

    @Override
    public boolean getSleeplessState() {
        String ret = sendCmdWithResult(CMD_GET_POWER_SAVE_STATUS);
        return ret.contains("status: 1");
    }

    @Override
    public void enableScan() {
        if (!sendCmd(SET_SCAN_ON)) {
            fail();
        }
    }

    @Override
    public void disableScan() {
        if (!sendCmd(SET_SCAN_OFF)) {
            fail();
        }
    }

    @Override
    public boolean getScanState() {
        String ret = sendCmdWithResult(GET_SCAN_STATUS);
        return ret.contains("status: 1");
    }

    @Override
    public void enableBeamforming() {
        if (!sendCmd(SET_BEAMFORMING_ON)) {
            fail();
        }
    }

    @Override
    public void disableBeamforming() {
        if (!sendCmd(SET_BEAMFORMING_OFF)) {
            fail();
        }
    }

    @Override
    public boolean getBeamformingStatus() {
        String ret = sendCmdWithResult(GET_BEAMFORMING_STATUS);
        return ret.contains("status: 1");
    }

    @Override
    public void enableStbcRx() {
        if (!sendCmd(SET_STBC_RX_ON)) {
            fail();
        }
    }

    @Override
    public void disableStbcRx() {
        if (!sendCmd(SET_STBC_RX_OFF)) {
            fail();
        }
    }

    @Override
    public boolean getStbcRxStatus() {
        String ret = sendCmdWithResult(GET_STBC_RX_STATUS);
        return ret.contains("status: 1");
    }

    private boolean sendCmd(String cmd) {
        String ret = sendCmdWithResult(cmd);
        return ret != null && ret.startsWith(CMD_SUCCESS);
    }

    private boolean sendCmdEng(String cmd) {
        String ret = sendCmdEngWithResult(cmd);
        return ret != null && ret.startsWith(CMD_SUCCESS);
    }

    private String sendCmdWithResult(String command) {
        return HidlUtils.sendCmd(WCND_SOCKET_NAME + "eng " + command);
    }

    private String sendCmdEngWithResult(String command) {
        return HidlUtils.sendCmd(WCND_ENG_SOCKET_NAME + "eng " + command);
    }

    private void checkResult(String result) {
        if (result != null && result.startsWith(CMD_SUCCESS)) {
            return;
        }
        fail();
    }
    private void fail() {
        throw new OperationFailedException();
    }
}
