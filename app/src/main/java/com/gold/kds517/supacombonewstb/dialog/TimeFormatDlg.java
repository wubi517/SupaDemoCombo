package com.gold.kds517.supacombonewstb.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gold.kds517.supacombonewstb.R;
import com.gold.kds517.supacombonewstb.apps.Constants;
import com.gold.kds517.supacombonewstb.apps.MyApp;

/**
 * Created by RST on 3/23/2018.
 */

public class TimeFormatDlg extends Dialog implements View.OnClickListener{
    CheckBox checkBox1,checkBox2;
    Button btn_ok,btn_cancel;
    private TextView txt_header;
    private TextView txt_first_line;
    private TextView txt_second_line;
    private int format = 0;
    private String time_format;
    DialogUpdateListener listener;
    public TimeFormatDlg(@NonNull Context context,int format, final DialogUpdateListener listener) {
        super(context);
        this.listener = listener;
        this.format = format;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_time_format);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        txt_header = findViewById(R.id.search_txt);
        txt_first_line = findViewById(R.id.txt_first_line);
        txt_second_line = findViewById(R.id.txt_second_line);
        checkBox1 = findViewById(R.id.checkbox1);
        checkBox2 = findViewById(R.id.checkbox2);
        checkBox1.setOnClickListener(this);
        checkBox2.setOnClickListener(this);

        switch (format){
            case 0:
                time_format = (String) MyApp.instance.getPreference().get(Constants.TIME_FORMAT);
                if(time_format.equalsIgnoreCase("12hour")){
                    checkBox1.setChecked(true);
                    checkBox2.setChecked(false);
                }else {
                    checkBox2.setChecked(true);
                    checkBox1.setChecked(false);
                }
                break;
            case 1:
                txt_header.setText("STREAM FORMAT");
                txt_first_line.setText(".ts");
                txt_second_line.setText(".m3u8");
                time_format = (String) MyApp.instance.getPreference().get(Constants.getStreamFormat());
                if(time_format.equalsIgnoreCase("ts")){
                    checkBox1.setChecked(true);
                    checkBox2.setChecked(false);
                }else {
                    checkBox2.setChecked(true);
                    checkBox1.setChecked(false);
                }
                break;
        }

        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ok:
                switch (format){
                    case 0:
                        if(checkBox1.isChecked()){
                            MyApp.instance.getPreference().put(Constants.TIME_FORMAT,"12hour");
                        }else {
                            MyApp.instance.getPreference().put(Constants.TIME_FORMAT,"24hour");
                        }
                        break;
                    case 1:
                        if(checkBox1.isChecked()){
                            MyApp.instance.getPreference().put(Constants.getStreamFormat(),"ts");
                        }else {
                            MyApp.instance.getPreference().put(Constants.getStreamFormat(),"m3u8");
                        }
                        break;
                }
                listener.OnUpdateNowClick(TimeFormatDlg.this);
                break;
            case R.id.btn_cancel:
                listener.OnUpdateSkipClick(TimeFormatDlg.this);
                break;
            case R.id.checkbox1:
                checkBox1.setChecked(true);
                checkBox2.setChecked(false);
                break;
            case R.id.checkbox2:
                checkBox1.setChecked(false);
                checkBox2.setChecked(true);
                break;
        }
    }

    public interface DialogUpdateListener {
        public void OnUpdateNowClick(Dialog dialog);
        public void OnUpdateSkipClick(Dialog dialog);
    }
}
