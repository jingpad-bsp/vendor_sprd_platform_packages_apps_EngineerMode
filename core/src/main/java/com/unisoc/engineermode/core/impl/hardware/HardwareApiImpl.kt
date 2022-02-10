package com.unisoc.engineermode.core.impl.hardware

import com.unisoc.engineermode.core.factory.ImplementationFactory
import com.unisoc.engineermode.core.intf.*

object HardwareApiImpl : IHardwareApi {

    override fun chargeApi(): ICharge {
        return ImplementationFactory.create(ICharge::class) as ICharge
    }

    override fun sensePll(): ISensePll {
        return ImplementationFactory.create(ISensePll::class) as ISensePll
    }

    override fun coulometerPowerApi(): ICoulometerPower {
        return ImplementationFactory.create(ICoulometerPower::class) as ICoulometerPower
    }

    override fun antennaApi(): IAntenna {
        return ImplementationFactory.create(IAntenna::class) as IAntenna
    }

    override fun asdivApi(): IAsdiv {
        return AsdivImpl.AsdivImplHolder.INSTANCE
    }

    override fun thermalApi(): IThermal {
        return ImplementationFactory.create(IThermal::class) as IThermal
    }
}
