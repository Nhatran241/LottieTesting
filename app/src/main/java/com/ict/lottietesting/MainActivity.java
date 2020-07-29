package com.ict.lottietesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieListener;
import com.nhatran241.simplepermission.PermissionManager;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Permission;

public class MainActivity extends AppCompatActivity implements PermissionManager.IGrantPermissionListener {

    private static final int PICKFILE_RESULT_CODE = 2222;
    private ConnectButtonShadowView connectButtonShadowView;
    private LottieDrawable lottieDrawable = new LottieDrawable();
    private EditText start,end;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectButtonShadowView = findViewById(R.id.main_btnConnect);
        start = findViewById(R.id.edt_start);
        end = findViewById(R.id.edt_end);
    }

    @Override
    public void OnGrantPermissionSuccess(PermissionManager.PermissionType permissionType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    @Override
    public void OnGrantPermissionFail(PermissionManager.PermissionType permissionType, String errror) {
        if(PermissionManager.deny_dontaskagain==errror){
            PermissionManager.getInstance().goToSetting(this, PermissionManager.PermissionType.READ_STORAGE);
        }else {
            PermissionManager.getInstance().GrantPermission(this, PermissionManager.PermissionType.READ_STORAGE,this);
        }
    }
    public void SelectFile(View view) {
        PermissionManager.getInstance().GrantPermission(this, PermissionManager.PermissionType.READ_STORAGE, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.getInstance().onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionManager.getInstance().onActivityForResult(this,requestCode,resultCode,data);
        if (requestCode == PICKFILE_RESULT_CODE &&data!=null) {
            Uri uri = data.getData();
            LaterFunction(uri);
        }
    }

    public void LaterFunction(Uri uri) {
        BufferedReader br;
        FileOutputStream os;
        try {
            br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            //WHAT TODO ? Is this creates new file with
            //the name NewFileName on internal app storage?
            os = openFileOutput("newFileName", Context.MODE_PRIVATE);
            String line = null;
            while ((line = br.readLine()) != null) {
                os.write(line.getBytes());
                Log.w("nlllllllllllll",line);
                LottieCompositionFactory.fromJsonString(line,"ca").addListener(new LottieListener<LottieComposition>() {
                    @Override
                    public void onResult(LottieComposition result) {
                        lottieDrawable.setComposition(result);
                        lottieDrawable.setRepeatCount(LottieDrawable.INFINITE);
                        lottieDrawable.addAnimatorListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                super.onAnimationRepeat(animation);

                            }
                        });

                        connectButtonShadowView.setBackgroundResource(lottieDrawable,false);
                    }});
            }

            br.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Run(View view) {
        int startF=0;
        int endF = 10000;
        if(!start.getText().toString().equals("")){
            startF =Integer.parseInt(start.getText().toString());
        }
        if(!end.getText().toString().equals("")){
            endF =Integer.parseInt(end.getText().toString());
        }
        if(startF<endF){
            lottieDrawable.setMinAndMaxFrame(startF,endF);
        }else {

            lottieDrawable.setMinAndMaxFrame(endF,startF);
        }
        lottieDrawable.start();
    }
}
