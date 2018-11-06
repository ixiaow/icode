package com.icode.topiclayouttest;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 编写人： xw
 * 创建时间：2018/11/5 11:10
 * 功能描述：
 */
public class TopicLayout extends LinearLayout implements View.OnClickListener, ViewPager.OnAdapterChangeListener, ViewPager.OnPageChangeListener {
    private static final String TAG = "TopicLayout";

    private int mTopicLayoutWidth; //当前控件的宽度
    private List<String> mTabNames; //tab名称集合
    private Context mContext;//上下文
    private OnTabSelectListener mOnTabSelectListener;
    private Paint mPaint;
    private RectF rectF;
    private float mIndicatorTranslateX;
    private float mIndicatorTranslateY;
    private boolean isOnce = false;
    private TextView mCurrentTabText;
    private ViewPager mViewPager;
    private List<TextView> mTextViews;

    public void setOnTabSelectListener(OnTabSelectListener onTabSelectListener) {
        this.mOnTabSelectListener = onTabSelectListener;
    }

    public TopicLayout(Context context) {
        this(context, null);
    }

    public TopicLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopicLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mTabNames = new ArrayList<>(0);
        setOrientation(VERTICAL);
        rectF = new RectF();
        rectF.set(0, 0, convertDimension(22), convertDimension(2));
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        setWillNotDraw(false);
    }

    private float convertDimension(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                mContext.getResources().getDisplayMetrics());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout...");
                TopicLayout.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mTopicLayoutWidth = getMeasuredWidth();
                updateTabs();
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!isOnce && getChildCount() > 0) {
            isOnce = true;
            View child = getChildAt(getChildCount() - 1);
            LinearLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.bottomMargin = (int) convertDimension(3);
            child.setLayoutParams(lp);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mIndicatorTranslateX, mIndicatorTranslateY);
        canvas.drawRoundRect(rectF, convertDimension(1), convertDimension(1), mPaint);
        canvas.restore();
    }

    public void setTabNames(List<String> tabNames) {
        mTabNames = tabNames;
        updateTabs();
    }

    public void setupWithViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        PagerAdapter adapter = this.mViewPager.getAdapter();
        if (adapter == null) {
            return;
        }
        int count = adapter.getCount();
        List<String> tabNames = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            tabNames.add((String) title);
        }
        setTabNames(tabNames);
        this.mViewPager.addOnAdapterChangeListener(this);
        this.mViewPager.addOnPageChangeListener(this);
    }

    private void updateTabs() {
        Log.d(TAG, "update Tabs...");
        this.removeAllViews();
        if (mTabNames == null || mTabNames.isEmpty()) {
            Log.d(TAG, "mTabName is empty");
            return;
        }

        if (mTopicLayoutWidth == 0) {
            Log.d(TAG, "mTopicLayoutWidth is 0");
            return;
        }
        mTextViews = new ArrayList<>();
        LinearLayout linearLayout = newLinearLayout();
        int mViewWidth = 0;
        int tmpViewWidth;
        boolean isAdd = false;
        for (String tabName : mTabNames) {

            TextView tabText = newTabText(tabName);
            mTextViews.add(tabText);
            if (mViewWidth == 0) {
                tabText.setSelected(true);
                mCurrentTabText = tabText;
            }
            int width = getTextViewWidth(tabText);
            tmpViewWidth = mViewWidth + width;
            if (tmpViewWidth <= mTopicLayoutWidth) {
                mViewWidth += width;
                linearLayout.addView(tabText);
                isAdd = true;
                continue;
            }
            Log.d(TAG, "for addView....");
            this.addView(linearLayout);
            mViewWidth = width;
            linearLayout = newLinearLayout();
            linearLayout.addView(tabText);
            isAdd = false;
        }
        if (isAdd) {
            Log.d(TAG, "addView....");
            this.addView(linearLayout);
        }
        int childCount = linearLayout.getChildCount();
        if (childCount > 0) {
            TextView child = (TextView) linearLayout.getChildAt(0);
            Paint paint = new Paint();
            paint.setTextSize(child.getTextSize());
            float measureText = paint.measureText(child.getText().toString());
            rectF.left = (child.getMeasuredWidth() - measureText) / 2f;
            rectF.top = child.getMeasuredHeight();
            rectF.right += rectF.left;
            rectF.bottom += rectF.top;
            postInvalidate();
        }
        Log.d(TAG, "childCount: " + getChildCount());
    }

    @NonNull
    private LinearLayout newLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(lp);
        linearLayout.setOrientation(HORIZONTAL);
        return linearLayout;
    }

    @NonNull
    private TextView newTabText(String tabName) {
        TextView tabText = new TextView(mContext);
        float density = mContext.getResources().getDisplayMetrics().density;
        int textSize = (int) (mContext.getResources().getDimensionPixelSize(R.dimen.topic_tab_text_size) / density);
        ColorStateList colorStateList = mContext.getResources().getColorStateList(R.color.selector_topic_tab_text);
        tabText.setTextSize(textSize);
        tabText.setTextColor(colorStateList);
        tabText.setGravity(Gravity.CENTER);
        tabText.setMaxLines(1);
        tabText.setEllipsize(TextUtils.TruncateAt.END);
        tabText.setText(tabName);
        int width = mContext.getResources().getDimensionPixelSize(R.dimen.topic_tab_text_width);
        int height = mContext.getResources().getDimensionPixelSize(R.dimen.topic_tab_text_height);
        LayoutParams layoutParams = new LayoutParams(width, height);
        tabText.setLayoutParams(layoutParams);
        tabText.setOnClickListener(this);
        return tabText;
    }

    private void updateIndicator(View v, int position) {
        LinearLayout.LayoutParams params = (LayoutParams) v.getLayoutParams();
        int leftMargin = params.width;
        int x = leftMargin * position;

        int indexOfChild = indexOfChild((View) v.getParent());
        int y = params.height * indexOfChild;

        if ((x != 0 || y != 0) && mIndicatorTranslateX == x && mIndicatorTranslateY == y) {
            return;
        }
        mIndicatorTranslateX = x;
        mIndicatorTranslateY = y;
        postInvalidate();
    }

    /*
     * view控件的宽度
     */
    private int getTextViewWidth(TextView view) {
        int width = mContext.getResources().getDimensionPixelSize(R.dimen.topic_tab_text_width);
        int height = mContext.getResources().getDimensionPixelSize(R.dimen.topic_tab_text_height);
        int w = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int h = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        view.measure(w, h);
        return view.getMeasuredWidth();
    }

    @Override
    public void onClick(View v) {
        if (!(v instanceof TextView)) {
            return;
        }
        TextView textView = (TextView) v;
        textView.setSelected(true);
        if (mCurrentTabText != null) {
            mCurrentTabText.setSelected(false);
        }
        mCurrentTabText = textView;
        LinearLayout parent = (LinearLayout) v.getParent();
        int position = parent.indexOfChild(v);
        updateIndicator(v, position);
        if (mOnTabSelectListener != null) {
            mOnTabSelectListener.select(textView, position);
        }

        if (mViewPager != null) {
            int index = mTabNames.indexOf(textView.getText().toString());
            mViewPager.setCurrentItem(index);
        }
    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter pagerAdapter,
                                 @Nullable PagerAdapter pagerAdapter1) {
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            return;
        }
        int count = adapter.getCount();
        List<String> tabNames = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            tabNames.add((String) title);
        }
        setTabNames(tabNames);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {

        TextView textView = mTextViews.get(position);
        ViewGroup parent = (ViewGroup) textView.getParent();
        int indexOfChild = parent.indexOfChild(textView);
        updateIndicator(textView, indexOfChild);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public interface OnTabSelectListener {
        void select(TextView tabText, int position);
    }
}
