

package com.example.bingduoduo.base;

import android.content.res.ColorStateList;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

import butterknife.Bind;
import com.example.bingduoduo.R;
import com.example.bingduoduo.utils.SystemBarUtils;


public abstract class BaseDrawerLayoutActivity extends BaseToolbarActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.id_drawer_layout)
    protected DrawerLayout mDrawerLayout;
    @Bind(R.id.id_navigation_view)
    protected NavigationView mNavigationView;

    protected DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    protected NavigationView getNavigationView() {
        return mNavigationView;
    }

    @Override
    protected void init() {
        super.init();
        initDrawer();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    protected void initStatusBar() {
        SystemBarUtils.tintStatusBarForDrawer(this, mDrawerLayout, getResources().getColor(R.color.colorPrimary));
    }
    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();


        mNavigationView.setNavigationItemSelectedListener(this);

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{{android.R.attr.state_checked, android.R.attr.state_enabled},
                        {android.R.attr.state_enabled},
                        {}},
                new int[]{BaseApplication.color(R.color.colorPrimary), BaseApplication.color(R.color.colorSecondaryText), 0xffDCDDDD});
        mNavigationView.setItemIconTintList(colorStateList);//设置图标的颜色变化
        mNavigationView.setItemTextColor(colorStateList);//设置item的颜色变化
    }
    @Override
    public void onBackPressed() {//返回按钮
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
