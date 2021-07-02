package com.laxco.gardening.AdaptersModels;

/**
 * This project file is owned by DevMwarabu, johnmwarabuchone@gmail.com.
 * Created on 7/1/21. Copyright (c) 2021 DevMwarabu
 */
public class Plant {
    private String strName,strDesc,strOrigin,strHeight,strImageUrl,strCatId,strId;

    public Plant(String strName, String strDesc, String strOrigin, String strHeight, String strImageUrl, String strCatId, String strId) {
        this.strName = strName;
        this.strDesc = strDesc;
        this.strOrigin = strOrigin;
        this.strHeight = strHeight;
        this.strImageUrl = strImageUrl;
        this.strCatId = strCatId;
        this.strId = strId;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public String getStrOrigin() {
        return strOrigin;
    }

    public void setStrOrigin(String strOrigin) {
        this.strOrigin = strOrigin;
    }

    public String getStrHeight() {
        return strHeight;
    }

    public void setStrHeight(String strHeight) {
        this.strHeight = strHeight;
    }

    public String getStrImageUrl() {
        return strImageUrl;
    }

    public void setStrImageUrl(String strImageUrl) {
        this.strImageUrl = strImageUrl;
    }

    public String getStrCatId() {
        return strCatId;
    }

    public void setStrCatId(String strCatId) {
        this.strCatId = strCatId;
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }
}
