package com.unisoc.engineermode.core.impl.hardware;

import java.nio.charset.StandardCharsets;
import android.util.Log;

import com.unisoc.engineermode.core.utils.SocketUtils;

/*Parse the phasecheck as the little endian*/
public class PhaseCheckParse {
    private static String TAG = "PhaseCheckParse";

    private static int TYPE_WRITE_CHARGE_SWITCH = 6;
    private static int TYPE_GET_KERNEL_LOG_LEVEL = 11;
    private static int TYPE_SET_KERNEL_LOG_LEVEL = 12;
    private static int TYPE_WRITE_MIPI_SWITCH = 13;
    private static int TYPE_GET_CC_TEST_RESULT = 15;
    private static int TYPE_GET_CC_TEST_VOL = 19;
    private static int TYPE_GET_DELTANV_INFO = 20;
    private static int TYPE_READ_CHARGE_STATUS = 21;
    private static int TYPE_READ_CHARGE_LEVEL = 22;
    private static int TYPE_GET_CC_ENERGY_NEW_KERNEL = 27;
    private static int TYPE_GET_CC_VOL_NEW_KERNEL = 28;
    private static int TYPE_SET_CABC_MODE = 32;
    private static int TYPE_SET_CPU_DEBUG_MODE = 35;
    private static int TYPE_GET_CPU_DEBUG_MODE = 36;

    private static int BUF_SIZE = 4096;
    private byte[] stream = new byte[300];
    private AdaptBinder binder;

    public PhaseCheckParse() {
        if (!checkPhaseCheck()) {
            stream = null;
        }

        binder = new AdaptBinder();
        Log.e(TAG, "Get The service connect!");
    }

    private boolean checkPhaseCheck() {
        Log.d(TAG, " " + stream[0] + stream[1] + stream[2] + stream[3]);
        return (stream[0] == '9' || stream[0] == '5')
            && stream[1] == '0'
            && stream[2] == 'P'
            && stream[3] == 'S';
    }

    public String getDeltaNVInfo() {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        binder.transact(TYPE_GET_DELTANV_INFO, data, reply, 0);
        String retValue = reply.readString();
        Log.e(TAG, "getDeltaNVInfo retValue is : " + retValue);
        data.recycle();
        return retValue;
    }

    public boolean writeChargeSwitch(int value) {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        data.writeInt(value);
        binder.transact(TYPE_WRITE_CHARGE_SWITCH, data, reply, 0);
        Log.e(TAG, "writeChargeSwitch data = " + reply.readString());
        data.recycle();
        return true;
    }

    public String readChargeState() {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        binder.transact(TYPE_READ_CHARGE_STATUS, data, reply, 0);
        String retValue = reply.readString();
        Log.e(TAG, "readChargeState retValue is : " + retValue);
        data.recycle();
        return retValue;
    }

    public String readChargeLevel() {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        binder.transact(TYPE_READ_CHARGE_LEVEL, data, reply, 0);
        String retValue = reply.readString();
        Log.e(TAG, "readChargeLevel retValue is : " + retValue);
        data.recycle();
        return retValue;
    }

    public boolean writeMIPISwitch(int value) {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        data.writeInt(value);
        binder.transact(TYPE_WRITE_MIPI_SWITCH, data, reply, 0);
        Log.e(TAG, "writeMIPISwitch data = " + reply.readString());
        data.recycle();
        return true;
    }

    public String getCcTestSwitch() {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        binder.transact(TYPE_GET_CC_TEST_RESULT, data, reply, 0);
        String retValue = reply.readString();
        Log.e(TAG, "getCcTestSwitch retValue is : " + retValue);
        data.recycle();
        return retValue;
    }

    public String getCcTestVoltage() {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        binder.transact(TYPE_GET_CC_TEST_VOL, data, reply, 0);
        String retValue = reply.readString();
        Log.e(TAG, "getCcTestVoltage retValue is : " + retValue);
        data.recycle();
        return retValue;
    }

    public String getCcEnergyNewKernel() {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        binder.transact(TYPE_GET_CC_ENERGY_NEW_KERNEL, data, reply, 0);
        String retValue = reply.readString();
        Log.e(TAG, "getCcEnergyNewKernel retValue is : " + retValue);
        data.recycle();
        return retValue;
    }

    public String getCcVoltagesNewKernel() {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        binder.transact(TYPE_GET_CC_VOL_NEW_KERNEL, data, reply, 0);
        String retValue = reply.readString();
        Log.e(TAG, "getCcVoltagesNewKernel retValue is : " + retValue);
        data.recycle();
        return retValue;
    }

    public int getKernelLogLevelState() {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        binder.transact(TYPE_GET_KERNEL_LOG_LEVEL, data, reply, 0);
        int retValue = reply.readInt();
        Log.e(TAG, "get kernel log level state retValue is : " + retValue);
        data.recycle();
        return retValue;
    }

    public boolean setKernelLogLevelState(int state) {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        data.writeInt(state);
        binder.transact(TYPE_SET_KERNEL_LOG_LEVEL, data, reply, 0);
        int retValue = reply.readInt();
        Log.e(TAG, "set kernel log level state retValue is : " + retValue);
        data.recycle();
        return retValue == 1;
    }

    public boolean setCabc(int value) {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        data.writeInt(value);
        binder.transact(TYPE_SET_CABC_MODE, data, reply, 0);
        int retValue = reply.readInt();
        Log.e(TAG, "setCabc data = " + reply.readString());
        data.recycle();
        return 1 == retValue;
    }

    /**Unisoc:Bug1522715 add cpu debug switch @{ **/
    public boolean setCpuDebug(int value) {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        data.writeInt(value);
        binder.transact(TYPE_SET_CPU_DEBUG_MODE, data, reply, 0);
        int retValue = reply.readInt();
        Log.e(TAG, "setCpuDebug data = " + reply.readString());
        data.recycle();
        return 1 == retValue;
    }

    public String getCpudebug() {
        Parcel data = new Parcel();
        Parcel reply = new Parcel();
        binder.transact(TYPE_GET_CPU_DEBUG_MODE, data, reply, 0);
        String retValue = reply.readString();
        Log.e(TAG, "get cpu debug state retValue is : " + retValue);
        data.recycle();
        return retValue;
    }

    /**  @}  **/
    private static class AdaptParcel {
        int code;
        int dataSize;
        int replySize;
        byte[] data;
    }

    static class AdaptBinder {
        private AdaptParcel mAdpt;
        private static final String SOCKET_NAME = "phasecheck_srv";

        AdaptBinder() {
            mAdpt = new AdaptParcel();
            mAdpt.data = new byte[BUF_SIZE];
            mAdpt.code = 0;
            mAdpt.dataSize = 0;
            mAdpt.replySize = 0;
        }

        private void int2byte(byte[] dst, int offset, int value) {
            dst[offset+3] = (byte)(value >> 24 & 0xff);
            dst[offset+2] = (byte)(value >> 16 & 0xff);
            dst[offset+1] = (byte)(value >> 8 & 0xff);
            dst[offset] = (byte)(value & 0xff);
        }

        int byte2Int(byte[] bytes, int off) {
            int b0 = bytes[off] & 0xFF;
            int b1 = bytes[off + 1] & 0xFF;
            int b2 = bytes[off + 2] & 0xFF;
            int b3 = bytes[off + 3] & 0xFF;
            return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
        }

        synchronized void sendCmdAndRecResult(AdaptParcel adpt) {
            Log.d(TAG, "send cmd");
            byte[] buf = new byte[BUF_SIZE];
            int2byte(buf, 0, adpt.code);
            int2byte(buf, 4, adpt.dataSize);
            int2byte(buf, 8, adpt.replySize);

            System.arraycopy(adpt.data, 0, buf, 12, adpt.dataSize+adpt.replySize);
            Log.d(TAG, "code = "+adpt.code);
            Log.d(TAG, "dataSize = "+adpt.dataSize);
            Log.d(TAG, "replySize = "+adpt.replySize);

            if (!SocketUtils.sendCmd(SOCKET_NAME, buf, buf)) {
                Log.e(TAG, "send command failed");
                return;
            }

            adpt.code = byte2Int(buf, 0);
            adpt.dataSize = byte2Int(buf, 4);
            adpt.replySize = byte2Int(buf, 8);

            Log.d(TAG, "code = " + adpt.code);
            Log.d(TAG, "dataSize = " + adpt.dataSize);
            Log.d(TAG, "replySize =  "+ adpt.replySize);

            System.arraycopy(buf, 12, adpt.data, 0, adpt.dataSize+adpt.replySize);
        }

        private void convertParcel(AdaptParcel adpt, int code, Parcel data, Parcel reply) {
            data.setDataPosition(0);
            reply.setDataPosition(0);

            data.writeByteArrayInternal(adpt.data, 0, adpt.dataSize);
            reply.writeByteArrayInternal(adpt.data, adpt.dataSize, adpt.replySize);

            Log.e(TAG, "convertParcel: dataSize = "+data.dataSize()+", replySize = "+ reply.dataSize());

            data.setDataPosition(0);
            reply.setDataPosition(0);
        }

        private void convertAdaptParcel(int code, Parcel data, Parcel reply) {
            if(mAdpt == null){
                Log.e(TAG, "convertAdaptParcel2: mAdpt == null!");
                return;
            }
            mAdpt.code = code;

            data.setDataPosition(0);
            reply.setDataPosition(0);

            data.logArray();
            byte[] bData = new byte[data.dataSize()];
            data.readByteArray(bData);
            for(int i = 0; i < data.dataSize(); i++){
                mAdpt.data[i] = bData[i];
            }

            byte[] bReply = new byte[reply.dataSize()];
            reply.readByteArray(bReply);
            for(int i = 0; i < reply.dataSize(); i++){
                mAdpt.data[i+data.dataSize()] = bReply[i];
            }
            mAdpt.dataSize = data.dataSize();
            mAdpt.replySize = reply.dataSize();
            Log.e(TAG, "convertAdaptParcel2: dataSize = "+data.dataSize()+", replySize = "+ reply.dataSize());

            data.setDataPosition(0);
            reply.setDataPosition(0);
        }

        void transact(int code, Parcel data, Parcel reply, int flags) {
            Log.e(TAG, "transact start....");
            convertAdaptParcel(code, data, reply);
            sendCmdAndRecResult(mAdpt);
            convertParcel(mAdpt, code, data, reply);

            Log.e(TAG, "transact end....");
        }
    }

    private class Parcel {
        private int mDataSize;
        private int mPos;
        private byte[] mData;

        private Parcel() {
            mData = new byte[BUF_SIZE];
            mPos = 0;
            mDataSize = 0;
        }

        void writeByteArrayInternal(byte[] b, int offset, int len) {
            if (len == 0) {
                return;
            }
            System.arraycopy(b, offset, mData, mPos, len);
            mPos += len;
            mDataSize += len;
        }

        void readByteArray(byte[] val) {
            System.arraycopy(mData, mPos, val, 0, val.length);
            mPos += val.length;
        }

        int dataSize() {
            return mDataSize;
        }

        void writeInt(int i) {
            Log.d(TAG, "writeInt i="+i);
            mData[mPos+3] = (byte)(i >> 24 & 0xff);
            mData[mPos+2] = (byte)(i >> 16 & 0xff);
            mData[mPos+1] = (byte)(i >> 8 & 0xff);
            mData[mPos] = (byte)(i & 0xff);
            mPos += 4;
            mDataSize += 4;
        }

        int readInt() {
            int b0 = mData[mPos] & 0xFF;
            int b1 = mData[mPos + 1] & 0xFF;
            int b2 = mData[mPos + 2] & 0xFF;
            int b3 = mData[mPos + 3] & 0xFF;
            mPos += 4;
            return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
        }

        void setDataPosition(int i) {
            mPos = i;
        }

        String readString() {
            int nNum = readInt();
            byte[] b = new byte[nNum];
            Log.d(TAG, "readString num = "+nNum);
            readByteArray(b);

            return new String(b, StandardCharsets.UTF_8);
        }

        void recycle() {
            reset();
        }

        void reset() {
            mPos = 0;
            mDataSize = 0;
        }

        void logArray(){
            Log.e(TAG, "array length = "+mData.length);
            for(int i = 0; i < mData.length; i++){
                if (i > 19) {
                    break;
                }
                Log.e(TAG, "Parcel LogArray : ("+i+") = "+mData[i]);
            }
        }
    }
}
