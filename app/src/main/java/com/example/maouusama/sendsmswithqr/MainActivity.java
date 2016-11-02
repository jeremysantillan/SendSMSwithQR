package com.example.maouusama.sendsmswithqr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private EditText mEtInput;
    private Button mBtnScan;
    private ZXingScannerView mScannerView;
    private static final int PERMISSION_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEtInput = (EditText) findViewById(R.id.etInput);
        mBtnScan = (Button) findViewById(R.id.btnScan);
        mBtnScan.setEnabled(false)
        ;


        //check permissions are already approved
        if (isAllowed() == true) {
            Toast.makeText(this, "You already have the permission", Toast.LENGTH_LONG).show();
            mBtnScan.setEnabled(true);
        } else {
            //request permissions
            requestPermissions();
        }
    }

    //check if granted or not
    private Boolean isAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;

    }



    //if not granted, request permission here:
    private void requestPermissions(){
//        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
//            //you can add your explanation here
//        }
        //finally, ask permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.SEND_SMS}, PERMISSION_CODE);
    }

    //result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == PERMISSION_CODE){
            if(grantResults.length>0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_LONG).show();
                mBtnScan.setEnabled(true);
            }
            else{
                Toast.makeText(this,"Permissions denied", Toast.LENGTH_LONG).show();
            }
        }
    }







    public void onClick(View v) {
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
        mScannerView.stopCamera();}
        catch(Exception e){
            System.out.println("ScannerView on pause method");
        }
    }

    @Override
    public void handleResult(Result result) {
        Log.w("handleResult", result.getText());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage("Success!");

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(result.getText().toString(), null, mEtInput.getText().toString(), null, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        mScannerView.resumeCameraPreview(this);

    }


}
