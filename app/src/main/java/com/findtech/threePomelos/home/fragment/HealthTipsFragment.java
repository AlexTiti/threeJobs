package com.findtech.threePomelos.home.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.activity.BabyHealthTipsContentActivity;
import com.findtech.threePomelos.base.BaseLazyFragment;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.ScreenUtils;
import com.findtech.threePomelos.utils.Tools;

import java.text.ParseException;

/**
 * Created by Alex on 2017/5/5.
 * <pre>
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/05
 *     desc    ：
 *     version ： 1.0
 */
public class HealthTipsFragment  extends BaseLazyFragment implements View.OnClickListener{

    private RelativeLayout childcare_points;
    private RelativeLayout dp_sign;
    private RelativeLayout science_tip;
    private RelativeLayout grown_concern;
    private RelativeLayout interaction;
    private TextView text_1 ;
    private TextView title_health;
    BabyInfoEntity  babyInfoEntity = BabyInfoEntity.getInstance();
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.healthfragment;
    }

    @Override
    protected void initViewsAndEvents(View view) {
        childcare_points = (RelativeLayout) view.findViewById(R.id.childcare_points);
        dp_sign = (RelativeLayout) view.findViewById(R.id.dp_sign);
        science_tip = (RelativeLayout) view.findViewById(R.id.science_tip);
        grown_concern = (RelativeLayout) view.findViewById(R.id.grown_concern);
        interaction = (RelativeLayout) view.findViewById(R.id.interaction);
        text_1 = (TextView) view.findViewById(R.id.text_1);
        title_health = (TextView) view.findViewById(R.id.title_health);

        View view1 = view.findViewById(R.id.view);
        ViewGroup.LayoutParams layoutParams = view1.getLayoutParams();
        layoutParams.height = ScreenUtils.getStatusBarHeight(getActivity());
        view1.setLayoutParams(layoutParams);
        view1.setBackgroundColor(Color.TRANSPARENT);
        if (babyInfoEntity.getBirthday() != null) {
            String s = babyInfoEntity.getBirthday().substring(5);
            title_health.setText(getString(R.string.label_baby_birthday_health) + " " + s);


            String currentDate = Tools.getSystemTimeInChina("yyyy-MM-dd");

            String birthday = babyInfoEntity.getBirthday().replace("年", "-").replace("月", "-").replace("日", "");

            try {
                MyCalendar myCalendar = new MyCalendar(birthday, currentDate,getActivity());
                text_1.setText(myCalendar.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            title_health.setText(getString(R.string.label_baby_birthday_health) + " " +Tools.getCurrentTime() );
            text_1.setText("0"+getString(R.string.text_tag_babydata_text_day));

        }
        childcare_points.setOnClickListener(this);
        dp_sign.setOnClickListener(this);
        science_tip.setOnClickListener(this);
        grown_concern.setOnClickListener(this);
        interaction.setOnClickListener(this);

    }

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), BabyHealthTipsContentActivity.class);

        switch (v.getId()) {
            case R.id.childcare_points://育儿要点
                intent.putExtra(Tools.TIPS_TYPE, NetWorkRequest.FILE_CHILDCARE_POINTS);
                intent.putExtra(Tools.TIPS_TYPE_TITLE, getResources().getString(R.string.title_activity_childcare_points));
                startActivity(intent);
                break;
            case R.id.dp_sign://发育体征
                intent.putExtra(Tools.TIPS_TYPE, NetWorkRequest.FILE_DEVELOPMENT_SIGNS);
                intent.putExtra(Tools.TIPS_TYPE_TITLE, getResources().getString(R.string.title_activity_dp_sign));
                startActivity(intent);
                break;
            case R.id.science_tip://科普提示
                intent.putExtra(Tools.TIPS_TYPE, NetWorkRequest.FILE_SCIENCE_TIP);
                intent.putExtra(Tools.TIPS_TYPE_TITLE, getResources().getString(R.string.title_activity_science_tip));
                startActivity(intent);
                break;
            case R.id.grown_concern://成长关注
                intent.putExtra(Tools.TIPS_TYPE, NetWorkRequest.FILE_GROWN_CONCERN);
                intent.putExtra(Tools.TIPS_TYPE_TITLE, getResources().getString(R.string.title_activity_grown_concern));
                startActivity(intent);
                break;
            case R.id.interaction://亲子互动
                intent.putExtra(Tools.TIPS_TYPE, NetWorkRequest.FILE_PARENTAL);
                intent.putExtra(Tools.TIPS_TYPE_TITLE, getResources().getString(R.string.title_activity_parental_interaction));
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd("HealthTipsFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart("HealthTipsFragment");
    }
}
