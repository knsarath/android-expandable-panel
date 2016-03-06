package com.example.sarath.expandablelayout;

import android.content.Context;
import android.content.res.Resources;
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
    public static final int DEFAULT_ANIMATION_DURATION = 500;
    private TextView mDefaultHeaderTextView;
    private TextView mDefaultSubHeaderTextView;
    private View mCollapseExpandSwitch;
    private View mContentView;
    private View mHeaderView;
    private LinearLayout mContentContainerView;
    private LinearLayout mHeaderContainerView;
    private boolean mIsCollapsed = true;
    private boolean mUseDefaultHeaderView = true;
    private String mSwitchId = "";
    private int mAnimationDuration;
    private boolean initiallyCollapsed = true;

    public interface CollapseExpandListener {
        void onCollapseStarted();

        void onCollapseFinished();

        void onExpandStarted();

        void onExpandFinished();
    }

    public void setCollapseExpandListener(CollapseExpandListener collapseExpandListener) {
        mCollapseExpandListener = collapseExpandListener;
    }

    CollapseExpandListener mCollapseExpandListener;

    public void setUseDefaultHeaderView(boolean useDefaultHeaderView) {
        mUseDefaultHeaderView = useDefaultHeaderView;
        if (mUseDefaultHeaderView) {
            setHeaderLayout(R.layout.default_header_view);
            setSwitch(mHeaderContainerView, "expand_collapse_switch");
        } else {
            setHeaderLayout(mHeaderView);
        }

    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    private void setHeaderLayout(View headerView) {
        mHeaderView = headerView;
        inflateHeader(mHeaderView);
    }


    public ExpandablePanel(Context context) {
        super(context);
        init(null);

    }

    public ExpandablePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExpandablePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View mainView = inflate(getContext(), R.layout.expandable_panel, this);
        mHeaderContainerView = (LinearLayout) mainView.findViewById(R.id.header);
        mContentContainerView = (LinearLayout) mainView.findViewById(R.id.content);
        TypedArray attributeSet = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.ExpandablePanel);
        try {
            initiallyCollapsed = attributeSet.getBoolean(R.styleable.ExpandablePanel_initiallyCollapsed, initiallyCollapsed);
            if (initiallyCollapsed) {
                collapse(mContentContainerView, 1);
                mIsCollapsed = true;
            } else {
                mIsCollapsed = false;
            }
            mUseDefaultHeaderView = attributeSet.getBoolean(R.styleable.ExpandablePanel_useDefaultHeaderView, mUseDefaultHeaderView);
            mSwitchId = attributeSet.getString(R.styleable.ExpandablePanel_expandCollapseSwitchId);
            if (mUseDefaultHeaderView) {
                setUseDefaultHeaderView(true);
            } else {
                setHeaderLayout(attributeSet.getResourceId(R.styleable.ExpandablePanel_headerView, -1));
                setSwitch(mHeaderContainerView, mSwitchId);
            }
            setContentLayout(attributeSet.getResourceId(R.styleable.ExpandablePanel_content_view, -1));
            mAnimationDuration = attributeSet.getInteger(R.styleable.ExpandablePanel_expandAnimationDuration, DEFAULT_ANIMATION_DURATION);


        } catch (android.content.res.Resources.NotFoundException e) {
            e.printStackTrace();
        }
        //Don't forget this
        attributeSet.recycle();
    }

    private void setSwitch(LinearLayout headerContainerView, String switchId) {
        Resources res = getContext().getResources();
        int id = res.getIdentifier(switchId, "id", getContext().getPackageName());
        View switchView = mHeaderContainerView.findViewById(id);
        setCollapseExpandSwitch(switchView);

    }

    private void setCollapseExpandSwitch(View collapseExpandSwitch) {
        mCollapseExpandSwitch = collapseExpandSwitch;
        if (mCollapseExpandSwitch != null) {
            mCollapseExpandSwitch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandOrCollapse();
                    mIsCollapsed = !mIsCollapsed;
                }
            });
        }
    }

    private void setHeaderLayout(int headerViewResourceId) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mHeaderView = inflater.inflate(headerViewResourceId, null);
        inflateHeader(mHeaderView);
    }

    private void expandOrCollapse() {
        if (mIsCollapsed)
            expand(mContentContainerView, mAnimationDuration);
        else
            collapse(mContentContainerView, mAnimationDuration);

    }

    public void setDefaultHeaderTextView(TextView defaultHeaderTextView) {
        mDefaultHeaderTextView = defaultHeaderTextView;
    }

    public void setDefaultSubHeaderTextView(TextView defaultSubHeaderTextView) {
        mDefaultSubHeaderTextView = defaultSubHeaderTextView;
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
            mContentContainerView.removeAllViews();
            mContentContainerView.addView(view);
        }
    }

    private void inflateHeader(View view) {
        if (mHeaderContainerView != null && view != null) {
            mHeaderContainerView.removeAllViews();
            mHeaderContainerView.addView(view);
        }
    }

    public void expand(final View v, long animationDuration) {
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

        if (mCollapseExpandListener != null) {
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mCollapseExpandListener.onExpandStarted();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCollapseExpandListener.onExpandFinished();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        a.setDuration(animationDuration);
        v.startAnimation(a);
    }

    public void collapse(final View v, long animationDuration) {
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

        if (mCollapseExpandListener != null) {
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mCollapseExpandListener.onCollapseStarted();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCollapseExpandListener.onCollapseFinished();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        a.setDuration(animationDuration);
        v.startAnimation(a);
    }


}
