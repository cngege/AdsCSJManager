package ads.csjAdManager2;
//激励广告 激励视频


import android.app.Activity;
import android.os.Bundle;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationAdEcpmInfo;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationBaseManager;

public class ADReward {
    private static ADReward instance;
    private AdMain m_mainInstance;
    private TTRewardVideoAd ad;
    private AdMainCallBack m_adMainCallBack;

    public static ADReward getInstance(){
        if(instance == null){
            instance = new ADReward();
            instance.m_mainInstance = AdMain.getInstance();
        }
        return instance;
    }


    //加载激励视频
    public AdMainCallBack LoadAd(String id) {
        if(m_adMainCallBack == null){
            m_adMainCallBack = new AdMainCallBack();
        }

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(id)  //广告位ID
                .setOrientation(TTAdConstant.VERTICAL)  //激励视频方向  //横竖屏设置
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
                ad = ttRewardVideoAd;
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.CACHE, ttRewardVideoAd);
                }
                //showRewardAd((Activity)AdMain.getInstance().getGameCtx(), ttRewardVideoAd);
            }
        });
        return m_adMainCallBack;
    }

    //展示激励视频
    public void ShowAd() {
        Activity activity = (Activity)AdMain.getInstance().getGameCtx();
        if (activity == null || ad == null) {
            m_mainInstance.DebugPrintE("%s act == null || ttRewardVideoAd == null" , "激励广告");
            return;
        }

        TTRewardVideoAd.RewardAdInteractionListener listen = new TTRewardVideoAd.RewardAdInteractionListener() {
            @Override
            public void onAdShow() {
                //广告展示
                //获取展示广告相关信息，需要再show回调之后进行获取
                MediationBaseManager manager = ad.getMediationManager();
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
                m_mainInstance.DebugPrintI("%s 广告点击" , "激励广告");
            }

            @Override
            public void onAdClose() {
                //广告关闭
                m_mainInstance.DebugPrintI("%s 广告关闭" , "激励广告");
                ad.getMediationManager().destroy();

            }

            @Override
            public void onVideoComplete() {
                //广告视频播放完成
                m_mainInstance.DebugPrintI("%s 广告视频播放完成" , "激励广告");
            }

            @Override
            public void onVideoError() {
                //广告视频错误
                m_mainInstance.DebugPrintE("%s 广告视频错误" , "激励广告");
            }

            @Override
            public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                //奖励发放 已废弃 请使用 onRewardArrived 替代
            }

            @Override
            public void onRewardArrived(boolean isRewardValid, int rewardType, Bundle extraInfo) {
                //奖励发放
                if (isRewardValid) {
                    // 验证通过
                    // 从extraInfo读取奖励信息
                    m_mainInstance.DebugPrintI("%s 奖励已经获得" , "激励广告");

                } else {
                    // 未验证通过
                    m_mainInstance.DebugPrintI("%s 奖励未获得" , "激励广告");
                }
            }

            @Override
            public void onSkippedVideo() {
                //广告跳过
                m_mainInstance.DebugPrintI("%s 跳过了广告" , "激励广告");
            }
        };

        ad.setRewardAdInteractionListener(listen);

        ad.setRewardPlayAgainInteractionListener(listen);

        ad.showRewardVideoAd(activity); //展示激励视频
    }

}
