package com.findtech.threePomelos.music.model;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   author   :   Administrator
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/05/15
 *   desc     :
 *   version  :   V 1.0.5
 */
public interface PresentIn<T> {

    void setData(ArrayList<T> arrayList);

    void onError();

}
