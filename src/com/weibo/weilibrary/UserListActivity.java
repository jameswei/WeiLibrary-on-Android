package com.weibo.weilibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
public class UserListActivity extends Activity {
    private Activity mInstance;
    private ListView usersListView;
    private String usersListStr;
    private Context mCtx;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userid = this.getIntent().getStringExtra("userid");
        this.usersListStr = this.getIntent().getStringExtra("list");
        setContentView(R.layout.activity_users);
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
        usersListView = (ListView) findViewById(R.id.userslist);
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new Thread(new Runnable() {
                    private Resty client;

                    @Override
                    public void run() {
                        client = new Resty();
                        client.withHeader("accept", "application/json");
                        client.withHeader("content-type", "application/json");
                        try {
                            JSONResource result = client.json("http://10.0.2.2:9080/user/book/" + new JSONArray(usersListStr).getJSONObject(i).getString("id"));
                            if (result != null) {
                                JSONArray array = result.array();
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString("userid", userid);
                                bundle.putString("list", array.length() == 0 ? "[]" : array.toString());
                                intent.setClass(mCtx, BookListActivity.class);
                                intent.setAction("com.weibo.weilibrary.BookListActivity");
                                intent.addCategory("com.weibo.weilibrary.BookListActivityCategory");
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("userid", userid);
//                try {
//                    bundle.putString("data", new JSONArray(usersListStr).getJSONObject(i).toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                intent.setAction("com.weibo.weilibrary.UserActivity");
//                intent.addCategory("com.weibo.weilibrary.UserActivityCategory");
//                intent.putExtras(bundle);
//                startActivity(intent);
            }
        });
        // 传参数
        UserListAdapter adapter = new UserListAdapter(this, this.usersListStr);
        usersListView.setAdapter(adapter);
    }

    public final class UserItem {
        public TextView userName;
        public TextView bio;
        public TextView tag;
        public TextView owned;
    }

    private class UserListAdapter extends BaseAdapter {

        private Context ctx;
        private LayoutInflater layoutInflater;
        private JSONArray data;


        public UserListAdapter(Context ctx, String dataStr) {
            super();
            this.ctx = ctx;
            this.layoutInflater = LayoutInflater.from(this.ctx);
            try {
                this.data = new JSONArray(dataStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getCount() {
            return this.data.length();
        }

        @Override
        public Object getItem(int i) {
            if (this.data.length() >= 0) {
                try {
                    return this.data.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            } else return null;
        }

        @Override
        public long getItemId(int i) {
//            try {
//                return Long.parseLong(this.data.getJSONObject(i).getString("id"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            JSONObject itemData = null;
            try {
                itemData = this.data.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            UserItem item;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.user_item, null);
                item = new UserItem();
                item.userName = (TextView) view.findViewById(R.id.username);
                item.bio = (TextView) view.findViewById(R.id.bio);
                item.tag = (TextView) view.findViewById(R.id.tags);
                item.owned = (TextView) view.findViewById(R.id.own);
                view.setTag(item);
            } else {
                item = (UserItem) view.getTag();
            }

            try {
                item.userName.setText(itemData != null ? itemData.getString("username") : "");
                item.bio.setText(itemData != null ? itemData.getString("bio") : "");
                item.tag.setText(itemData != null ? itemData.getString("tags") : "");
                item.owned.setText(itemData != null ? item.owned.getText().toString().replace("{booksnum}", String.valueOf(itemData.getJSONArray("books").length())) : item.owned.getText().toString().replace("{booksnum}", "0"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (i % 2 == 0) {
                view.setBackgroundColor(Color.WHITE);
            } else view.setBackgroundColor(Color.GRAY);
            return view;
        }
    }
}
