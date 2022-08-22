package com.shuvo.ttit.onegpx.arrayLists;

import android.location.Location;

import java.util.ArrayList;

public class ArrrayFile {

    private ArrayList<Location> myLatlng;
    private String listName;
    private String descc;

    public ArrrayFile(ArrayList<Location> latLngs, String lm, String descc) {
        this.myLatlng = latLngs;
        this.listName = lm;
        this.descc = descc;
    }

    public String getDescc() {
        return descc;
    }

    public void setDescc(String descc) {
        this.descc = descc;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public ArrayList<Location> getMyLatlng() {
        return myLatlng;
    }

    public void setMyLatlng(ArrayList<Location> myLatlng) {
        this.myLatlng = myLatlng;
    }
}
