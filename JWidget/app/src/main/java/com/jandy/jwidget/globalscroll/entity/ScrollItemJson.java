package com.jandy.jwidget.globalscroll.entity;

import androidx.annotation.Keep;

@Keep
public class ScrollItemJson {
    private int initId; //初始排序
    private int sortId;
    private String name;
    private String iconUrl;
    private String iconRectUrl; //方形图
    private String tiledName;  //栏目名称
    private String versionId="";  //版本号
    private String ext;
    private int type = -1; //-1正常 0锁住 1定时
    private int appActive = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public int getInitId() {
        return initId;
    }

    public void setInitId(int initId) {
        this.initId = initId;
    }

    public int getAppActive() {
        return appActive;
    }

    public void setAppActive(int appActive) {
        if (appActive > 0) {
            this.appActive = appActive > this.appActive ? appActive : this.appActive;
            return;
        }
        this.appActive = 0;
    }

    public String getIconRectUrl() {
        return iconRectUrl;
    }

    public void setIconRectUrl(String iconRectUrl) {
        this.iconRectUrl = iconRectUrl;
    }

    public String getTiledName() {
        return tiledName;
    }

    public void setTiledName(String tiledName) {
        this.tiledName = tiledName;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }
}
