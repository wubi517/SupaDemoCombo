package com.gold.kds517.supacombonewstb.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gold.kds517.supacombonewstb.BuildConfig;
import com.gold.kds517.supacombonewstb.MainActivity;
import com.gold.kds517.supacombonewstb.adapter.CategoryListAdapter;
import com.gold.kds517.supacombonewstb.apps.CategoryType;
import com.gold.kds517.supacombonewstb.apps.Constants;
import com.gold.kds517.supacombonewstb.apps.MyApp;
import com.gold.kds517.supacombonewstb.dialog.AccountDlg;
import com.gold.kds517.supacombonewstb.dialog.ConnectionDlg;
import com.gold.kds517.supacombonewstb.dialog.HideCategoryDlg;
import com.gold.kds517.supacombonewstb.dialog.ParentContrlDlg;
import com.gold.kds517.supacombonewstb.dialog.PinDlg;
import com.gold.kds517.supacombonewstb.dialog.PinMultiScreenDlg;
import com.gold.kds517.supacombonewstb.dialog.ReloadDlg;
import com.gold.kds517.supacombonewstb.dialog.SettingDlg;
import com.gold.kds517.supacombonewstb.dialog.TimeFormatDlg;
import com.gold.kds517.supacombonewstb.dialog.UpdateDlg;
import com.gold.kds517.supacombonewstb.listner.SimpleGestureFilter;
import com.gold.kds517.supacombonewstb.listner.SimpleGestureFilter.SimpleGestureListener;
import com.gold.kds517.supacombonewstb.R;
import com.gold.kds517.supacombonewstb.models.CategoryModel;
import com.gold.kds517.supacombonewstb.vpn.fastconnect.core.OpenConnectManagementThread;
import com.gold.kds517.supacombonewstb.vpn.fastconnect.core.OpenVpnService;
import com.gold.kds517.supacombonewstb.vpn.fastconnect.core.VPNConnector;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.gold.kds517.supacombonewstb.apps.MyApp.num_server;

public class WelcomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener,SimpleGestureListener {
    SharedPreferences serveripdetails;
    String version,app_Url;
    private SimpleGestureFilter detector;
    Context context = null;
    ImageView image_left, image_center, image_right,image_ad1,image_ad2,icon;
    ListView categroy_list;
    TextView txt_left, txt_center, txt_right, txt_date, txt_time;
    Button btn_left,btn_right;
    CategoryListAdapter categoryListAdapter;
    List<CategoryModel> categories;
    List<String > settingDatas;
    List<Integer>imageDatas;
    LinearLayout ly_center;
    private int current_position = 0;
    private int current_player = 0;
    int category_pos;
    boolean is_center = false,doubleBackToExitPressedOnce = false;

    String[] category_names;
    String[] category_ids;
    boolean[] checkedItems;
    List<String> selectedIds= new ArrayList<>();
    private VPNConnector mConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        serveripdetails = this.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        LinearLayout main_lay = findViewById(R.id.main_lay);
        Bitmap myImage = getBitmapFromURL(Constants.GetMainImage(WelcomeActivity.this));
        Drawable dr = new BitmapDrawable(myImage);
        main_lay.setBackgroundDrawable(dr);
        detector = new SimpleGestureFilter(WelcomeActivity.this, WelcomeActivity.this);
        context = this;
        MyApp.is_welcome = true;
        settingDatas = new ArrayList<>();
        settingDatas.addAll(Arrays.asList(getResources().getStringArray(R.array.setting_list)));
        imageDatas = new ArrayList<>();
        imageDatas.add(R.drawable.icon_lock);//0  parent control
        imageDatas.add(R.drawable.icon_reload);//1  reload portal
        imageDatas.add(R.drawable.icon_lock);//2   sorting method
        imageDatas.add(R.drawable.icon_lock);//3  internal players
        imageDatas.add(R.drawable.icon_lock);//4  external players
        imageDatas.add(R.drawable.icon_osd);//5   stream format
        imageDatas.add(R.drawable.icon_osd);//6   time format
        imageDatas.add(R.drawable.icon_lock);//7  hide live category
        imageDatas.add(R.drawable.icon_lock);//8  hide vod category
        imageDatas.add(R.drawable.icon_lock);//9  hide series category
        imageDatas.add(R.drawable.icon_reboot);//10  check update
        imageDatas.add(R.drawable.icon_reboot);//11  speed test
        imageDatas.add(R.drawable.icon_account);//12  user account
        imageDatas.add(R.drawable.icon_lock);//13   general setting
        imageDatas.add(R.drawable.icon_vpn);//14   vpn
        imageDatas.add(R.drawable.icon_lock);//15   clear catch
        imageDatas.add(R.drawable.icon_log_out);//16  log out
        image_ad1 = findViewById(R.id.image_ad1);
        image_ad2 = findViewById(R.id.image_ad2);
        icon = findViewById(R.id.icon);

        Picasso.with(this).load(Constants.GetIcon(this))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.icon)
                .into(icon);

        Picasso.with(this).load(Constants.GetAd1(this))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.ad1)
                .into(image_ad1);
        Picasso.with(this).load(Constants.GetAd2(this))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.ad2)
                .into(image_ad2);

//        if(!getApplicationContext().getPackageName().equalsIgnoreCase(new String(Base64.decode(new String (Base64.decode("LmdvbGQua2RTG1kdmJHUXVhMlJZMjl0TG1kdmJHUXVhMlJ6TlRFM0xuTm9hWHA2WDI1bGR3PT0=".substring(11),Base64.DEFAULT)).substring(11),Base64.DEFAULT)))){
//            return;
//        }
        ly_center = (LinearLayout)findViewById(R.id.ly_center);
        ly_center.setOnClickListener(this);
        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_time = (TextView) findViewById(R.id.txt_time);
        image_left = (ImageView) findViewById(R.id.image_left);
        image_center = (ImageView) findViewById(R.id.image_center);
        image_right = (ImageView) findViewById(R.id.image_right);
        txt_left = (TextView) findViewById(R.id.txt_left);
        txt_right = (TextView) findViewById(R.id.txt_right);
        txt_center = (TextView) findViewById(R.id.txt_center);
        categroy_list = (ListView) findViewById(R.id.category_list);
        btn_left = (Button)findViewById(R.id.btn_left);
        btn_right = (Button)findViewById(R.id.btn_right);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        categroy_list.setOnItemClickListener(this);
        Thread myThread = null;
        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();

        switch (txt_center.getText().toString().toLowerCase()) {
            case "tv":
                if (MyApp.instance.getPreference().get(Constants.getCHANNEL_POS()) == null) {
                    category_pos = 0;
                } else {
                    category_pos = (int) MyApp.instance.getPreference().get(Constants.getCHANNEL_POS());
                }
                categories = new ArrayList<>();
                getLiveFilter();
                categories = MyApp.live_categories_filter;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, categories);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
                break;
            case "tv series":
                if(MyApp.instance.getPreference().get(Constants.getSeriesPos())==null){
                    category_pos = 0;
                }else {
                    category_pos = (int)MyApp.instance.getPreference().get(Constants.getSeriesPos());
                }

                categories = new ArrayList<>();
                getSeriesFilter();
                categories = MyApp.series_categories_filter;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, categories);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
                break;
            case "video club":
                if(MyApp.instance.getPreference().get(Constants.getVodPos())==null){
                    category_pos = 0;
                }else {
                    category_pos = (int)MyApp.instance.getPreference().get(Constants.getVodPos());
                }
                categories = new ArrayList<>();
                getVodFilter();
                categories = MyApp.vod_categories_filter;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, categories);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
                break;
        }

        FullScreencall();

    }

    private void stopVPN() {
        if (mConn.service.getConnectionState() ==
                OpenConnectManagementThread.STATE_DISCONNECTED) {
            mConn.service.startReconnectActivity(this);
        } else {
            mConn.service.stopVPN();
            MyApp.is_vpn=false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_left:
                RowLeft();
                break;
            case R.id.btn_right:
                RowRight();
                break;
            case R.id.ly_center:
                if(txt_center.getText().toString().equalsIgnoreCase("setting")){
                    showSettingDlg();
                }else if(txt_center.getText().toString().equalsIgnoreCase("guide")){
                    startActivity(new Intent(WelcomeActivity.this, TvGuideActivity.class));
                }else if(txt_center.getText().toString().equalsIgnoreCase("multi")){
                    showScreenModeList();
                }else if(txt_center.getText().toString().equalsIgnoreCase("record")){
                    startActivity(new Intent(this,RecordingActivity.class));
                }
                break;
        }
    }

    private void showMultiSelection(final CategoryType categoryType) {
        int i_live=2,i_vod=2,i_series=1;
        switch (categoryType){
            case vod:
                if (MyApp.instance.getPreference().get(Constants.getInvisibleVodCategories())!=null) selectedIds=(List<String>) MyApp.instance.getPreference().get(Constants.getInvisibleVodCategories());
                category_names=new String[MyApp.vod_categories.size()-i_vod];
                category_ids=new String[MyApp.vod_categories.size()-i_vod];
                checkedItems=new boolean[category_names.length];
                for (int i=0;i<MyApp.vod_categories.size()-i_vod;i++){
                    CategoryModel categoryModel =MyApp.vod_categories.get(i+i_vod);
                    category_names[i]= categoryModel.getName();
                    category_ids[i]= categoryModel.getId();
                    checkedItems[i] = !selectedIds.contains(categoryModel.getId());
                }
                break;
            case live:
                if (MyApp.instance.getPreference().get(Constants.getInvisibleLiveCategories())!=null) selectedIds=(List<String>) MyApp.instance.getPreference().get(Constants.getInvisibleLiveCategories());
                category_names=new String[MyApp.live_categories.size()-i_live];
                category_ids=new String[MyApp.live_categories.size()-i_live];
                checkedItems=new boolean[category_names.length];
                for (int i=0;i<MyApp.live_categories.size()-i_live;i++){
                    CategoryModel categoryModel =MyApp.live_categories.get(i+i_live);
                    category_names[i]= categoryModel.getName();
                    category_ids[i]= categoryModel.getId();
                    checkedItems[i] = !selectedIds.contains(categoryModel.getId());
                }
                break;
            case series:
                if (MyApp.instance.getPreference().get(Constants.getInvisibleSeriesCategories())!=null) selectedIds=(List<String>) MyApp.instance.getPreference().get(Constants.getInvisibleSeriesCategories());
                category_names=new String[MyApp.series_categories.size()-i_series];
                category_ids=new String[MyApp.series_categories.size()-i_series];
                checkedItems=new boolean[category_names.length];
                for (int i=0;i<MyApp.series_categories.size()-i_series;i++){
                    CategoryModel categoryModel =MyApp.series_categories.get(i+i_series);
                    category_names[i]= categoryModel.getName();
                    category_ids[i]= categoryModel.getId();
                    checkedItems[i] = !selectedIds.contains(categoryModel.getId());
                }
                break;
        }
        HideCategoryDlg dlg=new HideCategoryDlg(this, category_names, checkedItems, new HideCategoryDlg.DialogSearchListener() {
            @Override
            public void OnItemClick(Dialog dialog, int position, boolean checked) {
                if (!checked){
                    if (!selectedIds.contains(category_ids[position])){
                        selectedIds.add(category_ids[position]);
                    }
                }else {
                    if (selectedIds.contains(category_ids[position])){
                        selectedIds.removeAll(Arrays.asList(category_ids[position]));
                    }
                }
            }

            @Override
            public void OnOkClick(Dialog dialog) {
                selectedIds=new ArrayList<>();
                for (int m=0;m<checkedItems.length;m++){
                    if (!checkedItems[m]) selectedIds.add(category_ids[m]);
                }
                switch (categoryType){
                    case series:
                        MyApp.instance.getPreference().put(Constants.getInvisibleSeriesCategories(),selectedIds);
                        break;
                    case live:
                        MyApp.instance.getPreference().put(Constants.getInvisibleLiveCategories(),selectedIds);
                        break;
                    case vod:
                        MyApp.instance.getPreference().put(Constants.getInvisibleVodCategories(),selectedIds);
                        break;
                }
            }

            @Override
            public void OnCancelClick(Dialog dialog) {

            }

            @Override
            public void OnSelectAllClick(Dialog dialog) {
                for(int i=0;i<checkedItems.length;i++){
                    checkedItems[i]=true;
                    selectedIds.clear();
                }
            }
        });
        dlg.show();
    }
    private void  RowLeft(){
        String center_str="";
        switch (txt_center.getText().toString().toLowerCase()) {
            case "tv":
                center_str="video club";
                break;
            case "multi":
                center_str="tv";
                break;
            case "tv series":
                center_str="multi";
                break;
            case "guide":
                center_str="tv series";
                break;
            case "setting":
                center_str="record";
                break;
            case "video club":
                center_str="setting";
                break;
            case "record":
                center_str = "guide";
                break;
        }
        changeButtons(center_str);
    }
    private void changeButtons(String center_str){
        switch (center_str) {
            case "video club":
                if(MyApp.instance.getPreference().get(Constants.getVodPos())==null){
                    category_pos = 0;
                }else {
                    category_pos = (int)MyApp.instance.getPreference().get(Constants.getVodPos());
                }
                getVodFilter();
                categories = MyApp.vod_categories_filter;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, categories);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
                image_center.setImageResource(R.drawable.video_club);
                txt_center.setText("VIDEO CLUB");
                image_left.setImageResource(R.drawable.setting);
                txt_left.setText("SETTING");
                image_right.setImageResource(R.drawable.tv_icon);
                txt_right.setText("TV");
                break;
            case "tv":
                if (MyApp.instance.getPreference().get(Constants.getCHANNEL_POS()) == null) {
                    category_pos = 0;
                } else {
                    category_pos = (int) MyApp.instance.getPreference().get(Constants.getCHANNEL_POS());
                }

                getLiveFilter();
                categories = MyApp.live_categories_filter;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, categories);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
                image_center.setImageResource(R.drawable.tv_icon);
                txt_center.setText("TV");
                image_right.setImageResource(R.drawable.multi);
                txt_right.setText("MULTI");
                image_left.setImageResource(R.drawable.video_club);
                txt_left.setText("VIDEO CLUB");
                break;
            case "multi":
                categories = new ArrayList<>();
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, categories);
                categroy_list.setAdapter(categoryListAdapter);
                image_center.setImageResource(R.drawable.multi);
                txt_center.setText("MULTI");
                image_right.setImageResource(R.drawable.tv_serires);
                txt_right.setText("TV SERIES");
                image_left.setImageResource(R.drawable.tv_icon);
                txt_left.setText("TV");
                break;
            case "tv series":
                if (MyApp.instance.getPreference().get(Constants.getSeriesPos()) == null) {
                    category_pos = 0;
                } else {
                    category_pos = (int) MyApp.instance.getPreference().get(Constants.getSeriesPos());
                }

                getSeriesFilter();
                categories = MyApp.series_categories_filter;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, categories);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
//                            image_center.setPadding(Utils.dp2px(this,85),Utils.dp2px(this,8),Utils.dp2px(this,85),Utils.dp2px(this,0));
                image_center.setImageResource(R.drawable.tv_serires);
                txt_center.setText("TV SERIES");
                image_right.setImageResource(R.drawable.guide);
                txt_right.setText("GUIDE");
                image_left.setImageResource(R.drawable.multi);
                txt_left.setText("MULTI");
                break;
            case "guide":
                categories = new ArrayList<>();
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, categories);
                categroy_list.setAdapter(categoryListAdapter);
                image_center.setImageResource(R.drawable.guide);
                txt_center.setText("GUIDE");
                image_right.setImageResource(R.drawable.record);
                txt_right.setText("RECORD");
                image_left.setImageResource(R.drawable.tv_serires);
                txt_left.setText("TV SERIES");
                break;
            case "setting":
                categories = new ArrayList<>();
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, categories);
                categroy_list.setAdapter(categoryListAdapter);
                image_center.setImageResource(R.drawable.setting);
                image_right.setImageResource(R.drawable.video_club);
                image_left.setImageResource(R.drawable.record);
                txt_center.setText("SETTING");
                txt_right.setText("VIDEO CLUB");
                txt_left.setText("RECORD");
                break;
            case "record":
                categories = new ArrayList<>();
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this,categories);
                categroy_list.setAdapter(categoryListAdapter);
                image_center.setImageResource(R.drawable.record);
                image_left.setImageResource(R.drawable.guide);
                image_right.setImageResource(R.drawable.setting);
                txt_center.setText("RECORD");
                txt_left.setText("GUIDE");
                txt_right.setText("SETTING");
                break;
        }
    }
    private void getVodFilter() {
        List<String> selectedIds=(List<String>) MyApp.instance.getPreference().get(Constants.getInvisibleVodCategories());
        MyApp.vod_categories_filter=new ArrayList<>();
        MyApp.vod_categories_filter.addAll(MyApp.vod_categories);
        MyApp.fullMovieModels_filter = new ArrayList<>();
        MyApp.fullMovieModels_filter.addAll(MyApp.fullMovieModels);
        if (selectedIds!=null && selectedIds.size()!=0) {
            for(int i=0;i<MyApp.vod_categories.size();i++){
                CategoryModel categoryModel = MyApp.vod_categories.get(i);
                for (String string:selectedIds){
                    if (string.equalsIgnoreCase(categoryModel.getId())){
                        MyApp.vod_categories_filter.remove(categoryModel);
                        MyApp.fullMovieModels_filter.remove(MyApp.fullMovieModels.get(i));
                    }
                }
            }
        }
    }

    private void getLiveFilter() {
        List<String> selectedIds=(List<String>) MyApp.instance.getPreference().get(Constants.getInvisibleLiveCategories());
        MyApp.live_categories_filter=new ArrayList<>();
        MyApp.live_categories_filter.addAll(MyApp.live_categories);
        MyApp.fullModels_filter=new ArrayList<>();
        MyApp.fullModels_filter.addAll(MyApp.fullModels);
        if (selectedIds!=null && selectedIds.size()!=0) {
            for (int i=0;i<MyApp.live_categories.size();i++){
                CategoryModel categoryModel=MyApp.live_categories.get(i);
                for (String string:selectedIds){
                    if (string.equalsIgnoreCase(categoryModel.getId())) {
                        MyApp.live_categories_filter.remove(categoryModel);
                        MyApp.fullModels_filter.remove(MyApp.fullModels.get(i));
                    }
                }
            }

        }
    }

    private void getSeriesFilter() {
        List<String> selectedIds=(List<String>) MyApp.instance.getPreference().get(Constants.getInvisibleSeriesCategories());
        MyApp.series_categories_filter=new ArrayList<>();
        MyApp.series_categories_filter.addAll(MyApp.series_categories);
        MyApp.fullSeriesModels_filter = new ArrayList<>();
        MyApp.fullSeriesModels_filter.addAll(MyApp.fullSeriesModels);
        if (selectedIds!=null && selectedIds.size()!=0) {
            for(int i=0;i<MyApp.series_categories.size();i++){
                CategoryModel categoryModel = MyApp.series_categories.get(i);
                for (String string:selectedIds){
                    if (string.equalsIgnoreCase(categoryModel.getId())){
                        MyApp.series_categories_filter.remove(categoryModel);
                        MyApp.fullSeriesModels_filter.remove(MyApp.fullSeriesModels.get(i));
                    }
                }
            }
        }
    }

    private void  RowRight(){
        String center_str="";
        switch (txt_center.getText().toString().toLowerCase()) {
            case "tv":
                center_str="multi";
                break;
            case "multi":
                center_str="tv series";
                break;
            case "tv series":
                center_str="guide";
                break;
            case "guide":
                center_str="record";
                break;
            case "setting":
                center_str="video club";
                break;
            case "video club":
                center_str="tv";
                break;
            case "record":
                center_str="setting";
                break;
        }
        changeButtons(center_str);
    }
    private void ReloadDlg(){
        ReloadDlg reloadDlg = new ReloadDlg(WelcomeActivity.this, new ReloadDlg.DialogUpdateListener() {
            @Override
            public void OnUpdateNowClick(Dialog dialog) {
                dialog.dismiss();
                startActivity(new Intent(WelcomeActivity.this,SplashActivity.class));
                finish();
            }

            @Override
            public void OnUpdateSkipClick(Dialog dialog) {
                dialog.dismiss();
            }
        });
        reloadDlg.show();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        current_player = (int) MyApp.instance.getPreference().get(Constants.getCurrentPlayer());
        switch (txt_center.getText().toString().toLowerCase()) {
            case "tv":
                category_pos = position;
                MyApp.instance.getPreference().put(Constants.getCHANNEL_POS(),category_pos);

                if(categories.get(category_pos).getName().toLowerCase().contains("adult")){
                    PinDlg pinDlg = new PinDlg(WelcomeActivity.this, new PinDlg.DlgPinListener() {
                        @Override
                        public void OnYesClick(Dialog dialog, String pin_code) {
                            String pin = (String )MyApp.instance.getPreference().get(Constants.PIN_CODE);
                            if(pin_code.equalsIgnoreCase(pin)){
                                dialog.dismiss();
                                switch (current_player){
                                    case 0:
                                        startActivity(new Intent(WelcomeActivity.this, PreviewChannelActivity.class));
                                        break;
                                    case 1:
                                        startActivity(new Intent(WelcomeActivity.this, PreviewChannelIJKActivity.class));
                                        break;
                                    case 2:
                                        startActivity(new Intent(WelcomeActivity.this, PreviewChannelExoActivity.class));
                                        break;
                                }
                            }else {
                                Toast.makeText(WelcomeActivity.this, "Your Pin code was incorrect. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void OnCancelClick(Dialog dialog, String pin_code) {
                            dialog.dismiss();
                        }
                    });
                    pinDlg.show();
                }else {
                    switch (current_player){
                        case 0:
                            startActivity(new Intent(WelcomeActivity.this, PreviewChannelActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(WelcomeActivity.this, PreviewChannelIJKActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(WelcomeActivity.this, PreviewChannelExoActivity.class));
                            break;
                    }
                }
                categoryListAdapter.selectItem(position);
                break;
            case "tv series":
                is_center = true;
                category_pos = position;
                MyApp.instance.getPreference().put(Constants.getSeriesPos(),category_pos);

                categoryListAdapter.selectItem(position);
                startActivity(new Intent(this,PreviewSeriesActivity.class));
                break;
            case "video club":
                is_center = true;
                category_pos = position;
                MyApp.instance.getPreference().put(Constants.getVodPos(),category_pos);
                categoryListAdapter.selectItem(position);
                startActivity(new Intent(this,PreviewVodActivity.class));
//                startActivity(new Intent(this,RecordingActivity.class));
                break;
        }
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                RowLeft();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                RowRight();
                break;
        }
    }

    @Override
    public void onDoubleTap() {

    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
                txt_date.setText(Constants.welcome_format.format(new Date()));
            } catch (Exception e) {
            }
        });
    }

    public void FullScreencall() {
        if(Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else  {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected void onResume(){
        MyApp.is_welcome = true;
        super.onResume();
        if(MyApp.is_vpn){
            findViewById(R.id.ly_vpn).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.ly_vpn).setVisibility(View.GONE);
        }
        mConn = new VPNConnector(this, true) {
            @Override
            public void onUpdate(OpenVpnService service) {
            }
        };
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View view = getCurrentFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    String string, string1, string2;
                    if (num_server==1){
                        string = "DO YOU WISH TO EXIT APP?";
                        string1 = "Yes";
                        string2 = "No";
                    }else {
                        string = "DO YOU WISH TO EXIT APP OR SWITCH SERVER?";
                        string1 = "EXIT";
                        string2 = "SWITCH SERVER";
                    }
                    ConnectionDlg connectionDlg = new ConnectionDlg(context, new ConnectionDlg.DialogConnectionListener() {
                        @Override
                        public void OnYesClick(Dialog dialog) {
                            dialog.dismiss();
                            stopVPN();
                            finish();
                        }

                        @Override
                        public void OnNoClick(Dialog dialog) {
                            dialog.dismiss();
                            if (num_server!=1) {
                                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                stopVPN();
                                finish();
                            }
                        }
                    }, string,string1, string2);
                    connectionDlg.show();
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    btn_left.setFocusable(false);
                    btn_right.setFocusable(false);
                    RowRight();
                    categroy_list.requestFocus();
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    btn_left.setFocusable(false);
                    btn_right.setFocusable(false);
                    RowLeft();
                    categroy_list.requestFocus();
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if(txt_center.getText().toString().equalsIgnoreCase("setting")){
                        showSettingDlg();
                    }else if(txt_center.getText().toString().equalsIgnoreCase("guide")){
                        startActivity(new Intent(this, TvGuideActivity.class));
                    }else if (txt_center.getText().toString().equalsIgnoreCase("multi")){
                        showScreenModeList();
                    }else if(txt_center.getText().toString().equalsIgnoreCase("record")){
                        startActivity(new Intent(this,RecordingActivity.class));
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(category_pos<categories.size()-1){
                        categroy_list.setSelection(category_pos);
                        category_pos++;
                        categoryListAdapter.selectItem(category_pos);
                    }else {
                        category_pos = 0;
                        categroy_list.setSelection(category_pos);
                        categoryListAdapter.selectItem(category_pos);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if(category_pos>0){
                        categroy_list.setSelection(category_pos);
                        category_pos--;
                        categoryListAdapter.selectItem(category_pos);
                    }else {
                        category_pos = categories.size()-1;
                        category_pos--;
                        categoryListAdapter.selectItem(category_pos);
                        return true;
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void showSettingDlg() {
        SettingDlg settingDlg = new SettingDlg(WelcomeActivity.this, settingDatas,imageDatas, (dialog, position) -> {
            switch (position){
                case 0://Parent control
                    ParentContrlDlg dlg = new ParentContrlDlg(WelcomeActivity.this, new ParentContrlDlg.DialogUpdateListener() {
                        @Override
                        public void OnUpdateNowClick(Dialog dialog, int code) {
                            if(code==1){
                                dialog.dismiss();
                            }
                        }
                        @Override
                        public void OnUpdateSkipClick(Dialog dialog, int code) {
                            dialog.dismiss();
                        }
                    });
                    dlg.show();
                    break;
                case 1://Reload portal
                    ReloadDlg();
                    break;
                case 2://Sorting Method
                    showSortingDlg();
                    break;
                case 3://Internal players
                    showInternalPlayers();
                    break;
                case 4://External Players
                    showExternalPlayers();
                    break;
                case 5://Stream Format
                    showStreamFormatDlg();
                    break;
                case 6://Time Format
                    showTimeFormatDlg();
                    break;
                case 7://Hide Live category
                    //TODO
                    showMultiSelection(CategoryType.live);
                    break;
                case 8: // Hide Vod Category
                    //TODO
                    showMultiSelection(CategoryType.vod);
                    break;
                case 9://hide series category
                    //TODO
                    showMultiSelection(CategoryType.series);
                    break;
                case 10://check update
                    getRespond1();
                    break;
                case 11://speed test
                    startActivity(new Intent(this,InternetSpeedActivity.class));
                    break;
                case 12://user account
                    AccountDlg accountDlg = new AccountDlg(WelcomeActivity.this, dialog1 -> {
                    });
                    accountDlg.show();
                    break;
                case 13://general setting
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    break;
                case 14://vpn
                    startActivity(new Intent(WelcomeActivity.this,VpnActivity.class));
                    dialog.dismiss();
                    break;
                case 15://clear catch
                    MyApp.deleteCache(this);
                    break;
                case 16://log out
                    MyApp.instance.getPreference().remove(Constants.getLoginInfo());
                    MyApp.instance.getPreference().remove(Constants.getCHANNEL_POS());
                    MyApp.instance.getPreference().remove(Constants.getSeriesPos());
                    MyApp.instance.getPreference().remove(Constants.getVodPos());
                    MyApp.instance.getPreference().remove(Constants.getRecentChannels());

                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
                break;
            }
        });
        settingDlg.show();
    }

    private void showTimeFormatDlg(){
        TimeFormatDlg timeFormatDlg = new TimeFormatDlg(this,0, new TimeFormatDlg.DialogUpdateListener() {
            @Override
            public void OnUpdateNowClick(Dialog dialog) {
                Constants.getTimeFormat();
                dialog.dismiss();
            }

            @Override
            public void OnUpdateSkipClick(Dialog dialog) {
                Constants.getTimeFormat();
                dialog.dismiss();
            }
        });
        timeFormatDlg.show();
    }

    private void showStreamFormatDlg(){
        TimeFormatDlg timeFormatDlg = new TimeFormatDlg(this, 1, new TimeFormatDlg.DialogUpdateListener() {
            @Override
            public void OnUpdateNowClick(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void OnUpdateSkipClick(Dialog dialog) {
                dialog.dismiss();
            }
        });
        timeFormatDlg.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
//        Log.e("last_pos",String .valueOf(channel_list.getLastVisiblePosition()));
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
    private int selected_item;
    private void showScreenModeList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select One Mode");

        String[] screen_mode_list = {"Four Way Screen", "Three Way Screen", "Dual Screen"};

        builder.setSingleChoiceItems(screen_mode_list, 0,
                (dialog, which) -> selected_item = which);

        builder.setPositiveButton("OK", (dialog, which) -> {
            final Intent intent=new Intent(WelcomeActivity.this, MultiScreenActivity.class);
            if (selected_item==0) {
                boolean remember_four=false;
                if (MyApp.instance.getPreference().get("remember_four_screen")!=null) remember_four=(boolean) MyApp.instance.getPreference().get("remember_four_screen");
                if (!remember_four){
                    PinMultiScreenDlg pinMultiScreenDlg=new PinMultiScreenDlg(WelcomeActivity.this, new PinMultiScreenDlg.DlgPinListener() {
                        @Override
                        public void OnYesClick(Dialog dialog, String pin_code, boolean is_remember) {
                            if(!pin_code.equals(Constants.GetPin4(WelcomeActivity.this))) {
                                Toast.makeText(WelcomeActivity.this,"Invalid password!",Toast.LENGTH_LONG).show();
                                return;
                            }
                            MyApp.instance.getPreference().put("remember_four_screen",is_remember);
                            intent.putExtra("num_screen",4);
                            startActivity(intent);
                        }

                        @Override
                        public void OnCancelClick(Dialog dialog, String pin_code) {

                        }
                    },remember_four);
                    pinMultiScreenDlg.show();
                }else {
                    intent.putExtra("num_screen",4);
                    startActivity(intent);
                }
            }
            else if (selected_item==1) {
                boolean remember_three=false;
                if (MyApp.instance.getPreference().get("remember_three_screen")!=null) remember_three=(boolean) MyApp.instance.getPreference().get("remember_three_screen");
                if (!remember_three){
                    PinMultiScreenDlg pinMultiScreenDlg=new PinMultiScreenDlg(WelcomeActivity.this, new PinMultiScreenDlg.DlgPinListener() {
                        @Override
                        public void OnYesClick(Dialog dialog, String pin_code, boolean is_remember) {
                            if(!pin_code.equals(Constants.GetPin3(WelcomeActivity.this))) {
                                Toast.makeText(WelcomeActivity.this,"Invalid password!",Toast.LENGTH_LONG).show();
                                return;
                            }
                            MyApp.instance.getPreference().put("remember_three_screen",is_remember);
                            intent.putExtra("num_screen",3);
                            startActivity(intent);
                        }

                        @Override
                        public void OnCancelClick(Dialog dialog, String pin_code) {

                        }
                    },remember_three);
                    pinMultiScreenDlg.show();
                }else {
                    intent.putExtra("num_screen",3);
                    startActivity(intent);
                }
            }
            else {
                boolean remember_two=false;
                if (MyApp.instance.getPreference().get("remember_two_screen")!=null) remember_two=(boolean) MyApp.instance.getPreference().get("remember_two_screen");
                if (!remember_two){
                    PinMultiScreenDlg pinMultiScreenDlg=new PinMultiScreenDlg(WelcomeActivity.this, new PinMultiScreenDlg.DlgPinListener() {
                        @Override
                        public void OnYesClick(Dialog dialog, String pin_code, boolean is_remember) {
                            if(!pin_code.equals(Constants.GetPin2(WelcomeActivity.this))) {
                                Toast.makeText(WelcomeActivity.this,"Invalid password!",Toast.LENGTH_LONG).show();
                                return;
                            }
                            MyApp.instance.getPreference().put("remember_two_screen",is_remember);
                            intent.putExtra("num_screen",2);
                            startActivity(intent);
                        }

                        @Override
                        public void OnCancelClick(Dialog dialog, String pin_code) {

                        }
                    },remember_two);
                    pinMultiScreenDlg.show();
                }else {
                    intent.putExtra("num_screen",2);
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showInternalPlayers(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Player Option");
        String[] screen_mode_list = {"VLC Player", "IJK Player", "Exo Player"};
        if (MyApp.instance.getPreference().get(Constants.getCurrentPlayer())!=null)
            current_position = (int) MyApp.instance.getPreference().get(Constants.getCurrentPlayer());
        else current_position = 0;
        builder.setSingleChoiceItems(screen_mode_list, current_position,
                (dialog, which) -> current_position =which);
        builder.setPositiveButton("OK", (dialog, which) ->
                MyApp.instance.getPreference().put(Constants.getCurrentPlayer(), current_position));
        builder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showExternalPlayers(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle("Player Option");
        String[] player_mode_list = {"Disable","MX Player","Vlc Player"};
        if(MyApp.instance.getPreference().get(Constants.getExternalPlayer())!=null)
            current_position = (int) MyApp.instance.getPreference().get(Constants.getExternalPlayer());
        else current_position = 0;
        builder.setSingleChoiceItems(player_mode_list,current_position,
                ((dialog, which) -> current_position=which));
        builder.setPositiveButton("OK",((dialog, which) ->
                MyApp.instance.getPreference().put(Constants.getExternalPlayer(),current_position)));
        builder.setNegativeButton("Cancel",null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSortingDlg(){
        if (MyApp.instance.getPreference().get(Constants.getSORT())!=null)
            current_position = (int) MyApp.instance.getPreference().get(Constants.getSORT());
        else current_position = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Sorting Method");
        String[] screen_mode_list = {"Sort by Number", "Sort by Added", "Sort by Name"};
        builder.setSingleChoiceItems(screen_mode_list, current_position, (dialog, which) -> current_position =which);
        builder.setPositiveButton("OK", (dialog, which) ->
                MyApp.instance.getPreference().put(Constants.getSORT(), current_position));
        builder.setNegativeButton("Cancel", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void getRespond1(){
        String key = "";
        switch (MyApp.firstServer){
            case first:
                key=Constants.GetUrl1(this);
                break;
            case second:
                key=Constants.GetUrl2(this);
                break;
            case third:
                key=Constants.GetUrl3(this);
                break;
        }
        try {
            String response = MyApp.instance.getIptvclient().login(key);
            Log.e("response",response);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data_obj = object.getJSONObject("data");
                    String url = (String) data_obj.get("url");
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        if (url.endsWith("/")){
                            url = url;
                        }else {
                            url = url+"/";
                        }
                        JSONArray array = data_obj.getJSONArray("image_urls");
                        SharedPreferences.Editor server_editor = serveripdetails.edit();
                        server_editor.putString("ip", url);
                        version = (String )data_obj.get("version");
                        app_Url = (String )data_obj.get("app_url");
                        String dual_screen=data_obj.getString("pin_2");
                        String tri_screen=data_obj.getString("pin_3");
                        String four_way_screen=data_obj.getString("pin_4");
                        server_editor.putString("dual_screen",dual_screen);
                        server_editor.putString("tri_screen",tri_screen);
                        server_editor.putString("four_way_screen",four_way_screen);
                        server_editor.putString("i",(String) array.get(0));
                        server_editor.putString("m",(String )array.get(1));
                        server_editor.putString("l",(String) array.get(2));
                        server_editor.putString("d1",(String)array.get(3));
                        server_editor.putString("d2",(String )array.get(4));
                        server_editor.apply();
                        if(MyApp.instance.getPreference().get(Constants.getSORT())==null){
                            MyApp.instance.getPreference().put(Constants.getSORT(),0);
                        }
                        if(MyApp.instance.getPreference().get(Constants.getCurrentPlayer())==null){
                            MyApp.instance.getPreference().put(Constants.getCurrentPlayer(),0);
                        }
                        getUpdate();
                    } else {
                        Toast.makeText(this, "Invalid Server URL!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Server Error!", Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getUpdate(){
        MyApp.instance.versionCheck();
        double code = 0.0;
        try {
            code = Double.parseDouble(version);
        }catch (Exception e){
            code = 0.0;
        }
        MyApp.instance.loadVersion();
        double app_vs = Double.parseDouble(MyApp.version_name);
        if (code > app_vs) {
            UpdateDlg updateDlg = new UpdateDlg(this, new UpdateDlg.DialogUpdateListener() {
                @Override
                public void OnUpdateNowClick(Dialog dialog) {
                    dialog.dismiss();
                    new versionUpdate().execute(app_Url);
                }
                @Override
                public void OnUpdateSkipClick(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            updateDlg.show();
        }else {
            Toast.makeText(this,"Current version is lastest version.",Toast.LENGTH_SHORT).show();
        }
    }
    class versionUpdate extends AsyncTask<String, Integer, String> {
        ProgressDialog mProgressDialog;
        File file;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(WelcomeActivity.this);
            mProgressDialog.setMessage(getResources().getString(R.string.request_download));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                String destination = Environment.getExternalStorageDirectory() + "/";
                String fileName = "supanewui.apk";
                destination += fileName;
                final Uri uri = Uri.parse("file://" + destination);
                file = new File(destination);
                if(file.exists()){
                    file.delete();
                }
                output = new FileOutputStream(file, false);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(getApplicationContext(),"Update Failed",Toast.LENGTH_LONG).show();
            } else
                startInstall(file);
        }
    }

    private void startInstall(File fileName) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID + ".provider",fileName), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(fileName), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}
