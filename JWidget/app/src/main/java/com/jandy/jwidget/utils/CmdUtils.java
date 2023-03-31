package com.jandy.jwidget.utils;





import java.io.IOException;


public class CmdUtils {
    private static final String CMD_CAT_POWER_SUPPLY_AC_ONLINE = "cat /sys/class/power_supply/ac/online"; //获取是否插入AC
    private static final String CMD_CAT_POWER_SUPPLY_USB_ONLINE = "cat /sys/class/power_supply/usb/online"; //获取是否插入USB
    private static final String CMD_CAT_POWER_SUPPLY_BATTERY_LEVEL = "cat /sys/class/power_supply/battery/capacity"; //获取电池电量
    public static final String CMD_FIRMWARE_VERSION = "getprop ro.version.software"; //获取软件版本
    public static final String CMD_VERSION_TYPE = "getprop ro.version.type"; //获取版本类型
    public static final String CMD_GET_POWERSAVE = "getprop sys.powersave.mode"; //获取省电模式开关
    private static final String CMD_CPU_INFO = "cat /proc/cpuinfo";
    private static final String CMD_MEMORY_INFO = "cat /proc/meminfo";
    private static final String CMD_EMMC_SIZE = "cat /sys/class/block/mmcblk0/size"; //获取Emmc大小

    /**
     * 获取是否插电
     *
     * @return
     */
    public static int getIsCharging() {
        try {
            return (Integer.parseInt(UtShell.exec(CMD_CAT_POWER_SUPPLY_AC_ONLINE).trim())
                    | Integer.parseInt(UtShell.exec(CMD_CAT_POWER_SUPPLY_USB_ONLINE).trim()));
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取电池电量
     *
     * @return
     */
    public static int getBatteryLevel() {
        try {
            return Integer.parseInt(UtShell.exec(CMD_CAT_POWER_SUPPLY_BATTERY_LEVEL).trim());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取省电模式开关
     *
     * @return
     */
    public static String getPowersaveMode() {
        try {
            return UtShell.exec(CMD_GET_POWERSAVE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取Cpu信息
     *
     * @return
     */
    public static String getCpuInfo() {
        try {
            return UtShell.exec(CMD_CPU_INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取MemoryInfo
     *
     * @return
     */
    public static String getMemoryInfo() {
        try {
            return UtShell.exec(CMD_MEMORY_INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 进程句柄
     *
     * @return
     */
    public static String getSysProcessFd() {
        try {
            String cmd = "ls -la /proc/" + android.os.Process.myPid() + "/fd";
            return UtShell.exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * proc status
     *
     * @return
     */
    public static String getProcStatus() {
        try {
            String cmd = "cat /proc/" + android.os.Process.myPid() + "/status";
            return UtShell.exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getTopInfo() {
        try {
            String cmd = "top -n 1 -t -m 10";
            return UtShell.exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取软件版本
     *
     * @return
     */
    public static String getSoftwareVersion() {
        try {
            return UtShell.exec(CMD_FIRMWARE_VERSION);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取版本类型
     *
     * @return
     */
    public static String getVersionType() {
        try {
            return UtShell.exec(CMD_VERSION_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取emmc大小
     *
     * @return
     */
    public static float getEmmcSize() {
        try {
            return Float.parseFloat(UtShell.exec(CMD_EMMC_SIZE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    /**
     * 创建adb日志文件
     */
    public static String createAdbFlag() {
        try {
            return UtShell.exec("touch /sdcard/force_debug.flag");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 创建adb日志文件
     */
    public static String deleteAdbFlag() {
        try {
            return UtShell.exec("rm /sdcard/force_debug.flag");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设备的产品名称
     * m1/m1c/m1o/m1w/
     * @return
     */
    public static String deviceProductMode(){
        try {
            return UtShell.exec("getprop ro.product.ntt.mode");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * m1c设备的产品名称
     * @return 0,1是wifi版，2,3是4g版
     */
    public static String m1cDeviceMode(){
        try {
            return UtShell.exec("getprop ro.nwy.board.id");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 是否是m1w
     * @return
     */
    public static boolean isM1W(){
        try {
            return "m1w".equals(UtShell.exec("getprop ro.product.ntt.mode"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否是m1o
     * @return
     */
    public static boolean isM1O(){
        try {
            return "m1o".equals(UtShell.exec("getprop ro.product.ntt.mode"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
