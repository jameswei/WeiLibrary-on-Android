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
 * Created by weijia on 6/8/13.
 */
public class BookActivity extends Activity {
    private Activity mInstance;
    private TextView bookName;
    private TextView description;
    private TextView tag;
    private TextView status;
    private TextView owner;
    private TextView occupant;
    private Button borrow;
    private Context mCtx;
    private String userid;
    private String data;
    private String bookid;
    private View.OnClickListener statusListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            new Thread(new UpdateThread("/book/status/" + bookid, borrow.getText().toString().contains("借") ? false : true, userid)).start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userid = this.getIntent().getStringExtra("userid");
        this.data = this.getIntent().getStringExtra("data");
        try {
            this.bookid = new JSONObject(this.data).getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_book);
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
        status = (TextView) findViewById(R.id.status);
        owner = (TextView) findViewById(R.id.owner);
        occupant = (TextView) findViewById(R.id.occupant);
        borrow = (Button) findViewById(R.id.borrow);
        borrow.setOnClickListener(statusListener);
        try {
            JSONObject jsonData = new JSONObject(this.data);
            bookName.setText(jsonData.getString("bookname"));
            description.setText(jsonData.getString("description"));
            tag.setText(jsonData.getString("tags"));
            status.setText(jsonData.getBoolean("status") ? "available" : "unavailable");
            //owner.setText(owner.getText().toString().replace("{owner}", jsonData.getString("owner")));
            new Thread(new QueryThread(jsonData.getString("owner"), owner)).start();
            if (jsonData.getString("occupant") != null && jsonData.getString("occupant").length() > 0) {
                new Thread(new QueryThread(jsonData.getString("occupant"), occupant)).start();
            } else {
                occupant.setText(occupant.getText().toString().replace("{occupant}", "none"));
            }
            if (jsonData.getString("owner").equals(userid)) {
                borrow.setEnabled(false);
            } else {
                if (jsonData.getBoolean("status")) {
                    borrow.setText("借!");
                    borrow.setEnabled(true);
                } else {
                    if (jsonData.getString("occupant").equals(userid)) {
                        borrow.setText("还!");
                        borrow.setEnabled(true);
                    } else {
                        borrow.setText("借!");
                        borrow.setEnabled(false);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class QueryThread implements Runnable {
        private Resty client;
        private String id;
        private TextView view;

        public QueryThread(String id, TextView view) {
            this.id = id;
            this.view = view;
            this.client = new Resty();
            client.withHeader("accept", "application/json");
            client.withHeader("content-type", "application/json");
        }

        @Override
        public void run() {
            if (this.client != null) {
                try {
                    JSONResource result = this.client.json("http://10.0.2.2:9080/user/info/" + this.id);
                    if (result != null) {
                        this.view.setText(this.view.getText().toString().replace(this.view
                                .equals(owner) ? "{owner}" : "{occupant}", result.object().getString("username")));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class UpdateThread implements Runnable {
        private String uri;
        private Resty client;
        private JSONObject data;
        private boolean newStatus;

        public UpdateThread(String uri, boolean update, String userid) {
            newStatus = update;
            try {
                this.data = new JSONObject().put("status", update).put("userid", userid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.uri = uri;
            this.client = new Resty();
            client.withHeader("accept", "application/json");
            client.withHeader("content-type", "application/json");
            client.withHeader("content-length", String.valueOf(this.data != null ? this.data.toString().length() : 0));

        }

        @Override
        public void run() {
            if (this.client != null) {
                try {
                    JSONResource result = this.client.json("http://10.0.2.2:9080" + this.uri, Resty.put(Resty.content(this.data
                    )));
                    if (result != null) {
                        if (result.object().getInt("suc") == 1) {
                            mInstance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    status.setText(status.getText().toString().replace((!newStatus) ? "available" : "unavailable", (!newStatus) ? "unavailable" : "available"));
                                    if (!newStatus) {
                                        //occupant.setText(occupant.getText().toString().replace("none", userid));
                                        new Thread(new QueryThread(userid, occupant)).start();
                                        borrow.setText("还!");
                                    } else {
                                        occupant.setText("Occupant: none");
                                        borrow.setText("借!");
                                    }
                                }
                            });
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
