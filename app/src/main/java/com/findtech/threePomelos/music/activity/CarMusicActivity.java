package com.findtech.threePomelos.music.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.BaseActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.bluetooth.BLEDevice;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.service.RFStarBLEService;
import com.findtech.threePomelos.utils.BitmapUtil;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.ScreenUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.BounceScrollView;
import com.findtech.threePomelos.view.MyListView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CarMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener, BounceScrollView.ScrollingListenering, BLEDevice.RFStarBLEBroadcastReceiver, BounceScrollView.LoadListenering {

    MyListView listView;
    private String Tag = "CarMusicActivity";
    private MyApplication app;
    private ArrayList<MusicInfo> musicInfos = new ArrayList<>();
    Handler handler;
    private int number = 5;
    private TextView title_below;
    private RelativeLayout back_relative;
    private ImageView image_pic;
    private ImageView image_bac;
    long i = 1;
    private int preTop;
    private int preLeft;
    private int preBottom;
    private int preTop_text;
    private TextView textView_mode;
    private boolean isFirst = true;
    private BounceScrollView mScrollView;
    private TextView text_titlt_top;
    private int left;
    private int bot;
    private int right;
    private ImageView image_back_six;
    boolean isTop = false;
    int fileCount;                    // 文件个数
    private int count = 0;
    //  文件夹个数
    private int folderNumCount;
    private int cellCount;
    int backCount;//back个数
    int cellRowCount;           // cellRow个数
    // 文件夹集合
    private ArrayList<ArrayList> _folderArray = new ArrayList<>();
    //文件集合
    private ArrayList<ArrayList> _fileArray = new ArrayList<>();
    // 文件夹数目数组
    private ArrayList<byte[]> _folderNumArray = new ArrayList<>();
    private ArrayList<byte[]> _backArray = new ArrayList<>(); // back数组
    private ArrayList<String> _cellArray = new ArrayList<>();       // cell数组
    private ArrayList<byte[]> _fileNum = new ArrayList<>();// 文件数目数组
    private int changeCount;
    private CarMusicAdapter carMusicAdapter;
    public static int GETFILESYSTEM = 0;
    public static int GETDIRECTORY = 1;
    public static int GETPREDIRECTORY = 2;
    public static int GETTINGFOLDERINFO = 3;
    public static int GETTINGFILEINFO = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_story);
        IContent.getInstacne().SD_Mode = true;
        app = (MyApplication) getApplication();
        mScrollView = (BounceScrollView) findViewById(R.id.id_scrollView);
        mScrollView.setScrLis(this);
        mScrollView.setLoadListenering(this);
        changeCount = GETFILESYSTEM;
        View view1 = findViewById(R.id.view);
        ViewGroup.LayoutParams layoutParams = view1.getLayoutParams();
        bot = ScreenUtils.getStatusBarHeight(this);
        layoutParams.height = bot;
        view1.setLayoutParams(layoutParams);
        view1.setBackgroundColor(Color.TRANSPARENT);

        back_relative = (RelativeLayout) findViewById(R.id.back_relative);
        text_titlt_top = (TextView) findViewById(R.id.text_titlt_top);
        text_titlt_top.setPadding(0, ScreenUtils.getStatusBarHeight(this), 0, 0);
        title_below = (TextView) findViewById(R.id.title_below);
        image_bac = (ImageView) findViewById(R.id.image_bac);
        image_pic = (ImageView) findViewById(R.id.image_pic);
        image_pic.setImageResource(IContent.MUSIC_SEC_IMAGE[number]);
        textView_mode = (TextView) findViewById(R.id.textView_mode);
        image_back_six = (ImageView) findViewById(R.id.image_back_six);
        text_titlt_top.setText(getResources().getString(R.string.car_music));
        title_below.setText(getString(R.string.car_music_notice));
        back_relative.setBackgroundResource(IContent.MUSIC_BACKCOLOR[number]);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), IContent.MUSIC_SEC_IMAGE_O[number]);
        image_bac.setImageBitmap(BitmapUtil.blurBitmap(bitmap, this));
        carMusicAdapter = new CarMusicAdapter();
        listView = (MyListView) findViewById(R.id.music_recycle);
        listView.setAdapter(carMusicAdapter);
        listView.setOnItemClickListener(this);
        image_back_six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
        }
        refreshData();
        handler = HandlerUtil.getInstance(this);
        _cellArray.add(getResources().getString(R.string.back));
    }

    private void refreshData() {
        byte bytes[] = {0x55, (byte) 0xaa, 0x04, 0x02, 0x03, 0x00, 0x00, 0x00, 0x01, (0 - 0x0a)};
        _backArray.add(bytes);
        if (app.manager.cubicBLEDevice != null) {
            showProgressDialog(getResources().getString(R.string.getMessage_fromNet),getString(R.string.getMessage_fromNet_fail));
            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.GET_FILE);
        }
    }

    @Override
    public void scroll(int dt, int t, int top) {
        if (isFirst) {
            preTop = text_titlt_top.getTop() - textView_mode.getTop();
            preLeft = text_titlt_top.getLeft() - textView_mode.getLeft();
            preBottom = text_titlt_top.getBottom();
            preTop_text = text_titlt_top.getTop();
            right = text_titlt_top.getRight();
            left = text_titlt_top.getLeft();
            isFirst = false;
            L.e("AAAmode====" + left + "=" + right + "=" + preTop_text + "=" + preBottom);
        }
        if (t > preBottom - 150 && t < preBottom - 50) {
            L.e("AAAmode====", isTop + "isTop" + dt);
            if (!isTop && dt > 0) {
                L.e("AAAmode====", t + "====" + preTop_text + "==" + textView_mode.getTop() + "==" + left + "==" + textView_mode.getLeft());
                Animation animation = new TranslateAnimation(0, -preLeft, 0, -preTop);
                animation.setDuration(100);
                animation.setFillAfter(true);
                text_titlt_top.startAnimation(animation);
                isTop = true;
            }
            if (isTop && dt < 0) {
                L.e("AAAmode====", t + "====" + preTop_text + "==" + textView_mode.getTop() + "==" + left + "==" + textView_mode.getLeft());
                Animation animation = new TranslateAnimation(0, 1, 0, 1);
                animation.setDuration(100);
                animation.setFillAfter(true);
                text_titlt_top.startAnimation(animation);
                text_titlt_top.layout(left, preTop_text, right, preBottom);
                isTop = false;
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent, String macData, String uuid) {
        String action = intent.getAction();
        L.e("AAAA==========================", "=============================");
        if (action == null)
            return;
        if (intent.getAction().equals(RFStarBLEService.ACTION_WRITE_DONE)) {
            if (IContent.getInstacne().WRITEVALUE != null)
                app.manager.cubicBLEDevice.readValue(IContent.SERVERUUID_BLE, IContent.READUUID_BLE, IContent.getInstacne().WRITEVALUE);
        }  else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE)) {
            byte data[] = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
            doMusic(data);
        } else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE_READ)) {
            dismissProgressDialog();
            byte data[] = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
            L.e("AAAA", Arrays.toString(data));
            if (data.length != 0) {
                if (changeCount == GETFILESYSTEM) {
                    if (data[3] == (byte) 0x82 && data[4] == 0x03) {
                        L.e("AAAA==========================", "==========GETFILESYSTEM");
                        if (count > 0) {
                            getFolderNameInfo(data);
                        }
                        if (count == 0) {
                            L.e("AAAA==========================", "==========count == 0");
                            if (data[2] == 1 && data[5] == 0x7f) {
                                L.e("AAAA==========================", "==========data[5] == 0x7f");
                                return;
                            } else {
                                ArrayList<byte[]> folderNumArray = new ArrayList<>();
                                byte folderCnt = data[13];
                                byte folderCnt1 = data[14];
                                byte bytes[] = {folderCnt, folderCnt1};
                                int byteCount = ((bytes[0] & 0xff << 8) | (bytes[1] & 0xff));

                                for (short i = 1; i <= byteCount; i++) {
                                    byte shortbyte[] = new byte[2];
                                    shortbyte[0] = (byte) (i >> 8);
                                    shortbyte[1] = (byte) i;
                                    folderNumArray.add(shortbyte);
                                    folderNumCount = folderNumArray.size();
                                }

                                _folderArray.add(folderNumArray);
                                ArrayList<byte[]> fileCntArray = new ArrayList<>();
                                byte fileCnt = data[11];
                                byte fileCnt1 = data[12];
                                byte fileBytes[] = {fileCnt, fileCnt1};
                                int bytesCount = ((fileBytes[0] & 0xff << 8) | (fileBytes[1] & 0xff));

                                for (short i = 1; i <= bytesCount; i++) {
                                    byte fileByte[] = new byte[2];
                                    fileByte[0] = (byte) (i >> 8);
                                    fileByte[1] = (byte) i;
                                    fileCntArray.add(fileByte);
                                    L.e("fileCntArraysize", fileCntArray.size() + "===================");
                                }
                                _fileArray.add(fileCntArray);

                                byte folderSum = data[5];
                                byte folderSum1 = data[6];

                                byte folderNumByte[] = {folderSum, folderSum1};
                                _folderNumArray.add(folderNumByte);

                                cellCount = folderNumArray.size() + fileCntArray.size();
                                if (folderNumArray.size() != 0) {
                                    getFolderInfo(folderNumArray, _folderNumArray.get(0));
                                } else if (fileCntArray.size() != 0) {
                                    getFileInfo(fileCntArray, _folderNumArray.get(0));
                                } else {
                                    carMusicAdapter.notifyDataSetChanged();
                                }
                            }
                        } else if (count == folderNumCount) {
                            count = 0;
                            if (_fileArray.get(0).size() != 0) {
                                getFileInfo(_fileArray.get(0), _folderNumArray.get(0));
                            }
                        } else {
                            getFolderInfo(_folderArray.get(0), _folderNumArray.get(0));
                        }
                    }
                    if (data[3] == (byte) 0x82 && data[4] == 0x04) {
                        getFileInfoNum(data);
                        if (fileCount == _fileArray.get(0).size())
                            fileCount = 0;
                        else {
                            getFileInfo(_fileArray.get(0), _folderNumArray.get(0));
                        }
                    }
                    if (data[3] == (byte) 0x83 && data[4] == 0x0d) {
                        if (data[5] == 0x01) {
                            startFloat();
                            IContent.isModePlay = true;
                        } else if (data[2] == 1 && data[5] == 0x7f) {
                            ToastUtil.showToast(this, getResources().getString(R.string.playback_failed));
                        }
                    }
                } else if (changeCount == GETDIRECTORY) {
                    count = 0;
                    fileCount = 0;
                    goBackParentFolderInfo();
                    changeCount = GETFILESYSTEM;
                } else if (changeCount == GETPREDIRECTORY) {
                    count = 0;
                    fileCount = 0;
                    goBackPREFolderInfo();
                    changeCount = GETFILESYSTEM;
                } else if (changeCount == GETTINGFOLDERINFO) {
                    count = 0;
                    fileCount = 0;
                    getCurrentFolderInfo();
                    changeCount = GETFILESYSTEM;
                } else if (changeCount == GETTINGFILEINFO) {
                    if (data[3] == (byte) 0x83 && data[4] == (byte) 0x0d) {
                        if (data[5] == 0x01) {
                            if (fileCount == _fileArray.get(0).size())
                                fileCount = 0;
                            else {
                                getFileInfo(_fileArray.get(0), _folderNumArray.get(0));
                            }
                            changeCount = GETFILESYSTEM;
                        } else if (data[2] == 1 && data[5] == (byte) 0x7f) {
                            ToastUtil.showToast(this, getResources().getString(R.string.playback_failed));
                        }
                    } else {
                        getFileInfoNum(data);
                        selectSong();
                    }
                }
            }
            carMusicAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onReceiveDataAvailable(String dataType, String data, TravelInfoEntity travelInfoEntity, String time) {

    }

    @Override
    public void loadData() {


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cellRowCount = position;
        L.e("=======count","=="+position);
        if (_cellArray.size() != cellCount + 1) {
            L.e("=======count","=="+position);
            if (position == 0) {
                if (_folderNumArray.size() == 0) {
                    L.e("=======count","==="+_folderNumArray.size());
                    return;
                }
                L.e("=======count","=="+position);
                byte fileByte[] = _folderNumArray.get(0);
                if (fileByte[0] == 0x00 && fileByte[1] == 0x01) {
                    changeCount = GETFILESYSTEM;
                } else {
                    changeCount = GETPREDIRECTORY;
                }
            } else if (position < (_folderArray.size())) {
                changeCount = GETTINGFOLDERINFO;
            } else {
                changeCount = GETTINGFILEINFO;
            }
        } else {
            L.e("=======count","else"+position);
            changeCount = GETFILESYSTEM;
            if (position == 0) {
                goBackPREFolderInfo();
                carMusicAdapter.notifyDataSetChanged();
            } else if (position < _folderArray.size()) {
                L.e("=======count","else"+_folderArray.size());
                getCurrentFolderInfo();
                carMusicAdapter.notifyDataSetChanged();
            } else {
                selectSong();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void updateTrack() {
        super.updateTrack();
    }

    private void getFolderInfo(ArrayList<byte[]> folderNumArray, byte b[]) {
        byte[] _byte = folderNumArray.get(count);
        byte checkSum = (byte) (0 - (9 + b[0] + b[1] + _byte[0] + _byte[1]));
        byte bytes[] = {0x55, (byte) 0xaa, 0x04, 0x02, 0x03, b[0], b[1], _byte[0], _byte[1], checkSum};

        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
        }

        L.e("=======count",count+"");
        count++;

        L.e("=======count",count+"");
    }

    private void getFileInfo(ArrayList<byte[]> fileCntArray, byte fileByte[]) {
        byte bytes[] = fileCntArray.get(fileCount);
        byte checkSum = (byte) (0 - (10 + fileByte[0] + fileByte[1] + bytes[0] + bytes[1]));
        byte _bytes[] = {0x55, (byte) 0xaa, 0x04, 0x02, 0x04, fileByte[0], fileByte[1], bytes[0], bytes[1], checkSum};
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, _bytes);
        }
        fileCount++;
    }
    private void getFolderNameInfo(byte data[]) {
        L.e("AAAA==========================", "==========getFolderNameInfo");
        if (data.length != 0) {
            int h = 0;
            byte shortFolderBytes[] = new byte[8];
            StringBuilder str = new StringBuilder("\\u0");
            StringBuilder string = new StringBuilder();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                if (i > 27 && i < (data.length - 1)) {
                    if (i % 2 == 1) {
                        String dataStr = String.format("%x", data[i] & 0xff);
                        if (dataStr.equals("0")) {
                            StringBuilder stringBuilder = new StringBuilder("");
                            StringBuilder string1 = stringBuilder.append(str).append(dataStr).append(string);
                            hexString = hexString.append(string1);
                        } else if (dataStr.length() == 1) {
                            StringBuilder stringBuilder = new StringBuilder("0");
                            StringBuilder string1 = stringBuilder.append(dataStr).append(string);
                            hexString = hexString.append(string1);

                        } else {
                            StringBuilder string1 = str.append(dataStr).append(string);
                            hexString = hexString.append(string1);
                        }
                    } else if (i % 2 == 0) {
                        String dataStr = String.format("%x", data[i] & 0xff);
                        StringBuilder sb = new StringBuilder(dataStr);
                        if (dataStr.length() == 1) {
                            StringBuilder stringBuilder = new StringBuilder("0");
                            string = stringBuilder.append(sb);
                        } else {
                            string = sb;
                        }
                    }
                } else if (i > 18 && i < 27) {
                    shortFolderBytes[h] = data[i];
                    h++;
                }
            }
            if (data[2] == 1 && data[5] == 0x7f) {
                cellCount--;
            } else {
                ArrayList<byte[]> folderNumArray = new ArrayList<>();
                byte folderCnt = data[13];
                byte folderCnt1 = data[14];
                byte bytes[] = {folderCnt, folderCnt1};
                int byteCount = ((bytes[0] & 0xff << 8) | (bytes[1] & 0xff));
                for (short i = 1; i <= byteCount; i++) {
                    byte shortbyte[] = new byte[2];
                    shortbyte[0] = (byte) (i >> 8);
                    shortbyte[1] = (byte) i;
                    folderNumArray.add(shortbyte);
                }
                logData("folderNumArray", folderNumArray);
                _folderArray.add(folderNumArray);
                ArrayList<byte[]> fileCntArray = new ArrayList<>();
                byte fileCnt = data[11];
                byte fileCnt1 = data[12];
                byte fileBytes[] = {fileCnt, fileCnt1};
                int bytesCount = ((fileBytes[0] & 0xff << 8) | (fileBytes[1] & 0xff));
                for (short i = 1; i <= bytesCount; i++) {
                    byte fileByte[] = new byte[2];
                    fileByte[0] = (byte) (i >> 8);
                    fileByte[1] = (byte) i;
                    fileCntArray.add(fileByte);
                }
                _fileArray.add(fileCntArray);
                logData("fileCntArray", fileCntArray);
                byte folderSum = data[5];
                byte folderSum1 = data[6];
                L.e("KKKKKKK", data[5] + "====" + data[6]);
                byte folderNumByte[] = {folderSum, folderSum1};
                _folderNumArray.add(folderNumByte);

                if (data[2] == data.length - 6) {
                    L.e("AAAAA", data[2] + "=" + data.length);
                    if (data.length > 27 && data[27] > 0) {
                        String unicodeStr = new String(hexString.toString());
                        L.e("GGGGGGG==3", unicodeStr);
                        // for (int i = 0; i < 2; i++) {
                        String temStr1 = unicodeStr.replace("\\u", "\\U");
                        String temStr3 = temStr1.replace("\"", "\\\"");
                        String returnStr = null;
                        L.e("GGGGGGG==3", temStr3.toString());
                        returnStr = unicode2string(temStr3);
                        L.e("GGGGGGG=", returnStr);
                        // }
                        _cellArray.add(new String(returnStr));
                    } else {
                        try {
                            String s = new String(shortFolderBytes, "ascii");
                            _cellArray.add(s);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    log("ggggg", _cellArray);
                }

            }
        }
    }

    private void getCurrentFolderInfo() {

        backCount++;
        L.e("=======count",_folderNumArray.size()+"getCurrentFolderInfo"+backCount+"==="+cellRowCount);
        byte fileByte[] = _folderNumArray.get(cellRowCount);
        L.e("=======count",Tools.byte2Hex(fileByte));
        byte checkSum = (byte) (0 - (9 + fileByte[0] + fileByte[1]));
        byte bytes[] = {0x55, (byte) 0xaa, 0x04, 0x02, 0x03, 0x00, 0x00, fileByte[0], fileByte[1], checkSum};
        _backArray.add(bytes);
        if (app.manager.cubicBLEDevice != null)
            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
        _folderArray.clear();
        _fileArray.clear();
        _folderNumArray.clear();
        _cellArray.clear();
        _cellArray.add(getResources().getString(R.string.back));
    }

    private void goBackPREFolderInfo() {
        if (_folderNumArray.size() != 0) {
            byte fileByte[] = _folderNumArray.get(0);
            if (fileByte[0] == 0x00 && fileByte[1] == 0x01) {
                L.e("=======count","首页");
            } else {
                L.e("=======count","=="+backCount);
                backCount--;
                L.e("=======count","=="+backCount);
                byte fileByte_back[] = _backArray.get(backCount);
                if (fileByte_back[7] == 0x00 && fileByte_back[8] == 0x01) {
                    _backArray.clear();
                    backCount = 0;
                    _backArray.add(fileByte_back);
                }
                if (app.manager.cubicBLEDevice != null)
                    app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, fileByte_back);
                _folderArray.clear();
                _fileArray.clear();
                _folderNumArray.clear();
                _cellArray.clear();
                _cellArray.add(getResources().getString(R.string.back));

            }
        }

    }

    private void goBackParentFolderInfo() {
        if (_folderNumArray.size() != 0) {
            byte fileByte[] = _folderNumArray.get(0);
            if (fileByte[0] == 0x00 && fileByte[1] == 0x01) {
                L.e("这就是首页");
            } else {
                _backArray.clear();
                backCount = 0;
                byte bytes[] = {0x55, (byte) 0xaa, 0x04, 0x02, 0x03, 0x00, 0x00, 0x00, 0x01, (0 - 0x0a)};
                if (app.manager.cubicBLEDevice != null)
                    app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
                _backArray.add(bytes);
                _folderArray.clear();
                _fileArray.clear();
                _folderNumArray.clear();
                _cellArray.clear();
                _cellArray.add(getResources().getString(R.string.back));

            }
        }
    }

    private void getFileInfoNum(byte data[]) {
        _fileNum.clear();
        if (data.length != 0) {
            int h = 0;
            byte shortFolderBytes[] = new byte[11];

            StringBuilder string = new StringBuilder();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                if (i > 22 && i < (data.length - 1)) {
                    StringBuilder str = new StringBuilder("\\u");
                    if (i % 2 == 0) {
                        String dataStr = String.format("%x", data[i] & 0xff);

                        if (dataStr.equals("0")) {
                            StringBuilder stringBuilder = new StringBuilder("");
                            StringBuilder string1 = stringBuilder.append(str).append(dataStr).append(dataStr).append(string);
                            hexString = hexString.append(string1);

                        } else if (dataStr.length() == 1) {
                            StringBuilder stringBuilder = new StringBuilder(0);
                            StringBuilder string1 = stringBuilder.append(dataStr).append(string);
                            hexString = hexString.append(string1);
                            L.e("GGGGGGG", string1.toString() + "===1");
                        } else {
                            L.e("GGGGGGG", "str=" + str + "dataSt=" + dataStr + "string=" + string);
                            StringBuilder string1 = str.append(dataStr).append(string);
                            hexString = hexString.append(string1);
                            L.e("GGGGGGG", hexString.toString());
                        }
                    } else if (i % 2 == 1) {
                        String dataStr = String.format("%x", data[i] & 0xff);
                        StringBuilder sb = new StringBuilder(dataStr);
                        if (dataStr.length() == 1) {
                            StringBuilder stringBuilder = new StringBuilder("0");
                            string = stringBuilder.append(sb);
                        } else {
                            string = sb;
                        }
                    }
                } else if (i >= 11 && i <= 21) {
                    shortFolderBytes[h] = data[i];
                    h++;
                }
            }
            if (data[2] == 1 && data[5] == 0x7f) {
                cellCount--;
            } else {

                byte fileSum = data[7];
                byte fileSum1 = data[8];
                byte fileBytes[] = {fileSum, fileSum1};
                int bytesCount = ((fileBytes[0] & 0xff << 8) | (fileBytes[1] & 0xff));
                for (short i = 1; i <= bytesCount; i++) {
                    byte shortbyte[] = new byte[2];
                    shortbyte[0] = (byte) (i >> 8);
                    shortbyte[1] = (byte) i;
                    _fileNum.add(shortbyte);

                }
                logData("_fileNum", _fileNum);
                if (data[2] == (data.length - 6)) {
                    if (data[22] > 0) {
                        String unicodeStr = new String(hexString.toString());
                        L.e("GGGGGGG", unicodeStr);
                        String temStr1 = unicodeStr.replace("\\u", "\\U");
                        String temStr3 = temStr1.replace("\"", "\\\"");
                        String returnStr = unicode2string(temStr3);
                        L.e("GGGGGGG", returnStr);
                        _cellArray.add(returnStr);
                    } else {
                        try {
                            String s = new String(shortFolderBytes, "utf-8");
                            _cellArray.add(s);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    log("1111111", _cellArray);
                }
            }
        }
    }

    private void selectSong() {
        byte folderByte[] = _folderNumArray.get(0);
        byte fileByte[] = _fileNum.get(cellRowCount - (_folderArray.size() ));

        byte checkSum = (byte) (0 - (20 + folderByte[0] + folderByte[1] + fileByte[0] + fileByte[1]));
        byte bytes[] = {0x55, (byte) 0xaa, 0x04, 0x03, 0x0d, folderByte[0], folderByte[1], fileByte[0], fileByte[1], checkSum};
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
        }
    }

    public static String unicode2string(String s) {
        List<String> list = new ArrayList<String>();
        String zz = "\\\\U[0-9,a-z,A-Z]{4}";
        Pattern pattern = Pattern.compile(zz);
        Matcher m = pattern.matcher(s);
        while (m.find()) {
            list.add(m.group());
        }
        for (int i = 0, j = 2; i < list.size(); i++) {
            String st = list.get(i).substring(j, j + 4);
            //将得到的数值按照16进制解析为十进制整数，再強转为字符
            char ch = (char) Integer.parseInt(st, 16);
            //用得到的字符替换编码表达式
            s = s.replace(list.get(i), String.valueOf(ch));
        }
        return s;
    }

    private void log(String tAg, ArrayList<String> stringArrayList) {
        for (int k = 0; k < stringArrayList.size(); k++) {
            L.e(tAg, stringArrayList.get(k));
        }
    }

    private void logData(String tAg, ArrayList<byte[]> bytes) {
        for (int i = 0; i < bytes.size(); i++) {
            L.e(tAg, Arrays.toString(bytes.get(i)));
        }
    }

    public class CarMusicAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return _cellArray == null ? 0 : _cellArray.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_music_item, parent, false);
                viewHolder.text_name = (TextView) convertView.findViewById(R.id.folder_name);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.folder_image);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String name = _cellArray.get(position);
            if (position == 0) {
                viewHolder.imageView.setImageResource(R.drawable.icon_file_back);
            } else if (position >= 1 && position < _folderArray.size() + 1) {
                ArrayList<byte[]> arrayList = new ArrayList<>();
                arrayList = _folderArray.get(position - 1);
                ArrayList<byte[]> arrayList1 = new ArrayList<>();
                arrayList1 = _fileArray.get(position - 1);
                if (arrayList.size() == 0 && arrayList1.size() == 0) {
                    L.e("AAA", arrayList.size() + "==" + arrayList1.size());
                    viewHolder.imageView.setImageResource(R.drawable.icon_music_car);
                } else {
                    L.e("AAA", arrayList.size() + "==" + arrayList1.size());
                    viewHolder.imageView.setImageResource(R.drawable.floder);
                }
            } else {
                viewHolder.imageView.setImageResource(R.drawable.icon_music_car);
            }
            viewHolder.text_name.setText(name);
            return convertView;
        }
    }

    class ViewHolder {
        private TextView text_name;
        private ImageView imageView;
    }
}
