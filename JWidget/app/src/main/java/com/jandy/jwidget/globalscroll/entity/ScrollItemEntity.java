package com.jandy.jwidget.globalscroll.entity;


import android.graphics.drawable.Drawable;


public class ScrollItemEntity {

    private int initId = 0;  //初始排序
    private int sortId = 0;//排序id
    private String name = "";
    private Drawable icon;
    private String iconUrl = "";
    private String iconRectUrl = "";
    private String versionId="";  //版本号
    private String tiledName = ""; //平铺模式，栏目名称
    private String ext = "";
    private int type = -1; //-1正常/已安装 0锁住 1定时
    private int appActive = 0; //0已安装 1未安装 2开始下载 3开始安装
    private boolean isCollect = false; //是否被收藏

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getTiledName() {
        return tiledName;
    }

    public void setTiledName(String tiledName) {
        this.tiledName = tiledName;
    }

    public String getIconRectUrl() {
        return iconRectUrl;
    }

    public void setIconRectUrl(String iconRectUrl) {
        this.iconRectUrl = iconRectUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
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

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }
}
