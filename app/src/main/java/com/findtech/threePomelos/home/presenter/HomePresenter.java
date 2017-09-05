package com.findtech.threePomelos.home.presenter;

import com.findtech.threePomelos.home.view.IViewMainHome;
import com.findtech.threePomelos.home.model.HomeModel;
import com.findtech.threePomelos.home.model.HomeModelImp;

/**
 * Created by Alex on 2017/5/3.
 * <pre>
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/03
 *     desc    ：
 *     version ： 1.0
 */
public class HomePresenter {

    HomeModel homeModel ;
    IViewMainHome iViewMainHome;

    public HomePresenter(IViewMainHome iViewMainHome) {
        this.iViewMainHome = iViewMainHome;
        homeModel = new HomeModelImp();
    }

    public void installModelData(){
        iViewMainHome.refreshUI(homeModel.getViewPagerFragments());

    }


}
