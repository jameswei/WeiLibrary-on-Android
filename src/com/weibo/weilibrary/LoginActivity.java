package com.weibo.weilibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.weibo.share.R;

import java.io.IOException;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;


/**
 * Created by weijia on 6/8/13.
 */
public class LoginActivity extends Activity {
    private Activity mInstance;
    private EditText userName;
    private EditText password;
    private Button login;
    private Context mCtx;
    private View.OnClickListener loginListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            String name = userName.getText().toString();
            String pwd = password.getText().toString();
            if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(name)) {
                password.setError(getString(R.string.error_password_required));
            } else if (pwd.length() < 4) {
                password.setError(getString(R.string.error_password_weak));
            } else {
                new Thread(new LoginThread(name, pwd)).start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mInstance = this;
        mCtx = this.getApplicationContext();
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {

        userName = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(loginListener);
    }

    private class LoginThread implements Runnable {

        private JSONObject data;
        private Resty client;

        public LoginThread(String username, String password) {
            try {
                this.data = new JSONObject().put("username", username).put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.client = new Resty();
            client.withHeader("accept", "application/json");
            client.withHeader("content-type", "application/json");
            client.withHeader("content-length", String.valueOf(this.data != null ? this.data.toString().length() : 0));
        }

        @Override
        public void run() {
            if (this.client != null && this.data != null) {
                try {
                    JSONResource result = this.client.json("http://10.0.2.2:9080/user/login", Resty.content(this.data));
                    if (result != null) {
                        String userid = result.object().getString("id");
                        if (result.object().getInt("suc") == 1) {
                            Intent intent = new Intent();
                            intent.setClass(mCtx, HomeActivity.class);
                            intent.setAction("com.weibo.weilibrary.HomeActivity");
                            intent.addCategory("com.weibo.weilibrary.HomeActivityCategory");
                            Bundle bundle = new Bundle();
                            bundle.putString("userid", userid);
                            bundle.putString("data", result.object().getJSONObject("data").toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            userName.setText("");
                            password.setText("");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
