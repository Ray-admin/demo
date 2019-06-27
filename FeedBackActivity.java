package com.octopus.wishTravel.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.octopus.wishTravel.R;
import com.octopus.wishTravel.adapter.FeedBackImageAdapter;
import com.octopus.wishTravel.base.BaseActivity;
import com.octopus.wishTravel.utils.LQRPhotoSelectUtils;
import com.octopus.wishTravel.utils.PopupWindowUtils;
import com.octopus.wishTravel.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 意见反馈activity
 */

public class FeedBackActivity extends BaseActivity {
    private static final String TAG = "FeedBackActivity";

    //选择图片请求码
    private static final int REQUEST_FILE_PERMISSION_CODE = 0;

    //拍照请求码
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 1;


    //显示图片 Grid
    @BindView(R.id.gv_upload_picture)
    GridView gvUploadPicture;

    //反馈的内容
    @BindView(R.id.et_feedback_content)
    EditText etFeedbackContent;

    //总字数
    @BindView(R.id.tv_total_length)
    TextView tvTotalLength;

    //功能异常选项
    @BindView(R.id.rb_abnormal_function)
    RadioButton rbAbnormalFunction;

    //产品建议
    @BindView(R.id.rb_product_suggestion)
    RadioButton rbProductSuggestion;

    //产品投诉
    @BindView(R.id.rb_product_complaint)
    RadioButton rbProductComplaint;
    //其他问题
    @BindView(R.id.rb_other_problems)
    RadioButton rbOtherProblems;
    //选择类型
    @BindView(R.id.rg_select_type)
    RadioGroup rgSelectType;

    //手机号
    @BindView(R.id.et_phone_number)
    EditText etPhoneNumber;

    //提交反馈
    @BindView(R.id.btn_submit_feed_back)
    Button btnSubmitFeedBack;


    //拍照or选择相册pop
    private PopupWindow setPicturePop;


    //Grid Adapter
    private FeedBackImageAdapter adapter;

    //图片路径list
    private List<String> filePathList = new ArrayList<>();

    private LQRPhotoSelectUtils lqrPhotoSelectUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initView() {
        setTitleBar(Utils.getString(R.string.ID_FEED_BACK),
                getResources().getDrawable(R.mipmap.icon_indicator_mywish_rest_blackback),
                getResources().getColor(R.color.color_white));
        etFeedbackContent.setHintTextColor(getResources().getColor(R.color.color_edit_hint));
    }

    @Override
    protected void initData() {

        adapter = new FeedBackImageAdapter(this, filePathList);
        gvUploadPicture.setAdapter(adapter);
        Log.e(TAG, "initData: " + etFeedbackContent.getText().length());
        tvTotalLength.setText(String.valueOf(etFeedbackContent.getText().length()));


        // 创建LQRPhotoSelectUtils（一个Activity对应一个LQRPhotoSelectUtils）
        lqrPhotoSelectUtils = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                // 当拍照或从图库选取图片成功后回调
                updatePhotoPath(Utils.getRealFilePath(FeedBackActivity.this, outputUri));
            }
        }, false);
    }

    @Override
    protected void addListener() {
        /**
         * gridView item 点击事件
         *
         * 判断条件为  当size = 0 时 说明没有添加过图片，直接显示pop
         *
         * else中的条件为 当点击的i ==  size 时再显示pop  避免出现点击添加过的图片显示pop 但是图片不更新的情况
         */
        gvUploadPicture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (filePathList.size() == 0) {
                    showGetPicturePop();
                } else {
                    Log.e(TAG, "onItemClick:   i -----  " + i);
                    Log.e(TAG, "onItemClick:   total ----- " + filePathList.size() + 1);
                    //当i==size 时 则是说明点击的是添加图片按钮
                    if (i == filePathList.size()) {
                        showGetPicturePop();
                    }
                }


            }
        });


        /**意见内容输入框监听，计算字数*/
        etFeedbackContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e(TAG, "afterTextChanged: " + editable.length());
                tvTotalLength.setText(String.valueOf(editable.length()));
            }
        });


    }


    /**
     * 显示选择照片or拍照Pop
     */
    private void showGetPicturePop() {
        PopupWindowUtils.showSelectPicturePop(FeedBackActivity.this, gvUploadPicture, new PopupWindowUtils.OnSelectPictureListener() {
            @Override
            public void onSelectPicture() {
                checkReadFilePermission();
            }

            @Override
            public void onTakePhoto() {
                checkCameraPermission();
            }
        });

    }


    /**
     * 检查读取SD卡权限
     */

    private void checkReadFilePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FILE_PERMISSION_CODE);
            } else {
                Log.e(TAG, "checkCameraPermission: " + "已申请权限");
                selectPicture();
            }
        } else {
            selectPicture();
        }
    }


    /**
     * 检查摄像头权限
     */
    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION_CODE);
            } else {
                Log.e(TAG, "checkCameraPermission: " + "已申请权限");
                takePhoto();
            }
        } else {
            takePhoto();
        }
    }


    /**
     * 选择图片
     */
    private void selectPicture() {
        lqrPhotoSelectUtils.selectPhoto();
    }


    /**
     * 拍照
     */
    private void takePhoto() {
        lqrPhotoSelectUtils.takePhoto();
    }

    /**
     * PopWindow dismissListener
     */
    private PopupWindow.OnDismissListener dismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            if (setPicturePop != null) {
                setPicturePop = null;
            }
        }
    };


    /**
     * 拍照 or 选择照片 返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        lqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
    }


    /**
     * 更新图片
     *
     * @param path
     */
    public void updatePhotoPath(String path) {
        filePathList.add(path);
        adapter.notifyDataSetChanged();
    }


    /**
     * 申请权限result
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {


            //选择文件
            case REQUEST_FILE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "权限申请成功");
                    selectPicture();
                } else {
                    Log.e(TAG, "权限申请失败");
                }
                break;

            //拍照
            case REQUEST_CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "权限申请成功");
                    takePhoto();
                } else {
                    Log.e(TAG, "权限申请失败");
                }
                break;


            default:
                break;
        }
    }


    /**
     * 提交按钮
     */
    @OnClick(R.id.btn_submit_feed_back)
    public void onViewClicked() {

    }
}
