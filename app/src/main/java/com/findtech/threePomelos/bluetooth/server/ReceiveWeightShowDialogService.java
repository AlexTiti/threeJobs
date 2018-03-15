package com.findtech.threePomelos.bluetooth.server;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 接受推车重量并显示对话框
 * @author Alex
 */
public class ReceiveWeightShowDialogService extends Service {

    private NetWorkRequest netWorkRequest;
    private OperateDBUtils operateDBUtils;
    private int mStartId;
    private CustomDialog customDialog;
    private ProgressDialog progressDialog;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        netWorkRequest = new NetWorkRequest(getApplicationContext());
        operateDBUtils = new OperateDBUtils(getApplicationContext());

        initProgressDialog();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        mStartId = startId;
        if (intent.getExtras() != null) {
            if (customDialog != null && customDialog.isShowing()) {
                stopTimer();
                customDialog.dismiss();
                customDialog = null;
            }
            startTimer();
            final String weight = intent.getExtras().getString("weight");
            String currentTime = intent.getExtras().getString("date");
            L.e("currentTime==",weight+"==="+currentTime);
            final Date currentDate = Tools.getDateFromTimeStr(currentTime);
            final CustomDialog.Builder builder = new CustomDialog.Builder(getApplicationContext());
            builder.setTitle(getString(R.string.app_name));
            builder.setShowBindInfo(getString(R.string.xliff_weight_service, weight) );
            builder.setShowButton(true);
            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    saveWeightDataToServer(weight, currentDate);
                    stopTimer();
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton(getString(R.string.cancle),
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            stopTimer();
                            stopSelf(mStartId);
                        }
                    });
            customDialog = builder.create();
            customDialog.getWindow().setType(
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            customDialog.show();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void saveWeightDataToServer(final String weight, final Date date) {
        netWorkRequest.isExistTheTimeOnTable(NetWorkRequest.BABY_WEIGHT, date, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        addWeightToServer(weight, date);
                    } else if (list.size() == 1) {

                        updateWeightToServer(weight, date);
                    } else if (list.size() > 1) {
                        AVObject.deleteAllInBackground(list, new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    addWeightToServer(weight, date);
                                } else {
                                    Toast.makeText(getApplicationContext(),getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void addWeightToServer(final String weight, final Date date) {
        netWorkRequest.addWeightAndTimeToServer(weight, date, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    operateDBUtils.saveWeightToDB(weight, Tools.getTimeFromDate(date));
//                    delegate.onReceiveDataAvailable(RequestUtils.WEIGHT, weight, null, Tools.getTimeFromDate(date));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                }
                stopSelf(mStartId);
            }
        });
    }

    private void updateWeightToServer(final String weight, final Date date) {

        netWorkRequest.updateWeightAndTimeToServer(weight, date, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    operateDBUtils.saveWeightToDB(weight, Tools.getTimeFromDate(date));

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                }
                stopSelf(mStartId);
            }
        });
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.save_message));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void startTimer() {
        if ( mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (customDialog != null && customDialog.isShowing()) {
                        customDialog.dismiss();
                        stopSelf(mStartId);
                    }
                }
            };
        }
        if (mTimer != null && mTimerTask != null) {
            mTimer.schedule(mTimerTask, 10000);
        }
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
}
