package com.gold.kds517.supacombonewstb.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.FileReader;

public class Utils {
    private static final String 	PACKAGE_NAME		= "org.videolan.vlc";
    private static final String 	PLAYBACK_ACTIVITY	= "org.videolan.vlc.gui.video.VideoPlayerActivity";
    private static final String 	PACKAGE_NAME_PRO 		= "com.mxtech.videoplayer.pro";
    private static final String 	PACKAGE_NAME_AD 		= "com.mxtech.videoplayer.ad";
    private static final String 	PLAYBACK_ACTIVITY_PRO	= "com.mxtech.videoplayer.ActivityScreen";
    private static final String 	PLAYBACK_ACTIVITY_AD	= "com.mxtech.videoplayer.ad.ActivityScreen";
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static String getPhoneMac(Context context) {
        try {
            String s = getEthMacfromEfuse("/sys/class/efuse/mac");
            if (s == null) {
                s = getEthMacfromEfuse("/sys/class/net/eth0/address");
            }
            if (s == null) {
                final Class<?> forName = Class.forName("android.os.SystemProperties");
                s = (String)forName.getMethod("get", String.class).invoke(forName, "ubootenv.var.ethaddr");
                if (s == null) {
                    final WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    if (wifiManager != null) {
                        s = wifiManager.getConnectionInfo().getMacAddress();
                    }
                }
            }
            if (s == null) {
                return "c44eac0561b5";
            }
            return s.replace(":", "");
        }
        catch (Exception ex) {
            return "000000000099";
        }
    }
    private static String getEthMacfromEfuse(final String s) {
        String s2;
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(s), 12);
            try {
                final String line = bufferedReader.readLine();
                bufferedReader.close();
                s2 = line;
            }
            finally {
                bufferedReader.close();
            }
        }
        catch (Exception ex) {
            s2 = null;
        }
        return s2;
    }


    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        if(hours > 0){
            finalTimerString = hours + ":";
        }
        if(seconds < 10){secondsString = "0" + seconds;
        }else{secondsString = "" + seconds;}
        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        percentage =(((double)currentSeconds)/totalSeconds) * 100;
        return percentage.intValue();
    }

    public static int progressToTimer(int progress, long totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);
        return currentDuration * 1000;
    }

    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    private static class VLCPackageInfo
    {
        final String packageName;
        final String activityName;

        VLCPackageInfo( String packageName, String activityName ) {
            this.packageName = packageName;
            this.activityName = activityName;
        }
    }

    private static final VLCPackageInfo[] PACKAGES = {
            new VLCPackageInfo(PACKAGE_NAME, PLAYBACK_ACTIVITY)
    };

    public static VLCPackageInfo getVlcPackageInfo(Context context)
    {
        for( VLCPackageInfo pkg: PACKAGES )
        {
            try
            {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(pkg.packageName, 0);
                if( info.enabled )
                    return pkg;
                else
                    return null;
            }
            catch(PackageManager.NameNotFoundException ex)
            {
                return null;
//                Utils.toaster(this,"VLC player does not exist.");
//                Log.v( TAG, "MX Player package `" + pkg.packageName + "` does not exist." );
            }
        }

        return null;
    }

    public static class MXPackageInfo
    {
        public final String packageName;
        public final String activityName;

        MXPackageInfo( String packageName, String activityName ) {
            this.packageName = packageName;
            this.activityName = activityName;
        }
    }

    public static final MXPackageInfo[] PACKAGES1 = {
            new MXPackageInfo(PACKAGE_NAME_PRO, PLAYBACK_ACTIVITY_PRO),
            new MXPackageInfo(PACKAGE_NAME_AD, PLAYBACK_ACTIVITY_AD),
    };

    /**
     * @return null if any MX Player packages not exist.
     */
    public static MXPackageInfo getMXPackageInfo(Context context)
    {
        for( MXPackageInfo pkg: PACKAGES1 )
        {
            try
            {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(pkg.packageName, 0);
                if(info.enabled ){
                    return pkg;
                }
            }
            catch(PackageManager.NameNotFoundException ex)
            {
            }
        }

        return null;
    }
}
