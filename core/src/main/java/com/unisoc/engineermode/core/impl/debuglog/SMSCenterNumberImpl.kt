package com.unisoc.engineermode.core.impl.debuglog

import com.unisoc.engineermode.core.annotation.Implementation
import com.unisoc.engineermode.core.impl.nonpublic.SmsManagerExProxy
import com.unisoc.engineermode.core.intf.ISMSCenterNumber

@Implementation(
    interfaceClass = ISMSCenterNumber::class
)
object SMSCenterNumberImpl : ISMSCenterNumber {

    //    public static class SMSCenterNumberImplHolder {
    //        static final IDebugLogApi.ISMSCenterNumber INSTANCE = new SMSCenterNumberImpl();
    //    }

    override fun getSmsCenterNumber(subId: Int): String {
        return SmsManagerExProxy.getSmscForSubscriber(subId)
    }

    override fun setSmsCenterNumber(subId: Int, smsAddress: String): Boolean {
        var result = true
        try {
            result = SmsManagerExProxy.setSmscForSubscriber(subId, smsAddress)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }
}
