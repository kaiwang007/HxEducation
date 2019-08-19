package com.lefeee.hxeducation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lefeee.util.SPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {
    EditText et_username, et_password;
    Button btn_login;

    ProgressDialog m_pDialog;

    boolean login_ret = false;

    SPreferences preferences;
    private Runnable mAutoRunnable = new Runnable() {
        @Override
        public void run() {
            toggle();
        }
    };

    Looper loop = Looper.myLooper();
    final MessageHandler loginMsgHandler = new MessageHandler(loop);

    private <T extends View> T $(int id) {
        return (T) findViewById(id);
    }

    class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            m_pDialog.cancel();

            if (login_ret) {
                Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "login fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setListener();

        preferences = SPreferences.get(getApplicationContext());

        loginMsgHandler.postDelayed(mAutoRunnable, 1000);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void setListener() {
        btn_login.setOnClickListener((View v) -> {

            m_pDialog = new ProgressDialog(LoginActivity.this);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_pDialog.setMessage("登录中...");
            m_pDialog.setIndeterminate(false);
            m_pDialog.setCancelable(true);
            m_pDialog.show();

            new Thread() {
                public void run() {
                    String str_ret;
                    JSONObject jsonObject;
                    String username = et_username.getText().toString();
                    String password = et_password.getText().toString();

                    ApiCall apiCall = new ApiCall();
                    str_ret = apiCall.CheckUser(username, password);
                    if (!str_ret.isEmpty()) {
                        try {
                            jsonObject = new JSONObject(str_ret);
                            if (jsonObject != null && jsonObject.has("code") && jsonObject.getString("code").equals("0")) {
//                                Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_SHORT).show();
                                login_ret = true;

                                preferences.saveLoginStatus(1);
                                preferences.saveUsername(username);
                                preferences.savePassword(password);
                            } else {
//                                Toast.makeText(getApplicationContext(), "login fail", Toast.LENGTH_SHORT).show();
                                login_ret = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Message loginMsg = Message.obtain();
                    loginMsgHandler.sendMessage(loginMsg);
                }
            }.start();

        });
    }

    private void initViews() {
        et_username = $(R.id.et_username);
        et_password = $(R.id.et_psw);
        btn_login = $(R.id.btn_login);
    }

    //这里添加判断是否有登录
    private boolean isLogin() {
        int loginStatus = SPreferences.get(this).getLoginStatus();
        //return false;

        return loginStatus == 1;
    }

    private void toggle() {
        if (isLogin()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            this.finish();
        }
    }

}
