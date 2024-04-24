package ads.csjAdManager;
// 横幅广告

import android.app.Activity;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.ArrayList;
import java.util.List;

public class AdBanner {

    private static AdBanner instance;
    private AdMain m_mainInstance;
    private AdMainCallBack m_adMainCallBack;
    private final ArrayList<TTNativeExpressAd> m_ad = new ArrayList<>();
    private TTNativeExpressAd m_currectAd;
    private String m_id;
    // 广告显示时相关回调接口
    private final CallBack m_adCallback;
    private FrameLayout m_frameLayout;
    private boolean canPreLoad = false;
    private int preLoadADNum = 1;
    // 横幅广告显示位置 UP CENTER DOWN
    private String pos = "UP";

    public static AdBanner getInstance(){
        if(instance == null){
            instance = new AdBanner();
        }
        return instance;
    }
    AdBanner(){
        m_mainInstance = AdMain.getInstance();
        m_adCallback = new CallBack();
        m_frameLayout = new FrameLayout(m_mainInstance.getGameCtx());
        //m_frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT));
    }

    public void EnablePreLoad(boolean enable){
        canPreLoad = enable;
    }

    public void EnablePreLoad(boolean enable,int num){
        canPreLoad = enable;
        preLoadADNum = num;
    }

    /**
     * 设置横幅在屏幕上的显示位置
     * @param p UP/DOWN
     */
    public void setShowPosition(String p){
        pos = p;
    }

    public AdMainCallBack LoadAd(String id){
        m_id = id;
        if(m_adMainCallBack == null){
            m_adMainCallBack = new AdMainCallBack();
        }

        Point screenSize = m_mainInstance.getScreen();
        AdSlot adSolt = new AdSlot.Builder()
                .setCodeId(id)  //广告位ID
                .setImageAcceptedSize(screenSize.x,0)  //设置广告宽高 单位px
                .supportRenderControl()  //支持模板样式
                .setAdCount(preLoadADNum)
                .build();

        //加载Banner广告
        TTAdNative adNativeLoader_banner = TTAdSdk.getAdManager().createAdNative(m_mainInstance.getGameCtx());
        adNativeLoader_banner.loadBannerExpressAd(adSolt, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                //广告加载失败
                m_mainInstance.DebugPrintE("banner广告 广告加载失败 code:" +errorCode + " " +errorMsg);
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onError(AdMainCallBack.LoadStatusType.NONE, null,errorCode,errorMsg);
                }
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                //广告加载成功
                if (list != null && !list.isEmpty()) {
                    m_ad.addAll(list);

                    if(m_adMainCallBack.adLoadStatusCallBack != null){
                        m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.LOAD, list);
                    }
                }
                else{
                    m_mainInstance.DebugPrintI("banner广告 广告加载成功 但是列表中没有内容");
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

    //展示Banner广告
    public void ShowAd() {
        FrameLayout container = AdMain.getInstance().getMainView();
        if(container == null){
            m_mainInstance.DebugPrintE("banner广告 container == null");
        }
        if (m_ad.isEmpty()) {
            if(canPreLoad && !m_id.isEmpty()){
                LoadAd(m_id);
            }
            m_mainInstance.DebugPrintE("banner广告 m_ad.isEmpty()");
            return;
        }
        m_currectAd = m_ad.remove(0);
        if(canPreLoad && m_ad.size()<=preLoadADNum){
            LoadAd(m_id);
        }

        m_currectAd.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int i) {
                //广告点击
                if(m_adCallback.adShowListenerCall!=null) m_adCallback.adShowListenerCall.onAdClicked(view, i);
                m_mainInstance.DebugPrintI("banner广告点击");
            }

            @Override
            public void onAdShow(View view, int i) {
                //广告展示
                if(m_adCallback.adShowListenerCall!=null) m_adCallback.adShowListenerCall.onAdShow(view, i);
                m_mainInstance.DebugPrintI("banner广告展示");
                //获取展示广告相关信息，需要再show回调之后进行获取
//                MediationBaseManager manager = bannerAd.getMediationManager();
//                if (manager != null && manager.getShowEcpm() != null) {
//                    MediationAdEcpmInfo showEcpm = manager.getShowEcpm();
//                    String ecpm = showEcpm.getEcpm(); //展示广告的价格
//                    String sdkName = showEcpm.getSdkName();  //展示广告的adn名称
//                    String slotId = showEcpm.getSlotId(); //展示广告的代码位ID
//                }
                //用户点击x之后...
            }

            @Override
            public void onRenderFail(View view, String s, int i) {
                //广告渲染失败
                if(m_adCallback.adShowListenerCall!=null) m_adCallback.adShowListenerCall.onRenderFail(view,s, i);
                m_mainInstance.DebugPrintE("banner广告渲染失败");
            }

            @Override
            public void onRenderSuccess(View view, float w, float h) {
                //广告渲染成功
                if(m_adCallback.adShowListenerCall!=null) m_adCallback.adShowListenerCall.onRenderSuccess(view, w, h);
                m_mainInstance.DebugPrintI("banner广告渲染成功");

                View bannerView = m_currectAd.getExpressAdView(); //获取Banner View
                if (bannerView != null) {
                    //bannerView.
                    m_mainInstance.DebugPrintI("banner广告 添加到视图中（w/h " + w + ' ' + h + ")");

                    if(pos.equals("UP")){
                        bannerView.setX(0);
                        bannerView.setY(0);
                    }else if(pos.equals("DOWN")){
                        bannerView.setX(0);
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
                        lp.bottomMargin = 0;
                        lp.gravity = Gravity.BOTTOM;
                        bannerView.setLayoutParams(lp);
                    }
                    //m_frameLayout.setY(500);
                    //m_frameLayout.setBackgroundColor(0xfff);
                    //FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
                    //lp.bottomMargin = 0;
                    //lp.gravity = Gravity.BOTTOM;
                    //m_frameLayout.setLayoutParams(lp);
                    //m_frameLayout.addView(bannerView);
                    container.addView(bannerView);
                    //container.addView(m_frameLayout);
                }

            }
        });

        // 用户点击不喜欢按钮回调
        m_currectAd.setDislikeCallback((Activity)AdMain.getInstance().getGameCtx(), new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {
                if(m_adCallback.adDislikeListenerCall!=null) m_adCallback.adDislikeListenerCall.onShow();
                m_mainInstance.DebugPrintI("banner广告onShow");
            }
            @Override
            public void onSelected(int position, String value, boolean enforce) {
                //用户点击了dislike按钮 点击了不喜欢按钮
                //container.removeAllViews();
                //可能在这里写关闭逻辑
                if(m_adCallback.adDislikeListenerCall!=null) m_adCallback.adDislikeListenerCall.onSelected(position,value,enforce);
                m_mainInstance.DebugPrintI("banner广告onSelected " + position + " " + value);
                if (container != null) {
                    container.removeView(m_currectAd.getExpressAdView()); // 移除获取到的 模板广告视图
                }
                m_currectAd.destroy();
            }
            @Override
            public void onCancel() {
                if(m_adCallback.adDislikeListenerCall!=null) m_adCallback.adDislikeListenerCall.onCancel();
                m_mainInstance.DebugPrintI("banner广告onCancel");
            }
        });
        m_currectAd.render();
    }
    // 激励类型广告播放简单回调
//    public interface BannerCallBackListen{
//        void onSuccess(TTFullScreenVideoAd ad);
//        void onError(TTFullScreenVideoAd ad); // 错误
//    }
    // 广告播放完整回调
    public interface AdShowListener extends TTNativeExpressAd.ExpressAdInteractionListener {
        @Override
        public void onAdClicked(View view, int i);
        @Override
        public void onAdShow(View view, int i);
        @Override
        public void onRenderFail(View view, String s, int i);
        @Override
        public void onRenderSuccess(View view, float w, float h);
    }

    public interface AdDislikeListener extends TTAdDislike.DislikeInteractionCallback{
        @Override
        public void onShow();

        @Override
        public void onSelected(int position, String value, boolean enforce);

        @Override
        public void onCancel();
    }

    public class CallBack{

        //public RewardCallBackListen rewardSimpleCall;

        /**
         * 激励类型视频中特有的回调, 激励视频是否播放成功,是否给予奖励
         * @param call  回调函数
         * @return  返回回调接口,方便继续接入其他回调
         */
//        public AdBanner.CallBack RewardHandle(AdBanner.RewardCallBackListen call){
//            rewardSimpleCall = call;
//            return this;
//        }

        public AdShowListener adShowListenerCall;
        public CallBack ShowStatusHandle(AdShowListener call){
            adShowListenerCall = call;
            return this;
        }

        public AdDislikeListener adDislikeListenerCall;
        public CallBack adDislikeHandle(AdDislikeListener call){
            adDislikeListenerCall = call;
            return this;
        }
    }
}
