package com.example.morgan.bottomnavigation;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public List<Map<String, Object>> data = new ArrayList<Map<String, Object>>(); //时间的列表

    public long startTime=0;//开始时间
    public long endTime=0;//结束时间
    public String activeTag="";//行为标签
    public boolean doing=false;//默认 没有在计时
    public long baseTime=0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    setContentView(R.layout.activity_list);
                    bindNav();//绑定底部菜单nav
                    render_list();//渲染列表
                    return true;
                case R.id.navigation_start:
                    if(doing){
                        //正在计时页面
                        setContentView(R.layout.activity_doing);
                        //恢复数据
                        EditText activeTagInput=(EditText)findViewById(R.id.activeTag);
                        activeTagInput.setText(activeTag);
                        time_start();

                    }else{
                        //开始计时页面
                        setContentView(R.layout.activity_select);

                    }

                    bindNav();
                    return true;
                case R.id.navigation_about:
                    setContentView(R.layout.activity_about);
                    bindNav();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置主页是列表页
        setContentView(R.layout.activity_list);

        //mTextMessage = (TextView) findViewById(R.id.message);
        bindNav();//绑定底部的菜单栏

        render_list();//渲染列表页

        //
    }

    public void Button_startTime_Click(View view){
        doing=true;//开始计时
        startTime=getTimestamp();//初始化开始时间戳

        Button btn=(Button)findViewById(R.id.startTimeButton);


        hide_soft_input(view);//强制隐藏键盘

        activeTag=((EditText)findViewById(R.id.activeTag)).getText().toString();
        setContentView(R.layout.activity_doing);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        EditText activeTagInput=(EditText)findViewById(R.id.activeTag);
        activeTagInput.setText(activeTag);
        time_start();
    }
    public void Button_endTime_Click(View view) throws ParseException {
        doing=false;//计时状态结束
        endTime=getTimestamp();//设置结束的时间戳
        baseTime=0;//恢复初始化
        activeTag=((EditText)findViewById(R.id.activeTag)).getText().toString();

        hide_soft_input(view);//强制隐藏键盘

        //Chronometer timer = (Chronometer) findViewById(R.id.timer);
        //long mRecordTime = SystemClock.elapsedRealtime();

        setContentView(R.layout.activity_list);
        bindNav();

        String starttime=getFormatTime(startTime);
        String endtime=getFormatTime(endTime);
        String time=starttime+"~"+endtime;




        Map<String, Object> item = new HashMap<String, Object>();

        long TimeLong = endTime-startTime;
        //
        //yy/1000:相差多少秒

        //yy/1000/60:相差多少分钟

        //yy/1000/60/60:相差多少小时

        //yy/1000/60/60/24:相差多少天
        //

        long h=TimeLong/1000/60/60;
        long m=TimeLong/1000/60;
        long s=TimeLong/1000;

        String hh="";
        if(h<10){
            hh="0"+h;
        }else{
            hh=""+h;
        }

        String mm="";
        if(h<10){
            mm="0"+m;
        }else{
            mm=""+m;
        }
        String ss="";
        if(s<10){
            ss="0"+s;
        }else{
            ss=""+s;
        }

        item.put("TimeLong",hh+"h "+mm+"m "+ss+"s");
        item.put("startTime",starttime);
        item.put("endTime",endtime);
        item.put("activeTag",activeTag);


        //填充数据
        data.add(item);

        //存储到 手机里 xml
        //数据操作结束

        render_list();


    }

    public void Button_return_home_Click(View view){
        Button btn=(Button)findViewById(R.id.endTimeButton);
        setContentView(R.layout.activity_main);
    }

    public void Button_page_home_Click(View view){
        //home
        setContentView(R.layout.activity_main);
    }
    public void Button_page_doing_Click(View view){
        //list
        setContentView(R.layout.activity_doing);
    }
    public void Button_page_list_Click(View view){
        //list
        setContentView(R.layout.activity_list);
    }
    public void Button_page_about_Click(View view){
        //about
        setContentView(R.layout.activity_about);
    }

    public long getTimestamp(){
        long time=System.currentTimeMillis();///1000;//获取系统时间戳
        return time;
    }
    public long getTimestamp_for_php(){
        long time=System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳
        return time;
    }
    public String getStringfromTimestamp(long time){
        String  str=String.valueOf(time);
        return str;
    }
    public String getFormatTime(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(time);
    }

    public void render_list(){
        //渲染 list
        //克隆一个 倒序排列的 list
        List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>(); //时间的列表



        for(int i=0; i<data.size(); i++) {
            Map<String, Object> it = data.get(i);
            data_list.add(it);
        }
        Collections.reverse(data_list);//倒叙 内容反转

        //控件
        ListView list=(ListView)findViewById(R.id.timeList);
        //适配器
        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), data_list,
                R.layout.item, new String[] { "TimeLong", "startTime","endTime","activeTag" },
                new int[] { R.id.TimeLong, R.id.startTime,R.id.endTime,R.id.activeTag });
        //绑定
        list.setAdapter(simpleAdapter);
        //
    }

    public void bindNav(){
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    public void time_start(){
        Chronometer timer = (Chronometer) findViewById(R.id.timer);
        if(baseTime==0){
            baseTime=SystemClock.elapsedRealtime();
        }
        timer.setBase(baseTime);//计时器清零
        //timer.setBase(System.currentTimeMillis());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime()-baseTime) / 1000 / 60);
        timer.setFormat("0"+String.valueOf(hour)+":%s");
        //timer.setFormat(hour+"(%s)");
        timer.start();
    }

    public void hide_soft_input(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        //imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);//强制显示键盘
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }
    public void select_chi(View view){
        doing=true;//开始计时
        startTime=getTimestamp();//初始化开始时间戳
        hide_soft_input(view);//强制隐藏键盘
        activeTag="吃";
        setContentView(R.layout.activity_doing);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        EditText activeTagInput=(EditText)findViewById(R.id.activeTag);
        activeTagInput.setText(activeTag);
        time_start();
    }
    public void select_he(View view){
        doing=true;//开始计时
        startTime=getTimestamp();//初始化开始时间戳
        hide_soft_input(view);//强制隐藏键盘
        activeTag="喝";
        setContentView(R.layout.activity_doing);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        EditText activeTagInput=(EditText)findViewById(R.id.activeTag);
        activeTagInput.setText(activeTag);
        time_start();
    }
    public void select_la(View view){
        doing=true;//开始计时
        startTime=getTimestamp();//初始化开始时间戳
        hide_soft_input(view);//强制隐藏键盘
        activeTag="拉";
        setContentView(R.layout.activity_doing);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        EditText activeTagInput=(EditText)findViewById(R.id.activeTag);
        activeTagInput.setText(activeTag);
        time_start();
    }
    public void select_sa(View view){
        doing=true;//开始计时
        startTime=getTimestamp();//初始化开始时间戳
        hide_soft_input(view);//强制隐藏键盘
        activeTag="撒";
        setContentView(R.layout.activity_doing);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        EditText activeTagInput=(EditText)findViewById(R.id.activeTag);
        activeTagInput.setText(activeTag);
        time_start();
    }
    public void select_shui(View view){
        doing=true;//开始计时
        startTime=getTimestamp();//初始化开始时间戳
        hide_soft_input(view);//强制隐藏键盘
        activeTag="睡";
        setContentView(R.layout.activity_doing);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        EditText activeTagInput=(EditText)findViewById(R.id.activeTag);
        activeTagInput.setText(activeTag);
        time_start();
    }
    public void select_ku(View view){
        doing=true;//开始计时
        startTime=getTimestamp();//初始化开始时间戳
        hide_soft_input(view);//强制隐藏键盘
        activeTag="哭";
        setContentView(R.layout.activity_doing);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        EditText activeTagInput=(EditText)findViewById(R.id.activeTag);
        activeTagInput.setText(activeTag);
        time_start();
    }
}
