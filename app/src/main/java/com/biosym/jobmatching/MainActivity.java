package com.biosym.jobmatching;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.core.Amplify;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.core.Amplify;

public class MainActivity extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthUser currentUser = Amplify.Auth.getCurrentUser();

        Intent intent;
        if(currentUser == null){
            // 遷移先をログイン画面に指定する
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        }else {
            // 遷移先をホーム画面に指定する
            intent = new Intent(getApplicationContext(), HomeActivity.class);
        }
        // 画面遷移を起こす
        startActivity(intent);
        finish();
    }
}
