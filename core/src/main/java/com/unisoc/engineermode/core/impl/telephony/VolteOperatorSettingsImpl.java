package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi;
import com.unisoc.engineermode.core.utils.IATUtils;

class VolteOperatorSettingsImpl implements ITelephonyApi.IVolteOperatorSettings {

    private static final String TAG = "VolteOperatorSettingsImpl";

    public static class VolteOperatorSettingsImplHolder {
        static final ITelephonyApi.IVolteOperatorSettings INSTANCE = new VolteOperatorSettingsImpl();
    }

    @Override
    public String getIpsecStatus(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "95,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setIpsecStatus(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getRegSubscribe(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "30,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setRegSubscribe(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMwiEnable(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "33,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setMwiEnable(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getSIPHeaderCompact(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "44,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setSIPHeaderCompact(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getSMSoverIPEnable(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_GET_SMS_IP, simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return mStrTmp.split("\\:")[1].split("\n")[0].trim();
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setSMSoverIPEnable(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getUssiEnable(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "41,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setUssiEnable(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getInitialRegister(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "32,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setInitialRegister(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getIntegrityAlgorithm(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "96,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setIntegrityAlgorithm(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getCipherAlgorithm(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "97,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setCipherAlgorithm(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getRegSubscrib(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "31,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setRegSubscrib(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMwiSubscrib(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "35,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setMwiSubscrib(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMtuValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "86,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setMtuValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getPcscfPort(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "87,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setPcscfPort(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getRegExpireValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "29,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setRegExpireValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getTregTimerValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "25,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setTregTimerValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getTemergregValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "26,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setTemergregValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getVoiceCodeType(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "65,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setVoiceCodeType(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMinBandWidth(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "69,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setMinBandWidth(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMaxBandWidth(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "70,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setMaxBandWidth(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMinBitRate(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "71,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setMinBitRate(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMaxBitRate(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "72,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setMaxBitRate(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getDefaultBitRate(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "77,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setDefaultBitRate(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getEvsRate(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "68,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setEvsRate(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getEvsDefaultBitRate(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "76,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setEvsDefaultBitRate(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getWbRate(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "67,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setWbRate(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getWbDefaultRate(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "75,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setWbDefaultRate(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getNbRate(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "66,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setNbRate(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getNbDefaultRate(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "74,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setNbDefaultRate(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getChannelAwarkMode(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "73,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setChannelAwarkMode(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMoAmrOa(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "78,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setMoAmrOa(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getNamedTelEvent(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "79,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setNamedTelEvent(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getAudioRTPTimeout(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "45,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setAudioRTPTimeout(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getAudioRTCPTime(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "47,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setAudioRTCPTime(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getAudioRSValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "89,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setAudioRSValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getAudioRRValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "90,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setAudioRRValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getVideoRsValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "91,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setVideoRsValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getVideoRRValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "92,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setVideoRRValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getTcallTimerValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "23,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setTcallTimerValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getRingingTimerValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "48,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setRingingTimerValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getRingbackTimer(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "49,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setRingbackTimer(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getNoAnswerTimer(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "50,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setNoAnswerTimer(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getSessionTimer(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "37,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setSessionTimer(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMinSeTimer(int simIdx) throws Exception {
        String strTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "36,0", simIdx);
        if (strTmp.contains(IATUtils.AT_OK)) {
            return anayResult(strTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, strTmp);
        }
    }

    @Override
    public void setMinSeTimer(String value, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "36,1," + "\"" + value + "\"", simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMTSessionTimer(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "39,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setMTSessionTimer(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getConferenceURI(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "53,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setConferenceURI(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getVideoCodeType(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "101,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setVideoCodeType(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getReliable180(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "100,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setReliable180(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getDirectAlerting(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "99,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setDirectAlerting(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getResourceAlways(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "102,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setResourceAlways(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getPrecondition(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "93,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setPrecondition(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getVideoUpgrade(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "94,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setVideoUpgrade(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getVideoEarly(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "51,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setVideoEarly(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getAlertingValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "80,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setAlertingValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMidCallSwitch(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "81,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setMidCallSwitch(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getPreAlerting(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "82,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setPreAlerting(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getRSRVCCSupport(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "83,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setRSRVCCSupport(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getRSRVCCAlert(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "84,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setRSRVCCAlert(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getRSRVCCMid(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "85,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setRSRVCCMid(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getVideoMaxResolution(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "106,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setVideoMaxResolution(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getNationalURL(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "42,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setNationalURL(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getInternationalURL(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "43,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setInternationalURL(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getTimerRefresher(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "38,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setTimerRefresher(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getMTTimerRefresher(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "40,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setMTTimerRefresher(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getVideoAudioMerge(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "54,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setVideoAudioMerge(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getAudioDynamic(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "104,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setAudioDynamic(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getVideoDynamic(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "20,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setVideoDynamic(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getBsfURIValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "60,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setBsfURIValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getBsfPORTValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "61,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setBsfPORTValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getXcapURIValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "62,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setXcapURIValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getXcapPortValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "63,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setXcapPortValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getXcapAuidValue(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "64,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setXcapAuidValue(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getLocalCall(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "55,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setLocalCall(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getDnsSrvEnable(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "56,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setDnsSrvEnable(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getHttpsEnable(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "58,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setHttpsEnable(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getActivateCfnl(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "103,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setActivateCfnl(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getHttpPutMedia(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "59,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setHttpPutMedia(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    @Override
    public String getIPPriority(int simIdx) throws Exception {
        String mStrTmp = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "57,0", simIdx);
        if (mStrTmp.contains(IATUtils.AT_OK)) {
            return anayResult(mStrTmp);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, mStrTmp);
        }
    }

    @Override
    public void setIPPriority(String atCmd, int simIdx) throws Exception {
        String atRSP = IATUtils.sendATCmd(atCmd, simIdx);
        if (!atRSP.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRSP);
        }
    }

    private String anayResult(String result) {
        String res = null;
        try {
            if (!result.contains("OK")) {
                return res = "FAILED";
            }
            res = result.split("\\:")[1].split("\n")[0].split(",")[1].trim().replace("\"", "");
            Log.d(TAG, "anayResult anayResult is: " + res);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return res;
    }
}
