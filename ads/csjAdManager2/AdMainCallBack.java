package ads.csjAdManager2;

import android.support.annotation.Nullable;

public class AdMainCallBack {

    public SDKInitCallBack sdkInitCallBack;

    public AdLoadStatusCallBack adLoadStatusCallBack;

    public enum LoadStatusType{
        NONE,       //未指示是什么类�?一般是onError
        LOAD,       //加载成功或错�?
        RENDER,     //渲染成功或错�?
        CACHE       //缓存成功或错�?
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

    public void Handler(AdLoadStatusCallBack call){
        adLoadStatusCallBack = call;
    }


}
