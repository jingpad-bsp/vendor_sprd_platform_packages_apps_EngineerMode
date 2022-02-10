package com.unisoc.engineermode.core.impl.telephony;

import android.util.Log;
import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.common.engconstents;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.ITelephonyApi.IVolteUeSettings;
import com.unisoc.engineermode.core.utils.IATUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Implementation(
    interfaceClass = IVolteUeSettings.class,
    properties =  {
    })
public class VolteUESettingsImpl implements IVolteUeSettings {
    private static final String TAG = "VolteUESettingsImpl";
    private static final int SIM0 = 0;

    private Map<VoiceCodec, Integer> voiceCodecValueMap = new EnumMap<>(VoiceCodec.class);
    private Map<VideoCodec, Integer> videoCodecValueMap = new EnumMap<>(VideoCodec.class);
    private Map<Bandwidth, Integer> bandwidthValueMap = new EnumMap<>(Bandwidth.class);
    private Map<Bitrate, Integer> bitrateValueMap = new EnumMap<>(Bitrate.class);
    private Map<SmsPdu, Integer> smsPduValueMap = new EnumMap<>(SmsPdu.class);

    public VolteUESettingsImpl() {
        /*
            we put code-value relations here other than enum' definition
            because this relations might be changed in the future and
            definitions are in interfaces which should keep stable
        */
        voiceCodecValueMap.put(VoiceCodec.EVS, 0x1);
        voiceCodecValueMap.put(VoiceCodec.AMR_WB, 0x2);
        voiceCodecValueMap.put(VoiceCodec.AMR_NB, 0x4);

        videoCodecValueMap.put(VideoCodec.H264, 0x1);
        videoCodecValueMap.put(VideoCodec.H265, 0x2);

        int i = 0;
        for (Bandwidth bw : Bandwidth.values()) {
            bandwidthValueMap.put(bw, i);
            i++;
        }

        i = 0;
        for (Bitrate br : Bitrate.values()) {
            bitrateValueMap.put(br, i);
            i++;
        }

        i = 0;
        for (SmsPdu sp : SmsPdu.values()) {
            smsPduValueMap.put(sp, i);
            i++;
        }
    }

    @Override
    public VoiceCodec[] getVoiceCodecType() {
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "6,0", SIM0);
        if (result.contains(IATUtils.AT_OK)) {
            return getTypes(result, voiceCodecValueMap).toArray(new VoiceCodec[0]);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public void setVoiceCodecType(VoiceCodec[] voiceCodecs) {
        Log.d(TAG, "setVoiceCodecType, "+ Arrays.toString(Arrays.stream(voiceCodecs).map(Enum::name).toArray()));
        int codeValue = getValues(voiceCodecs, voiceCodecValueMap);
        String atCmd = String.format("%s6,1,\"%d\"",engconstents.ENG_AT_SPVOLTEENG, codeValue);
        String atRsp = IATUtils.sendATCmd(atCmd, SIM0);

        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRsp);
        }
    }

    @Override
    public VideoCodec[] getVideoCodecType() {
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "9,0", SIM0);
        if (result.contains(IATUtils.AT_OK)) {
            return getTypes(result, videoCodecValueMap).toArray(new VideoCodec[0]);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public void setVideoCodecType(VideoCodec[] videoCodecs) {
        Log.d(TAG, "setVideoCodecType, "+ Arrays.toString(Arrays.stream(videoCodecs).map(Enum::name).toArray()));
        int codeValue = getValues(videoCodecs, videoCodecValueMap);
        String atCmd = String.format("%s9,1,\"%d\"",engconstents.ENG_AT_SPVOLTEENG, codeValue);
        String atRsp = IATUtils.sendATCmd(atCmd, SIM0);
        if (!atRsp.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, atRsp);
        }
    }

    @Override
    public Bandwidth getMaxBandWidth() {
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "7,0", SIM0);
        if (result.contains(IATUtils.AT_OK)) {
            return getType(result, bandwidthValueMap);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public void setMaxBandWidth(Bandwidth bandwidth) {
        int value = getValue(bandwidth, bandwidthValueMap);
        String at = engconstents.ENG_AT_SPVOLTEENG + "7,1," + "\"" + value + "\"";
        String result = IATUtils.sendATCmd(at, SIM0);
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public Bitrate getMaxBitRate() {
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "8,0", SIM0);
        if (result.contains(IATUtils.AT_OK)) {
            return getType(result, bitrateValueMap);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public void setMaxBitRate(Bitrate bitRate) {
        int value = getValue(bitRate, bitrateValueMap);
        String at = engconstents.ENG_AT_SPVOLTEENG + "8,1," + "\"" + value + "\"";
        String result = IATUtils.sendATCmd(at, SIM0);
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public SmsPdu getSmsPdu() {
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "19,0", SIM0);
        if (result.contains(IATUtils.AT_OK)) {
            return getType(result, smsPduValueMap);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public void setSmsPdu(SmsPdu pdu) {
        int value = getValue(pdu, smsPduValueMap);
        String at = engconstents.ENG_AT_SPVOLTEENG + "19,1," + "\"" + value + "\"";
        String result = IATUtils.sendATCmd(at, SIM0);
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public String getSprdVolte() {
        String cmd = String.format("%s1,0", engconstents.ENG_AT_SPVOLTEENG);
        String result = IATUtils.sendATCmd(cmd, SIM0);
        if (result.contains(IATUtils.AT_OK)) {
            return extractValue(result);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public void setSprdVolte(String value) {
        String cmd = String.format("%s1,1,\"%s\"", engconstents.ENG_AT_SPVOLTEENG, value);
        String result = IATUtils.sendATCmd(cmd, SIM0);
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }


    @Override
    public boolean getVideoCallState() {
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "3,0", SIM0);
        if (result.contains(IATUtils.AT_OK)) {
            return getState(result);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public void setVideoCallState(boolean state) {
        String value = state? "1" : "0";
        String at = engconstents.ENG_AT_SPVOLTEENG + "3,1," + "\"" + value + "\"";
        String result = IATUtils.sendATCmd(at, SIM0);
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public boolean getVideoConferenceState() {
        String result = IATUtils.sendATCmd(engconstents.ENG_AT_SPVOLTEENG + "5,0", SIM0);
        if (result.contains(IATUtils.AT_OK)) {
            return getState(result);
        } else {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    @Override
    public void setVideoConferenceState(boolean state) {
        String value = state? "1" : "0";
        String at = engconstents.ENG_AT_SPVOLTEENG + "5,1," + "\"" + value + "\"";
        String result = IATUtils.sendATCmd(at, SIM0);
        if (!result.contains(IATUtils.AT_OK)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }
    }

    private Pattern pattern = Pattern.compile("\\+SPVOLTEENG: [0-9]+,\"(.*)\"\r\nOK\r\n");
    private String extractValue(String result) {
        Matcher matcher = pattern.matcher(result);
        if (!matcher.find() || (matcher.group(1) == null)) {
            throw new OperationFailedException(ErrorCode.AT_RETURN_ERROR, result);
        }

        return matcher.group(1);
    }

    private boolean getState(String result) {
        String value = extractValue(result);
        if (value.equals("0")) {
            return false;
        } else if (value.equals("1")) {
            return true;
        }
        throw new OperationFailedException("get state failed," + result);
    }

    private <T extends Enum<T>> List<T> getTypes(String result, Map<T, Integer> typeValueMap) {
        List<T> types = new ArrayList<>();
        int value;
        try {
            value = Integer.parseInt(extractValue(result));
        } catch (NumberFormatException e) {
            throw new OperationFailedException("value is not number," + result);
        }

        for (Map.Entry<T, Integer> entry : typeValueMap.entrySet()) {
            if ((value & entry.getValue()) != 0) {
                types.add(entry.getKey());
            }
        }

        Log.d(TAG, "get codes: " +
            types.stream().map(Enum::name).reduce((t, u) -> t + "," + u).orElse(""));
        return types;
    }

    private <T extends Enum<T>> T getType(String result, Map<T, Integer> typeValueMap) {
        int value;
        try {
            value = Integer.parseInt(extractValue(result));
        } catch (NumberFormatException e) {
            throw new OperationFailedException("value is not number," + result);
        }

        for (Map.Entry<T, Integer> entry : typeValueMap.entrySet()) {
            if ((value == entry.getValue())) {
                Log.d(TAG, "get code: " + entry.getKey().name());
                return entry.getKey();
            }
        }
        throw new OperationFailedException("get value failed," + result);
    }

    private <T extends Enum<T>> int getValues(T[] types, Map<T, Integer> typeValueMap) {
        int value = 0;

        for (T t : types) {
            Integer val = typeValueMap.get(t);
            if (val != null) {
                value |= val;
            }
        }
        return value;
    }

    private <T extends Enum<T>> int getValue(T type, Map<T, Integer> typeValueMap) {
        Integer value = typeValueMap.get(type);
        if (value == null) {
            throw new OperationFailedException("get value failed," + type.name());
        }
        return value;
    }
}
