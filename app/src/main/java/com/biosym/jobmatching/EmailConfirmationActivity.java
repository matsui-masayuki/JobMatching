package com.biosym.jobmatching;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.datastore.DataStoreException;
import com.amplifyframework.datastore.DataStoreItemChange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EmailConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_confirmation);
    }

    //認証ボタンクリック時の処理
    public void onConfirmButtonPressed(View view) {
        // 1. Confirm the code
        // 2. Re-login
        // 3. Save the user details such names in Data-store

        EditText txtConfirmationCode = findViewById(R.id.txtConfirmationCode);
        if (txtConfirmationCode.getText().toString().isEmpty()) {
            txtConfirmationCode.setError("認証コードを入力してください。");
            return;
        }else{
            Amplify.Auth.confirmSignUp(
                    getName(),
                    txtConfirmationCode.getText().toString(),
                    this::onSuccess,
                    this::onError
            );
        }
    }

    private void onError(AuthException e) {
        runOnUiThread(() -> Toast
                .makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
                .show());
    }

    private void onSuccess(AuthSignUpResult authSignUpResult) {
        reLogin();
    }

    private void reLogin() {
        String username = getName();
        String password = getPassword();

        Amplify.Auth.signIn(
                username,
                password,
                this::onLoginSuccess,
                this::onError
        );
    }

    private void onLoginSuccess(AuthSignInResult authSignInResult) {

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        /*
        String userId = Amplify.Auth.getCurrentUser().getUserId();
        String name = getName();
        Amplify.DataStore.save(
                User.builder().id(userId).name(name).build(),
                this::onSavedSuccess,
                this::onError
        );*/
    }

    /*private <T extends Model> void onSavedSuccess(DataStoreItemChange<T> tDataStoreItemChange) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    private void onError(DataStoreException e) {
        runOnUiThread(() -> Toast
                .makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
                .show());
    }*/

    private String getName() {
        return getIntent().getStringExtra("name");
    }

    private String getPassword() {
        return getIntent().getStringExtra("password");
    }

    private String getEmail() {
        return getIntent().getStringExtra("email");
    }
}