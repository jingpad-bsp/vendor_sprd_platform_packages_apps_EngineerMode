package com.unisoc.engineermode.core.utils

import android.util.Log
import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy

object CmdUtils {
    private const val TAG = "CmdUtils"
    private const val CMD_SRV_SOCKET_NAME = "cmd_skt"

    @JvmStatic
    @Synchronized
    fun run(cmd: String): String? {
        Log.d(TAG, "run cmd, cmd is $cmd")
        enableCmdservice()
        val result = SocketUtils.sendCmd(CMD_SRV_SOCKET_NAME, cmd + '\n')
        disableCmdservice()
        Log.d(TAG, "run cmd, result is $result")
        return result
    }

    @JvmStatic
    private fun enableCmdservice() {
        SystemPropertiesProxy.set("persist.sys.cmdservice.enable", "enable")
    }

    @JvmStatic
    private fun disableCmdservice() {
        SystemPropertiesProxy.set("persist.sys.cmdservice.enable", "disable")
    }

}