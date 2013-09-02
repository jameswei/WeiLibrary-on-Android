package com.weibo.weilibrary;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.weibo.share.R;

import java.io.IOException;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

/**
 * Created by weijia on 6/18/13.
 */
public class NewBookActivity extends Activity {
    private Activity mInstance;
    private TextView bookName;
    private TextView description;
    private TextView tag;
    private Button addBook;
    private Context mCtx;
    private String userid;
    private String data;
    private String bookid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userid = this.getIntent().getStringExtra("userid");
        setContentView(R.layout.activity_newbook);
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
        bookName = (TextView) findViewById(R.id.bookname);
        description = (TextView) findViewById(R.id.description);
        tag = (TextView) findViewById(R.id.tags);
        addBook = (Button) findViewById(R.id.addbook);
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new AddBookThread(bookName.getText().toString(), description.getText().toString(), tag.getText().toString())).start();
            }
        });
    }

    private class AddBookThread implements Runnable {

        private JSONObject data;
        private Resty client;

        public AddBookThread(String bookName, String description, String tags) {
            try {
                this.data = new JSONObject().put("bookname", bookName).put("tags", tags).put("description", description);
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
                    JSONResource result = this.client.json("http://10.0.2.2:9080/user/book/" + userid, Resty.content(this.data));
                    if (result != null) {
                        String userid = result.object().getString("id");
                        if (result.object().getInt("suc") == 1) {
//                            Intent intent = new Intent();
//                            intent.setClass(mCtx, HomeActivity.class);
//                            intent.setAction("com.weibo.weilibrary.HomeActivity");
//                            intent.addCategory("com.weibo.weilibrary.HomeActivityCategory");
//                            Bundle bundle = new Bundle();
//                            bundle.putString("userid", userid);
//                            bundle.putString("data", result.object().getJSONObject("data").toString());
//                            intent.putExtras(bundle);
//                            startActivity(intent);
                            mInstance.finish();
                        } else {
                            bookName.setText("");
                            description.setText("");
                            tag.setText("");
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
