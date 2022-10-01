package com.rj.chatrj.model;

import java.util.List;

public class StandardResponse {
    private String result;
    private String info;
    private String extendedInfo;
    private String extendedInfo2;
    private List<String> infoList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getExtendedInfo() {
        return extendedInfo;
    }

    public void setExtendedInfo(String extendedInfo) {
        this.extendedInfo = extendedInfo;
    }

    public String getExtendedInfo2() {
        return extendedInfo2;
    }

    public void setExtendedInfo2(String extendedInfo2) {
        this.extendedInfo2 = extendedInfo2;
    }

    public List<String> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<String> infoList) {
        this.infoList = infoList;
    }
}
