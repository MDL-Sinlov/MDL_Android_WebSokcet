package mdl.sinlov.android.websocket.app.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Locale;

/**
 * get device info
 * <pre>
 *     sinlov
 *
 *     /\__/\
 *    /`    '\
 *  ≈≈≈ 0  0 ≈≈≈ Hello world!
 *    \  --  /
 *   /        \
 *  /          \
 * |            |
 *  \  ||  ||  /
 *   \_oo__oo_/≡≡≡≡≡≡≡≡o
 *
 * </pre>
 * Created by "sinlov" on 16/8/26.
 */
public class DevicesInfo {
    private static DevicesInfo instance;
    private DevicesInfo(){}
    private static Activity mActivity;

    public static synchronized DevicesInfo getInstance(Activity activity){
        if (instance == null){
            instance = new DevicesInfo();
        }
        mActivity = activity;
        return instance;
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

//    public String getLocalMacAddress() {
//        WifiManager wifi = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = wifi.getConnectionInfo();
//        if(info.getMacAddress() == null){
//            return "";
//        }
//        return info.getMacAddress();
//    }

    public String getLocalMacAddress() {
        String macAddress = "48:59:29:f2:0d:94";
        try {
            WifiManager wifiMgr = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
            if (null != info) {
                if (!TextUtils.isEmpty(info.getMacAddress()))
                    macAddress = info.getMacAddress();
                else
                    return macAddress;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return macAddress;
        }
        return macAddress;
    }



    public String getImei() {
        try{
            TelephonyManager mTm = (TelephonyManager)mActivity.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = mTm.getDeviceId();
            if(imei !=null ){
                return imei;
            }
            String imsi = mTm.getSubscriberId();
            if(imsi !=null){
                return imsi;
            }
            String androidid = getAndroidId();
            if(androidid !=null){
                return androidid;
            }
            return getUUID();
        }catch (Exception e){
            e.printStackTrace();
            return getUUID();
        }
    }

    public String getUUID() {
        return java.util.UUID.randomUUID().toString();
    }

    public String getSystemVersion(){
        return android.os.Build.VERSION.RELEASE;
    }

    public String getDevicesName(){
        return android.os.Build.MODEL;
    }

    public String getLanguage(){
        Locale locale = mActivity.getResources().getConfiguration().locale;
        return locale.getLanguage();
    }

    public String getPackageName(){
        return mActivity.getPackageName();
    }

    public int getGameVersion(){
        try {
            int versionCode = mActivity.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            return versionCode;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    private static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }


    public String getAndroidId(){
        String androidId = Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }


    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }

}
