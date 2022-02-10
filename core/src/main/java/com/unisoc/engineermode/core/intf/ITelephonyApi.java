package com.unisoc.engineermode.core.intf;

import com.unisoc.engineermode.core.data.telephony.VamosCpcData;
import com.unisoc.engineermode.core.data.telephony.netinfostat.NetInfoStatData;
import com.unisoc.engineermode.core.factory.BaseInterface;

import java.util.List;

public interface ITelephonyApi {

    interface ITelephonyInfo {
        boolean isSupportLte();
        boolean isSupportCsfb();
        boolean isSupportWcdma();
        boolean isSupportC2k();//CDMA 2000
        boolean isSupportNr();
        boolean isSupportLplusG();
        boolean isSupportLplusW();
        boolean isSupportLplusL();
        boolean isSupportWplusG();
        boolean isSupportWplusW();
        boolean isSupportNetwork();
        boolean isSupportTdscdma();
        boolean isSupportGsm();
        boolean isSupportAPCFunc();
    }

    interface IEngTestStatus {
        boolean isEngTest();
        void set(int type);
    }

    interface ICallFunc {
        void openAutoAnswer() throws Exception;
        void closeAutoAnswer() throws Exception;
        String getCfu();
        void setCfu(String value) throws Exception;
    }

    interface INrCap {
        String getState() throws Exception;
        void setState(String value) throws Exception;
    }

    interface IControlEvdo extends BaseInterface {
        String getEvdoStatus() throws Exception;
        void openEvdo() throws Exception;
        void closeEvdo() throws Exception;
    }

    interface IControlMeid extends BaseInterface {
        String getMeidStatus() throws Exception;
        void openMeidSwitch() throws Exception;
        void closeMeidSwitch() throws Exception;
    }

    interface IWcdmaPrefer extends BaseInterface {
        String getWcdmaPrefer();
        void openWcdmaPrefer();
        void closeWcdmaPrefer();
    }

    interface IVolteUeSettings extends BaseInterface {
        enum VoiceCodec {
            EVS,
            AMR_WB,
            AMR_NB
        }

        enum VideoCodec {
            H264,
            H265
        }

        enum Bitrate {
            KBPS_5_9,
            KBPS_7_2,
            KBPS_8_0,
            KBPS_9_6,
            KBPS_13_2,
            KBPS_16_4,
            KBPS_24_4,
            KBPS_32_0,
            KBPS_48_0,
            KBPS_64_0,
            KBPS_96_0,
            KBPS_128_0
        }

        enum Bandwidth {
            NB,
            WB,
            SWB,
            FB
        }

        enum SmsPdu {
            TPDU,
            RPDU,
            TEXT
        }

        VoiceCodec[] getVoiceCodecType();
        void setVoiceCodecType(VoiceCodec[] voiceCodes);
        VideoCodec[] getVideoCodecType();
        void setVideoCodecType(VideoCodec[] videoCodes);
        Bandwidth getMaxBandWidth();
        void setMaxBandWidth(Bandwidth bandwidth);
        Bitrate getMaxBitRate();
        void setMaxBitRate(Bitrate bitrate);
        SmsPdu getSmsPdu();
        void setSmsPdu(SmsPdu pdu);
        String getSprdVolte();
        void setSprdVolte(String value);
        boolean getVideoCallState();
        void setVideoCallState(boolean state);
        boolean getVideoConferenceState();
        void setVideoConferenceState(boolean state);
    }

    interface IVolteTemporarySettings {
        void setImpi(String atCmd, int simIdx) throws Exception;
        void setImpu(String atCmd, int simIdx) throws Exception;
        void setDomain(String atCmd, int simIdx) throws Exception;
        void setPcscf(String cmdValue) throws Exception;
    }

    interface IVolteOperatorSettings {
        String getIpsecStatus(int simIdx) throws Exception;
        void setIpsecStatus(String atCmd, int simIdx) throws Exception;
        String getRegSubscribe(int simIdx) throws Exception;
        void setRegSubscribe(String atCmd, int simIdx) throws Exception;
        String getMwiEnable(int simIdx) throws Exception;
        void setMwiEnable(String atCmd, int simIdx) throws Exception;
        String getSIPHeaderCompact(int simIdx) throws Exception;
        void setSIPHeaderCompact(String atCmd, int simIdx) throws Exception;
        String getSMSoverIPEnable(int simIdx) throws Exception;
        void setSMSoverIPEnable(String atCmd, int simIdx) throws Exception;
        String getUssiEnable(int simIdx) throws Exception;
        void setUssiEnable(String atCmd, int simIdx) throws Exception;
        String getInitialRegister(int simIdx) throws Exception;
        void setInitialRegister(String atCmd, int simIdx) throws Exception;
        String getIntegrityAlgorithm(int simIdx) throws Exception;
        void setIntegrityAlgorithm(String atCmd, int simIdx) throws Exception;
        String getCipherAlgorithm(int simIdx) throws Exception;
        void setCipherAlgorithm(String atCmd, int simIdx) throws Exception;
        String getRegSubscrib(int simIdx) throws Exception;
        void setRegSubscrib(String atCmd, int simIdx) throws Exception;
        String getMwiSubscrib(int simIdx) throws Exception;
        void setMwiSubscrib(String atCmd, int simIdx) throws Exception;
        String getMtuValue(int simIdx) throws Exception;
        void setMtuValue(String atCmd, int simIdx) throws Exception;
        String getPcscfPort(int simIdx) throws Exception;
        void setPcscfPort(String atCmd, int simIdx) throws Exception;
        String getRegExpireValue(int simIdx) throws Exception;
        void setRegExpireValue(String atCmd, int simIdx) throws Exception;
        String getTregTimerValue(int simIdx) throws Exception;
        void setTregTimerValue(String atCmd, int simIdx) throws Exception;
        String getTemergregValue(int simIdx) throws Exception;
        void setTemergregValue(String atCmd, int simIdx) throws Exception;
        String getVoiceCodeType(int simIdx) throws Exception;
        void setVoiceCodeType(String atCmd, int simIdx) throws Exception;
        String getMinBandWidth(int simIdx) throws Exception;
        void setMinBandWidth(String atCmd, int simIdx) throws Exception;
        String getMaxBandWidth(int simIdx) throws Exception;
        void setMaxBandWidth(String atCmd, int simIdx) throws Exception;
        String getMinBitRate(int simIdx) throws Exception;
        void setMinBitRate(String atCmd, int simIdx) throws Exception;
        String getMaxBitRate(int simIdx) throws Exception;
        void setMaxBitRate(String atCmd, int simIdx) throws Exception;
        String getDefaultBitRate(int simIdx) throws Exception;
        void setDefaultBitRate(String atCmd, int simIdx) throws Exception;
        String getEvsRate(int simIdx) throws Exception;
        void setEvsRate(String atCmd, int simIdx) throws Exception;
        String getEvsDefaultBitRate(int simIdx) throws Exception;
        void setEvsDefaultBitRate(String atCmd, int simIdx) throws Exception;
        String getWbRate(int simIdx) throws Exception;
        void setWbRate(String atCmd, int simIdx) throws Exception;
        String getWbDefaultRate(int simIdx) throws Exception;
        void setWbDefaultRate(String atCmd, int simIdx) throws Exception;
        String getNbRate(int simIdx) throws Exception;
        void setNbRate(String atCmd, int simIdx) throws Exception;
        String getNbDefaultRate(int simIdx) throws Exception;
        void setNbDefaultRate(String atCmd, int simIdx) throws Exception;
        String getChannelAwarkMode(int simIdx) throws Exception;
        void setChannelAwarkMode(String atCmd, int simIdx) throws Exception;
        String getMoAmrOa(int simIdx) throws Exception;
        void setMoAmrOa(String atCmd, int simIdx) throws Exception;
        String getNamedTelEvent(int simIdx) throws Exception;
        void setNamedTelEvent(String atCmd, int simIdx) throws Exception;
        String getAudioRTPTimeout(int simIdx) throws Exception;
        void setAudioRTPTimeout(String atCmd, int simIdx) throws Exception;
        String getAudioRTCPTime(int simIdx) throws Exception;
        void setAudioRTCPTime(String atCmd, int simIdx) throws Exception;
        String getAudioRSValue(int simIdx) throws Exception;
        void setAudioRSValue(String atCmd, int simIdx) throws Exception;
        String getAudioRRValue(int simIdx) throws Exception;
        void setAudioRRValue(String atCmd, int simIdx) throws Exception;
        String getVideoRsValue(int simIdx) throws Exception;
        void setVideoRsValue(String atCmd, int simIdx) throws Exception;
        String getVideoRRValue(int simIdx) throws Exception;
        void setVideoRRValue(String atCmd, int simIdx) throws Exception;
        String getTcallTimerValue(int simIdx) throws Exception;
        void setTcallTimerValue(String atCmd, int simIdx) throws Exception;
        String getRingingTimerValue(int simIdx) throws Exception;
        void setRingingTimerValue(String atCmd, int simIdx) throws Exception;
        String getRingbackTimer(int simIdx) throws Exception;
        void setRingbackTimer(String atCmd, int simIdx) throws Exception;
        String getNoAnswerTimer(int simIdx) throws Exception;
        void setNoAnswerTimer(String atCmd, int simIdx) throws Exception;
        String getSessionTimer(int simIdx) throws Exception;
        void setSessionTimer(String atCmd, int simIdx) throws Exception;
        String getMinSeTimer(int simIdx) throws Exception;
        void setMinSeTimer(String value, int simIdx) throws Exception;
        String getMTSessionTimer(int simIdx) throws Exception;
        void setMTSessionTimer(String atCmd, int simIdx) throws Exception;
        String getConferenceURI(int simIdx) throws Exception;
        void setConferenceURI(String atCmd, int simIdx) throws Exception;
        String getVideoCodeType(int simIdx) throws Exception;
        void setVideoCodeType(String atCmd, int simIdx) throws Exception;
        String getReliable180(int simIdx) throws Exception;
        void setReliable180(String atCmd, int simIdx) throws Exception;
        String getDirectAlerting(int simIdx) throws Exception;
        void setDirectAlerting(String atCmd, int simIdx) throws Exception;
        String getResourceAlways(int simIdx) throws Exception;
        void setResourceAlways(String atCmd, int simIdx) throws Exception;
        String getPrecondition(int simIdx) throws Exception;
        void setPrecondition(String atCmd, int simIdx) throws Exception;
        String getVideoUpgrade(int simIdx) throws Exception;
        void setVideoUpgrade(String atCmd, int simIdx) throws Exception;
        String getVideoEarly(int simIdx) throws Exception;
        void setVideoEarly(String atCmd, int simIdx) throws Exception;
        String getAlertingValue(int simIdx) throws Exception;
        void setAlertingValue(String atCmd, int simIdx) throws Exception;
        String getMidCallSwitch(int simIdx) throws Exception;
        void setMidCallSwitch(String atCmd, int simIdx) throws Exception;
        String getPreAlerting(int simIdx) throws Exception;
        void setPreAlerting(String atCmd, int simIdx) throws Exception;
        String getRSRVCCSupport(int simIdx) throws Exception;
        void setRSRVCCSupport(String atCmd, int simIdx) throws Exception;
        String getRSRVCCAlert(int simIdx) throws Exception;
        void setRSRVCCAlert(String atCmd, int simIdx) throws Exception;
        String getRSRVCCMid(int simIdx) throws Exception;
        void setRSRVCCMid(String atCmd, int simIdx) throws Exception;
        String getVideoMaxResolution(int simIdx) throws Exception;
        void setVideoMaxResolution(String atCmd, int simIdx) throws Exception;
        String getNationalURL(int simIdx) throws Exception;
        void setNationalURL(String atCmd, int simIdx) throws Exception;
        String getInternationalURL(int simIdx) throws Exception;
        void setInternationalURL(String atCmd, int simIdx) throws Exception;
        String getTimerRefresher(int simIdx) throws Exception;
        void setTimerRefresher(String atCmd, int simIdx) throws Exception;
        String getMTTimerRefresher(int simIdx) throws Exception;
        void setMTTimerRefresher(String atCmd, int simIdx) throws Exception;
        String getVideoAudioMerge(int simIdx) throws Exception;
        void setVideoAudioMerge(String atCmd, int simIdx) throws Exception;
        String getAudioDynamic(int simIdx) throws Exception;
        void setAudioDynamic(String atCmd, int simIdx) throws Exception;
        String getVideoDynamic(int simIdx) throws Exception;
        void setVideoDynamic(String atCmd, int simIdx) throws Exception;
        String getBsfURIValue(int simIdx) throws Exception;
        void setBsfURIValue(String atCmd, int simIdx) throws Exception;
        String getBsfPORTValue(int simIdx) throws Exception;
        void setBsfPORTValue(String atCmd, int simIdx) throws Exception;
        String getXcapURIValue(int simIdx) throws Exception;
        void setXcapURIValue(String atCmd, int simIdx) throws Exception;
        String getXcapPortValue(int simIdx) throws Exception;
        void setXcapPortValue(String atCmd, int simIdx) throws Exception;
        String getXcapAuidValue(int simIdx) throws Exception;
        void setXcapAuidValue(String atCmd, int simIdx) throws Exception;
        String getLocalCall(int simIdx) throws Exception;
        void setLocalCall(String atCmd, int simIdx) throws Exception;
        String getDnsSrvEnable(int simIdx) throws Exception;
        void setDnsSrvEnable(String atCmd, int simIdx) throws Exception;
        String getHttpsEnable(int simIdx) throws Exception;
        void setHttpsEnable(String atCmd, int simIdx) throws Exception;
        String getActivateCfnl(int simIdx) throws Exception;
        void setActivateCfnl(String atCmd, int simIdx) throws Exception;
        String getHttpPutMedia(int simIdx) throws Exception;
        void setHttpPutMedia(String atCmd, int simIdx) throws Exception;
        String getIPPriority(int simIdx) throws Exception;
        void setIPPriority(String atCmd, int simIdx) throws Exception;
    }

    interface IVolteDevelopSettings extends BaseInterface {
        int getDevFlag1();
        int getDevFlag2();
        int getDevFlag3();
        void setDevFlag1(int value);
        void setDevFlag2(int value);
        void setDevFlag3(int value);
    }

    interface IVoltePlmnSettings extends BaseInterface {
        String getPlmn1();
        String getPlmn2();
        void setPlmn1(String plmn);
        void setPlmn2(String plmn);
    }

    interface IVolteEnable extends BaseInterface {
        boolean get();
        void set(boolean value);
    }

    interface ICftResult extends BaseInterface {
        String getGsmTdScdmaCFTResult() throws Exception;
        String getWcdmaCFTResult() throws Exception;
        String getC2kCFTResult() throws Exception;
        String getLteCFTResult() throws Exception;
        String getNrCFTResult() throws Exception;
    }

    interface ILtePrefer extends BaseInterface {
        void openLtePrefer(String val) throws Exception;
        void closeLtePrefer() throws Exception;
        String getLtePrefer() throws Exception;
    }

    interface INetInfoLteUeCap {
        boolean getCap(int simIdx, boolean[] isOn);
        boolean openDlca(int simIdx);
        boolean closeDlca(int simIdx);
        boolean openUlca(int simIdx);
        boolean closeUlca(int simIdx);
        boolean openUl64Qam(int simIdx);
        boolean closeUl64Qam(int simIdx);
    }

    interface INetInfoWcdmaUeCap {
        class UeCap {
            public boolean ul16Qam;
            public boolean snow3g;
            public boolean wDiversity;
            public boolean dbHsdpa;
            public boolean hsdpa;
        }

        class NwCap {
            // TODO
        }

        UeCap getUeCap(int simIdx);
//        boolean openCpc(int simIdx);
//        boolean closeCpc(int simIdx);
        boolean openUl16Qam(int simIdx);
        boolean closeUl16Qam(int simIdx);
        boolean openDbHsdpa(int simIdx);
        boolean closeDbHsdpa(int simIdx);
//        boolean openDcHsdpa(int simIdx);
//        boolean closeDcHsdpa(int simIdx);
//        boolean openEFach(int simIdx);
//        boolean closeEFach(int simIdx);
//        boolean openERach(int simIdx);
//        boolean closeERach(int simIdx);
        boolean openSnow3G(int simIdx);
        boolean closeSnow3G(int simIdx);
        boolean openWDiversity(int simIdx);
        boolean closeWDiversity(int simIdx);
//        boolean openType3i(int simIdx);
//        boolean closeType3i(int simIdx);
        boolean openUlHsdpa(int simIdx);
        boolean closeUlHsdpa(int simIdx);
//        boolean openUlHsupa(int simIdx);
//        boolean closeUlHsupa(int simIdx);
    }

    interface INetInfoC2kUeCap {
        String getUeCapInfo(int simIdx);
    }

    interface INetInfoGsmUeCap {
        boolean getVamos(int simIdx);
        boolean openVamos(int simIdx);
        boolean closeVamos(int simIdx);
        boolean getDiversity(int simIdx);
        boolean openDiversity(int simIdx);
        boolean closeDiversity(int simIdx);
    }

    interface INetInfoLte {
        void getServingCell(int simIdx, String[] names, String[] values);
        void getServingCellNR(int simIdx, String[] names, String[] values);
        void getAdjacentCell(int simIdx, String[][]values);
        void getBetweenAdjacentCell2G(int simIdx, String[][]values);
        void getBetweenAdjacentCell3G(int simIdx, String[][]values);
        List<String> getOutfieldNetworkInfo(int simIdx, String[] names);
    }

    interface INetInfoWcdma {
        void getServingCell(int simIdx, String[] names, String[] values);
        void getAdjacentCell(int simIdx, String[][]values);
        void getBetweenAdjacentCell2G(int simIdx, String[][]values);
        void getBetweenAdjacentCell4G(int simIdx, String[][]values);
        List<String> getOutfieldNetworkInfo(int simIdx, String[] names);
    }

    interface INetInfoC2k {
        void getServingCell(int simIdx, String[] names, String[] values);
        void getAdjacentCell(int simIdx, String[][]values);
        void getBetweenAdjacentCell2G(int simIdx, String[][]values);
        void getBetweenAdjacentCell4G(int simIdx, String[][]values);
        List<String> getOutfieldNetworkInfo(int simIdx, String[] names);
    }

    interface INetInfoC2k1x extends BaseInterface {
        void getServingCell(int simIdx, String[] names, String[] values);
        void getAdjacentCell(int simIdx, String[][]values);
        List<String> getOutfieldNetworkInfo(int simIdx, String[] names);
    }

    interface INetInfoTdscdma {
        void getServingCell(int simIdx, String[] names, String[] values);
        void getAdjacentCell(int simIdx, String[][]values);
        void getBetweenAdjacentCell2G(int simIdx, String[][]values);
        void getBetweenAdjacentCell4G(int simIdx, String[][]values);
        List<String> getOutfieldNetworkInfo(int simIdx, String[] names);
    }

    interface INetInfoGsm {
        void getServingCell(int simIdx, String[] names, String[] values);
        void getAdjacentCell(int simIdx, String[][]values);
        void getBetweenAdjacentCell3G(int simIdx, String[][]values);
        void getBetweenAdjacentCell4G(int simIdx, String[][]values);
        List<String> getOutfieldNetworkInfo(int simIdx, String[] names);
    }

    interface INetInfoNr {
        void getServingCell(int simIdx, String[] names, String[] values);
        void getAdjacentCell(int simIdx, String[][]values);
        void getBetweenAdjacentCell5G(int simIdx, String[][]values);
        List<String> getOutfieldNetworkInfo(int simIdx, String[] names);
    }
    interface ICsfb2GmsDelay {
        boolean getStatus(int simIdx, int[] status);
        boolean openGrrcResident(int simIdx);
        boolean closeGrrcResident(int simIdx);
        boolean openGrrcRandomAccess(int simIdx);
        boolean closeGrrcRandomAccess(int simIdx);
    }

    interface IVamosCpc {
        VamosCpcData getVamosCpc();
    }

    interface INetInfoStat {
        NetInfoStatData getReselectInfo(int simIdx) throws Exception;
        NetInfoStatData getHandOverInfo(int simIdx) throws Exception;

        class DataItem {
            public String desc;
            public String data;
        }
        DataItem[] getAttachTime(int simIdx) throws Exception;
        DataItem[] getDropCount(int simIdx) throws Exception;
    }

    interface IGprsSetting {
        void attach(int simIdx) throws Exception;
        void detach(int simIdx) throws Exception;
        void setAlwaysAttach(int simIdx) throws Exception;
        void setNeededAttach(int simIdx) throws Exception;
        boolean[] getPdpContextState(int simIdx) throws Exception;
        boolean activateFirstPdp(int simIdx, int trafficClass, int pdpType);
        boolean activateSecondPdp(int simIdx, int pdpType);
        boolean deactivatePdp(int simIdx, int pdpType);
        boolean sendData(int simIdx, int pdpType, String dataLength, String data);
        boolean getAutoAttachState(int simIdx) throws Exception;

    }

    interface IDataServicePrefer {
        void open() throws Exception;
        void close() throws Exception;
    }

    interface IFastDormancy {
        void close() throws Exception;
        void open(int value) throws Exception;
    }

    interface INetWorkMode {
        void setNetWorkMode(String cmd, int slot) throws Exception;
        String getNetWorkMode(int slot);
    }

    interface ISimTrace {
        boolean get() throws Exception;
        void open() throws Exception;
        void close() throws Exception;
    }

    interface IC2kConfig {
        String getC2kConfig() throws Exception;
        void setC2kConfig(String value) throws Exception;
    }

    interface IVideoType {
        int get() throws Exception;
        void set(int type) throws Exception;
    }

    interface IManualAssert extends BaseInterface {
        void assertModem();
        boolean isModemResetting();
        boolean isCp2Reset();
        void enableCp2Reset();
        void disableCp2Reset();
        void powerOnCp2();
        boolean canResetCp2();
        boolean canAssertCp2();
    }

    interface ISimFunc {
        String readPlmn(int simIdx) throws Exception;
    }

    interface INetInfo {
        String getPlmn() throws Exception;
    }

    interface IUsageSetting {
        String get() throws Exception;
        void set(String value);
    }

    interface IRadioFunc {
        void setMaxPower(Band band, int dbm) throws Exception;
        void clearMaxPower(Band band) throws Exception;
        DualRfState getDualRfState() throws Exception;
        void setDualRfState(DualRfState state) throws Exception;

        enum Band {
            GSM850(1),
            GSM900(2),
            DCS1800(3),
            DCS1900(4),
            TD19(5),
            TD21(6),
            WBAND1(7),
            WBAND2(8),
            WBAND5(9),
            WBAND8(10);
            /*
            38 LTE Band38
            39 LTE Band39
            40 LTE Band40
            41 LTE Band41
            42 LTE Band1
            43 LTE Band3
            44 LTE Band5
            45 LTE Band7
            46 LTE Band8
            47 LTE Band20
            */

            private int value;
            Band(int value) {
                this.value = value;
            }

            public int getValue() {
                return value;
            }

            public static Band of(int value) {
                for (Band band : values()) {
                    if (band.getValue() == value) {
                        return band;
                    }
                }
                return null;
            }
        }
        enum WcdmaDualRfState {
           PRIMARY_ONLY,
           PRIMARY_AND_DIVERSITY,
           DIVERSITY_ONLY
        }
        class DualRfState {
            public WcdmaDualRfState wcdmaState;
            public boolean isLteSccTxOpened;
            public boolean isLteDiversityRxOpened;
            public boolean isLtePrimaryTxOpened;
            public boolean isLtePrimaryRxOpened;
        }

        int getUeCategory() throws Exception;

    }

    interface IVamos extends BaseInterface {
        VamosState getState() throws Exception;
        void openCapability() throws Exception;
        void closeCapability() throws Exception;
        class VamosState {
            public boolean isSupported;
            public boolean isWorking;
        }
    }

    interface IDnsFilter extends BaseInterface {
        void set(int type);
    }

    interface IBand {
        enum GsmBand {
            GSM900, DCS1800, PCS1900, GSM850,
        }
        List<GsmBand> getGsmBand(int simIdx) throws Exception;
        void setGsmBand(int simIdx, List<GsmBand> bands) throws Exception;

        class TdBand {
           public boolean band_a;
           public boolean band_f;
        }

        TdBand getTdBand(int simIdx) throws Exception;
        void setTdBand(int simIdx, TdBand bands) throws Exception;

        enum WcdmaBand {
            WCDMA_BAND1(1),
            WCDMA_BAND2(2),
            WCDMA_BAND5(5),
            WCDMA_BAND8(8);

            private int value;
            WcdmaBand(int value) {
                this.value = value;
            }

            public int getValue() {
                return value;
            }

            public static WcdmaBand of(int value) {
                for (WcdmaBand band : values()) {
                    if (band.getValue() == value) {
                        return band;
                    }
                }
                return null;
            }
        }

        List<WcdmaBand> getWcdmaBand(int simIdx) throws Exception;
        void setWcdmaBand(int simIdx, List<WcdmaBand> enabledBands, List<WcdmaBand> disabledBands) throws Exception;
        boolean isWcdmaBandOpen(int simIdx, WcdmaBand band) throws Exception;

        enum CDMA2000Band {
            CDMA2000_BAND1(1);

            private int valueC2K;
            CDMA2000Band(int value) {
                this.valueC2K = value;
            }

            public int getValue() {
                return valueC2K;
            }

            public static CDMA2000Band of(int value) {
                for (CDMA2000Band band : values()) {
                    if (band.getValue() == value) {
                        return band;
                    }
                }
                return null;
            }
        }

        List<CDMA2000Band> getCDMA2000Band(int simIdx) throws Exception;
        void setCDMA2000Band(int simIdx, List<CDMA2000Band> enabledBands, List<CDMA2000Band> disabledBands) throws Exception;
        boolean isCDMA2000BandOpen(int simIdx, CDMA2000Band band) throws Exception;

        class LteBand {
            public int[] tddBands = new int[32];
            public int[] fddBands = new int[32];
        }

        LteBand getLteBand(int simIdx) throws Exception;
        void setLteBand(int simIdx, LteBand bands) throws Exception;

        /** sprd 1002440  : 5G netInfo mode
         * UNISOC: Bug1381637 Adapt and modify according to the underlying NR support. @{ */
        class NrBand {
            public int[] tddBands = new int[16];
            public int[] fddBands = new int[16];
            public String[] nrTddBandName = {"NR_BAND_N34","NR_BAND_N38","NR_BAND_N39","NR_BAND_N40","NR_BAND_N41","NR_BAND_N50","NR_BAND_N51","NR_BAND_N77",
        "NR_BAND_N78","NR_BAND_N79"};
            public String[] nrFddBandName = {"NR_BAND_N1","NR_BAND_N2","NR_BAND_N3","NR_BAND_N5","NR_BAND_N7","NR_BAND_N8","NR_BAND_N12","NR_BAND_N20","NR_BAND_N25", "NR_BAND_N28",
                    "NR_BAND_N66","NR_BAND_N70","NR_BAND_N71","NR_BAND_N74"};
        }

        NrBand getNrBand(int simIdx, NrBand supportedBand) throws Exception;
        NrBand getSupportNrBand(int simIdx) throws Exception;
        void setNrBand(int simIdx, NrBand bands) throws Exception;
        /** @} */
    }

    interface ITelephonyMgr {
        int getPhoneCount();
        int getPreferredNetworkType(int subId);
    }

    ITelephonyInfo telephonyInfo();
    IEngTestStatus EngTestStatus();
    ICallFunc callFunc();
    INrCap nrCap();
    ICftResult cftResult();
    IControlEvdo controlEvdo();
    IControlMeid controlMeidSwitch();
    IWcdmaPrefer wcdmaPrefer();
    IVolteUeSettings volteUeSettings();
    IVolteTemporarySettings volteTemporarySettings();
    IVolteOperatorSettings volteOperatorSettings();
    IVolteDevelopSettings volteDevelopSettings();
    IVoltePlmnSettings voltePlmnSettings();
    IVolteEnable volteEnable();
    INetInfoLteUeCap lteUeCapApi();
    INetInfoWcdmaUeCap wcdmaUeCapApi();
    INetInfoC2kUeCap c2kUeCapApi();
    INetInfoC2k netInfoC2kApi();
    INetInfoC2k1x netInfoC2k1xApi();
    INetInfoGsmUeCap gsmUeCapApi();
    INetInfoLte netInfoLteApi();
    INetInfoWcdma netInfoWcdmaApi();
    INetInfoNr netInfoNrApi();
    INetInfoTdscdma netInfoTdscdmaApi();
    INetInfoGsm netInfoGsmApi();
    ICsfb2GmsDelay csfb2GsmDealyApi();
    IVamosCpc vamosCpcApi();
    INetInfoStat netInfoStat();
    IGprsSetting gprsOper();
    IDataServicePrefer dataServicePrefer();
    IFastDormancy fastDormancy();
    INetWorkMode netWorkMode();
    ISimTrace simTrace();
    IC2kConfig c2kConfig();
    IVideoType videoType();
    IManualAssert manualAssert();
    ISimFunc simFunc();
    INetInfo netInfo();
    IRadioFunc radioFunc();
    IBand band();
    IVamos vamos();
    IDnsFilter dnsFilter();
    ILtePrefer ltePrefer();
    IUsageSetting usageSettingApi();
}
