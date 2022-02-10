package com.unisoc.engineermode.core.impl.telephony;

import com.unisoc.engineermode.core.intf.*;
import com.unisoc.engineermode.core.factory.ImplementationFactory;

public class TelephonyApiImpl implements ITelephonyApi {
    private static final String TAG = "TelephonyAPI";

    public static ITelephonyApi getInstance() {
        return TelephonyApiImplHolder.INSTANCE;
    }

    private static class TelephonyApiImplHolder {
        static final ITelephonyApi INSTANCE = new TelephonyApiImpl();
    }

    private static class TelephonyInfoImplHolder {
        static final ITelephonyInfo INSTANCE = new TelephonyInfoImpl();
    }

    private static class NetInfoWcdmaUeCapImplHolder {
        static final INetInfoWcdmaUeCap INSTANCE = new NetInfoWcdmaUeCapImpl();
    }

    private static class NetInfoC2kUeCapImplHolder {
        static final INetInfoC2kUeCap INSTANCE = new NetInfoC2kUeCapImpl();
    }

    private static class NetInfoGsmUeCapImplHolder {
        static final INetInfoGsmUeCap INSTANCE = new NetInfoGsmUeCapImpl();
    }

    private static class NetInfoLteImplHolder {
        static final INetInfoLte INSTANCE = new NetInfoLteImpl();
    }

    private static class NetInfoNrImplHolder {
        static final INetInfoNr INSTANCE = new NetInfoNrImpl();
    }

    private static class UsageSettingImplHolder {
        static final IUsageSetting INSTANCE = new UsageSettingImpl();
    }

    private static class NetInfoWcdmaImplHolder {
        static final INetInfoWcdma INSTANCE = new NetInfoWcdmaImpl();
    }

    private static class NetInfoC2kImplHolder {
        static final INetInfoC2k INSTANCE = new NetInfoC2kImpl();
    }

    private static class NetInfoTdscdmaImplHolder {
        static final INetInfoTdscdma INSTANCE = new NetInfoTdscdmaImpl();
    }

    private static class NetInfoGsmImplHolder {
        static final INetInfoGsm INSTANCE = new NetInfoGsmImpl();
    }

    private static class Csfb2GmsDelayImplHolder {
        static final ICsfb2GmsDelay INSTANCE = new Csfb2GmsDelayImpl();
    }

    private static class VamosCpcImplHolder {
        static final IVamosCpc INSTANCE = new VamosCpcImpl();
    }

    private static class NetInfoStatImplHolder {
        static final INetInfoStat INSTANCE = new NetInfoStatImpl();
    }

    private static class GprsSettingImplHolder {
        static final IGprsSetting INSTANCE = new GprsSettingImpl();
    }

    private static class DataServicePreferImplHolder {
        static final IDataServicePrefer INSTANCE = new DataServicePreferImpl();
    }

    private static class FastDormancyImplHolder {
        static final IFastDormancy INSTANCE = new FastDormancyImpl();
    }

    private static class SimTraceImplHolder {
        static final ISimTrace INSTANCE = new SimTraceImpl();
    }

    private static class C2kConfigImplHolder {
        static final IC2kConfig INSTANCE = new C2kConfigImpl();
    }

    private static class VideoTypeImplHolder {
        static final IVideoType INSTANCE = new VideoTypeImpl();
    }

    private static class SimFuncImplHolder {
        static final ISimFunc INSTANCE = new SimFuncImpl();
    }

    private static class NetInfoImplHolder {
        static final INetInfo INSTANCE = new NetInfoImpl();
    }

    private static class RadioFuncImplHolder {
        static final IRadioFunc INSTANCE = new RadioFuncImpl();
    }

    private static class BandImplHolder {
        static final IBand INSTANCE = new BandImpl();
    }

    private static class NetWorkModeImplHolder {
        static final INetWorkMode INSTANCE = new NetWorkModeImpl();
    }

    @Override
    public ITelephonyInfo telephonyInfo() {
        return TelephonyInfoImplHolder.INSTANCE;
    }

    @Override
    public IEngTestStatus EngTestStatus() {
        return EngTestStatusImpl.EngTestStatusImplHolder.INSTANCE;
    }

    @Override
    public ICallFunc callFunc() {
        return CallFuncImpl.CallFuncImplHolder.INSTANCE;
    }

    @Override
    public INrCap nrCap() {
        return NrCapImpl.NrCapImplHolder.INSTANCE;
    }

    @Override
    public ICftResult cftResult() {
        return (ICftResult) ImplementationFactory.create(ICftResult.class);
    }

    @Override
    public IControlEvdo controlEvdo() {
        return (IControlEvdo) ImplementationFactory.create(IControlEvdo.class);
    }

    @Override
    public IWcdmaPrefer wcdmaPrefer() {
        return (IWcdmaPrefer) ImplementationFactory.create(IWcdmaPrefer.class);
    }

    @Override
    public IVolteUeSettings volteUeSettings() {
        return (IVolteUeSettings) ImplementationFactory.create(IVolteUeSettings.class);
    }

    @Override
    public IVolteTemporarySettings volteTemporarySettings() {
        return VolteTemporarySettingsImpl.VolteTemporarySettingsImplHolder.INSTANCE;
    }

    @Override
    public IVolteOperatorSettings volteOperatorSettings() {
        return VolteOperatorSettingsImpl.VolteOperatorSettingsImplHolder.INSTANCE;
    }

    @Override
    public IVolteDevelopSettings volteDevelopSettings() {
        return (IVolteDevelopSettings) ImplementationFactory.create(IVolteDevelopSettings.class);
    }

    @Override
    public IVolteEnable volteEnable() {
        return VolteEnableImpl.VolteEnableImplHolder.INSTANCE;
    }

    @Override
    public IVoltePlmnSettings voltePlmnSettings() {
        return (IVoltePlmnSettings) ImplementationFactory.create(IVoltePlmnSettings.class);
    }

    @Override
    public INetInfoLteUeCap lteUeCapApi() {
       return NetInfoLteUeCapImpl.NetInfoLteUeCapImplHolder.INSTANCE;
    }

    @Override
    public INetInfoWcdmaUeCap wcdmaUeCapApi() {
        return NetInfoWcdmaUeCapImplHolder.INSTANCE;
    }

    @Override
    public INetInfoC2kUeCap c2kUeCapApi() {
        return NetInfoC2kUeCapImplHolder.INSTANCE;
    }

    @Override
    public INetInfoGsmUeCap gsmUeCapApi() {
        return NetInfoGsmUeCapImplHolder.INSTANCE;
    }

    @Override
    public INetInfoLte netInfoLteApi() {
        return NetInfoLteImplHolder.INSTANCE;
    }

    @Override
    public INetInfoNr netInfoNrApi() {
        return NetInfoNrImplHolder.INSTANCE;
    }

    @Override
    public IUsageSetting usageSettingApi() {
        return UsageSettingImplHolder.INSTANCE;
    }

    @Override
    public INetInfoWcdma netInfoWcdmaApi() {
        return NetInfoWcdmaImplHolder.INSTANCE;
    }

    @Override
    public INetInfoC2k netInfoC2kApi() {
        return NetInfoC2kImplHolder.INSTANCE;
    }

    @Override
    public INetInfoC2k1x netInfoC2k1xApi() {
        return (INetInfoC2k1x)ImplementationFactory.create(INetInfoC2k1x.class);
    }

    @Override
    public INetInfoTdscdma netInfoTdscdmaApi() {
        return NetInfoTdscdmaImplHolder.INSTANCE;
    }

    @Override
    public INetInfoGsm netInfoGsmApi() {
        return NetInfoGsmImplHolder.INSTANCE;
    }

    @Override
    public ICsfb2GmsDelay csfb2GsmDealyApi() {
        return Csfb2GmsDelayImplHolder.INSTANCE;
    }

    @Override
    public IVamosCpc vamosCpcApi() {
        return VamosCpcImplHolder.INSTANCE;
    }

    @Override
    public INetInfoStat netInfoStat() {
        return NetInfoStatImplHolder.INSTANCE;
    }

    @Override
    public IGprsSetting gprsOper() {
        return GprsSettingImplHolder.INSTANCE;
    }

    @Override
    public IDataServicePrefer dataServicePrefer() {
        return DataServicePreferImplHolder.INSTANCE;
    }

    @Override
    public IFastDormancy fastDormancy() {
        return FastDormancyImplHolder.INSTANCE;
    }

    @Override
    public ISimTrace simTrace() {
        return SimTraceImplHolder.INSTANCE;
    }

    @Override
    public IC2kConfig c2kConfig() {
        return C2kConfigImplHolder.INSTANCE;
    }

    @Override
    public IVideoType videoType() {
        return VideoTypeImplHolder.INSTANCE;
    }

    @Override
    public IManualAssert manualAssert() {
        return (IManualAssert) ImplementationFactory.create(IManualAssert.class);
    }

    @Override
    public ISimFunc simFunc() {
        return SimFuncImplHolder.INSTANCE;
    }

    @Override
    public INetInfo netInfo() {
        return NetInfoImplHolder.INSTANCE;
    }

    @Override
    public IRadioFunc radioFunc() {
        return RadioFuncImplHolder.INSTANCE;
    }

    @Override
    public IBand band() {
        return BandImplHolder.INSTANCE;
    }

    @Override
    public INetWorkMode netWorkMode() {
        return NetWorkModeImplHolder.INSTANCE;
    }

    @Override
    public IVamos vamos() {
        return (IVamos) ImplementationFactory.create(IVamos.class);
    }

    @Override
    public IDnsFilter dnsFilter() {
        return (IDnsFilter)ImplementationFactory.create(IDnsFilter.class);
    }

    @Override
    public IControlMeid controlMeidSwitch() {
        return (IControlMeid)ImplementationFactory.create(IControlMeid.class);
    }

    @Override
    public ILtePrefer ltePrefer() {
        return (ILtePrefer)ImplementationFactory.create(ILtePrefer.class);
    }
}
