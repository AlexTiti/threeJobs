package com.findtech.threePomelos.home.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.BaseLazyFragment;
import com.findtech.threePomelos.bluetooth.BLEDevice;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.music.activity.CarMusicActivity;
import com.findtech.threePomelos.music.activity.HotRecommenActivity;
import com.findtech.threePomelos.music.utils.DownMusicBean;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.musicserver.Nammu;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.service.RFStarBLEService;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.music.activity.MusicBabyActivity;
import com.findtech.threePomelos.music.activity.MusicLocalActivity;
import com.findtech.threePomelos.music.activity.RencentMusicActivity;
import com.findtech.threePomelos.music.activity.SixItemMusicActivity;
import com.findtech.threePomelos.utils.ScreenUtils;
import com.findtech.threePomelos.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;




/**
 * Created by Alex on 2017/5/3.
 * <pre>
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/03
 *     desc    ：
 *     version ： 1.0
 */
public class MusicFragment extends BaseLazyFragment implements View.OnClickListener,BLEDevice.RFStarBLEBroadcastReceiver {

    private RelativeLayout relayout_musiclocal,relayout_popular;
    private RelativeLayout relayout_story;
    private RelativeLayout relayout_poetry;
    private RelativeLayout relayout_rhyme;
    private RelativeLayout three_character;
    private RelativeLayout relayout_english;
    private RelativeLayout relayout_car_music;
    private RelativeLayout relayout_babylike;
    private RelativeLayout relayout_recentplay;
    private NetWorkRequest netWorkRequest;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_music_frabment;
    }

    @Override
    protected void initViewsAndEvents(View view) {
        relayout_babylike = (RelativeLayout) view.findViewById(R.id.relayout_babylike);
        relayout_story = (RelativeLayout) view.findViewById(R.id.relayout_story);
        relayout_poetry = (RelativeLayout) view.findViewById(R.id.relayout_poetry);
        relayout_rhyme = (RelativeLayout) view.findViewById(R.id.relayout_rhyme);
        three_character = (RelativeLayout) view.findViewById(R.id.three_character);
        relayout_english = (RelativeLayout) view.findViewById(R.id.relayout_english);
        relayout_car_music = (RelativeLayout) view.findViewById(R.id.relayout_car_music);
        relayout_recentplay = (RelativeLayout) view.findViewById(R.id.relayout_recentplay);
        relayout_musiclocal = (RelativeLayout) view.findViewById(R.id.relayout_musiclocal);
        relayout_popular =(RelativeLayout) view.findViewById(R.id.relayout_popular);
        netWorkRequest = new NetWorkRequest(getActivity());
        if (IContent.getInstacne(). downList.size() == 0)
        netWorkRequest.getMusicDownList(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null){
                    for (int i=0;i<list.size();i++) {
                        AVObject avObject = list.get(i);
                        IContent.getInstacne(). downList.add( new DownMusicBean(avObject.getString("musicName"),avObject.getNumber("typeNumber")) );
                    }
                    L.e("MusicFragment===========","================="+IContent.getInstacne(). downList.size());
                }
            }
        });
        if (IContent.getInstacne().collection_array.size() == 0){
            netWorkRequest.getMusicCollect(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        for (AVObject avObject : list) {
                            String musicName = avObject.getString("musicName");
                            Number type = avObject.getNumber("typeNumber");
                            DownMusicBean bean = new DownMusicBean(musicName,type);
                            IContent.getInstacne().collection_array.add(bean);
                            L.e("===================",musicName+"=================");
                        }
                    }
                }
            });
        }
        View view1 = view.findViewById(R.id.view);
        ViewGroup.LayoutParams layoutParams = view1.getLayoutParams();
        layoutParams.height = ScreenUtils.getStatusBarHeight(getActivity());
        view1.setLayoutParams(layoutParams);
        view1.setBackgroundColor(Color.TRANSPARENT);
        relayout_story.setOnClickListener(this);
        relayout_poetry.setOnClickListener(this);
        relayout_rhyme.setOnClickListener(this);
        three_character.setOnClickListener(this);
        relayout_english.setOnClickListener(this);
        relayout_car_music.setOnClickListener(this);
        relayout_popular.setOnClickListener(this);
        relayout_musiclocal.setOnClickListener(this);
        relayout_babylike.setOnClickListener(this);
        relayout_recentplay.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Nammu.init(getActivity());
            checkPermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(){
            if (!Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        PERMISSIONS_STORAGE, 1
                );
            }
    }

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {
    }

    @Override
    public void onClick(View v) {
        Intent intent = null ;
        switch (v.getId()){
            case R.id.relayout_story:
                intent = new Intent(mContext, SixItemMusicActivity.class);
                intent.putExtra(IContent.TITLE,mContext.getResources().getString(R.string.story));
                intent.putExtra(IContent.CONTENT,getString(R.string.story_notice));
                intent.putExtra(IContent.NUMBER,2);
                startActivity(intent);
                break;
            case R.id.relayout_rhyme:
                intent = new Intent(mContext, SixItemMusicActivity.class);
                intent.putExtra(IContent.TITLE,mContext.getResources().getString(R.string.rhyme));
                intent.putExtra(IContent.CONTENT,getString(R.string.rhyme_notice));
                intent.putExtra(IContent.NUMBER,0);
                startActivity(intent);
                break;

            case R.id.relayout_poetry:
                intent = new Intent(mContext, SixItemMusicActivity.class);
                intent.putExtra(IContent.TITLE,mContext.getResources().getString(R.string.poetry));
                intent.putExtra(IContent.CONTENT,getString(R.string.poetry_notice));
                intent.putExtra(IContent.NUMBER,1);
                startActivity(intent);
                break;

            case R.id.three_character:
                intent = new Intent(mContext, SixItemMusicActivity.class);
                intent.putExtra(IContent.TITLE,mContext.getResources().getString(R.string.three_character));
                intent.putExtra(IContent.CONTENT,getString(R.string.three_character_notice));
                intent.putExtra(IContent.NUMBER,4);
                startActivity(intent);
                break;
            case R.id.relayout_english:
                intent = new Intent(mContext, SixItemMusicActivity.class);
                intent.putExtra(IContent.TITLE,mContext.getResources().getString(R.string.english));
                intent.putExtra(IContent.CONTENT,getString(R.string.english_notice));
                intent.putExtra(IContent.NUMBER,3);
                startActivity(intent);
                break;
            case R.id.relayout_car_music:
                if (app.manager.cubicBLEDevice != null ) {
                    L.e("====================","====================="+app.manager.cubicBLEDevice);
                    app.manager.cubicBLEDevice.registerReceiver();
                    app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
                    app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE,IContent.WRITEUUID_BLE,IContent.READMODE);
                } else
                    ToastUtil.showToast(mContext,getResources().getString(R.string.link_notice));

                break;
            case  R.id.relayout_musiclocal:
                startActivity(new Intent(mContext, MusicLocalActivity.class));
                break;
            case  R.id.relayout_babylike:
                startActivity(new Intent(mContext, MusicBabyActivity.class));
                break;
            case  R.id.relayout_recentplay:
                startActivity(new Intent(mContext, RencentMusicActivity.class));
                break;

            case  R.id.relayout_popular:
                startActivity(new Intent(mContext, HotRecommenActivity.class));
                break;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent, String macData, String uuid) {
        String action = intent.getAction();
        if (action == null)
            return;
        byte data[] = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
        if (intent.getAction().equals(RFStarBLEService.ACTION_WRITE_DONE)){
            L.e("===========================","====================="+RFStarBLEService.ACTION_WRITE_DONE);
            if (IContent.getInstacne().WRITEVALUE != null)
                app.manager.cubicBLEDevice.readValue(IContent.SERVERUUID_BLE,IContent.READUUID_BLE,IContent.getInstacne().WRITEVALUE);

        }else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE_READ)){
            if ( data[3] == (byte)0x81 &&  data[4] == 0x03 ){
                if (data[5] == 0x01) {
                    L.e("===========================","=====================data[5] == 0x01");
                    startActivity(new Intent(getActivity(), CarMusicActivity.class));
                    ToastUtil.showToast(getActivity(),getResources().getString(R.string.change_sd_mode));
                }
                else {

                    app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.SDMODE);
                }
            }
            if ( data[3] == (byte)0x81 &&  data[4] == 0x02){
                if (data[5] == 0x01) {
                    ToastUtil.showToast(getActivity(), getResources().getString(R.string.change_sd_mode));
                    startActivity(new Intent(getActivity(), CarMusicActivity.class));
                }else{
                    ToastUtil.showToast(getActivity(), getString(R.string.sd_notice));
                }
            }
        }else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE)){

            if ( data[3] == (byte)0x81 &&  data[4] == 0x02){
                if (data[5] == 0x01) {
                    ToastUtil.showToast(getActivity(), getResources().getString(R.string.change_sd_mode));
                    startActivity(new Intent(getActivity(), CarMusicActivity.class));
                }else{
                    ToastUtil.showToast(getActivity(), getString(R.string.sd_notice));
                }
            }

        }
    }

    @Override
    public void onReceiveDataAvailable(String dataType, String data, TravelInfoEntity travelInfoEntity, String time) {

    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd("MusicFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart("MusicFragment");
    }
}
