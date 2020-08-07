package com.lsjwzh.widget.recyclerviewpagerdeomo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lsjwzh.widget.recyclerviewpager.RCBanner;

import java.util.ArrayList;
import java.util.List;

public class LoopPagerBannerActivity extends Activity {
    protected RCBanner mRCBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_loopviewpager_banner);
        initViewPager();
    }

    private BaseQuickAdapter adapter;

    protected void initViewPager() {
        mRCBanner = findViewById(R.id.banner);
        mRCBanner.setAdapter(adapter = new BaseQuickAdapter<BannerItem, BaseViewHolder>(R.layout.demo_list_banner_item) {
            @Override
            protected void convert(BaseViewHolder helper, BannerItem item) {
                helper.setText(R.id.text, item.title);
                helper.setImageResource(R.id.image, item.imgRes);

            }
        });
        adapter.setNewData(builData());
        mRCBanner.setData(adapter.getData());
        mRCBanner.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setNewData(builDataNew());
                mRCBanner.setData(adapter.getData());
            }
        },5000);
    }

    private List builData() {
        List<BannerItem> items = new ArrayList<>();
        items.add(new BannerItem("title_1", R.drawable.cheese_1));
        items.add(new BannerItem("title_2", R.drawable.cheese_2));
        items.add(new BannerItem("title_3", R.drawable.cheese_3));
        items.add(new BannerItem("title_4", R.drawable.cheese_4));
        items.add(new BannerItem("title_5", R.drawable.cheese_5));
        return items;
    }
    private List builDataNew() {
        List<BannerItem> items = new ArrayList<>();
        items.add(new BannerItem("title_1", R.drawable.cheese_1));
//        items.add(new BannerItem("title_2", R.drawable.cheese_2));
//        items.add(new BannerItem("title_3", R.drawable.cheese_3));
        return items;
    }

    public static class BannerItem {
        public String title;
        public int imgRes;

        public BannerItem(String title, int imgRes) {
            this.title = title;
            this.imgRes = imgRes;
        }
    }

}
