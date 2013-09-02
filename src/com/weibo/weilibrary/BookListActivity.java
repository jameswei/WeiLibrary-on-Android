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

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

/**
 * Created by weijia on 6/8/13.
 */
public class BookListActivity extends Activity {
    private Activity mInstance;
    private ListView booksListView;
    private String booksListStr;
    private Context mCtx;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        this.userid = this.getIntent().getStringExtra("userid");
        this.booksListStr = this.getIntent().getStringExtra("list");
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
        booksListView = (ListView) findViewById(R.id.bookslist);
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("userid", userid);
                try {
                    bundle.putString("data", new JSONArray(booksListStr).getJSONObject(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.setClass(mCtx, BookActivity.class);
                intent.setAction("com.weibo.weilibrary.BookActivity");
                intent.addCategory("com.weibo.weilibrary.BookActivityCategory");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        BookListAdapter adapter = new BookListAdapter(this, this.booksListStr);
        booksListView.setAdapter(adapter);

    }

    private final class BookItem {
        public TextView bookName;
        public TextView description;
        public TextView tag;
        //public TextView owner;
        public TextView status;
    }

    private class BookListAdapter extends BaseAdapter {

        private Context ctx;
        private LayoutInflater layoutInflater;
        private JSONArray data;

        public BookListAdapter(Context ctx, String data) {
            super();
            this.ctx = ctx;
            this.layoutInflater = LayoutInflater.from(ctx);
            try {
                this.data = new JSONArray(data);
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
            BookItem item;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.book_item, null);
                item = new BookItem();
                item.bookName = (TextView) view.findViewById(R.id.bookname);
                item.description = (TextView) view.findViewById(R.id.description);
                item.tag = (TextView) view.findViewById(R.id.tag);
                //item.owner = (TextView) view.findViewById(R.id.owner);
                item.status = (TextView) view.findViewById(R.id.status);
                view.setTag(item);
            } else {
                item = (BookItem) view.getTag();
            }

            try {
                item.bookName.setText(itemData != null ? itemData.getString("bookname") : "unknown");
                item.description.setText(itemData != null ? itemData.getString("description") : "");
                item.tag.setText(itemData != null ? itemData.getString("tags") : "");
                //item.owner.setText(itemData != null ? itemData.getString("owner") : null);
                item.status.setText(itemData != null ? itemData
                        .getBoolean("status") ? "available" : "not available" : "unknown");
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
