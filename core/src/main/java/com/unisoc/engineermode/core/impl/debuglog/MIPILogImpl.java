package com.unisoc.engineermode.core.impl.debuglog;

import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy;
import com.unisoc.engineermode.core.intf.IMIPILog;
import com.unisoc.engineermode.core.utils.SocketUtils;

@Implementation(
        interfaceClass = IMIPILog.class,
        properties =  {
        })
public class MIPILogImpl implements IMIPILog {
    private static final String PROPERTIES_MIPI_CHANNEL = "persist.sys.mipi.channel";
    private static final String PROP_SSDA_MODE = "persist.vendor.radio.modem.config";
    private static final String NR_FLAG = "NR";
    private static final String CHANNEL_CLOSE = "0";
    private static final String CHANNEL_TRANNING = "1";
    private static final String CHANNEL_WTL = "2";

    @Override
    public boolean isSupportNr() {
        try {
            String ssdaMode = SystemPropertiesProxy.get(PROP_SSDA_MODE);
            boolean support = ssdaMode != null && (ssdaMode.contains(NR_FLAG));
            return support;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setMIPILogSerdes(String cmd) throws Exception {
        String result = SocketUtils.SendSlogModemAt(cmd);
        if (!(result != null && result.contains(SocketUtils.OK))) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public String getMIPILogSerdes(String cmd) {
        return SocketUtils.SendSlogModemAt(cmd);
    }

    @Override
    public void setMIPILogChannel(String channel) {
        SystemPropertiesProxy.set(PROPERTIES_MIPI_CHANNEL, channel);
    }

    @Override
    public String getMIPILogChannel() {
        return SystemPropertiesProxy.get(PROPERTIES_MIPI_CHANNEL, CHANNEL_CLOSE);
    }
}
