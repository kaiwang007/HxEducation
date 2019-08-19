package com.lefeee.hxeducation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lefeee.adapter.WorkListRecyclerViewAdapter;
import com.lefeee.bean.WorkList;
import com.lefeee.util.SPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private String TAG = "MainActivity";

    ProgressDialog m_pDialog;

    SPreferences preferences;

    private String str_worklist;
    List<WorkList> mWorklist;
    private WorkListRecyclerViewAdapter mWorkListAdapter;
    private RecyclerView mRecyclerView;
    private TextView tv_no_message;

    Looper loop = Looper.myLooper();
    final MainActivity.MessageHandler msgHandler = new MainActivity.MessageHandler(loop);

    class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            String result = msg.obj.toString();

            if ("worklist".equals(result)) {
                m_pDialog.cancel();

                try {

                    mWorklist = WorkList.arrayWorkListFromData(str_worklist);
                    for (WorkList temp: mWorklist) {
                        Log.d(TAG, temp.toString());
                    }
                } catch (Exception e) {

/*
                    JSONArray arr = new JSONArray(str_worklist);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject tempJson = (JSONObject) arr.get(i);
                        String finishtime_end = tempJson.getString("finishtime_end");
                        String finishtime_start = tempJson.getString("finishtime_start");
                        String classId = tempJson.getString("classId");
                        String tasktype = tempJson.getString("tasktype");
                        String paperCode = tempJson.getString("paperCode");
                        String teamaterialversion = tempJson.getString("teamaterialversion");
                        String testpapertitle = tempJson.getString("testpapertitle");
                        String subjectId = tempJson.getString("subjectId");
                        Log.d(TAG, "i:" + i);
                        Log.d(TAG, "finishtime_end:" + finishtime_end);
                        Log.d(TAG, "finishtime_start:" + finishtime_start);
                        Log.d(TAG, "classId:" + classId);
                        Log.d(TAG, "tasktype:" + tasktype);
                        Log.d(TAG, "paperCode:" + paperCode);
                        Log.d(TAG, "teamaterialversion:" + teamaterialversion);
                        Log.d(TAG, "testpapertitle:" + testpapertitle);
                        Log.d(TAG, "subjectId:" + subjectId);
                    }
                } catch (JSONException e) {
*/
                    e.printStackTrace();
                }

                Log.d(TAG, "worklist size=" + mWorklist.size());
                if (mWorklist.size() > 0) {
                    mWorkListAdapter = new WorkListRecyclerViewAdapter(mWorklist);
                    mRecyclerView.setAdapter(mWorkListAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    tv_no_message.setVisibility(View.GONE);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    tv_no_message.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_nav1:
                    mTextMessage.setText(R.string.title_nav1);

                    getWorkList();

                    return true;
                case R.id.navigation_nav2:
                    mTextMessage.setText(R.string.title_nav2);
                    Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_nav3:
                    mRecyclerView.setVisibility(View.GONE);
                    tv_no_message.setVisibility(View.GONE);
                    mTextMessage.setText(R.string.title_nav3);
                    return true;
//                case R.id.navigation_nav4:
//                    mTextMessage.setText(R.string.title_nav4);
//                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        preferences = SPreferences.get(getApplicationContext());

        mRecyclerView = findViewById(R.id.worklist);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        tv_no_message = findViewById(R.id.no_message_view);
        getWorkList();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }


    public void getWorkList() {
        m_pDialog = new ProgressDialog(MainActivity.this);
        m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_pDialog.setMessage("获取任务列表中...");
        m_pDialog.setIndeterminate(false);
        m_pDialog.setCancelable(true);
        m_pDialog.show();

        new Thread(){
            public void run() {
                JSONObject jsonObject = new JSONObject();
                int teaAccId = 15;

                ApiCall apiCall = new ApiCall();
                str_worklist = apiCall.WorkListGet(teaAccId);

                Message workMsg = Message.obtain();
                workMsg.obj = "worklist";
                msgHandler.sendMessage(workMsg);
            }
        }.start();
    }


}
