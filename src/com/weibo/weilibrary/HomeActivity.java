package com.weibo.weilibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.weibo.share.R;

import java.io.IOException;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;


/**
 * Created by weijia on 6/8/13.
 */
public class HomeActivity extends Activity {
    private Activity mInstance;
    private TextView userName;
    private TextView email;
    private TextView bio;
    private TextView tag;
    private TextView booksNum;
    private TextView about;
    //private ImageView avatar;
    private Button allUsers;
    private Button allBooks;
    private Button addBook;
    private Context mCtx;
    private String userid;
    private String data;
    private OnClickListener queryListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.allusers: {
                    new Thread(new QueryThread("/user/all")).start();
                    break;
                }
                case R.id.allbooks: {
                    new Thread(new QueryThread("/book/all")).start();
                    break;
                }
            }
        }
    };
    private OnClickListener addBookListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("userid", userid);
            intent.setClass(mCtx, NewBookActivity.class);
            intent.setAction("com.weibo.weilibrary.NewBookActivity");
            intent.addCategory("com.weibo.weilibrary.NewBookActivityCategory");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userid = this.getIntent().getStringExtra("userid");
        this.data = this.getIntent().getStringExtra("data");
        setContentView(R.layout.activity_home);
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

        userName = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        bio = (TextView) findViewById(R.id.bio);
        tag = (TextView) findViewById(R.id.tag);
        booksNum = (TextView) findViewById(R.id.booksnum);
        about = (TextView) findViewById(R.id.about);
        allUsers = (Button) findViewById(R.id.allusers);
        allBooks = (Button) findViewById(R.id.allbooks);
        addBook = (Button) findViewById(R.id.addbook);
        allUsers.setOnClickListener(queryListener);
        allBooks.setOnClickListener(queryListener);
        addBook.setOnClickListener(addBookListener);
        about.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("activity", "HomeActivity");
                intent.setClass(mCtx, SplashActivity.class);
                intent.setAction("com.weibo.weilibrary.SplashActivity");
                intent.addCategory("com.weibo.weilibrary.SplashActivityCategory");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        try {
            JSONObject jsonData = new JSONObject(this.data);
            userName.setText(jsonData.getString("username"));
            email.setText(jsonData.getString("email"));
            bio.setText(jsonData.getString("bio"));
            tag.setText(jsonData.getString("tags"));
            booksNum.setText(booksNum.getText().toString().replace("{booksnum}", String.valueOf(jsonData.getJSONArray("books").length())));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class QueryThread implements Runnable {
        private String uri;
        private Resty client;

        public QueryThread(String uri) {
            this.uri = uri;
            this.client = new Resty();
            client.withHeader("accept", "application/json");
            client.withHeader("content-type", "application/json");
            //client.withHeader("content-length",String.valueOf(this.data!=null? this.data.toString().length():0));
        }

        @Override
        public void run() {
            if (this.client != null) {
                try {
                    JSONResource result = this.client.json("http://10.0.2.2:9080" + this.uri);
                    if (result != null) {
                        JSONArray array = result.array();
                        int index = -1;
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            if (obj.getString("id").equals(userid)) {
                                index = i;
                                break;
                            }
                        }
                        if (index != -1) {
                            array.remove(index);
                        }
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("userid", userid);
                        bundle.putString("list", array.length() == 0 ? "[]" : array.toString());
                        if (this.uri.split("/")[1].equals("user")) {
                            intent.setClass(mCtx, UserListActivity.class);
                            intent.setAction("com.weibo.weilibrary.UserListActivity");
                            intent.addCategory("com.weibo.weilibrary.UserListActivityCategory");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else if (this.uri.split("/")[1].equals("book")) {
                            intent.setClass(mCtx, BookListActivity.class);
                            intent.setAction("com.weibo.weilibrary.BookListActivity");
                            intent.addCategory("com.weibo.weilibrary.BookListActivityCategory");
                            intent.putExtras(bundle);
                            startActivity(intent);
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
