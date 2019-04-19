
package com.example.bingduoduo.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.bingduoduo.R;
import com.example.bingduoduo.base.BaseApplication;
import com.example.bingduoduo.base.BaseToolbarActivity;
import com.example.bingduoduo.presenter.IEditorActivityView;
import com.example.bingduoduo.utils.Check;
import com.example.bingduoduo.utils.FileUtils;
import com.example.bingduoduo.utils.SystemBarUtils;
import com.example.bingduoduo.utils.Toast;

import java.io.File;

import butterknife.Bind;


public class EditorActivity extends BaseToolbarActivity implements IEditorActivityView, View.OnClickListener {
    public static final String SHARED_ELEMENT_NAME = "SHARED_ELEMENT_NAME";
    public static final String SHARED_ELEMENT_COLOR_NAME = "SHARED_ELEMENT_COLOR_NAME";
    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_Folder = "folder";

    private EditorFragment mEditorFragment;

    private String mName;
    private String currentFilePath;


    @Bind(R.id.pager)
    protected ViewPager mViewPager;
    // private TabIconView mTabIconView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_editor;
    }

    @Override
    public void onCreateAfter(Bundle savedInstanceState) {
        ViewCompat.setTransitionName(mViewPager, SHARED_ELEMENT_NAME);
        getIntentData();
        mEditorFragment = EditorFragment.getInstance(currentFilePath);
        initViewPager();
    }

    @Override
    public void initData() {

    }

    private void initViewPager() {
        mViewPager.setAdapter(new EditFragmentAdapter(getSupportFragmentManager()));
    }

    @Override
    protected void initStatusBar() {
        SystemBarUtils.tintStatusBar(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void otherSuccess(int flag) {
    }

    @Override
    public void onFailure(int errorCode, String message, int flag) {
        switch (flag) {
            default:
                BaseApplication.showSnackbar(getWindow().getDecorView(), message);
                break;
        }
    }


    @Override
    public void showWait(String message, boolean canBack, int flag) {
        super.showWaitDialog(message, canBack);
    }

    @Override
    public void hideWait(int flag) {
        super.hideWaitDialog();
    }

    @Override
    public void onNameChange(@NonNull String name) {
        this.mName = name;
    }

    private final int SYSTEM_GALLERY = 1;

    @Override
    public void onClick(View v) {
        if (R.id.id_shortcut_insert_photo == v.getId()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);// Pick an item fromthe
            intent.setType("image/*");// 从所有图片中进行选择
            startActivityForResult(intent, SYSTEM_GALLERY);
            return;
        } else if (R.id.id_shortcut_insert_link == v.getId()) {
            //插入链接
            insertLink();
            return;
        } else if (R.id.id_shortcut_grid == v.getId()) {
            //插入表格
            insertTable();
            return;
        }
        //点击事件分发
        mEditorFragment.getPerformEditable().onClick(v);
    }



    private class EditFragmentAdapter extends FragmentPagerAdapter {

        public EditFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mEditorFragment;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }



    private void getIntentData() {
        Intent intent = this.getIntent();
        int flags = intent.getFlags();
        if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
            if (intent.getAction() != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
                if (SCHEME_FILE.equals(intent.getScheme())) {
                    //文件
                    String type = getIntent().getType();
                    // mImportingUri=file:///storage/emulated/0/Vlog.xml
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri = intent.getData();

                    if (uri != null && SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
                        //这是一个文件
                        currentFilePath = FileUtils.uri2FilePath(getBaseContext(), uri);
                    }
                }
            }
        }
    }

    @NonNull
    @Override
    protected String getTitleString() {
        return "";
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    private MenuItem mActionOtherOperate;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor_act, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mEditorFragment.onBackPressed()) {
                    return true;
                }
                break;
            case R.id.action_edit://编辑
                mViewPager.setCurrentItem(0, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == SYSTEM_GALLERY) {
            Uri uri = data.getData();
            String[] pojo = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.managedQuery(uri, pojo, null, null, null);
            if (cursor != null) {
//                    ContentResolver cr = this.getContentResolver();
                int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(colunm_index);
                //以上代码获取图片路径
                Uri.fromFile(new File(path));//Uri.decode(imageUri.toString())
                mEditorFragment.getPerformEditable().perform(R.id.id_shortcut_insert_photo, Uri.fromFile(new File(path)));
            } else {
                Toast.showShort(this, "图片处理失败");
            }
        }

    }


    /**
     * 插入表格
     */
    private void insertTable() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.view_common_input_table_view, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("插入表格")
                .setView(rootView)
                .show();

        TextInputLayout rowNumberHint = (TextInputLayout) rootView.findViewById(R.id.rowNumberHint);
        TextInputLayout columnNumberHint = (TextInputLayout) rootView.findViewById(R.id.columnNumberHint);
        EditText rowNumber = (EditText) rootView.findViewById(R.id.rowNumber);
        EditText columnNumber = (EditText) rootView.findViewById(R.id.columnNumber);


        rootView.findViewById(R.id.sure).setOnClickListener(v -> {
            String rowNumberStr = rowNumber.getText().toString().trim();
            String columnNumberStr = columnNumber.getText().toString().trim();

            if (Check.isEmpty(rowNumberStr)) {
                rowNumberHint.setError("不能为空");
                return;
            }
            if (Check.isEmpty(columnNumberStr)) {
                columnNumberHint.setError("不能为空");
                return;
            }


            if (rowNumberHint.isErrorEnabled())
                rowNumberHint.setErrorEnabled(false);
            if (columnNumberHint.isErrorEnabled())
                columnNumberHint.setErrorEnabled(false);

            mEditorFragment.getPerformEditable().perform(R.id.id_shortcut_grid, Integer.parseInt(rowNumberStr), Integer.parseInt(columnNumberStr));
            dialog.dismiss();
        });

        rootView.findViewById(R.id.cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * 插入链接
     */
    private void insertLink() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.view_common_input_link_view, null);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogTheme)
                .setTitle("插入链接")
                .setView(rootView)
                .show();

        TextInputLayout titleHint = (TextInputLayout) rootView.findViewById(R.id.inputNameHint);
        TextInputLayout linkHint = (TextInputLayout) rootView.findViewById(R.id.inputHint);
        EditText title = (EditText) rootView.findViewById(R.id.name);
        EditText link = (EditText) rootView.findViewById(R.id.text);


        rootView.findViewById(R.id.sure).setOnClickListener(v -> {
            String titleStr = title.getText().toString().trim();
            String linkStr = link.getText().toString().trim();

            if (Check.isEmpty(titleStr)) {
                titleHint.setError("不能为空");
                return;
            }
            if (Check.isEmpty(linkStr)) {
                linkHint.setError("不能为空");
                return;
            }

            if (titleHint.isErrorEnabled())
                titleHint.setErrorEnabled(false);
            if (linkHint.isErrorEnabled())
                linkHint.setErrorEnabled(false);

            mEditorFragment.getPerformEditable().perform(R.id.id_shortcut_insert_link, titleStr, linkStr);
            dialog.dismiss();
        });

        rootView.findViewById(R.id.cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

}
