package com.unisoc.engineermode.core.intf;

public interface IModem {
    interface Capability {
        boolean isW();
        boolean isG();
        boolean isWG();
    }

    Capability getCapability(int phoneId);
    Capability[] getCapabilities();
}
