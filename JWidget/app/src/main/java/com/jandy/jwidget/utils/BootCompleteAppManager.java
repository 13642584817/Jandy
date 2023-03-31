package com.jandy.jwidget.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;


import java.util.ArrayList;
import java.util.List;


public class BootCompleteAppManager {

    private static final String TAG = BootCompleteAppManager.class.getSimpleName();

    private static BootCompleteAppManager instance;
    private Context mContext;
    private final Object mLocked = new Object();

    public static void init(Context context) {
        if (instance == null) {
            synchronized (BootCompleteAppManager.class) {
                if (instance == null) {
                    instance = new BootCompleteAppManager(context);
                }
            }
        }
    }

    public static BootCompleteAppManager getInstance() {
        if (instance == null) {
            throw new Error("BootCompleteAppManager未初始化");
        }
        return instance;
    }

    private BootCompleteAppManager(Context context) {
        this.mContext = context;
    }


    /**
     * 应用开机自启动使能
     *
     * @param enable
     * @param pkg
     * @param receiveName
     */
    public void enableBootCompletedApp(boolean enable, String pkg, String receiveName) {
        try {
            final ComponentName receiver = new ComponentName(pkg, receiveName);
            int state = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            mContext.getPackageManager().setComponentEnabledSetting(receiver, state, PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 注册了广播接收器
     *
     * @param packageName
     * @return
     */
    private boolean isPackageHasBootCompletePermissionReceivers(String packageName) {
        try {
            PackageInfo packInfo = mContext.getPackageManager().getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
            String name = getPackageBootReceiverName(packInfo);
            if (!TextUtils.isEmpty(name)) return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 排除一些包名
     *
     * @param packageName
     * @return
     */
    private boolean isExcludePackageName(String packageName) {
        if (!TextUtils.isEmpty(packageName)
                && !packageName.contains("com.spr")
                && !packageName.contains("com.android")
                && !packageName.contains("com.baidu.input_benewtech")
                && !packageName.contains("android")
                && !packageName.contains("com.neo")
                && !packageName.contains("com.adups")
                && !packageName.contains("com.ntt.core.service")
                && !packageName.contains("plugin.sprd")
                && !packageName.contains("com.emoji")
                && !packageName.contains("com.redteamobile.monitor")
        ) {
            return true;
        }
        return false;
    }

    /**
     * 获取开机启动广播接受者的Component Name
     * @param pack
     * @return
     */
    private String getPackageBootReceiverName(PackageInfo pack) {
        if (pack == null) return null;
        try {
            if (pack.receivers == null || pack.receivers.length <= 0) {
                return null;
            }
            for (ActivityInfo info : pack.receivers) {
                if ("android.permission.RECEIVE_BOOT_COMPLETED".equals(info.permission)) {
                    return info.name;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
