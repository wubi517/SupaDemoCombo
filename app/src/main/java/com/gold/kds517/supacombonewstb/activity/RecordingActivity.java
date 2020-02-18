package com.gold.kds517.supacombonewstb.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gold.kds517.supacombonewstb.R;
import com.gold.kds517.supacombonewstb.adapter.RecordingListAdapter;
import com.gold.kds517.supacombonewstb.apps.Constants;
import com.gold.kds517.supacombonewstb.apps.MyApp;
import com.gold.kds517.supacombonewstb.dialog.StopRecordingDlg;
import com.gold.kds517.supacombonewstb.models.RecordingModel;
import com.gold.kds517.supacombonewstb.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordingActivity extends AppCompatActivity {
    private String path = Environment.getExternalStorageDirectory().toString()+"/DOWNLOAD_AIO_VIDEO";
    private List<RecordingModel> recordingModels;
    private List<RecordingModel> real_models;
    private boolean is_exit = true;
    private ListView recording_list;
    private RecordingListAdapter adapter;
    private TextView txt_time;
    private String title,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        recordingModels = new ArrayList<>();
        if(MyApp.instance.getPreference().get(Constants.getRecordingChannels())==null){
            is_exit = false;
        }else {
            is_exit = true;
            recordingModels = (List<RecordingModel>) MyApp.instance.getPreference().get(Constants.getRecordingChannels());
        }
        recording_list = findViewById(R.id.recording_list);
        recording_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayRecording(position);
            }
        });
        findViewById(R.id.ly_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GetRecordingList();
        txt_time = findViewById(R.id.txt_time);
        Thread myThread = null;
        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();
    }

    private void PlayRecording(int pos){
        String str_dec = "Do you want to watch this?";
        StopRecordingDlg dlg = new StopRecordingDlg(this, str_dec, new StopRecordingDlg.DialogUpdateListener() {
            @Override
            public void OnUpdateNowClick(Dialog dialog) {
                dialog.dismiss();
                title = real_models.get(pos).getName().substring(14);
                url = real_models.get(pos).getUrl();
                int external_player = 0;
                if(MyApp.instance.getPreference().get(Constants.getExternalPlayer())==null){
                    external_player = 0;
                }else {
                    external_player = (int) MyApp.instance.getPreference().get(Constants.getExternalPlayer());
                }
                switch (external_player){
                    case 0:
                        Intent intent = new Intent(RecordingActivity.this,VideoPlayActivity.class);
                        intent.putExtra("title",real_models.get(pos).getName().substring(14));
                        intent.putExtra("url",real_models.get(pos).getUrl());
                        intent.putExtra("is_recording",true);
                        startActivity(intent);
                        break;
                    case 1:
                        Utils.MXPackageInfo pkginfo = Utils.getMXPackageInfo(RecordingActivity.this);
                        if(pkginfo!=null){
                            externalMXplayer(pkginfo.packageName,pkginfo.activityName);
                        }else {
                            showExternalPlayerDialog(external_player);
                        }
                        break;
                    case 2:
                        if(Utils.getVlcPackageInfo(RecordingActivity.this)!=null){
                            externalvlcplayer();
                        }else {
                            showExternalPlayerDialog(external_player);
                        }
                        break;
                }
            }

            @Override
            public void OnUpdateSkipClick(Dialog dialog) {
                dialog.dismiss();
                File directory = new File(path);
                File[] files = directory.listFiles();
                if(files!=null && files.length>0){
                    for(int i = 0;i<files.length;i++){
                        if(files[i].getName().equalsIgnoreCase(real_models.get(pos).getName())){
                            files[i].delete();
                        }
                    }
                }
                real_models.remove(pos);
                adapter.notifyDataSetChanged();
                MyApp.instance.getPreference().put(Constants.getRecordingChannels(),real_models);
            }
        });
        dlg.show();
    }

    private void GetRecordingList(){
        real_models = new ArrayList<>();
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files!=null && files.length>0){
            Log.e("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++){
//                Log.e("Files", "FileName:" + files[i].getName());
                if(!is_exit){
                    files[i].delete();
                }else {
                    for(int j=0;j<recordingModels.size();j++){
//                        Log.e("Files2", "FileName:" + recordingModels.get(j).getName());
                        if(recordingModels.get(j).getName().equalsIgnoreCase(files[i].getName())){
//                            Log.e("Files1", "FileName:" + files[i].getName());
                            RecordingModel recordingModel = new RecordingModel();
                            recordingModel.setTime(recordingModels.get(j).getTime());
                            recordingModel.setDate(recordingModels.get(j).getDate());
                            recordingModel.setName(recordingModels.get(j).getName());
                            recordingModel.setSize(String.valueOf(files[i].length()/1024/1024));
                            recordingModel.setUrl(files[i].getAbsolutePath());
                            real_models.add(recordingModel);
                            break;
                        }
                    }
                }
            }
        }else {
            MyApp.instance.getPreference().put(Constants.getRecordingChannels(),new ArrayList<>());
        }
        adapter = new RecordingListAdapter(this,real_models);
        recording_list.setAdapter(adapter);
    }


    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void doWork() {
        runOnUiThread(() -> {
            try {
                txt_time.setText(Constants.clockFormat.format(new Date()));
            } catch (Exception e) {
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    finish();
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void externalMXplayer(String pkg_name,String act_name){
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(pkg_name);
            intent.setClassName(pkg_name, act_name);
            intent.setDataAndType(uri, "application/x-mpegURL");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }

    private void externalvlcplayer(){
        Uri uri = Uri.parse(url);
        Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
        vlcIntent.setPackage("org.videolan.vlc");
        vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
        vlcIntent.putExtra("title", title);
        vlcIntent.putExtra("from_start", true);
        vlcIntent.putExtra("position", 90000l);
        vlcIntent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
//        vlcIntent.putExtra("subtitles_location", "/sdcard/Movies/Fifty-Fifty.srt");
        startActivity(vlcIntent);
    }

    private void showExternalPlayerDialog(int external_player){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Install External Player");
        builder.setMessage("Do you want to install this player")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = null;
                    switch (external_player){
                        case 1:
                            intent= new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=com.mxtech.videoplayer.ad"));
                            break;
                        case 2:
                            intent= new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=org.videolan.vlc&hl=en_US"));
                            break;
                    }
                    startActivity(intent);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();
    }
}
