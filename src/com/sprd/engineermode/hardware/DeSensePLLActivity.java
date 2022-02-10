
package com.sprd.engineermode.hardware;

import com.sprd.engineermode.R;
import com.unisoc.engineermode.core.CoreApi;
import com.unisoc.engineermode.core.exception.ErrorCode;
import com.unisoc.engineermode.core.exception.OperationFailedException;
import com.unisoc.engineermode.core.intf.IHardwareApi;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.unisoc.engineermode.core.intf.ISensePll;

public class DeSensePLLActivity extends Activity {

    private static final String TAG = "DeSensePLLActivity/Activity";
    //private static final String CMD_ERROR = "cmd_error";

    private ISensePll sensePllApi = CoreApi.getHardwareApi().sensePll();
    private EditText deSensePLLAddress = null;
    private EditText deSensePLLData = null;
    private EditText deSensePLLNumber = null;
    private TextView deSensePLLResult = null;
    private Button mRead = null;
    private Button mWrite = null;
    private int count = 0;

    /*
    private LocalSocket mSocketClient = null;
    private LocalSocketAddress mSocketAddress = null;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;
    private boolean isConnected = false;
    private boolean isSuccess = true;
    */
    private ButListener mButListener = new ButListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_sense_pll);

        //start cmd_services to crate locak socket
        //SystemPropertiesProxy.set("persist.sys.cmdservice.enable", "enable");

        deSensePLLAddress = (EditText) this.findViewById(R.id.de_sense_pll_address);
        deSensePLLData = (EditText) this.findViewById(R.id.de_sense_pll_data);
        deSensePLLNumber = (EditText) this.findViewById(R.id.de_sense_pll_number);
        deSensePLLResult = (TextView) this.findViewById(R.id.de_sense_pll_result);
        mRead = (Button) this.findViewById(R.id.de_sense_pll_read);
        mWrite = (Button) this.findViewById(R.id.de_sense_pll_write);
        mRead.setOnClickListener(mButListener);
        mWrite.setOnClickListener(mButListener);

        //String status = SystemPropertiesProxy.get("persist.sys.cmdservice.enable", "");
        //Log.d(TAG, "status:" + status);

        //wait 100ms for cmd_service starting
        /*
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

        //connectSocket("cmd_skt", LocalSocketAddress.Namespace.ABSTRACT);
        new AlertDialog.Builder(this)
        .setTitle("Info")
        .setMessage(this.getString(R.string.desense_pll_tips))
        .setPositiveButton(this.getString(R.string.alertdialog_ok), null)
        .show();
    }

    @Override
    protected void onDestroy() {
        /*
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            if (mSocketClient != null) {
                mSocketClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        SystemPropertiesProxy.set("persist.sys.cmdservice.enable", "disable");
        String disable = SystemPropertiesProxy.get("persist.sys.cmdservice.enable", "");
        Log.d(TAG, "disable:" + disable);
        */

        super.onDestroy();
    }

    private class ButListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.equals(mRead)) {
                String address = deSensePLLAddress.getText().toString();
                String number = deSensePLLNumber.getText().toString();
                if (address.length() < 8 ) {
                    Toast.makeText(DeSensePLLActivity.this, "Please input 8 bit Address",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("00000000".equals(address)) {
                    Toast.makeText(DeSensePLLActivity.this, "Input Address Invalid",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (0 == number.length()) {
                    Toast.makeText(DeSensePLLActivity.this, "Please input Number",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String result = "";
                try {
                    result = sensePllApi.read(address, Integer.parseInt(number));
                } catch (OperationFailedException e) {
                    e.printStackTrace();
                    if (e.getCode() == ErrorCode.SOCKET_CONN_FAILED) {
                        Toast.makeText(DeSensePLLActivity.this,"connect socket failed", Toast.LENGTH_SHORT).show();
                    } else if (e.getCode() == ErrorCode.CMD_EXEC_ERROR) {
                        deSensePLLData.setText("");
                        deSensePLLResult.setText(result);
                        Toast.makeText(DeSensePLLActivity.this, "Read Fail", Toast.LENGTH_SHORT).show();
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DeSensePLLActivity.this, "Read Fail", Toast.LENGTH_SHORT).show();
                    return;
                }

                deSensePLLData.setText("");
                deSensePLLResult.setText(result);
                Toast.makeText(DeSensePLLActivity.this, "Read Success", Toast.LENGTH_SHORT).show();

            } else if (view.equals(mWrite)) {
                String address = deSensePLLAddress.getText().toString();
                String data = deSensePLLData.getText().toString();
                if (address.length() < 8) {
                    Toast.makeText(DeSensePLLActivity.this, "Please input 8 bit Address",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (0 == data.length()) {
                    Toast.makeText(DeSensePLLActivity.this, "Please input Data",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    sensePllApi.write(address, data);
                } catch (OperationFailedException e) {
                    e.printStackTrace();
                    if (e.getCode() == ErrorCode.SOCKET_CONN_FAILED) {
                        Toast.makeText(DeSensePLLActivity.this,"connect socket failed", Toast.LENGTH_SHORT).show();
                    } else if (e.getCode() == ErrorCode.CMD_EXEC_ERROR) {
                        deSensePLLResult.setText("");
                        Toast.makeText(DeSensePLLActivity.this, "Write Fail", Toast.LENGTH_SHORT).show();
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DeSensePLLActivity.this, "Write Fail", Toast.LENGTH_SHORT).show();
                    return;
                }

                deSensePLLResult.setText("");
                Toast.makeText(DeSensePLLActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /*
    private void connectSocket(String socketName, Namespace namespace) {
        try {
            mSocketClient = new LocalSocket();
            mSocketAddress = new LocalSocketAddress(socketName, namespace);
            mSocketClient.connect(mSocketAddress);
            isConnected = true;
            Log.d(TAG, "mSocketClient connect is " + mSocketClient.isConnected());
        } catch (Exception e) {
            isConnected = false;
            Log.d(TAG, "mSocketClient connect is false");
            e.printStackTrace();
        }
    }

    private String sendCmdAndResult(String cmd) {
        byte[] buffer = new byte[1024];
        String result = CMD_ERROR;
        try {
            mOutputStream = mSocketClient.getOutputStream();
            if (mOutputStream != null) {
                final StringBuilder cmdBuilder = new StringBuilder(cmd).append('\0');
                final String cmmand = cmdBuilder.toString();
                mOutputStream.write(cmmand.getBytes(StandardCharsets.UTF_8));
                mOutputStream.flush();
            }
            mInputStream = mSocketClient.getInputStream();
            count = mInputStream.read(buffer, 0, 1024);
            result = new String(buffer, "utf-8");
            Log.d(TAG, "count is " + count + ",result is " + result);
        } catch (Exception e) {
            Log.d(TAG, "Failed get outputStream: " + e);
            e.printStackTrace();
        }
        return result;
    }
    */
}
