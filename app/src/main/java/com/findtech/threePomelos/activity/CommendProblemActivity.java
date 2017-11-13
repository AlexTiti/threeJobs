package com.findtech.threePomelos.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.adapter.ExpandListAdapter;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.mydevices.activity.DeviceDetailActivity;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.IContent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex
 * @date 2017/11/09
 */
public class CommendProblemActivity extends MyActionBarActivity implements View.OnClickListener {

    private ExpandableListView mExpandableListView;
    private List<ArrayList<String>> lists = new ArrayList<>();
    private List<String> strings = new ArrayList<>();
    private ImageView imageView5;
    private NetWorkRequest netWorkRequest;
    private String company = "3Pomelos";
    private String clickType = "Pomelos_A3";
    private int[] titleList = {R.string.bluetooth_common,R.string.bluetooth_scan_common,R.string.bluetooth_fail_common,R.string.bluetooth_data_common};
    private int[] methods = {R.string.bluetooth_common_deal,R.string.bluetooth_scan_common_deal,R.string.bluetooth_fail_common_deal,R.string.bluetooth_data_common_deal};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commen_problem);
        setToolbar(getResources().getString(R.string.common_problem),true,null);
        mExpandableListView = (ExpandableListView) findViewById(R.id.exListView);
        imageView5 = (ImageView) findViewById(R.id.imageView5);
        imageView5.setOnClickListener(this);
        netWorkRequest = new NetWorkRequest(this);
        for (int i= 0;i<titleList.length;i++){
            strings.add(getString(titleList[i]));
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(getString(methods[i]));
            lists.add(arrayList);
        }

        ExpandListAdapter adapter = new ExpandListAdapter(lists,strings,this);
        mExpandableListView.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView5:
                allProblem();
                break;
        }
    }
    public void allProblem(){
        netWorkRequest.getInstruction(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e==null){
                    if (list ==null || list.size() == 0) {
                        return;
                    }
                    String url0 = null;
                    String url1 = null;
                    for (AVObject avObject : list){
                        if ( avObject.getString(NetWorkRequest.COMPANY).equals(company) && avObject.getString(NetWorkRequest.DEVICEIDENTIFITER).equals(clickType)){
                            if (avObject.getString("function").equals("C")){
                                AVFile avFile =avObject.getAVFile("instruction_File");
                                url0  = avFile.getUrl();
                            } else {
                                AVFile avFile1 = avObject.getAVFile("instruction_File");
                                url1 = avFile1.getUrl();

                            }
                        }
                    }
                    Intent intent = new Intent(CommendProblemActivity.this, InstructionsMainActivity.class);
                    intent.putExtra("url0",url0);
                    intent.putExtra("url1",url1);
                    startActivity(intent);
                }else {
                    checkNetWork();
                }

            }
        });
    }

}
