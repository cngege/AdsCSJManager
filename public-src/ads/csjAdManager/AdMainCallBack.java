﻿package ads.csjAdManager;


import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;

public class AdMainCallBack {

    public SDKInitCallBack sdkInitCallBack;

    public AdLoadStatusCallBack adLoadStatusCallBack;

    public enum LoadStatusType{
        NONE,       //未指示是什么类型 一般是onError
        LOAD,       //加载成功或错误
        RENDER,     //渲染成功或错误
        CACHE       //缓存成功或错误
    }


    public interface SDKInitCallBack{
        void onSuccess();
        void onError(int i, String e);
    }

    public interface AdLoadStatusCallBack{
        void onSuccess(LoadStatusType type, Object obj);
        void onError(LoadStatusType type, Object obj, int i, String e);
    }


    public void Handler(SDKInitCallBack call){
        sdkInitCallBack = call;
    }

    public AdMainCallBack Handler(AdLoadStatusCallBack call){
        adLoadStatusCallBack = call;
        return this;
    }























    public interface AdStatusListener extends TTFullScreenVideoAd.FullScreenVideoAdInteractionListener {
        @Override
        public void onAdShow();
        @Override
        public void onAdVideoBarClick();
        @Override
        public void onAdClose();
        @Override
        public void onVideoComplete();
        @Override
        public void onSkippedVideo();
    }
}
