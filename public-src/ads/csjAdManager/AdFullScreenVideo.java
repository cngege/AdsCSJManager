package ads.csjAdManager;
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

import java.util.ArrayList;
import java.util.List;

public class AdFullScreenVideo {
// 注意 ： 同一种类型的广告同时播放（比如横幅）不要使用单例模式

    // 单例
    private static AdFullScreenVideo instance;
    private final AdMain m_mainInstance;
    // 广告显示时相关回调接口
    private final CallBack m_adCallback;
    // LoadAd加载完成的所有广告
    private final ArrayList<TTFullScreenVideoAd> m_ad = new ArrayList<TTFullScreenVideoAd>();
    // 当前正在播放的广告或立刻准备下一个播放的广告
    private TTFullScreenVideoAd m_currectAd;
    private String m_id;
    private AdMainCallBack m_adMainCallBack;

    private boolean canPreLoad = false;
    private int preLoadADNum = 1;

    // 预设的广告监听器
    private TTFullScreenVideoAd.FullScreenVideoAdInteractionListener m_statusListen;

    public static AdFullScreenVideo getInstance(){
        if(instance == null){
            instance = new AdFullScreenVideo();
        }
        return instance;
    }

    AdFullScreenVideo(){
        m_mainInstance = AdMain.getInstance();
        m_adCallback = new AdFullScreenVideo.CallBack();
    }

    public void EnablePreLoad(boolean enable){
        canPreLoad = enable;
    }

    public void EnablePreLoad(boolean enable,int num){
        canPreLoad = enable;
        preLoadADNum = num;
    }

    @Deprecated
    public void setPreLoadADNum(int num){
        preLoadADNum = num;
    }

    public AdMainCallBack LoadAd(String id){
        m_id = id;
        if(m_adMainCallBack == null){
            m_adMainCallBack = new AdMainCallBack();
        }
        // 已经大于预加载数量了
        if(canPreLoad && m_ad.size() > preLoadADNum){
            return m_adMainCallBack;
        }

        //加载插全屏广告
        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(AdMain.getInstance().getGameCtx());
        adNativeLoader.loadFullScreenVideoAd(new AdSlot.Builder()
                .setCodeId(id)
                .setOrientation(TTAdConstant.VERTICAL)
                .setMediationAdSlot(new MediationAdSlot.Builder().setMuted(false).build())
                .build(), new TTAdNative.FullScreenVideoAdListener() {
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
                m_ad.add(ttFullScreenVideoAd);
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.CACHE, ttFullScreenVideoAd);
                }
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

    //展示插全屏广告
    public void ShowAd() {
        Activity activity = (Activity)AdMain.getInstance().getGameStaticCtx();
        if (m_ad.isEmpty()) {
            if(canPreLoad && !m_id.isEmpty()){
                LoadAd(m_id);
            }
            if(m_adCallback.rewardSimpleCall != null) m_adCallback.rewardSimpleCall.onError(null);
            m_mainInstance.DebugPrintE("[%s] m_ad.isEmpty()" , "插全屏广告");
            return;
        }
        m_currectAd = m_ad.remove(0);
        if(canPreLoad && m_ad.size()<=preLoadADNum){
            LoadAd(m_id);
        }

        m_currectAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
            @Override
            public void onAdShow() {
                //广告展示
//                MediationBaseManager manager = ad.getMediationManager();
//                //获取展示广告相关信息，需要再show回调之后进行获取
//                if (manager != null && manager.getShowEcpm() != null) {
//                    MediationAdEcpmInfo showEcpm = manager.getShowEcpm();
//                    String ecpm = showEcpm.getEcpm(); //展示广告的价格
//                    String sdkName = showEcpm.getSdkName();  //展示广告的adn名称
//                    String slotId = showEcpm.getSlotId(); //展示广告的代码位ID
//                }
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onAdShow();
            }

            @Override
            public void onAdVideoBarClick() {
                //广告点击 点击的是广告中让你点击的指定内容
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onVideoComplete();
                AdMain.getInstance().DebugPrintI("调试-广告点击");
            }

            @Override
            public void onAdClose() {
                //广告关闭
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onAdClose();
                m_currectAd.getMediationManager().destroy();
                // 播放完毕检测是否要预加载
                if(canPreLoad && m_ad.size()<=preLoadADNum){
                    LoadAd(m_id);
                }
                AdMain.getInstance().DebugPrintI("调试-广告关闭");
            }

            @Override
            public void onVideoComplete() {
                //广告视频播放完成
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onVideoComplete();
                if(m_adCallback.rewardSimpleCall != null) m_adCallback.rewardSimpleCall.onSuccess(m_currectAd);
                //adOver();     //广告播放完成不自动关闭
                AdMain.getInstance().DebugPrintI("调试-广告播放完成");
            }

            @Override
            public void onSkippedVideo() {
                //广告跳过
                if(m_adCallback.adShowListenerCall != null) m_adCallback.adShowListenerCall.onSkippedVideo();
                if(m_adCallback.rewardSimpleCall != null) m_adCallback.rewardSimpleCall.onError(m_currectAd);
                AdMain.getInstance().DebugPrintI("调试-广告跳过");
            }
        });

        m_currectAd.showFullScreenVideoAd(activity); //展示插全屏广告
    }

    // 激励类型广告播放简单回调
    public interface RewardCallBackListen{
        void onSuccess(TTFullScreenVideoAd ad);
        void onError(TTFullScreenVideoAd ad); // 错误
    }
    // 广告播放完整回调
    public interface AdShowListener extends TTFullScreenVideoAd.FullScreenVideoAdInteractionListener {
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

    public class CallBack{

        public RewardCallBackListen rewardSimpleCall;

        /**
         * 激励类型视频中特有的回调, 激励视频是否播放成功,是否给予奖励
         * @param call  回调函数
         * @return  返回回调接口,方便继续接入其他回调
         */
        public CallBack RewardHandle(AdFullScreenVideo.RewardCallBackListen call){
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