package com.unisoc.engineermode.core.impl.debuglog

import com.unisoc.engineermode.core.factory.ImplementationFactory
import com.unisoc.engineermode.core.intf.*


object DebugLogApiImpl : IDebugLogApi {

    override fun aiIpApi(): IAiIp {
        return ImplementationFactory.create(IAiIp::class) as IAiIp
    }

    override fun smsCenterNumberApi(): ISMSCenterNumber {
        return ImplementationFactory.create(ISMSCenterNumber::class) as ISMSCenterNumber
    }

    override fun gcfTestApi(): IGcfTest {
        return ImplementationFactory.create(IGcfTest::class) as IGcfTest
    }

    override fun phoneInfoApi(): IPhoneInfo {
        return ImplementationFactory.create(IPhoneInfo::class) as IPhoneInfo
    }

    override fun jeitaControlApi(): IJeitaControl {
        return ImplementationFactory.create(IJeitaControl::class) as IJeitaControl
    }

    override fun atDiagApi(): IAtDiag {
        return ImplementationFactory.create(IAtDiag::class) as IAtDiag
    }

    override fun cabcApi(): ICabc {
        return ImplementationFactory.create(ICabc::class) as ICabc
    }

    override fun sfpTestApi(): ISfpTest {
        return ImplementationFactory.create(ISfpTest::class) as ISfpTest
    }

    override fun mipiLogApi(): IMIPILog {
        return ImplementationFactory.create(IMIPILog::class) as IMIPILog
    }

    /**Unisoc:Bug1522715 add cpu debug switch @{ **/
    override fun cpuDebugApi(): ICpuDebug {
        return ImplementationFactory.create(ICpuDebug::class) as ICpuDebug
    }
    /**  @}  **/
}

