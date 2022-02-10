package com.unisoc.engineermode.core.intf;

import com.unisoc.engineermode.core.factory.BaseInterface;

public interface IConnectivityApi {

    interface IWifiEut {
        class WifiTX {
            public String band;
            public String channel;
            public String pktlength;
            public String pktcnt;
            public String powerlevel;
            public String rate;
            public String mode;
            public String preamble;
            public String bandwidth;
            public String bandsbw;
            public String guardinterval;
            public String path;
        }

        class WifiRX {
            public String band;
            public String bandwidth;
            public String channel;
            public String rxtestnum;
            public String bandcbw;
            public String bandsbw;
            public String path;
        }

        class WifiREG {
            public String type;
            public String addr;
            public String length;
            public String value;
        }

        /**
         * creat wcnd socket and send iwnpi wlan0 ifaceup because this cmd is the first cmd, so creat
         * socket here but the socket is not closed here, and close it when wifiDown(), cause the
         * "iwnpi wlan0 ifacedown" cmd is the last cmd sended by the wcnd socket
         *
         * @return true if success
         */
        boolean up();

        /**
         * send iwnpi wlan0 start
         *
         * @return the cmd result, true if success
         */
        boolean start();

        /**
         * send cmd(iwnpi wlan0 ifacedown) and close wcnd socket
         *
         * @return true if success
         */
        boolean down();

        /**
         * send iwnpi wlan0 stop
         *
         * @return true if success
         */
        boolean stop();

        /**
         * This is for TX EX: mode 802.11 pkt iwnpi wlan0 set_channel xx iwnpi wlan0 set_pkt_length xx
         * iwnpi wlan0 set_tx_count xx iwnpi wlan0 set_tx_power xx iwnpi wlan0 set_rate xx iwnpi wlan0
         * set_preamble xx iwnpi wlan0 set_bandwidth xx iwnpi wlan0 set_guard_interval iwnpi wlan0
         * tx_start mode Sin Wave iwnpi wlan0 set_channel xx iwnpi wlan0 set_pkt_length xx iwnpi wlan0
         * set_tx_count xx iwnpi wlan0 set_tx_power xx iwnpi wlan0 set_rate xx iwnpi wlan0 set_preamble
         * xx iwnpi wlan0 set_bandwidth xx iwnpi wlan0 set_guard_interval iwnpi wlan0 sin_wave
         *
         */

        boolean txGo(WifiTX tx);

        /**
         * This is for TX EX: iwnpi wlan0 tx_stop
         *
         */
        boolean txStop();

        /**
         * This is for TX EX: iwnpi wlan0 set_channel xx iwnpi wlan0 tx_start
         *
         */
        boolean txCw(WifiTX tx);

        /**
         * This is for RX EX: iwnpi wlan0 set_channel xx iwnpi wlan0 rx_start
         *
         */

        boolean rxStart(WifiRX rx);

        /**
         * This is for RX EX: iwnpi wlan0 get_rx_ok
         *
         */
        String rxResult();

        /**
         * This is for RX EX: iwnpi wlan0 rx_stop
         *
         */
        boolean rxStop();

        /**
         * This is for REG_R EX: iwnpi wlan0 get_reg %s(type) %x(Addr) %x(Length)
         *
         */
        String regr(WifiREG reg);

        /**
         * This is for REG_W EX: iwnpi wlan0 set_reg %s(type) %x(Addr) %x(Vlaue)
         *
         */
        String regw(WifiREG reg);
        boolean getPowerSaveState();
        void enablePowerSave();
        void disablePowerSave();
        boolean getBeamformingStatus();
        void enableBeamforming();
        void disableBeamforming();
        boolean getStbcRxStatus();
        void enableStbcRx();
        void disableStbcRx();

        /**
         * analysis the socket result
         */
//        boolean analysisResult(int cmd, String result);

//        boolean sendCmd(String cmd, int anaycmd);
//        String sendCmdStr(String cmd, int anaycmd);

        /**
         * we should load wifi driver and start wifi before testing TX/RX
         *
         * @return true if loading driver success, false if fail
         */
        boolean insMod();

        /**
         * we shoule reload the wifi driver when finish WifiTXActivity/WifiRXActivity
         *
         * @return true if reload success, false if fail
         */
        boolean removeMod();
//        String reconnectionCount();
//        String ssidSignalStrength();
        void setTxPower(int power);
        void enableAdaptive();
        void disableAdaptive();
        boolean getAdaptiveState();
        void enableSleepless();
        void disableSleepless();
        boolean getSleeplessState();
        void enableScan();
        void disableScan();
        boolean getScanState();
    }

    interface IWifi {

    }

    interface IBtEut {
        class BTTX {
            public String pattern;
            public String channel;
            public String pactype;
            public String paclen;
            public String txMode;
            public String powervalue;
            public String paccnt;
            public String lephy;
            public String datalength;
        }

        class BTRX {
            public String pattern;
            public String channel;
            public String pactype;
            public String gain;
            public String addr;
            public String modindex;
            public String lephy;
        }

        class TxMode {
            public String channel;
            public String mode;
        }

        boolean BTTxStart(BTTX tx);
        boolean BTBLETxStart(BTTX tx);
        boolean BTTxStop(BTTX tx);
        boolean BTBLETxStop(BTTX tx);
        boolean BTOff();
        boolean BTRxStart(BTRX rx);
        boolean BTBLERxStart(BTRX rx);
        String BTRxRead();
        String BTBLERxRead();
        boolean BTRxStop(BTRX rx);
        boolean BTBLERxStop(BTRX rx);
        boolean getControllerBqbState() throws Exception;
        boolean controllerBqbEnable(boolean enable) throws Exception;
        boolean btTxModeStart(TxMode tx);
        boolean btTxModeStop(TxMode tx);
    }

    interface IWcndEngControl {
        boolean isWcndEngRunning() throws Exception;
        void startWcndEng() throws Exception;
        void stopWcndEng() throws Exception;
    }

    IWifiEut wifiEut();
    IBtEut btEut();
    IWcndEngControl wcndEngControl();
}
