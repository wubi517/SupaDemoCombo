package com.gold.kds517.supacombonewstb.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gold.kds517.supacombonewstb.R;

import java.util.Calendar;

public class RecordingDlg extends Dialog {
    private String channel_name;
    private int duration;
    private TimePickerDialog pickerDialog;
    private LinearLayout ly_start;
    private TextView txt_start;
    private CheckBox checkBox;
    private boolean is_checked = false;
    public RecordingDlg(@NonNull Context context, String name,final RecordingDlg.DialogUpdateListener listener) {
        super(context);
        this.channel_name = name;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_record);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText txt_name = (EditText)findViewById(R.id.txt_old_pass);
        final EditText txt_duration= (EditText)findViewById(R.id.txt_new_pass);
        txt_start = findViewById(R.id.txt_time);
        ly_start = findViewById(R.id.ly_start);
        checkBox = findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                is_checked = isChecked;
                if(isChecked){
                    ly_start.setVisibility(View.VISIBLE);
                }else {
                    ly_start.setVisibility(View.GONE);
                }
            }
        });
        ly_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalender = Calendar.getInstance();
                int hour = myCalender.get(Calendar.HOUR_OF_DAY);
                int minute = myCalender.get(Calendar.MINUTE);
                pickerDialog = new TimePickerDialog(context,android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                if (tp.isShown()) {
                                    myCalender.set(Calendar.HOUR_OF_DAY, sHour);
                                    myCalender.set(Calendar.MINUTE, sMinute);

                                }
                                String shour = "";
                                String smin = "";
                                if(sHour==0){
                                    shour = "00";
                                }else if(sHour<10){
                                    shour = "0"+sHour;
                                }else {
                                    shour = sHour+"";
                                }
                                if(sMinute==0){
                                    smin = "00";
                                }else if(sMinute<10){
                                    smin = "0"+sMinute;
                                }else {
                                    smin = sMinute+"";
                                }
                                txt_start.setText(shour + ":" + smin);
                            }
                        }, hour, minute, true);
                pickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                pickerDialog .show();
            }
        });
        txt_name.setText(channel_name);
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnUpdateSkipClick(RecordingDlg.this,null,0,null,is_checked);
            }
        });
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_duration.getText().toString().isEmpty()){
                    Toast.makeText(context,"Duration can not be blank",Toast.LENGTH_SHORT).show();
                    return;
                }
                channel_name = txt_name.getText().toString();
                duration = Integer.parseInt(txt_duration.getText().toString());
                listener.OnUpdateNowClick(RecordingDlg.this,channel_name,duration,txt_start.getText().toString(),is_checked);
            }
        });
    }

    public interface DialogUpdateListener {
        public void OnUpdateNowClick(Dialog dialog, String channel_name, int duration,String time,boolean is_checked);
        public void OnUpdateSkipClick(Dialog dialog,String channel_name, int duration,String time,boolean is_checked);
    }
}
