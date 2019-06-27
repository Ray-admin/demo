package com.octopus.wishTravel.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.octopus.wishTravel.R;
import com.octopus.wishTravel.activity.AddOrEditTravelerActivity;
import com.octopus.wishTravel.bean.TravelerBean;

import java.util.List;


/**
 * 选择出游人adapter
 */
public class SelectTravelerListAdapter extends BaseQuickAdapter<TravelerBean, BaseViewHolder> {
    public SelectTravelerListAdapter(int layoutResId, @Nullable List<TravelerBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final TravelerBean item) {
        //出游人姓名
        helper.setText(R.id.tv_traveler_name, item.getTravelerName());
        //出游人性别
        helper.setText(R.id.tv_traveler_gender, item.getTravelerGender());
        //证件类型
        helper.setText(R.id.tv_traveler_certificate_category, item.getTravelerCertificateCategory());
        //证件号
        helper.setText(R.id.tv_traveler_id_num, item.getIdNum());

        //编辑出游人信息
        ImageView editTravelerInfo = helper.getView(R.id.iv_edit_traveler_info);
        editTravelerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddOrEditTravelerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", item);
                intent.putExtra("type", "edit");
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        //选中未选中图标
        ImageView selectionStatus = helper.getView(R.id.iv_traveler_selection_status);
        if (item.isSelected()) {
            selectionStatus.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_indicator_write_selected_));
        } else {
            selectionStatus.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_indicator_write_off_check));
        }

    }
}
