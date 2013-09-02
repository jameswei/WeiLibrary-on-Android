package com.weibo.weilibrary;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.weibo.share.R;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

/**
 * Created by weijia on 6/8/13.
 */
public class UserActivity extends Activity {
    private Activity mInstance;
    private TextView userName;
    private TextView email;
    private TextView bio;
    private TextView tag;
    private TextView ownedBooksNum;
    private TextView occupiedBooksNum;
    private Context mCtx;
    private Button returnButton;
    private String userid;
    private String data;
    private View.OnClickListener returnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mInstance = this;
        mCtx = this.getApplicationContext();
        this.userid = savedInstanceState.getString("userid");
        this.data = savedInstanceState.getString("data");
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
        userName = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        bio = (TextView) findViewById(R.id.bio);
        tag = (TextView) findViewById(R.id.tag);
        ownedBooksNum = (TextView) findViewById(R.id.own);
        occupiedBooksNum = (TextView) findViewById(R.id.occupant);
        this.returnButton = (Button) findViewById(R.id.return_to_user_list);
        this.returnButton.setOnClickListener(returnListener);
        JSONObject jsonData = null;
        try {
            jsonData = new JSONObject(this.data);
            userName.setText(jsonData.getString("username"));
            email.setText(jsonData.getString("email"));
            bio.setText(jsonData.getString("bio"));
            tag.setText(jsonData.getString("tags"));
            ownedBooksNum.setText(ownedBooksNum.getText().toString().replace("{booksnum}", String.valueOf(jsonData.getJSONArray("books").length())));
            occupiedBooksNum.setText(occupiedBooksNum.getText().toString().replace("{booksnum}", String.valueOf(jsonData.getJSONArray("occupied").length())));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
