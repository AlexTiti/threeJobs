package com.findtech.threePomelos.base;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.WindowCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findtech.threePomelos.R;

import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.service.RFStarBLEService;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.io.File;

/**
 * Created by zhi.zhang on 10/22/15.
 */
public class MyActionBarActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener{

    public Toolbar mToolbar;
    public ImageView btn_tagimage_edit_next;
    public LinearLayout share_button_layout;
    public LinearLayout toolbar_layout;
    public RelativeLayout HomeMenuLayout,share;
   public File file_music;
    public String Log_TAG ;
    public RelativeLayout btn_menu_more;
    protected final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 122;
    protected final int REQUEST_CODE_ASK_ACCESS_COARSE_LOCATION_PERMISSIONS = 123;
    protected final int REQUEST_CODE_ASK_ACCESS_FINE_LOCATION_PERMISSIONS = 124;
    protected final int REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW_PERMISSIONS = 125;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == HomeMenuLayout) {

                PackageManager pm = getPackageManager();
                boolean permission = (PackageManager.PERMISSION_GRANTED ==
                        pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", getPackageName()));
                if (permission) {
                    /**
                     * show pic Dialog
                     */
                } else {
                    ActivityCompat.requestPermissions(MyActionBarActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }


                return;
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log_TAG = this.getLocalClassName();

        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_MODE_OVERLAY);//长按出现复制粘贴栏在顶部占位问题解决方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                requestAlertWindowPermission();
            }
        }
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.home_toolbar_layout);
        MyApplication.getInstance().addActivity(this);
        L.e("QQQQQQQQ","=="+this.getClass());

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        L.e(Log_TAG,"setContentView");
        toolbar_layout = (LinearLayout) findViewById(R.id.toolbar_layout);
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        L.e(Log_TAG,"setContentView");
        if (toolbar_layout == null) {
            L.e("setContentView","setContentView===");
            return;
        }
        toolbar_layout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        colorChange("ff6862");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    protected void setToolbar(Toolbar toolbar, DrawerLayout drawerLayout) {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /* findView */
        ActionBarDrawerToggle mDrawerToggle;
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    protected void setToolbar(Toolbar toolbar, boolean canBack) {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    protected void setToolbar(String title, boolean canBack ,String  showWhat) {

        mToolbar = (Toolbar) toolbar_layout.findViewById(R.id.toolbar);
        btn_menu_more = (RelativeLayout) toolbar_layout.findViewById(R.id.action_more);
        btn_tagimage_edit_next = (ImageView) toolbar_layout.findViewById(R.id.btn_tagimage_edit_next);
        share_button_layout = (LinearLayout) toolbar_layout.findViewById(R.id.share_button_layout);

        if (mToolbar.getVisibility() == View.GONE  ) {
            mToolbar.setVisibility(View.VISIBLE);
        }
        if (btn_tagimage_edit_next.getVisibility() == View.VISIBLE) {
            btn_tagimage_edit_next.setVisibility(View.GONE);
        }
        if (share_button_layout.getVisibility() == View.VISIBLE) {
            share_button_layout.setVisibility(View.GONE);
        }

        if (showWhat != null){
            switch (showWhat){
                case "Save And Share":
                    share_button_layout.setVisibility(View.VISIBLE);
                    break;
                case "Edit WaterPrint":
                    btn_tagimage_edit_next.setVisibility(View.VISIBLE);
                    break;

            }
        }
        TextView mTextView = (TextView) toolbar_layout.findViewById(R.id.toolbar_title);
        if (title == null || title.isEmpty()) {
            mTextView.setText(getResources().getString(R.string.app_name));
        }
        mTextView.setText(title);
        mTextView.setTextColor(getResources().getColor(R.color.white));
        mTextView.setTextSize(20f);
        setToolbar(mToolbar, canBack);
    }
    protected   void setToolBarDiss(){
        mToolbar = (Toolbar) toolbar_layout.findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.GONE);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideInput(view, ev)) {
                HideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ZZ", "requestCode = " + requestCode + " resultCode = " + resultCode + " data = " + data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW_PERMISSIONS) {
                if (!Settings.canDrawOverlays(this)) {
                    ToastUtil.showToast(MyActionBarActivity.this, getResources().getString(R.string.not_open_message), Toast.LENGTH_LONG);
                }
            }
        }
    }

    private void requestAlertWindowPermission() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.notice));
        builder.setNotifyInfo(getResources().getString(R.string.open_bluetooth_message));
        builder.setShowButton(true);
        builder.setPositiveButton(getResources().getString(R.string.set), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_ASK_SYSTEM_ALERT_WINDOW_PERMISSIONS);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ToastUtil.showToast(MyActionBarActivity.this, getResources().getString(R.string.open_bluetooth_cancle), Toast.LENGTH_LONG);
            }
        });
        builder.create().show();
    }

    // 判定是否需要隐藏
    private boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] location = {0, 0};
            v.getLocationInWindow(location);
            int left = location[0], top = location[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (ev.getX() > left
                    && ev.getX() < right
                    && ev.getY() > top
                    && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 判断是连接还是断开
    protected void connectedOrDis(String action) {
        if (RFStarBLEService.ACTION_GATT_CONNECTED.equals(action)) {
            Log.d(MyApplication.TAG, "111111111 连接完成");

        } else if (RFStarBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
            Log.d(MyApplication.TAG, "111111111 连接断开");
        }
    }

    /**
     * @param color String RGB 色值 eg. ff6862
     */

    protected void colorChange(String color) {
        if (mToolbar != null || TextUtils.isEmpty(color)) {
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                // 很明显，这两货是新API才有的。
                window.setStatusBarColor(colorBurn(Integer.parseInt(color, 16), 0.1f));
//                window.setNavigationBarColor(colorBurn(Integer.parseInt(color, 16), 0f));
            }
        }
    }

    protected void colorChange(String color, boolean hasToolbar) {
        if (hasToolbar != true || TextUtils.isEmpty(color)) {
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.setStatusBarColor(colorBurn(Integer.parseInt(color, 16), 0.1f));
            }
        }
    }

    /**
     * 颜色加深处理
     *
     * @param RGBValues RGB的值，由alpha（透明度）、red（红）、green（绿）、blue（蓝）构成，
     *                  Android中我们一般使用它的16进制，
     *                  例如："#FFAABBCC",最左边到最右每两个字母就是代表alpha（透明度）、
     *                  red（红）、green（绿）、blue（蓝）。每种颜色值占一个字节(8位)，值域0~255
     *                  所以下面使用移位的方法可以得到每种颜色的值，然后每种颜色值减小一下，在合成RGB颜色，颜色就会看起来深一些了
     * @return color
     */
    private int colorBurn(int RGBValues, float sub) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;

        red = (int) Math.floor(red * (1 - sub));
        green = (int) Math.floor(green * (1 - sub));
        blue = (int) Math.floor(blue * (1 - sub));
        return Color.rgb(red, green, blue);//E55D58
    }

    //---------------------------------------Tool Bar Right------------------------------//







    public void initShareView(){
        if (toolbar_layout == null) {
            return;
        } else {
            share = (RelativeLayout) toolbar_layout.findViewById(R.id.share);
            share.setVisibility(View.VISIBLE);

        }
    }




    public void readyGoToKilled(Class<?> clazz){
    startActivity(new Intent(this,clazz));
    finish();
  }

    public void readyGo(Class<?> clazz){
        startActivity(new Intent(this,clazz));
    }

}
