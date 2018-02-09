package com.findtech.threePomelos.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.adapter.TipsRecycleAdpter;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhi.zhang
 * @date 5/3/16
 */
public class BabyHealthTipsContentActivity extends MyActionBarActivity implements TipsRecycleAdpter.ItemClick {

    private TextView bodyHealthContent;
    private BabyInfoEntity babyInfoEntity = BabyInfoEntity.getInstance();
    String tipType;
    private RecyclerView recycle_view_tips;
    private TipsRecycleAdpter tipsRecycleAdpter;
    private NetWorkRequest netWorkRequest;
    private ArrayList<String> dates = new ArrayList<>();
    MyCalendar myCalendar;
    private String baby_date;
    private int position;
    ImageView viewstub;
    LinearLayout layout_baby_content;
    String body;
    boolean date = false;
    boolean data_b = false;
    private int click_position;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_health_tip);
        registerMusicBroadcast();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tipType = bundle.getString(Tools.TIPS_TYPE, NetWorkRequest.FILE_HEALTH_TIPS);
            setToolbar(bundle.getString(Tools.TIPS_TYPE_TITLE,
                    getResources().getString(R.string.title_activity_baby_health_tip)), true,null);
        }
        viewstub = (ImageView) findViewById(R.id.viewstub);
        layout_baby_content = (LinearLayout) findViewById(R.id.layout_baby_content);
        bodyHealthContent = (TextView) findViewById(R.id.tv_body);
        recycle_view_tips = (RecyclerView) findViewById(R.id.recycle_view_tips);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycle_view_tips.setLayoutManager(linearLayout);
        tipsRecycleAdpter = new TipsRecycleAdpter();
        tipsRecycleAdpter.setItemClickListtener(this,this);
        recycle_view_tips.setAdapter(tipsRecycleAdpter);
        netWorkRequest = new NetWorkRequest(this);
        requestBabyHealthDate();
        String currentDate = Tools.getSystemTimeInChina("yyyy-MM-dd");
        String birthday = null;
        if (TextUtils.isEmpty(babyInfoEntity.getBirthday())){
            String birthdayBady ;
            OperateDBUtils utils = new OperateDBUtils(this);
            utils.queryBabyInfoDataFromDB();
            birthdayBady = currentDate;
            birthday = birthdayBady.replace("年", "-").replace("月", "-").replace("日", "");
        }else {
            birthday = babyInfoEntity.getBirthday().replace("年", "-").replace("月", "-").replace("日", "");
        }
        babyInfoEntity.setBabyTotalDay(this, birthday, "0");
        try {
            myCalendar = new MyCalendar(birthday, currentDate,this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (myCalendar == null){
            requestBabyHealthDataAndUpdateUI(tipType,"0-0-1");
        }else {
            baby_date = myCalendar.getDateForHealthTips();
            requestBabyHealthDataAndUpdateUI(tipType, baby_date);
        }
    }

    private void requestBabyHealthDate(){
        netWorkRequest.getAllHealthDate( new FindCallback() {
            @Override
            public void done(List list, AVException e) {
                if (e == null){
                    for (int i=0;i<list.size();i++){
                        AVObject avObject = (AVObject) list.get(i);
                        String date = avObject.getString("Date");
                        dates.add(date);
                        if (date.equals(baby_date)){
                            position = i ;
                            tipsRecycleAdpter.setPosi(position);
                        }
                    }
                    date = true;
                    refershUI();
                    tipsRecycleAdpter.setArrayList(dates);
                    tipsRecycleAdpter.notifyDataSetChanged();
                    if (position - 3 >= 0) {
                        position -= 3;
                    }
                    recycle_view_tips.scrollToPosition(position);
                }else{

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }



    private void refershUI(){

if (date && data_b) {
    layout_baby_content.setVisibility(View.GONE);
    dismissProgressDialog();
    bodyHealthContent.setText(Html.fromHtml(body));
}
    }


    private void requestBabyHealthDataAndUpdateUI(final String type,final String date) {
            showProgressDialog(getString(R.string.getMessage_fromNet),null);
            if (TextUtils.isEmpty(babyInfoEntity.getBirthday())) {
                return;
            }
            body = null;
            AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.HEALTH_TIPS);
            query.whereEqualTo("Date", date);
            query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.setMaxCacheAge(24 * 3600 * 7 * 1000);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> avObjects, AVException e) {
                    if (e == null) {
                        layout_baby_content.setVisibility(View.GONE);
                        if (avObjects.size() > 0) {
                            AVFile avFile = avObjects.get(0).getAVFile(type);
                            if (avFile == null) {
                                ToastUtil.showToast(BabyHealthTipsContentActivity.this, getResources().getString(R.string.data_exception));
                                return;
                            }
                            avFile.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, AVException e) {
                                    if (e == null) {
                                       body = new String(data);
                                        data_b = true;
                                        refershUI();
                                    } else {
                                        errorDo();
                                    }
                                }
                            }, new ProgressCallback() {
                                @Override
                                public void done(Integer integer) {
                                }
                            });
                        } else {
                            dismissProgressDialog();
                            ToastUtil.showToast(BabyHealthTipsContentActivity.this, getResources().getString(R.string.no_data));
                        }
                    } else {
                        errorDo();
                    }
                }
            });
    }

    public void errorDo(){
        dismissProgressDialog();
        bodyHealthContent.setText(null);
        layout_baby_content.setVisibility(View.VISIBLE);

        layout_baby_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_baby_content.setVisibility(View.GONE);
                if (myCalendar == null){
                    requestBabyHealthDataAndUpdateUI(tipType,"0-0-1");
                }else {
                    if (click_position!= -1 &&dates!= null ) {
                        requestBabyHealthDataAndUpdateUI(tipType, dates.get(click_position));
                    }
                }
                // requestBabyHealthDate();
            }
        });
    }



    @Override
    public void onClick(int position, TipsRecycleAdpter.ViewHolder holder) {
        click_position = position;
        data_b = false;
        requestBabyHealthDataAndUpdateUI(tipType,dates.get(position));
        tipsRecycleAdpter.setSelest_position(position);
        tipsRecycleAdpter.notifyDataSetChanged();



    }
}
