package com.ict.lottietesting;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieListener;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ConnectButtonShadowView extends CardView {
    AnimationDrawable frameAnimation;
    public static final int STATUS_CONNECT = 1;
    public static final int STATUS_CONNECTING = 2;
    public static final int STATUS_CONNECTED = 3;
    private CustomDrawable connectRes;
    private CustomDrawable connectingRes;
    private CustomDrawable connectedRes;
    private int connectResId,connectingResId,connectedResId;
    private int startFrameConnecting, endFrameConnecting,startFrameConnected,endFrameConnected,startFrameConnect,endFrameConnect;
    private int currentStatus = STATUS_CONNECT;
    private float elevation,maxElevation,radius;
    AppCompatImageView connectButtonView;
    private boolean shadowConnect,shadowConnecting,shadowConnected;
    private float dStartFrameConnecting;
    private float dEndFrameConnecting;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setClickable(true);
        }
    };


    public ConnectButtonShadowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs,context);
    }

    public ConnectButtonShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,context);
    }

    public ConnectButtonShadowView(Context context) {
        super(context);
        init(null,context);
    }


    private void init(AttributeSet attrs,Context context) {
//        if (attrs == null)
//            return;
//        new Thread(() -> {
        setVisibility(INVISIBLE);
        connectButtonView = new AppCompatImageView(context);
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        addView(connectButtonView,lp);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ConnectButtonShadowView);
        connectResId = ta.getResourceId(R.styleable.ConnectButtonShadowView_connectBackground, -1);
        connectingResId = ta.getResourceId(R.styleable.ConnectButtonShadowView_connectingBackground, -1);
        connectedResId = ta.getResourceId(R.styleable.ConnectButtonShadowView_connectedBackground, -1);
        float speed = ta.getFloat(R.styleable.ConnectButtonShadowView_lottie_speed, 1f);


        startFrameConnecting = ta.getInt(R.styleable.ConnectButtonShadowView_startFrameConnecting, -1);
        endFrameConnecting = ta.getInt(R.styleable.ConnectButtonShadowView_endFrameConnecting, -1);
        startFrameConnect = ta.getInt(R.styleable.ConnectButtonShadowView_startFrameConnect, -1);
        endFrameConnect = ta.getInt(R.styleable.ConnectButtonShadowView_endFrameConnect, -1);
        startFrameConnected = ta.getInt(R.styleable.ConnectButtonShadowView_startFrameConnected, -1);
        endFrameConnected = ta.getInt(R.styleable.ConnectButtonShadowView_endFrameConnected, -1);


        shadowConnect=ta.getBoolean(R.styleable.ConnectButtonShadowView_shadowOnConnect,false);
        shadowConnecting=ta.getBoolean(R.styleable.ConnectButtonShadowView_shadowOnConnecting,false);
        shadowConnected=ta.getBoolean(R.styleable.ConnectButtonShadowView_shadowOnConnected,false);
        elevation=ta.getDimension(R.styleable.ConnectButtonShadowView_Elevation,0f);
        maxElevation=ta.getDimension(R.styleable.ConnectButtonShadowView_MaxElevation,elevation);
        radius=ta.getDimension(R.styleable.ConnectButtonShadowView_CornerRadius,0f);
        setCardBackgroundColor(ta.getColor(R.styleable.ConnectButtonShadowView_BackgroundColor, Color.TRANSPARENT));

        ta.recycle();
//        new Thread(() -> {
        if(connectResId != -1)
        connectRes = new CustomDrawable(getContext(), connectResId, speed);
        if(connectingResId!= -1)
        connectingRes = new CustomDrawable(getContext(), connectingResId, speed);
        if(connectedResId!=-1)
        connectedRes = new CustomDrawable(getContext(), connectedResId, speed);
        setStatus(STATUS_CONNECT);
//        }).start();

    }

    private void showShadow(Boolean b){
        if(b){
            setRadius(radius);
            setCardElevation(elevation);
            setMaxCardElevation(maxElevation);
        }else {
            setRadius(0f);
            setCardElevation(0f);
            setMaxCardElevation(0f);
        }
    }

    public void setStatus(int status) {
        switch (status) {
            case STATUS_CONNECT:
                setBackgroundResource(connectRes,shadowConnect);
                try {
                    connectingRes.clear();
                    connectedRes.clear();

                }catch (Exception e){};
                setClickable(true);
                break;
            case STATUS_CONNECTING:
                setBackgroundResource(connectingRes,shadowConnecting);
                try {

                    connectRes.clear();
                    connectedRes.clear();

                }catch (Exception e){};
                setClickable(false);
                handler.postDelayed(runnable, 5000);
                break;
            case STATUS_CONNECTED:
                setBackgroundResource(connectedRes,shadowConnected);
                try {


                    connectingRes.clear();
                    connectRes.clear();

            }catch (Exception e){};
                setClickable(false);
                handler.postDelayed(runnable, 5000);
                break;
            default:
                return;
        }
        currentStatus = status;
    }

    public int getStatus() {
        return currentStatus;
    }
    public void setBackgroundResource(LottieDrawable resource,boolean b) {
        if (resource == null)
            return;
        post(() -> {
                connectButtonView.setImageDrawable(resource);
                resource.start();
            showShadow(b);
            setVisibility(VISIBLE);
        });
    }


    private void setBackgroundResource(CustomDrawable resource,boolean b) {
        if (resource == null)
            return;
        post(() -> {
            if (resource.isLottie()) {
                connectButtonView.setImageDrawable(resource.getLottieDrawable());
                resource.getLottieDrawable().start();
            } else {
                connectButtonView.setImageDrawable(resource.getDrawable());
//
//                if (frameAnimation != null && frameAnimation.isRunning()) {
//                    frameAnimation.stop();
//                    frameAnimation = null;
//
//                }
//                try {
//                    frameAnimation = (AnimationDrawable) getBackground();
//                    if (frameAnimation != null)
//                        frameAnimation.start();
//                } catch (ClassCastException e) {
////                    return;
//                }
            }

            showShadow(true);
            setVisibility(VISIBLE);
        });
    }

//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (frameAnimation == null)
//            return;
//        if (frameAnimation.isRunning())
//            return;
//        post(() -> frameAnimation.start());
//
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (frameAnimation == null)
//            return;
//        if (!frameAnimation.isRunning())
//            return;
//        post(() -> frameAnimation.stop());
//
//    }

    private LottieDrawable createLottieDrawable(int id, float speed) {
        final LottieDrawable lottieDrawable = new LottieDrawable();
        LottieCompositionFactory.fromRawRes(getContext(), id).addListener(new LottieListener<LottieComposition>() {
            @Override
            public void onResult(LottieComposition result) {
                lottieDrawable.setComposition(result);
                lottieDrawable.addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        super.onAnimationRepeat(animation);
                        Log.d("nhatnhat", "onAnimationRepeat: ");
                        if(id==connectingResId && startFrameConnecting >0 && endFrameConnecting >0){
                            lottieDrawable.setMinAndMaxFrame(startFrameConnecting,endFrameConnecting);
                        }else if(id==connectedResId&& startFrameConnected >0 && endFrameConnected >0){
                            lottieDrawable.setMinAndMaxFrame(startFrameConnected,endFrameConnected);
                        }else if(id==connectResId&& startFrameConnect >0 && endFrameConnect >0){
                            lottieDrawable.setMinAndMaxFrame(startFrameConnect,endFrameConnect);
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        lottieDrawable.setMinFrame(0);
                        lottieDrawable.setMaxFrame(Integer.MAX_VALUE);
                    }
                });
                lottieDrawable.setRepeatCount(LottieDrawable.INFINITE);
                lottieDrawable.setSpeed(speed);
            }
        });

        return lottieDrawable;
    }

    private LottieDrawable createLottieDrawable(int id) {
        return createLottieDrawable(id, 1f);
    }

    private class CustomDrawable {
        LottieDrawable lottieDrawable;
        Drawable drawable;
        boolean isLottie = false;


        public CustomDrawable(Context context, int id) {
            Log.d("ICT", "CustomDrawable  " + id);
            if (ResourceHelper.checkTypeId(context, id, ResourceHelper.TYPE_RAW)) {
                lottieDrawable = createLottieDrawable(id);
                isLottie = true;
            } else
                drawable = getResources().getDrawable(id);
        }

        public CustomDrawable(Context context, int id, float speed) {
            Log.d("ICT", "CustomDrawable  " + id);
            if (ResourceHelper.checkTypeId(context, id, ResourceHelper.TYPE_RAW)) {
                lottieDrawable = createLottieDrawable(id, speed);
                isLottie = true;
            } else
                drawable = getResources().getDrawable(id);
        }


        public Drawable getDrawable() {
            return drawable;
        }
        public LottieDrawable getLottieDrawable() {
            return lottieDrawable;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }

        public boolean isLottie() {
            return isLottie;
        }
        public void clear(){
            if(lottieDrawable!=null){
                lottieDrawable.cancelAnimation();
            }
        }
    }

}
