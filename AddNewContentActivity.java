package com.octopus.wishTravel.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.octopus.wishTravel.R;
import com.octopus.wishTravel.adapter.SelectTypeAdapter;
import com.octopus.wishTravel.base.BaseActivity;
import com.octopus.wishTravel.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 添加新内容
 */
public class AddNewContentActivity extends BaseActivity {
    private static final String TAG = "AddNewContentActivity";


    //选择内容类型
    @BindView(R.id.rl_select_content_type)
    RelativeLayout rlSelectContentType;
    //内容输入框
    @BindView(R.id.et_content)
    EditText etContent;
    //总长度
    @BindView(R.id.tv_total_length)
    TextView tvTotalLength;
    //保存按钮
    @BindView(R.id.btn_save_content)
    Button btnSaveContent;
    @BindView(R.id.tv_type)
    TextView tvType;


    //选择类型popWindow
    private PopupWindow selectTypePop;

    //选择类型index
    private int selectIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_new_content;
    }

    @Override
    protected void initView() {
        setTitleBar("添加新内容",
                getResources().getDrawable(R.mipmap.icon_indicator_mywish_rest_blackback),
                getResources().getColor(R.color.color_white));
        etContent.setHintTextColor(getResources().getColor(R.color.color_edit_hint));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void addListener() {
        //内容监听器，试试显示输入字数
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG, "afterTextChanged: " + s.length());
                tvTotalLength.setText(String.valueOf(s.length()));
            }
        });
    }

    /**
     * 按钮点击
     *
     * @param view
     */

    @OnClick({R.id.rl_select_content_type, R.id.btn_save_content})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //选择类型
            case R.id.rl_select_content_type:
                showAddContentPop();
                break;

            //保存
            case R.id.btn_save_content:
                String content = etContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showToast("请输入内容");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("content", content);
                    intent.putExtra("index", selectIndex);
                    Log.d(TAG, "onViewClicked: " + intent);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }


    /**
     * 选择类别popWindow
     */

    private void showAddContentPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_add_new_content_layout, null);
        if (selectTypePop == null) {
            selectTypePop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        //设置可触摸
        selectTypePop.setTouchable(true);
        //设置获取焦点
        selectTypePop.setFocusable(true);
        //设置背景，点击可消失
        selectTypePop.setBackgroundDrawable(new BitmapDrawable());

        //设置pop占满屏幕
        selectTypePop.setClippingEnabled(false);

        // 设置点击窗口外边窗口消失
        selectTypePop.setOutsideTouchable(true);
        //设置dismiss监听
        selectTypePop.setOnDismissListener(dismissListener);

        //顶部空白view
        View touchView = view.findViewById(R.id.view_touch_view);

        //滚轮选择view
        WheelView wheelView = view.findViewById(R.id.wv_select_type);

        //取消按钮
        TextView cancelText = view.findViewById(R.id.tv_cancel);
        //完成按钮
        TextView completeText = view.findViewById(R.id.tv_complete);

        //设置不循环滚动
        wheelView.setCyclic(false);
        wheelView.setAdapter(new SelectTypeAdapter(createTypes()));
        //设置中间线
        wheelView.setLineSpacingMultiplier(1.8f);

        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                Log.e(TAG, "onItemSelected: " + index);
                selectIndex = index;

            }
        });

        wheelView.setCurrentItem(selectIndex);
        //外部空白view
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                selectTypePop.dismiss();
                return false;
            }
        });

        //取消按钮
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTypePop.dismiss();
            }
        });

        //确认按钮
        completeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTypePop.dismiss();
                tvType.setText(createTypes().get(selectIndex));
            }
        });


        selectTypePop.showAtLocation(etContent, Gravity.BOTTOM, 0, 0);


    }

    /**
     * 创建可选类别
     *
     * @return
     */
    private List<String> createTypes() {
        List<String> types = new ArrayList<>();
        types.clear();
        types.add("出行必备");
        types.add("行前注意事项");
        types.add("攻略秘籍准备");
        types.add("其他");
        return types;
    }

    /**
     * PopWindow disMissListener
     */

    private PopupWindow.OnDismissListener dismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            if (selectTypePop != null) {
                selectTypePop = null;
            }
        }
    };
}
