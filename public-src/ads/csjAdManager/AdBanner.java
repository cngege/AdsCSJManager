package ads.csjAdManager;
// 横幅广告

import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;

public class AdBanner {

    private static AdBanner instance;
    private AdMain m_mainInstance;
    private AdMainCallBack m_adMainCallBack;
    private TTNativeExpressAd ad;

    public static AdBanner getInstance(){
        if(instance == null){
            instance = new AdBanner();
            instance.m_mainInstance = AdMain.getInstance();
        }
        return instance;
    }

    public AdMainCallBack LoadAd(String id){
        if(m_adMainCallBack == null){
            m_adMainCallBack = new AdMainCallBack();
        }

        Point screenSize = m_mainInstance.getScreen();
        AdSlot adSolt = new AdSlot.Builder()
                .setCodeId(id)  //广告位ID
                .setImageAcceptedSize(screenSize.x,0)  //设置广告宽高 单位px
                .supportRenderControl()  //支持模板样式
                .setAdCount(1)
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
                    ad = list.get(0);
                    if(m_adMainCallBack.adLoadStatusCallBack != null){
                        m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.LOAD, list);
                    }
                    //showBannerView((Activity)m_mainInstance.getGameCtx(), list.get(0), m_mainInstance.getMainView()); //注 ：bannerContainer为展示Banner广告的容器
                }
                else{
                    m_mainInstance.DebugPrintI("banner广告 广告加载成功 但是列表中没有内容");
                }
            }
        });
        return m_adMainCallBack;
    }

    //展示Banner广告
    public void ShowAd() {
        FrameLayout container = AdMain.getInstance().getMainView();
        if (ad == null || container == null) {
            m_mainInstance.DebugPrintE("banner广告 showBannerView bannerAd == null || container == null");
            return;
        }

        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int i) {
                //广告点击
                m_mainInstance.DebugPrintI("banner广告点击");
            }

            @Override
            public void onAdShow(View view, int i) {
                //广告展示
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
                m_mainInstance.DebugPrintE("banner广告渲染失败");
            }

            @Override
            public void onRenderSuccess(View view, float w, float h) {
                //广告渲染成功
                m_mainInstance.DebugPrintI("banner广告渲染成功");

                View bannerView = ad.getExpressAdView(); //获取Banner View
                if (bannerView != null) {
                    //bannerView.
                    m_mainInstance.DebugPrintI("banner广告 添加到视图中（w/h " + w + ' ' + h + ")");
                    container.addView(bannerView);
                }

            }
        });

        // 用户点击不喜欢按钮回调
        ad.setDislikeCallback((Activity)AdMain.getInstance().getGameCtx(), new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {
                m_mainInstance.DebugPrintI("banner广告onShow");
            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                //用户点击了dislike按钮 点击了不喜欢按钮
                //container.removeAllViews();
                //可能在这里写关闭逻辑
                m_mainInstance.DebugPrintI("banner广告onSelected " + position + " " + value);
                ad.destroy();
                container.removeView(ad.getExpressAdView()); // 移除获取到的 模板广告视图
            }

            @Override
            public void onCancel() {
                m_mainInstance.DebugPrintI("banner广告onCancel");
            }
        });
        ad.render();
    }

}
