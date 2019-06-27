package com.octopus.wishTravel.adapter;

import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.octopus.wishTravel.GlideApp;
import com.octopus.wishTravel.R;
import com.octopus.wishTravel.bean.DestinationCardBean;
import com.octopus.wishTravel.widget.SWImageView;

import java.util.List;


/**
 * 目的地底部card adapter
 */
public class DestinationCardCardAdapter extends BaseQuickAdapter<DestinationCardBean, BaseViewHolder> {
    public DestinationCardCardAdapter(int layoutResId, @Nullable List<DestinationCardBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DestinationCardBean item) {
        //目的地卡片标题
        helper.setText(R.id.tv_destination_card_title, item.getTitle());
        //目的地卡片内容
        helper.setText(R.id.tv_destination_card_content, item.getContent());
        //目的地背景图
        SWImageView swImageView = helper.getView(R.id.iv_destination_card_image);
        GlideApp.with(mContext).load(item.getDrawable()).into(swImageView);

        //根布局
        LinearLayout linearLayout = helper.getView(R.id.ll_destination_card_root_view);
        if (helper.getAdapterPosition() == 0) {
            linearLayout.setPadding((int) mContext.getResources().getDimension(R.dimen.xdp_15_0), 0, 0, 0);
        } else {
            linearLayout.setPadding(0, 0, 0, 0);
        }
    }


}