package com.gold.kds517.supacombonewstb.apps;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gold.kds517.supacombonewstb.models.CategoryModel;
import com.gold.kds517.supacombonewstb.models.EPGChannel;
import com.gold.kds517.supacombonewstb.models.EPGEvent;
import com.gold.kds517.supacombonewstb.models.FullModel;
import com.gold.kds517.supacombonewstb.models.FullMovieModel;
import com.gold.kds517.supacombonewstb.models.FullSeriesModel;
import com.gold.kds517.supacombonewstb.models.MovieModel;
import com.gold.kds517.supacombonewstb.models.SeriesModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Constants {
    public static final String APP_INFO = "app_info";
    public static final String LOGIN_INFO = "login_info";
    public static final String FAV_INFO = "user_info";
    public static final String MAC_ADDRESS = "mac_addr";
    public static final String MOVIE_FAV = "movie_app";
    public static final String SERIES_FAV="series_fav";

    public static final String CHANNEL_POS = "channel_pos";
    public static final String SUB_POS ="sub_pos";
    public static final String SERIES_POS = "series_pos";
    public static final String VOD_POS = "vod_pos2";
    public static final String PIN_CODE = "pin_code";
    public static final String OSD_TIME = "osd_time";
    public static final String IS_PHONE = "is_phone";
    public static final String TIME_FORMAT = "time_format";
    private static final String STREAM_FORMAT = "stream_format";
    private static final String RECORDING_CHANNELS = "recording_channels";
    public static final String INVISIBLE_LIVE_CATEGORIES0 = "invisible_vod_categories";
    public static final String INVISIBLE_VOD_CATEGORIES0 = "invisible_live_categories";
    public static final String INVISIBLE_SERIES_CATEGORIES0 = "invisible_series_categories";
    public static SimpleDateFormat epgFormat = new SimpleDateFormat("yyyyMMddHHmmss Z");
    public static String xxx_category_id ="-1";

    public static SimpleDateFormat stampFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat clockFormat=new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat year_dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat time_format = new SimpleDateFormat("MM-dd HH:mm");
    public static long SEVER_OFFSET;
    public static String all_id = "100";
    public static String fav_id = "200";
    public static String All="All";
    public static String Favorites="Favorites";
    public static final String CURRENT_PLAYER0 = "current_player";
    private static final String EXTERNAL_PLAYER = "external_player";
    private static final String RECENT_CHANNELS="RECENT_CHANNELS";
    private static final String RECENT_MOVIES="RECENT_MOVIES";
    private static final String RECENT_SERIES="RECENT_SERIES";
    public static String  recent_id = "1000";
    public static String Recently_Viewed = "Recently Viewed";
    public static String SORT = "sort";

    public static SimpleDateFormat catchupFormat = new SimpleDateFormat("yyyy-MM-dd:HH-mm");
    public static SimpleDateFormat epg_format = new SimpleDateFormat("HH.mm a EEE MM/dd");
    public static SimpleDateFormat guide_format=new SimpleDateFormat("EEE, dd  MMM");
    public static  SimpleDateFormat date_format = new SimpleDateFormat("d MMM hh:mm a");
    public static SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM d, hh:mm");
    public static SimpleDateFormat welcome_format = new SimpleDateFormat("EEE,  dd  MMM, yyyy");

    public static void getTimeFormat(){
        String timeformat = (String) MyApp.instance.getPreference().get(Constants.TIME_FORMAT);
        if(timeformat.equalsIgnoreCase("12hour")){
            clockFormat=new SimpleDateFormat("hh:mm");
            time_format = new SimpleDateFormat("MM-dd hh:mm");
            catchupFormat = new SimpleDateFormat("yyyy-MM-dd:hh-mm");
            epg_format = new SimpleDateFormat("hh.mm a EEE MM/dd");
            date_format = new SimpleDateFormat("d MMM hh:mm a");
            dateFormat1 = new SimpleDateFormat("MMM d, hh:mm");
        }else {
            clockFormat=new SimpleDateFormat("HH:mm");
            time_format = new SimpleDateFormat("MM-dd HH:mm");
            catchupFormat = new SimpleDateFormat("yyyy-MM-dd:HH-mm");
            epg_format = new SimpleDateFormat("HH.mm a EEE MM/dd");
            date_format = new SimpleDateFormat("d MMM HH:mm a");
            dateFormat1 = new SimpleDateFormat("MMM d, HH:mm");
        }
    }
    public static int findNowEvent(List<EPGEvent> epgEvents){
        Date now = new Date();
        now.setTime(now.getTime()-SEVER_OFFSET);
        for (int i=0;i<epgEvents.size();i++){
            EPGEvent epgEvent=epgEvents.get(i);
            if (now.after(epgEvent.getStartTime()) && now.before(epgEvent.getEndTime())) {
                return i;
            }
        }
        return -1;
    }

    public static FullModel getFavFullModel(List<FullModel> fullModels){
        for (FullModel fullModel:fullModels){
            if (fullModel.getCategory_id()==Constants.fav_id)
                return fullModel;
        }
        return null;
    }

    public static FullMovieModel getFavFullMovieModel(List<FullMovieModel>fullMovieModels){
        for (FullMovieModel fullModel:fullMovieModels){
            if (fullModel.getCategory_id()==Constants.fav_id)
                return fullModel;
        }
        return null;
    }

    public static FullSeriesModel getFavFullSeries(List<FullSeriesModel>fullMovieModels){
        for (FullSeriesModel fullModel:fullMovieModels){
            if (fullModel.getCategory_id()==Constants.fav_id)
                return fullModel;
        }
        return null;
    }

    public static String getSORT(){
        return SORT+MyApp.firstServer.getValue();
    }
    public static String getStreamFormat() {
        return STREAM_FORMAT + MyApp.firstServer.getValue();
    }
    public static String getRecentChannels(){
        return RECENT_CHANNELS+MyApp.firstServer.getValue();
    }
    public static String getRecordingChannels(){
        return RECORDING_CHANNELS+MyApp.firstServer.getValue();
    }
    public static String getRecentMovies(){
        return RECENT_MOVIES+MyApp.firstServer.getValue();
    }

    public static String getCHANNEL_POS() {
        return CHANNEL_POS +MyApp.firstServer.getValue();
    }
    public static String getLoginInfo(){
        return LOGIN_INFO+MyApp.firstServer.getValue();
    }
    public static String getFavInfo(){
        return FAV_INFO+MyApp.firstServer.getValue();
    }
    public static String getMovieFav(){
        return MOVIE_FAV+MyApp.firstServer.getValue();
    }

    public static String getSeriesFav(){
        return SERIES_FAV+MyApp.firstServer.getValue();
    }

    public static String getSubPos(){
        return SUB_POS+MyApp.firstServer.getValue();
    }

    public static String getVodPos(){
        return VOD_POS+MyApp.firstServer.getValue();
    }

    public static String getSeriesPos(){
        return SERIES_POS+MyApp.firstServer.getValue();
    }

    public static String getInvisibleLiveCategories(){
        return INVISIBLE_LIVE_CATEGORIES0+MyApp.firstServer.getValue();
    }

    public static String getInvisibleVodCategories(){
        return INVISIBLE_VOD_CATEGORIES0+MyApp.firstServer.getValue();
    }

    public static String getInvisibleSeriesCategories(){
        return INVISIBLE_SERIES_CATEGORIES0+MyApp.firstServer.getValue();
    }

    public static String getCurrentPlayer(){
        return CURRENT_PLAYER0+MyApp.firstServer.getValue();
    }

    public static String getExternalPlayer(){
        return EXTERNAL_PLAYER+MyApp.firstServer.getValue();
    }

    public static String getRecentSeries(){
        return RECENT_SERIES+MyApp.firstServer.getValue();
    }

    public static FullModel getRecentFullModel(List<FullModel> fullModels){
        for (FullModel fullModel:fullModels){
            if (fullModel.getCategory_id().equals(Constants.recent_id))
                return fullModel;
        }
        return null;
    }

    public static FullMovieModel getRecentFullMovieModel(List<FullMovieModel> fullModels){
        for (FullMovieModel fullModel:fullModels){
            if (fullModel.getCategory_id().equals(Constants.recent_id))
                return fullModel;
        }
        return null;
    }

    public static FullSeriesModel getRecentFullSeriesModel(List<FullSeriesModel> fullModels){
        for (FullSeriesModel fullModel:fullModels){
            if (fullModel.getCategory_id().equals(Constants.recent_id))
                return fullModel;
        }
        return null;
    }

    public static List<String> getListStrFromListEpg(List<EPGChannel> epgChannels){
        List<String> stringList = new ArrayList<>();
        for (EPGChannel epgChannel:epgChannels){
            stringList.add(epgChannel.getName());
        }
        return stringList;
    }

    public static List<String> getListStrFromListMovie(List<MovieModel> epgChannels){
        List<String> stringList = new ArrayList<>();
        for (MovieModel epgChannel:epgChannels){
            stringList.add(epgChannel.getName());
        }
        return stringList;
    }

    public static List<String> getListStrFromListSeries(List<SeriesModel> epgChannels){
        List<String> stringList = new ArrayList<>();
        for (SeriesModel epgChannel:epgChannels){
            stringList.add(epgChannel.getName());
        }
        return stringList;
    }

    public static FullModel getAllFullModel(List<FullModel> fullModels){
        for (FullModel fullModel:fullModels){
            if (fullModel.getCategory_id().equals(Constants.all_id))
                return fullModel;
        }
        return null;
    }
    public static CategoryModel getRecentCatetory(List<CategoryModel> categoryModels){
        for (CategoryModel categoryModel:categoryModels){
            if (categoryModel.getId().equals(Constants.recent_id))
                return categoryModel;
        }
        return null;
    }

    public static void setServerTimeOffset(String my_timestamp,String server_timestamp) {
        Log.e("server_timestamp",server_timestamp);
        try {
            long my_time= Long.parseLong(my_timestamp);
            Date date_server= stampFormat.parse(server_timestamp);
            my_time=my_time*1000;
            SEVER_OFFSET=my_time-date_server.getTime();
            Log.e("offset",String.valueOf(SEVER_OFFSET));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String Offset(boolean is_12,String string){
        try {
            Date that_date=stampFormat.parse(string);
            that_date.setTime(that_date.getTime()+ Constants.SEVER_OFFSET);
            if (is_12)return clockFormat.format(that_date);
            else return clockFormat.format(that_date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String Offset(boolean is_12,Date string){
        string.setTime(string.getTime()+ Constants.SEVER_OFFSET);
        if (is_12)return clockFormat.format(string);
        else return clockFormat.format(string);
    }


    public static String GetIcon(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("i","");
        return  app_icon;
    }

    public static String GetLoginImage(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("l","");
        return  app_icon;
    }

    public static String GetMainImage(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("m","");
        return  app_icon;
    }

    public static String GetAd1(Context mcontext)
    {
        String base_url="";
        SharedPreferences serveripdetails = mcontext.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        base_url=serveripdetails.getString("d1","");
        return  base_url;
    }

    public static String GetAd2(Context mcontext)
    {
        String base_url="";
        SharedPreferences serveripdetails = mcontext.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        base_url=serveripdetails.getString("d2","");
        return  base_url;
    }
    public static String GetAutho1(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("autho1","");
        return  app_icon;
    }

    public static String GetPin4(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("four_way_screen","");
        return  app_icon;
    }

    public static String GetPin3(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("tri_screen","");
        return  app_icon;
    }

    public static String GetPin2(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("dual_screen","");
        return  app_icon;
    }
    public static String GetUrl3(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("url3","");
        return  app_icon;
    }
    public static String GetUrl2(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("url2","");
        return  app_icon;
    }
    public static String GetUrl1(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("url1","");
        return  app_icon;
    }

    public static String GetStreamFormat(Context context){
        String stream_format;
        stream_format = (String) MyApp.instance.getPreference().get(Constants.getStreamFormat());
        return stream_format;
    }

    public static String GetCorrectFormatTime(int hour,int min){
        String current_time="";
        String shour="";
        String smin ="";
        if(hour==0){
            shour = "00";
        }else if(hour<10){
            shour = "0"+hour;
        }else {
            shour = hour+"";
        }
        if(min==0){
            smin="00";
        }else if(min<10){
            smin = "0"+min;
        }else {
            smin = min+"";
        }
        current_time = shour+":"+smin;
        return current_time;
    }
}
