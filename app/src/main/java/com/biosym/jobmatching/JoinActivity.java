package com.biosym.jobmatching;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class JoinActivity extends AppCompatActivity {

    EditText txtBirth;
    String sex = "N";
    String userDiv = "jobseeker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_join);
        txtBirth = (EditText) findViewById(R.id.txtBirth);
        txtBirth.setInputType(InputType.TYPE_NULL);
    }

    //ヘッダーのアイコンの処理
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //登録ボタンクリック時の処理
    public void onPressJoinPressed(View view) {
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtPassword = findViewById(R.id.txtPassword);
        EditText txtPasswordConfirm = findViewById(R.id.txtPasswordConfirm);
        EditText txtName = findViewById(R.id.txtName);
        EditText txtBirth = findViewById(R.id.txtBirth);
        String selectedSex = this.sex;
        String selectedUserdiv = this.userDiv;

        //バリデーションチェック
        boolean validationRes=true;
        if (txtName.getText().toString().isEmpty()) {
            txtName.setError("ユーザIDを入力してください。");
            validationRes=false;
        }
        if (txtBirth.getText().toString().isEmpty()) {
            txtBirth.setError("生年月日を選択してください。");
            validationRes=false;
        }
        if (txtEmail.getText().toString().isEmpty()) {
            txtEmail.setError("Emailアドレスを入力してください。");
            validationRes=false;
        }
        if (txtPassword.getText().toString().length()<8) {
            txtPassword.setError("パスワードは8桁以上で入力してください。");
            validationRes=false;
        }else if (!txtPassword.getText().toString().matches(txtPasswordConfirm.getText().toString())) {
            txtPassword.setError("パスワードとパスワード(確認用)が一致しません。");
            txtPasswordConfirm.setError("パスワードとパスワード(確認用)が一致しません。");
            validationRes=false;
        }

        //アカウント登録処理
        if(validationRes) {
            List<AuthUserAttribute> customAttr= new ArrayList<AuthUserAttribute>();
            customAttr.add(new AuthUserAttribute(AuthUserAttributeKey.email(), txtEmail.getText().toString()));
            customAttr.add(new AuthUserAttribute(AuthUserAttributeKey.custom("custom:birthdate"), txtBirth.getText().toString()));
            customAttr.add(new AuthUserAttribute(AuthUserAttributeKey.custom("custom:gender"), selectedSex));
            customAttr.add(new AuthUserAttribute(AuthUserAttributeKey.custom("custom:userdiv"), selectedUserdiv));

            AuthSignUpOptions options = AuthSignUpOptions.builder()
                    .userAttributes(customAttr)
                    .build();

            Amplify.Auth.signUp(
                    txtName.getText().toString(),
                    txtPassword.getText().toString(),
                    options,
                    this::onJoinSuccess,
                    this::onJoinError
            );
        }
    }

    //登録処理成功時の処理
    private void onJoinSuccess(AuthSignUpResult authSignUpResult) {
        Intent intent = new Intent(this, EmailConfirmationActivity.class);
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtPassword = findViewById(R.id.txtPassword);
        EditText txtName = findViewById(R.id.txtName);
        intent.putExtra("email", txtEmail.getText().toString());
        intent.putExtra("password", txtPassword.getText().toString());
        intent.putExtra("name", txtName.getText().toString());

        startActivity(intent);
    }

    //登録処理失敗時の処理
    private void onJoinError(AuthException e) {
        this.runOnUiThread(() -> {
            //ユーザID重複エラー
            if(e.getMessage().contains("Username")){
                EditText txtName = findViewById(R.id.txtName);
                txtName.setError("入力されたユーザIDは既に使われています。");
            }
        });
    }

    //性別とユーザ区分のラジオボタンの処理
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // 選択したラジオボタンの値を設定
        switch(view.getId()) {
            case R.id.radio_noselect:
                if (checked)
                    this.sex="N";
                break;
            case R.id.radio_man:
                if (checked)
                    this.sex="M";
                break;
            case R.id.radio_woman:
                if (checked)
                    this.sex="W";
                break;
            case R.id.radio_jobseeker:
                if (checked)
                    this.userDiv="jobseeker";
                break;
            case R.id.radio_recruiter:
                if (checked)
                    this.userDiv="recruiter";
                break;
        }
    }


    //日付選択用Spinnerを表示
    public void onClickShowDate(View v) {
        //初期値を設定(monthの値はmonth-1の値の設定が必要)
        int day = 1;
        int month = 0;
        int year = 2000;
        // date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(JoinActivity.this,android.R.style.Theme_Holo_Dialog,
                (view, year1, monthOfYear, dayOfMonth) -> txtBirth.setText(year1 + "/" + (monthOfYear + 1) + "/" + dayOfMonth), year, month, day);txtBirth.setError(null);
        datePickerDialog.getDatePicker().setSpinnersShown(true);
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.show();
    }

}