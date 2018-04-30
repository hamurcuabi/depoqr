package com.emrehmrc.depoqr;

import android.support.v4.app.FragmentManager;

public class ExpNavigationManage implements ExpManagerInterface {

    private static ExpNavigationManage mInstance;
    private FragmentManager mFragmentManager;
    private ExpMenuMain mainActivity;

    public static ExpNavigationManage getmInstance(ExpMenuMain mainActivity) {

        if(mInstance==null)mInstance=new ExpNavigationManage();
        mInstance.configure(mainActivity);
        return mInstance;
    }

    private void configure(ExpMenuMain mainActivity) {
        mainActivity=mainActivity;
        mFragmentManager=mainActivity.getSupportFragmentManager();
    }

    @Override
    public void showFragment(String title) {

        title.toCharArray();
    }
}
