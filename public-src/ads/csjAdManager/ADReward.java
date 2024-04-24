package ads.csjAdManager;
//激励广告 激励视频


import android.app.Activity;
import android.os.Bundle;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationAdEcpmInfo;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationBaseManager;

import java.util.ArrayList;

public class ADReward {
    private static ADReward instance;
    private AdMain m_mainInstance;

    private final CallBack m_adCallback;
    // LoadAd加载完成的所有广告
    private final ArrayList<TTRewardVideoAd> m_ad = new ArrayList<TTRewardVideoAd>();
    // 当前正在播放的广告或立刻准备下一个播放的广告
    private TTRewardVideoAd m_currectAd;
    private String m_id;
    private AdMainCallBack m_adMainCallBack;
    private boolean canPreLoad = false;
    private int preLoadADNum = 1;

    public static ADReward getInstance(){
        if(instance == null){
            instance = new ADReward();
        }
        return instance;
    }

    ADReward(){
        m_mainInstance = AdMain.getInstance();
        m_adCallback = new CallBack();
    }

    public void EnablePreLoad(boolean enable){
        canPreLoad = enable;
    }

    public void EnablePreLoad(boolean enable,int num){
        canPreLoad = enable;
        preLoadADNum = num;
    }

    //加载激励视频
    public AdMainCallBack LoadAd(String id) {
        m_id = id;
        if(m_adMainCallBack == null){
            m_adMainCallBack = new AdMainCallBack();
        }

        // 已经大于预加载数量了
        if(canPreLoad && m_ad.size() > preLoadADNum){
            return m_adMainCallBack;
        }

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(id)  //广告位ID
                .setOrientation(TTAdConstant.VERTICAL)  //激励视频方向  //横竖屏设置
                .setAdLoadType(canPreLoad? TTAdLoadType.PRELOAD : TTAdLoadType.LOAD)
                .build();

        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(AdMain.getInstance().getGameCtx());
        //这里为激励视频的简单功能，如需使用复杂功能，如gromore的服务端奖励验证，请参考demo中的AdUtils.kt类中激励部分
        adNativeLoader.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                //广告加载失败
                m_mainInstance.DebugPrintE("%s 广告加载失败 Code:%d Msg:%s" , "激励广告",errorCode,errorMsg);
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onError(AdMainCallBack.LoadStatusType.NONE, null,errorCode,errorMsg);
                }
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
                //可能是开始加载广告
                // 文档指示 可以从这里显示广告
                m_mainInstance.DebugPrintI("%s onRewardVideoAdLoad" , "激励广告");
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.LOAD, ttRewardVideoAd);
                }
            }

            @Override
            public void onRewardVideoCached() {
                //广告缓存成功 此api已经废弃，请使用onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd)
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {
                //广告缓存成功 在此回调中进行广告展示
                m_ad.add(ttRewardVideoAd);
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.CACHE, ttRewardVideoAd);
                }
                //showRewardAd((Activity)AdMain.getInstance().getGameCtx(), ttRewardVideoAd);
            }
        });
        return m_adMainCallBack;
    }

    /***
     * 放在 ShowAd 前面才有效
     * @return
     */
    public CallBack buildListen(){
        return m_adCallback;
    }


    //展示激励视频
    public void ShowAd() {
        Activity activity = (Activity)AdMain.getInstance().getGameCtx();
        if (m_ad.isEmpty()) {
            if(canPreLoad && !m_id.isEmpty()){
                LoadAd(m_id);
            }
            if(m_adCallback.rewardSimpleCall != null) m_adCallback.rewardSimpleCall.onError(null);
            m_mainInstance.DebugPrintE("%s m_ad.isEmpty()" , "激励广告");
            return;
        }

        m_currectAd = m_ad.remove(0);
        if(canPreLoad && m_ad.size()<=preLoadADNum){
            LoadAd(m_id);
        }

        TTRewardVideoAd.RewardAdInteractionListener listen = new TTRewardVideoAd.RewardAdInteractionListener() {
            @Override
            public void onAdShow() {
                //广告展示
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onAdShow();
                //获取展示广告相关信息，需要再show回调之后进行获取
                MediationBaseManager manager = m_currectAd.getMediationManager();
                if (manager != null && manager.getShowEcpm() != null) {
                    MediationAdEcpmInfo showEcpm = manager.getShowEcpm();
                    String ecpm = showEcpm.getEcpm(); //展示广告的价格
                    String sdkName = showEcpm.getSdkName();  //展示广告的adn名称
                    String slotId = showEcpm.getSlotId(); //展示广告的代码位ID
                }
            }

            @Override
            public void onAdVideoBarClick() {
                //广告点击
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onAdVideoBarClick();
                m_mainInstance.DebugPrintI("%s 广告点击" , "激励广告");
            }

            @Override
            public void onAdClose() {
                //广告关闭
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onAdClose();
                m_currectAd.getMediationManager().destroy();
                m_mainInstance.DebugPrintI("%s 广告关闭" , "激励广告");
            }

            @Override
            public void onVideoComplete() {
                //广告视频播放完成
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onVideoComplete();
                m_mainInstance.DebugPrintI("%s 广告视频播放完成" , "激励广告");
            }

            @Override
            public void onVideoError() {
                //广告视频错误
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onVideoError();
                m_mainInstance.DebugPrintE("%s 广告视频错误" , "激励广告");
            }

            @Override
            public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                //奖励发放 已废弃 请使用 onRewardArrived 替代
            }

            @Override
            public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                //奖励发放
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onRewardArrived(isRewardValid, rewardType, extraInfo);
                if (isRewardValid) {
                    // 验证通过
                    // 从extraInfo读取奖励信息
                    if(m_adCallback.rewardSimpleCall != null) m_adCallback.rewardSimpleCall.onSuccess(m_currectAd);
                    m_mainInstance.DebugPrintI("%s 奖励已经获得" , "激励广告");

                } else {
                    // 未验证通过
                    if(m_adCallback.rewardSimpleCall != null) m_adCallback.rewardSimpleCall.onError(m_currectAd);
                    m_mainInstance.DebugPrintI("%s 奖励未获得" , "激励广告");
                }
            }

            @Override
            public void onSkippedVideo() {
                //广告跳过
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onSkippedVideo();
                m_mainInstance.DebugPrintI("%s 跳过了广告" , "激励广告");
            }
        };

        m_currectAd.setRewardAdInteractionListener(listen);

        // 这一行可能是广告二开的回调
        m_currectAd.setRewardPlayAgainInteractionListener(listen);

        m_currectAd.showRewardVideoAd(activity); //展示激励视频
    }

    // 激励类型广告播放简单回调
    public interface RewardCallBackListen{
        void onSuccess(TTRewardVideoAd ad);
        void onError(TTRewardVideoAd ad); // 错误
    }
    // 广告播放完整回调
    public interface AdShowListener extends TTRewardVideoAd.RewardAdInteractionListener {
        @Override
        public void onAdShow();
        @Override
        public void onAdVideoBarClick();
        @Override
        public void onAdClose();
        @Override
        public void onVideoComplete();
        @Override
        public void onVideoError();
        @Override
        public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo);
        @Override
        public void onSkippedVideo();
    }

    public class CallBack{

        public RewardCallBackListen rewardSimpleCall;

        /**
         * 激励类型视频中特有的回调, 激励视频是否播放成功,是否给予奖励
         * @param call  回调函数
         * @return  返回回调接口,方便继续接入其他回调
         */
        public CallBack RewardHandle(RewardCallBackListen call){
            rewardSimpleCall = call;
            return this;
        }

        public AdShowListener adShowListenerCall;

        public CallBack ShowStatusHandle(AdShowListener call){
            adShowListenerCall = call;
            return this;
        }
    }

}
