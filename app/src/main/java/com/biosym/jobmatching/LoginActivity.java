package com.biosym.jobmatching;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.core.Amplify;

public class LoginActivity extends AppCompatActivity{

    private boolean isWifiConn = false;
    private boolean isMobileConn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        //ネットワークの接続状況取得
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network network : connMgr.getAllNetworks()) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
            }
        }
        if(!isWifiConn&&!isMobileConn){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ネットワークエラー")
                    .setMessage("端末がネットワークに接続されていません。\n接続を確認し、アプリを再起動してください。")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }

    //ログインボタンクリック時の処理
    public void onPressLogin(View view) {
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtPassword = findViewById(R.id.txtPassword);

        //バリデーションチェック
        boolean validationRes=true;
        if (txtEmail.getText().toString().isEmpty()) {
            txtEmail.setError("ユーザIDを入力してください。");
            validationRes=false;
        }
        if (txtPassword.getText().toString().isEmpty()) {
            txtPassword.setError("パスワードを入力してください。");
            validationRes=false;
        }

        //ログイン処理
        if(validationRes){
            Amplify.Auth.signIn(
                    txtEmail.getText().toString(),
                    txtPassword.getText().toString(),
                    this::onLoginSuccess,
                    this::onLoginError
            );
        }
    }

    //ログイン失敗時の処理
    private void onLoginError(AuthException e) {
        this.runOnUiThread(() -> {
            //ユーザID存在しない
            if(e.getMessage().contains("User")){
                EditText txtEmail = findViewById(R.id.txtEmail);
                txtEmail.setError("入力されたユーザIDは存在しません。");
            }
            //パスワードが違う
            if(e.getMessage().contains("not authorized")){
                EditText txtPass = findViewById(R.id.txtPassword);
                txtPass.setError("パスワードが違います。");
            }
        });

    }

    //ログイン成功時の処理
    private void onLoginSuccess(AuthSignInResult authSignInResult) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    //新規作成ボタンクリック時の処理
    public void onJoinPressed(View view) {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }
}
