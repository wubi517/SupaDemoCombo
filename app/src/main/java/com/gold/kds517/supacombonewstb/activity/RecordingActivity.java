package com.gold.kds517.supacombonewstb.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
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
                Intent intent = new Intent(RecordingActivity.this,VideoPlayActivity.class);
                intent.putExtra("title",real_models.get(pos).getName().substring(14));
                intent.putExtra("url",real_models.get(pos).getUrl());
                intent.putExtra("is_recording",true);
                startActivity(intent);
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
}
