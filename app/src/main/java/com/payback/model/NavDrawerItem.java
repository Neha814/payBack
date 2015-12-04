package com.payback.model;

/**
 * Created by Ravi on 29/07/15.
 */
public class NavDrawerItem {
    private boolean showNotify ;
    private String title ;
    private int icon_id ;


    public NavDrawerItem() {

    }

    public NavDrawerItem( boolean showNotify, String title) {
        this . showNotify = showNotify;
        this . title = title;
    }

    public NavDrawerItem( int icon_id,String title) {
        this . title = title;
        this . icon_id = icon_id;
    }

    public boolean isShowNotify() {
        return showNotify ;
    }

    public void setShowNotify( boolean showNotify) {
        this . showNotify = showNotify;
    }

    public String getTitle() {
        return title ;
    }

    public void setTitle(String title) {
        this . title = title;
    }

    public int getIcon_id() {
        return icon_id ;
    }

    public void setIcon_id( int icon_id) {
        this . icon_id = icon_id;
    }
}
