package io.gripxtech.locationjobdispatcher;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import io.gripxtech.locationjobdispatcher.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    ActivityLoginBinding binding;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);
        utils = new Utils(TAG, LoginActivity.this);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

        setupLogin();

        checklogin();
        login();
        getConfinge();

    }

    private void getConfinge() {
        binding.btnGetStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void login() {

    }

    private void checklogin() {

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = binding.tlUserName.getEditText().getText().toString();
                String password = binding.tlPassWord.getEditText().getText().toString();

                //check username and password is not empty
                utils.hideSoftKeyboard();
                String Name = binding.teUserName.getText().toString().trim();
                if (Name.isEmpty()) {
                    binding.tlUserName.setError(getString(R.string.userName_error));
                    return;
                }


                String pass = binding.tePassword.getText().toString().trim();
                if (pass.isEmpty()) {
                    binding.tlPassWord.setError(getString(R.string.password_error));
                    return;
                }

                binding.tlUserName.setErrorEnabled(false);
                binding.tlPassWord.setErrorEnabled(false);

                binding.btnGetStart.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupLogin() {

        binding.teUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable != null) {
                    if (editable.toString().trim().length() > 0) {
                        binding.tlUserName.setErrorEnabled(false);
                    }
                }

            }
        });

        binding.tePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                if (editable != null) {
                    if (editable.toString().trim().length() > 0) {
                        binding.tlUserName.setErrorEnabled(false);
                    }
                }
            }
        });
    }


    public ActivityLoginBinding getBinding() {
        return binding;
    }


}
