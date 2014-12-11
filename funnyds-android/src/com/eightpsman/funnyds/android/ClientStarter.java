package com.eightpsman.funnyds.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.eightpsman.funnyds.android.worker.ServerConnector;
import com.eightpsman.funnyds.core.ClientManager;
import com.eightpsman.funnyds.core.Constants;
import com.eightpsman.funnyds.core.Device;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/**
 * FunnyDS
 * Created by 8psman on 11/20/2014.
 * Email: 8psman@gmail.com
 */
public class ClientStarter extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{

    Device device;
    EditText serverAddress;
    TextView alert;

    RadioGroup serverGroup;
    RadioGroup modeGroup;

    EditText etWidth;
    EditText etHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_starter);

        device = AndroidUtils.getLocalDevice(this);
        /** info */
        ((EditText)findViewById(R.id.info_name)).setText(device.name);
        ((EditText)findViewById(R.id.info_ip)).setText(device.ip);
        (etWidth = (EditText)findViewById(R.id.info_width)).setText(""+(int)device.width);
        (etHeight = (EditText)findViewById(R.id.info_height)).setText("" + (int) device.height);
        ((EditText)findViewById(R.id.info_dpi)).setText(""+device.dpi);

        /** server */
        serverAddress = (EditText) findViewById(R.id.server_address);
        serverGroup = (RadioGroup) findViewById(R.id.server_group);
        serverGroup.setOnCheckedChangeListener(this);
        serverGroup.check(R.id.server_auto);

        /** mode */
        modeGroup = (RadioGroup) findViewById(R.id.mode_group);
        modeGroup.check(R.id.mode_exhi);

        /** start */
        alert = (TextView) findViewById(R.id.alert);
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        device.width = metrics.widthPixels;
        device.height = metrics.heightPixels;
        etWidth.setText(metrics.widthPixels + "");
        etHeight.setText(metrics.heightPixels + "");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start){
            alert.setTextColor(Color.BLACK);
            alert.setText("Connecting ...");
            /** get host */
            String host = null;
            if (serverGroup.getCheckedRadioButtonId() == R.id.server_set){
                host = serverAddress.getText().toString();
            }

            /** get mode */
            int mode = modeGroup.getCheckedRadioButtonId() == R.id.mode_exhi
                    ? Constants.MODE_EXHI : Constants.MODE_PRES;

            /** connect to server with host and mode */
            ClientManager manager = ClientManager.getUniqueInstance();
            manager.setLocalDevice(AndroidUtils.getLocalDevice(this));
            manager.setMode(mode);
            new ServerConnector(host, manager, handler).execute();
            showProgressDialog();
        }
    }

    AlertDialog progressDialog;
    public void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new AlertDialog.Builder(this).setCancelable(false).create();
        }
        progressDialog.show();
        findViewById(R.id.progress_view).setVisibility(View.VISIBLE);
    }

    public void hideProgressDialog(){
        if (progressDialog !=  null){
            progressDialog.hide();
        }
        findViewById(R.id.progress_view).setVisibility(View.GONE);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        if (id == R.id.server_auto){
            serverAddress.setEnabled(false);
        }else{
            serverAddress.setEnabled(true);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ServerConnector.FAIL:
                    hideProgressDialog();
                    alert.setTextColor(Color.RED);
                    alert.setText((String) msg.obj);
                    break;
                case ServerConnector.SUCCESS:
                    ClientManager manager = (ClientManager) msg.obj;
                    Class classToRun;
                    switch (manager.getMode()){
                        case Constants.MODE_EXHI:
                            classToRun = FunnyDS.class;
                            break;
                        case Constants.MODE_PRES:
                            classToRun = FunnyDS.class;
                            break;
                        case Constants.MODE_GAME:
                            classToRun = FunnyDS.class;
                            break;
                        default:
                            classToRun = FunnyDS.class;
                    }
                    hideProgressDialog();
                    finish();
                    Intent intent = new Intent(ClientStarter.this, classToRun);
                    startActivity(intent);
                    break;
            }
        }
    };
}