package com.findtech.threePomelos.entity;

/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/06/21
 *   desc     :
 *   version  :   V 1.0.5
 */
public class Datebean {

    private String date;

    private boolean isSelect;

    public Datebean(String date, boolean isSelect) {
        this.date = date;
        this.isSelect = isSelect;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getDate() {
        return date;
    }

    public boolean isSelect() {
        return isSelect;
    }
}
