package com.unisoc.engineermode.core.impl.hardware

import android.net.LocalSocket
import android.net.LocalSocketAddress
import android.util.Log
import com.unisoc.engineermode.core.annotation.Implementation

import com.unisoc.engineermode.core.impl.nonpublic.SystemPropertiesProxy
import com.unisoc.engineermode.core.exception.ErrorCode
import com.unisoc.engineermode.core.exception.OperationFailedException
import com.unisoc.engineermode.core.intf.ISensePll

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets


@Implementation(
    interfaceClass = ISensePll::class
)
object SensePllImpl : ISensePll {
    private const val TAG = "SENSEPLL"
    private const val CMD_ERROR = "cmd_error"

    @Throws(Exception::class)
    override fun read(address: String, count: Int): String {

        val readCmd = String.format("lookat -l %d 0x%s", count, address)
        Log.d(TAG, "readCmd->$readCmd")

        val readPllResult = sendCmdAndResult(readCmd)
        Log.d(TAG, "result->$readPllResult")

        if (!readPllResult.contains("VALUE")) {
            throw OperationFailedException(ErrorCode.CMD_EXEC_ERROR, readPllResult)
        }

        return readPllResult

    }

    @Throws(Exception::class)
    override fun write(address: String, data: String) {

        val writeCmd = String.format("lookat -s 0x%s 0x%s", data, address)
        Log.d(TAG, "writeCmd->$writeCmd")

        val result = sendCmdAndResult(writeCmd)
        Log.d(TAG, "write result->$result")
        if (!result.contains("Result")) {
            throw OperationFailedException(ErrorCode.CMD_EXEC_ERROR, result)
        }

    }


    private fun connectSocket(): LocalSocket? {
        val socketClient = LocalSocket()
        try {
            socketClient.connect(LocalSocketAddress("cmd_skt", LocalSocketAddress.Namespace.ABSTRACT))
        } catch (e: IOException) {
            e.printStackTrace()
            try {
                socketClient.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

            return null
        }

        return socketClient
    }

    @Throws(OperationFailedException::class)
    private fun sendCmdAndResult(cmd: String): String {
        var os: OutputStream? = null
        var ins: InputStream? = null
        val socketClient: LocalSocket?
        val buffer = ByteArray(1024)
        val result: String

        startCmdService()

        socketClient = connectSocket()
        if (socketClient == null) {
            stopCmdService()
            throw OperationFailedException(ErrorCode.SOCKET_CONN_FAILED)
        }

        try {
            os = socketClient.outputStream
            if (os != null) {
                val cmmand = cmd + '\u0000'
                os.write(cmmand.toByteArray(StandardCharsets.UTF_8))
                os.flush()
            }
            ins = socketClient.inputStream
            val count = ins!!.read(buffer, 0, 1024)
            result = String(buffer)
            Log.d(TAG, "count is $count,result is $result")
        } catch (e: IOException) {
            Log.d(TAG, "Failed get cmd output: $e")
            e.printStackTrace()
            throw OperationFailedException(ErrorCode.CMD_EXEC_ERROR, e.message)
        } finally {
            try {
                ins?.close()
                os?.close()

                socketClient.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            stopCmdService()
        }
        return result
    }

    private fun startCmdService() {
        SystemPropertiesProxy.set("persist.sys.cmdservice.enable", "enable")
        val status = SystemPropertiesProxy.get("persist.sys.cmdservice.enable", "")
        Log.d(TAG, "status:$status")
        //wait 100ms for cmd_service starting
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    private fun stopCmdService() {
        SystemPropertiesProxy.set("persist.sys.cmdservice.enable", "disable")
        val disable = SystemPropertiesProxy.get("persist.sys.cmdservice.enable", "")
        Log.d(TAG, "disable:$disable")
    }
}
