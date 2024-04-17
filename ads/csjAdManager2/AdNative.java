package ads.csjAdManager2;
// 开屏广�?

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationAdEcpmInfo;
import com.bytedance.sdk.openadsdk.mediation.manager.MediationBaseManager;

public class AdNative {

    private static AdNative instance;

    private AdMain m_mainInstance;

    private CSJSplashAd ad;

    private AdMainCallBack m_adMainCallBack;


    // 获取&创建单例�?
    public static AdNative getInstance(){
        if(instance == null){
            instance = new AdNative();
            instance.m_mainInstance = AdMain.getInstance();
        }
        return instance;
    }

    public AdMainCallBack LoadAd(String id){
        //加载开屏广�?
        if(m_adMainCallBack == null){
            m_adMainCallBack = new AdMainCallBack();
        }

        Point screenSize = m_mainInstance.getScreen();
        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(m_mainInstance.getGameCtx());
        adNativeLoader.loadSplashAd(new AdSlot.Builder()
                .setCodeId(id) //广告位ID
                .setImageAcceptedSize(screenSize.x,screenSize.y)  //设置广告宽高 单位px
                .build(), new TTAdNative.CSJSplashAdListener() {

            @Override
            public void onSplashLoadSuccess(CSJSplashAd csjSplashAd) {
                m_mainInstance.DebugPrintI("[%s] 广告加载成功","开屏广�?);
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.LOAD, csjSplashAd);
                }
            }

            @Override
            public void onSplashLoadFail(CSJAdError csjAdError) {
                //广告加载失败
                m_mainInstance.DebugPrintE("[%s] 广告加载失败 Code: %d Msg: %s","开屏广�?, csjAdError.getCode(),csjAdError.getMsg());
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onError(AdMainCallBack.LoadStatusType.LOAD, csjAdError,0, null);
                }
            }

            @Override
            public void onSplashRenderSuccess(CSJSplashAd csjSplashAd) {
                //广告渲染成功，在此展示广�?
                m_mainInstance.DebugPrintI("[%s] 广告渲染成功","开屏广�?);
                ad = csjSplashAd;
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.RENDER, csjSplashAd);
                }
                //ShowAd(csjSplashAd, m_mainInstance.getMainView()); //�?：splashContainer为展示Banner广告的容�?
            }

            @Override
            public void onSplashRenderFail(CSJSplashAd csjSplashAd, CSJAdError csjAdError) {
                if(csjAdError.getCode() == 602){
                    m_mainInstance.DebugPrintE("[%s] Code: %d 请检查网络是否可以访�?,"开屏广�?,csjAdError.getCode());
                }
                //广告渲染失败
                m_mainInstance.DebugPrintE("[%s] 广告渲染失败 Code: %d msg: %s","开屏广�?,csjAdError,csjAdError.getMsg());

                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onError(AdMainCallBack.LoadStatusType.RENDER, csjAdError,0, null);
                }
            }
        }, 3500);
        return m_adMainCallBack;
    }

    //显示开屏广�?
    public void ShowAd() {
        FrameLayout container = AdMain.getInstance().getMainView();
        if (ad == null || container == null) {
            m_mainInstance.DebugPrintE("[%s] (ad %s null || container %s null) ","开屏广�?, (ad == null)?"==":"!=",(container == null)?"==":"!=");
            return;
        }

        ad.setSplashAdListener(new CSJSplashAd.SplashAdListener() {
            @Override
            public void onSplashAdShow(CSJSplashAd csjSplashAd) {
                //广告展示
                //获取展示广告相关信息，需要再show回调之后进行获取
                m_mainInstance.DebugPrintI("[%s] onSplashAdShow","开屏广�?);
                MediationBaseManager manager = ad.getMediationManager();
                if (manager != null && manager.getShowEcpm() != null) {
                    MediationAdEcpmInfo showEcpm = manager.getShowEcpm();
                    String ecpm = showEcpm.getEcpm(); //展示广告的价�?
                    String sdkName = showEcpm.getSdkName();  //展示广告的adn名称
                    String slotId = showEcpm.getSlotId(); //展示广告的代码位ID
                }
            }

            @Override
            public void onSplashAdClick(CSJSplashAd csjSplashAd) {
                //广告点击
                m_mainInstance.DebugPrintI("[%s] 广告点击","开屏广�?);
            }

            @Override
            public void onSplashAdClose(CSJSplashAd csjSplashAd, int i) {
                //广告关闭
                m_mainInstance.DebugPrintI("[%s] 广告关闭","开屏广�?);
                ad.getMediationManager().destroy();
                container.removeView(csjSplashAd.getSplashView());
                //finish(); // 这个是关闭当前Activity
            }
        });

        View splashView = ad.getSplashView();
        container.addView(splashView);
    }

}
