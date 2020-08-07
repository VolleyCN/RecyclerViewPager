package com.lsjwzh.widget.recyclerviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @Describe
 * @Date : 2020/8/7
 * @Email : zhangmeng@newstylegroup.com
 * @Author : MENG
 */
public class RCBanner extends RelativeLayout implements RecyclerViewPager.OnPageChangedListener {
    private static final int RMP = RelativeLayout.LayoutParams.MATCH_PARENT;
    private static final int RWC = RelativeLayout.LayoutParams.WRAP_CONTENT;
    private static final int LWC = LinearLayout.LayoutParams.WRAP_CONTENT;
    private static final int NO_PLACEHOLDER_DRAWABLE = -1;
    private LoopRecyclerViewPager mViewPager;
    private LinearLayout mPointRealContainerLl;
    private boolean mAutoPlayAble = true;
    private int mAutoPlayInterval = 3000;
    private int mPageChangeDuration = 800;
    private int mPointGravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
    private int mPointLeftRightMargin;
    private int mPointTopBottomMargin;
    private int mPointContainerLeftRightPadding;
    private int mPointDrawableResId = R.drawable.rc_banner_selector_point_solid;
    private Drawable mPointContainerBackgroundDrawable;
    private AutoPlayTask mAutoPlayTask;
    private int mPlaceholderDrawableResId = NO_PLACEHOLDER_DRAWABLE;
    private RelativeLayout mPointContainerRl;
    private boolean mIsNeedShowIndicatorOnOnlyOnePage;
    private int mContentBottomMargin;
    private float mAspectRatio;
    private boolean mAllowUserScrollable = true;
    private List<Object> mModels = new ArrayList<>();

    public RCBanner(Context context) {
        this(context, null);
    }

    public RCBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RCBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDefaultAttrs(context);
        initCustomAttrs(context, attrs);
        initView(context);
    }

    private void initDefaultAttrs(Context context) {
        mAutoPlayTask = new AutoPlayTask(this);
        mPointLeftRightMargin = dp2px(context, 3);
        mPointTopBottomMargin = dp2px(context, 6);
        mPointContainerLeftRightPadding = dp2px(context, 10);
        mPointContainerBackgroundDrawable = new ColorDrawable(Color.parseColor("#44aaaaaa"));
        mContentBottomMargin = 0;
        mAspectRatio = 0;
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RCBanner);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.RCBanner_banner_pointDrawable) {
            mPointDrawableResId = typedArray.getResourceId(attr, R.drawable.rc_banner_selector_point_solid);
        } else if (attr == R.styleable.RCBanner_banner_pointContainerBackground) {
            mPointContainerBackgroundDrawable = typedArray.getDrawable(attr);
        } else if (attr == R.styleable.RCBanner_banner_pointLeftRightMargin) {
            mPointLeftRightMargin = typedArray.getDimensionPixelSize(attr, mPointLeftRightMargin);
        } else if (attr == R.styleable.RCBanner_banner_pointContainerLeftRightPadding) {
            mPointContainerLeftRightPadding = typedArray.getDimensionPixelSize(attr, mPointContainerLeftRightPadding);
        } else if (attr == R.styleable.RCBanner_banner_pointTopBottomMargin) {
            mPointTopBottomMargin = typedArray.getDimensionPixelSize(attr, mPointTopBottomMargin);
        } else if (attr == R.styleable.RCBanner_banner_indicatorGravity) {
            mPointGravity = typedArray.getInt(attr, mPointGravity);
        } else if (attr == R.styleable.RCBanner_banner_pointAutoPlayAble) {
            mAutoPlayAble = typedArray.getBoolean(attr, mAutoPlayAble);
        } else if (attr == R.styleable.RCBanner_banner_pointAutoPlayInterval) {
            mAutoPlayInterval = typedArray.getInteger(attr, mAutoPlayInterval);
        } else if (attr == R.styleable.RCBanner_banner_pageChangeDuration) {
            mPageChangeDuration = typedArray.getInteger(attr, mPageChangeDuration);
        } else if (attr == R.styleable.RCBanner_banner_placeholderDrawable) {
            mPlaceholderDrawableResId = typedArray.getResourceId(attr, mPlaceholderDrawableResId);
        } else if (attr == R.styleable.RCBanner_banner_isNeedShowIndicatorOnOnlyOnePage) {
            mIsNeedShowIndicatorOnOnlyOnePage = typedArray.getBoolean(attr, mIsNeedShowIndicatorOnOnlyOnePage);
        } else if (attr == R.styleable.RCBanner_banner_contentBottomMargin) {
            mContentBottomMargin = typedArray.getDimensionPixelSize(attr, mContentBottomMargin);
        } else if (attr == R.styleable.RCBanner_banner_aspectRatio) {
            mAspectRatio = typedArray.getFloat(attr, mAspectRatio);
        }
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    private void initView(Context context) {
        try {
            mViewPager = new LoopRecyclerViewPager(context);
            mViewPager.setTriggerOffset(0.15f);
            mViewPager.setFlingFactor(0.25f);
            mViewPager.setSinglePageFling(true);
            mViewPager.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
                @Override
                public boolean canScrollHorizontally() {
                    Log.e("Scrollable:", "Scrollable:" + mAllowUserScrollable);
                    return mAllowUserScrollable;
                }
            });
            mViewPager.setHasFixedSize(true);
            LayoutParams viewPagerLp = new LayoutParams(RMP, RMP);
            addView(mViewPager, viewPagerLp);
            mPointContainerRl = new RelativeLayout(context);
            if (Build.VERSION.SDK_INT >= 16) {
                mPointContainerRl.setBackground(mPointContainerBackgroundDrawable);
            } else {
                mPointContainerRl.setBackgroundDrawable(mPointContainerBackgroundDrawable);
            }
            mPointContainerRl.setPadding(mPointContainerLeftRightPadding, mPointTopBottomMargin, mPointContainerLeftRightPadding, mPointTopBottomMargin);
            LayoutParams pointContainerLp = new LayoutParams(RMP, RWC);
            // 处理圆点在顶部还是底部
            if ((mPointGravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP) {
                pointContainerLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            } else {
                pointContainerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
            addView(mPointContainerRl, pointContainerLp);
            LayoutParams indicatorLp = new LayoutParams(RWC, RWC);
            indicatorLp.addRule(CENTER_VERTICAL);
            mPointRealContainerLl = new LinearLayout(context);
            mPointRealContainerLl.setId(R.id.banner_indicatorId);
            mPointRealContainerLl.setOrientation(LinearLayout.HORIZONTAL);
            mPointRealContainerLl.setGravity(Gravity.CENTER_VERTICAL);
            mPointContainerRl.addView(mPointRealContainerLl, indicatorLp);
            int horizontalGravity = mPointGravity & Gravity.HORIZONTAL_GRAVITY_MASK;
            // 处理圆点在左边、右边还是水平居中
            if (horizontalGravity == Gravity.LEFT) {
                indicatorLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            } else if (horizontalGravity == Gravity.RIGHT) {
                indicatorLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                indicatorLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            }
            mViewPager.addOnPageChangedListener(this);
            switchToPoint(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnPageChanged(int oldPosition, int newPosition) {
        switchToPoint(mViewPager.getActualCurrentPosition());
    }

    private static class AutoPlayTask implements Runnable {
        private final WeakReference<RCBanner> mBanner;

        private AutoPlayTask(RCBanner banner) {
            mBanner = new WeakReference<>(banner);
        }

        @Override
        public void run() {
            RCBanner banner = mBanner.get();
            if (banner != null) {
                banner.startAutoPlay();
                banner.switchToNextPage();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onInvisibleToUser();
    }

    private void onInvisibleToUser() {
        stopAutoPlay();
    }

    public void setIndicatorVisibility(boolean visible) {
        mPointContainerRl.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setIndicatorTopBottomMarginDp(int marginDp) {
        setIndicatorTopBottomMarginPx(dp2px(getContext(), marginDp));
    }

    public void setIndicatorTopBottomMarginRes(@DimenRes int resId) {
        setIndicatorTopBottomMarginPx(getResources().getDimensionPixelOffset(resId));
    }

    public void setIndicatorTopBottomMarginPx(int marginPx) {
        mPointTopBottomMargin = marginPx;
        mPointContainerRl.setPadding(mPointContainerLeftRightPadding, mPointTopBottomMargin, mPointContainerLeftRightPadding, mPointTopBottomMargin);
    }

    private void initIndicator() {
        if (mPointRealContainerLl != null) {
            mPointRealContainerLl.removeAllViews();
            if (mIsNeedShowIndicatorOnOnlyOnePage || (!mIsNeedShowIndicatorOnOnlyOnePage && mModels.size() > 1)) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LWC, LWC);
                lp.setMargins(mPointLeftRightMargin, 0, mPointLeftRightMargin, 0);
                ImageView imageView;
                for (int i = 0; i < mModels.size(); i++) {
                    imageView = new ImageView(getContext());
                    imageView.setLayoutParams(lp);
                    imageView.setImageResource(mPointDrawableResId);
                    mPointRealContainerLl.addView(imageView);
                }
            }
        }
    }

    private void switchToPoint(int newCurrentPoint) {
        if (mPointRealContainerLl != null) {
            if (mModels != null && mModels.size() > 0 && newCurrentPoint < mModels.size() && ((mIsNeedShowIndicatorOnOnlyOnePage || (
                    !mIsNeedShowIndicatorOnOnlyOnePage && mModels.size() > 1)))) {
                mPointRealContainerLl.setVisibility(View.VISIBLE);
                for (int i = 0; i < mPointRealContainerLl.getChildCount(); i++) {
                    mPointRealContainerLl.getChildAt(i).setEnabled(i == newCurrentPoint);
                    // 处理指示器选中和未选中状态图片尺寸不相等
                    mPointRealContainerLl.getChildAt(i).requestLayout();
                }
            } else {
                mPointRealContainerLl.setVisibility(View.GONE);
            }
        }
    }


    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mViewPager != null) {
            mViewPager.setAdapter(adapter);
        }
    }

    public RecyclerView.Adapter getAdapter() {
        if (mViewPager != null) {
            return mViewPager.getAdapter();
        }
        return null;
    }

    public RecyclerView getViewPager() {
        return mViewPager;
    }


    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        if (mViewPager != null) {
            mViewPager.addOnItemTouchListener(listener);
        }
    }

    public void setData(List<? extends Object> models) {
        if (models == null || models.size() == 0) {
            return;
        }
        this.mModels.clear();
        this.mModels.addAll(models);
        initIndicator();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAspectRatio > 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width / mAspectRatio);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mAutoPlayAble) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    stopAutoPlay();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    startAutoPlay();
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAutoPlay();
    }

    public void startAutoPlay() {
        stopAutoPlay();
        if (mAutoPlayAble) {
            postDelayed(mAutoPlayTask, mAutoPlayInterval);
        }
    }

    public void stopAutoPlay() {
        if (mAutoPlayTask != null) {
            removeCallbacks(mAutoPlayTask);
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            startAutoPlay();
        } else {
            stopAutoPlay();
        }
    }

    /**
     * 切换到下一页
     */
    private void switchToNextPage() {
        try {
            if (mModels.size() <= 1) {
                return;
            }
            if (mViewPager != null) {
                mViewPager.smoothScrollToPosition(mViewPager.getCurrentPosition() + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
