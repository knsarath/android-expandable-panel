package com.example.sarath.expandablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpandablePanel extends LinearLayout {
    private static final long ANIMATION_DURATION = 500;
    private TextView mHeaderText;
    private TextView mSubHeader;
    private ImageView mCollapseExpandSwitch;
    private View mContentView;
    private View mHeaderView;
    private LinearLayout mContentContainerView;
    private LinearLayout mHeaderContainerView;
    private boolean mIsCollapsed = true;
    private boolean mUseDefaultHeaderView = true;



    public ExpandablePanel(Context context) {
        super(context);
        init();
    }

    public ExpandablePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExpandablePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init() {
        init(null);
    }

    private void init(AttributeSet attrs) {
        View mainView = inflate(getContext(), R.layout.expandable_panel, this);
        mHeaderContainerView = (LinearLayout) mainView.findViewById(R.id.header);
        mContentContainerView = (LinearLayout) mainView.findViewById(R.id.content);
        TypedArray attributeSet = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.ExpandablePanel);
        try {
            setContentLayout(attributeSet.getResourceId(R.styleable.ExpandablePanel_content_view, -1));
            setHeaderLayout(attributeSet.getResourceId(R.styleable.ExpandablePanel_headerView, R.layout.default_header_view));
            mCollapseExpandSwitch = (ImageView) mainView.findViewById(R.id.expand_collapse_switch);
            if (mCollapseExpandSwitch != null) {
                mCollapseExpandSwitch.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIsCollapsed = !mIsCollapsed;
                        expandOrCollapse();
                    }
                });
            }

        } catch (android.content.res.Resources.NotFoundException e) {
            e.printStackTrace();
        }
        //Don't forget this
        attributeSet.recycle();
    }

    private void setHeaderLayout(int headerViewResourceId) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mHeaderView = inflater.inflate(headerViewResourceId, null);
        inflateHeader(mHeaderView);
    }

    private void expandOrCollapse() {
        if (mIsCollapsed)
            expand(mContentContainerView);
        else
            collapse(mContentContainerView);

    }

    public void setHeaderText(TextView headerText) {
        mHeaderText = headerText;
    }

    public void setSubHeader(TextView subHeader) {
        mSubHeader = subHeader;
    }

    public void setCollapseExpandSwitch(ImageView collapseExpandSwitch) {
        mCollapseExpandSwitch = collapseExpandSwitch;
    }

    public void setContentLayout(int contentLayoutResource) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mContentView = inflater.inflate(contentLayoutResource, null);
        inflateContent(mContentView);
    }

    private void inflateContent(View view) {
        if (mContentContainerView != null && view != null) {
            mContentContainerView.addView(view);
        }
    }

    private void inflateHeader(View view) {
        if (mHeaderContainerView != null && view != null) {
            mHeaderContainerView.addView(view);
        }
    }

    public static void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(ANIMATION_DURATION);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(ANIMATION_DURATION);
        v.startAnimation(a);
    }


}
