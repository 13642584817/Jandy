package com.jandy.jwidget.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class UtDateFormat {


    public static final String SDF_TYPE_1 = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String SDF_TYPE_2 = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String SDF_TYPE_3 = "yyyyMMdd";
    public static final String SDF_TYPE_5 = "HH:mm";
    public static final String SDF_TYPE_6 = "yyyy年MM月dd日";
    public static final String SDF_TYPE_7 = "yyyy-MM";
    public static final String SDF_TYPE_8 = "MM月dd日";
    public static final String SDF_TYPE_9 = "MM-dd";
    public static final String SDF_TYPE_10 = "yyyy年MM月";
    public static final String SDF_TYPE_11 = "yyyy-MM-dd HH:mm:ss";
    public static final String SDF_TYPE_12 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String SDF_TYPE_13 = "HH:mm:ss";
    public static final String SDF_TYPE_14 = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String SDF_TYPE_15 = "yyyy.MM";
    public static final String SDF_TYPE_16 = "yyyy-MM-dd";
    public static final String SDF_TYPE_17 = "M月d日";

    /**
     * 将时间毫秒转换成 ##：##格式
     *
     * @return
     */
    public static String formatTime(long milliseconds) {
        if (milliseconds <= 0 || milliseconds >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = milliseconds / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 打印当前时间
     *
     * @return
     */
    public static String printfCurTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SDF_TYPE_11, Locale.getDefault());
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }


    /**
     * 获取时间撮
     *
     * @return
     */
    public static long getTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取时间撮
     *
     * @return
     */
    public static long getTimeStampSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * param2 > param1 返回true
     *
     * @param param1
     * @param param2
     * @param sdf
     * @return
     */
    public static boolean isDateExpire(String param1, String param2, String sdf) {
        if (TextUtils.isEmpty(param1) || TextUtils.isEmpty(param2) || TextUtils.isEmpty(sdf))
            return false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sdf, Locale.getDefault());
        try {
            long date1 = simpleDateFormat.parse(param1).getTime();
            long date2 = simpleDateFormat.parse(param2).getTime();
            return date2 > date1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否超过某个时间
     *
     * @param dateStr
     * @param seconds
     * @param sdf
     * @return true 当前时间超过设定的时间
     */
    public static boolean isDateExpire(String dateStr, int seconds, String sdf) {
        if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(sdf)) return false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sdf, Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.SECOND, seconds);
            return new Date().after(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 时间是否过期，如果(参数时间) < (系统时间)返回true，表示时间已经超过了参数时间
     *
     * @param dateStr
     * @param sdf
     * @return
     */
    public static boolean isDateExpire(String dateStr, String sdf) {
        if (TextUtils.isEmpty(dateStr)) return true;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sdf, Locale.getDefault());
        try {
            long dateMillis = simpleDateFormat.parse(dateStr).getTime();
            long currentMillis = System.currentTimeMillis();
            return dateMillis < currentMillis;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 参数时间再当前时间多少秒之后
     *
     * @param dateStr
     * @param sdf
     * @return 返回参数是秒，返回值为负数表示参数时间在当前时间之前发生
     */
    public static int dateAfterSeconds(String dateStr, String sdf) {
        if (TextUtils.isEmpty(dateStr)) return -1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sdf, Locale.getDefault());
        try {
            long dateSecond = simpleDateFormat.parse(dateStr).getTime() / 1000;
            long currentSecond = System.currentTimeMillis() / 1000;
            return (int) (dateSecond - currentSecond);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 参数时间再当前时间多少分钟之后
     *
     * @param dateStr
     * @param sdf
     * @return 返回参数是分钟，返回值为负数表示参数时间在当前时间之前发生
     */
    public static int dateAfterMinutes(String dateStr, String sdf) {
        if (TextUtils.isEmpty(dateStr)) return -1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sdf, Locale.getDefault());
        try {
            long dateMinute = simpleDateFormat.parse(dateStr).getTime() / 1000 / 60;
            long currentMinute = System.currentTimeMillis() / 1000 / 60;
            return (int) (dateMinute - currentMinute);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 格式化显示时间
     *
     * @param dateStr 参数
     * @param orgSdf  原始格式模板
     * @param tagSdf  目标格式模板
     * @return
     */
    public static String formatDate(String dateStr, String orgSdf, String tagSdf) {
        if (TextUtils.isEmpty(dateStr)) return "";
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(orgSdf, Locale.getDefault());
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(tagSdf, Locale.getDefault());
        try {
            Date date = simpleDateFormat1.parse(dateStr);
            return simpleDateFormat2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 格式化HH:mm
     *
     * @param currentMillis
     * @return
     */
    public static String formatHHMM(long currentMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return simpleDateFormat.format(new Date(currentMillis));
    }

    /**
     * 格式化HH:mm
     *
     * @param value
     * @return
     */
    public static String formatHHMM(String value) {
        if (TextUtils.isEmpty(value)) return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SDF_TYPE_1, Locale.getDefault());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(value);
            return simpleDateFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 格式化HH:mm:ss
     *
     * @param value
     * @return
     */
    public static String formatHHMMSS(String value) {
        if (TextUtils.isEmpty(value)) return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SDF_TYPE_1, Locale.getDefault());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(value);
            return simpleDateFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 格式化yyyy-MM-dd HH:mm:ss
     *
     * @param value
     * @return
     */
    public static String formatTimeStr(String value) {
        if (TextUtils.isEmpty(value)) return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SDF_TYPE_12, Locale.getDefault());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(SDF_TYPE_11, Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(value);
            return simpleDateFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 格式化HH:mm:ss
     *
     * @param currentMills
     * @return
     */
    public static String formatHHMMSS(long currentMills) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(new Date(currentMills));
    }

    /**
     * 格式化yyyy-MM-dd
     *
     * @param currentMills
     * @return
     */
    public static String formatyyyyMMdd(long currentMills) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SDF_TYPE_16, Locale.getDefault());
        return simpleDateFormat.format(new Date(currentMills));
    }

    /**
     * 格式化时间
     *
     * @param currentMills
     * @param sdf
     * @return
     */
    public static String formatDate(long currentMills, String sdf) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sdf, Locale.getDefault());
        return simpleDateFormat.format(new Date(currentMills));
    }

    /**
     * 格式化时间
     *
     * @param date
     * @param sdf
     * @return
     */
    public static String formatDate(Date date, String sdf) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sdf, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    /**
     * 获取时间撮
     *
     * @param date
     * @param sdf
     * @return
     */
    public static long formatTime(String date, String sdf) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(sdf, Locale.getDefault());
        try {
            return simpleDateFormat.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取时间撮
     *
     * @return
     */
    public static long formatTimeType1(String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SDF_TYPE_1, Locale.getDefault());
        try {
            return simpleDateFormat.parse(dateStr).getTime();
        } catch (Exception ignore) {
        }
        return 0;
    }

    /**
     * 获取今天0点的时间
     */
    public static long getTodayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTimeInMillis();
    }

    /**
     * 获取星期
     *
     * @param currentMills
     * @return
     */
    public static String getWeek(long currentMills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentMills));
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case 1:
                return "星期天";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            default:
                return "星期六";
        }
    }

    /**
     * 获取星期
     *
     * @param currentMills
     * @return
     */
    public static String getWeekZ(long currentMills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentMills));
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            default:
                return "周六";
        }
    }

    /**
     * 英文星期
     *
     * @param currentMills
     * @return
     */
    public static String getWeekEn(long currentMills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentMills));
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thur";
            case 6:
                return "Fri";
            default:
                return "Sat";
        }
    }

    /**
     * 获取当天是一周的第几天
     *
     * @return
     */
    public static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek - 1;
    }

    /**
     * 获取年月日
     *
     * @param currentMills
     * @return
     */
    public static String formatYYYYMMDD(long currentMills) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return simpleDateFormat.format(new Date(currentMills));
    }

    /**
     * 获取月日
     *
     * @param currentMills
     * @return
     */
    public static String formatMMDD(long currentMills) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        return simpleDateFormat.format(new Date(currentMills));
    }

    /**
     * 获取月日
     *
     * @param currentMills
     * @return
     */
    public static String formatMMDD_Z(long currentMills) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        return simpleDateFormat.format(new Date(currentMills));
    }

    /**
     * 间隔时间显示规则
     *
     * @param dateStr
     * @return
     */
    public static String formatInterval(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SDF_TYPE_1, Locale.getDefault());
        Date createDate;
        try {
            createDate = simpleDateFormat.parse(dateStr);
            Date nowDate = new Date();
            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTime(nowDate);
            Calendar createCalendar = Calendar.getInstance();
            createCalendar.setTime(createDate);

            int year = nowCalendar.get(Calendar.YEAR) - createCalendar.get(Calendar.YEAR);
            if (year > 0) return formatDate(dateStr, SDF_TYPE_1, SDF_TYPE_7);

            int month = nowCalendar.get(Calendar.MONTH) - createCalendar.get(Calendar.MONTH);
            if (month > 0) return formatDate(dateStr, SDF_TYPE_1, SDF_TYPE_9);


            int day = nowCalendar.get(Calendar.DAY_OF_MONTH) - createCalendar.get(Calendar.DAY_OF_MONTH);

            if (day >= 3) return formatDate(dateStr, SDF_TYPE_1, SDF_TYPE_9);

            if (day >= 2) return "前天" + formatDate(dateStr, SDF_TYPE_1, SDF_TYPE_5);

            if (day >= 1) return "昨天" + formatDate(dateStr, SDF_TYPE_1, SDF_TYPE_5);

            long second = (nowDate.getTime() - createDate.getTime()) / 1000;
            if (second / 60 > 5) return "今天" + formatDate(dateStr, SDF_TYPE_1, SDF_TYPE_5);

            return "刚刚";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String formatInterval(long millis) {
        if (millis == 0) return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SDF_TYPE_1, Locale.getDefault());
        return formatInterval(simpleDateFormat.format(new Date(millis)));
    }


    /**
     * 时间转换成毫秒
     *
     * @param ts
     * @param format
     * @return
     */
    public static long formatTimeMillis(String ts, String format) {
        if (TextUtils.isEmpty(ts)) return 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            long t = simpleDateFormat.parse(ts).getTime();
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     *
     */
    public static long formatTYPE16TimeMillis(String ts) {
        if (TextUtils.isEmpty(ts)) return 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SDF_TYPE_16, Locale.getDefault());
        try {
            long t = simpleDateFormat.parse(ts).getTime();
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 格式化生日
     *
     * @param birthDayStr
     * @return
     */
    public static String formatBirthday(String birthDayStr, String format) {

        if (birthDayStr.equals("null") || birthDayStr.isEmpty())
            return "";
        long currentTime = new Date().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());

        long birthTime = 0;
        try {
            birthTime = simpleDateFormat.parse(birthDayStr).getTime();
            if (birthTime > currentTime) {
                return "还没出生哦";
            }

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeInMillis(currentTime);
            int currentYear = currentCalendar.get(Calendar.YEAR);
            int currentMonth = currentCalendar.get(Calendar.MONTH);
            int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTimeInMillis(birthTime);
            int birthYear = birthCalendar.get(Calendar.YEAR);
            int birthMonth = birthCalendar.get(Calendar.MONTH);
            int birthDay = birthCalendar.get(Calendar.DAY_OF_MONTH);

            int year = 0;
            int month = 0;
            int day = 0;

            day = currentDay - birthDay + 1;
            if (day <= 0) {
                day = getMonthOfDays(currentYear, currentMonth) - birthDay + currentDay + 1;
                month = -1;
            }

            month += currentMonth - birthMonth;
            if (month < 0) {
                month = 12 + month;
                year = -1;
            }

            year += currentYear - birthYear;

            if (year > 0) {
                if (month > 0) {
                    return year + "岁" + month + "个月";
                } else {
                    return year + "岁";
                }
            }
            if (month > 0) {
                return month + "个月";
            } else {
                return (day - 1) + "天";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static int getMonthOfDays(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.get(Calendar.DATE);
    }


    public static String formatTimeDes(long milliseconds) {
        if (milliseconds <= 0 || milliseconds >= 24 * 60 * 60 * 1000) {
            return "";
        }
        long totalSeconds = milliseconds / 1000;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return formatter.format("%d小时%02d分钟后", hours, minutes).toString();
        } else {
            return formatter.format("%02d分钟后", minutes).toString();
        }
    }


    /**
     * Return whether it is today.
     *
     * @param millis1
     * @param millis2
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSameDay(final long millis1, final long millis2) {
        return formatYYYYMMDD(millis1).equals(formatYYYYMMDD(millis2));
    }


}
