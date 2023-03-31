package com.jandy.jwidget.utils;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class UtShell {

    private static final String TAG = UtShell.class.getSimpleName();

    public static String exec(String cmd, String charsetName) throws IOException {
        InputStreamReader inputStreamReader;
        Process exec = Runtime.getRuntime().exec(cmd);
        InputStream inputStream = exec.getInputStream();
        if (charsetName != null) {
            try {
                inputStreamReader = new InputStreamReader(inputStream, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                inputStreamReader = new InputStreamReader(inputStream);
            }
        } else {
            inputStreamReader = new InputStreamReader(inputStream);
        }
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                sb.append(readLine);
                sb.append("\n");
            } else {
                break;
            }
        }
        try {
            if (exec.waitFor() != 0) {
                Log.d(TAG, "exit value = " + exec.exitValue());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sb.toString().trim();
    }

    /**
     * 执行shell 命令
     * @param cmd
     * @return
     * @throws IOException
     */
    public static String exec(String cmd) throws IOException{
        return exec(cmd,null);
    }

    public static void reboot(Context context, String str) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            powerManager.reboot(str);
        } else {
            Log.d(TAG, "fail to reboot as PowerManager == null");
        }
    }


}
