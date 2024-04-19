package ads.csjAdManager2;
// 插全屏广告

import android.app.Activity;
import android.content.Context;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationAdEcpmInfo;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationBaseManager;

public class AdFullScreenVideo {

    private static AdFullScreenVideo instance;
    private AdMain m_mainInstance;
    private TTFullScreenVideoAd ad;
    private AdMainCallBack m_adMainCallBack;

    public static AdFullScreenVideo getInstance(){
        if(instance == null){
            instance = new AdFullScreenVideo();
            instance.m_mainInstance = AdMain.getInstance();
        }
        return instance;
    }

    public AdMainCallBack LoadAd(String id){
        if(m_adMainCallBack == null){
            m_adMainCallBack = new AdMainCallBack();
        }

        //加载插全屏广告
        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(AdMain.getInstance().getGameCtx());
        adNativeLoader.loadFullScreenVideoAd(new AdSlot.Builder().setCodeId(id).setOrientation(TTAdConstant.VERTICAL).setMediationAdSlot(new MediationAdSlot.Builder().setMuted(false).build()).build(), new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                //广告加载失败
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onError(AdMainCallBack.LoadStatusType.NONE, null,errorCode,errorMsg);
                }
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
                //广告加载成功
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.LOAD, ttFullScreenVideoAd);
                }
            }

            @Override
            public void onFullScreenVideoCached() {
                //广告缓存成功 此api已经废弃，请使用onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd)
            }

            @Override
            public void onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd) {
                //广告缓存成功 在此回调中进行广告展示
                ad = ttFullScreenVideoAd;
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.CACHE, ttFullScreenVideoAd);
                }
            }
        });
        return m_adMainCallBack;
    }


    //展示插全屏广告
    public void ShowAd() {

        Activity activity = (Activity)AdMain.getInstance().getGameCtx();
        if (activity == null || ad == null) {
            m_mainInstance.DebugPrintE("%s act == null || ttRewardVideoAd == null" , "插全屏广告");
            return;
        }

        ad.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
            @Override
            public void onAdShow() {
                //广告展示
                MediationBaseManager manager = ad.getMediationManager();
                //获取展示广告相关信息，需要再show回调之后进行获取
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
            }

            @Override
            public void onAdClose() {
                //广告关闭
            }

            @Override
            public void onVideoComplete() {
                //广告视频播放完成
            }

            @Override
            public void onSkippedVideo() {
                //广告跳过
            }
        });
        ad.showFullScreenVideoAd(activity); //展示插全屏广告
    }

}
