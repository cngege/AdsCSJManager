package ads.csjAdManager;

import android.app.Activity;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;

public class AdDrawFeed {
    private static AdDrawFeed instance;
    private AdMain m_mainInstance;
    private TTNativeExpressAd ad;


    private AdMainCallBack m_adMainCallBack;
    public static AdDrawFeed getInstance(){
        if(instance == null){
            instance = new AdDrawFeed();
            instance.m_mainInstance = AdMain.getInstance();
        }
        return instance;
    }

    public AdMainCallBack LoadAd(String id){
        if(m_adMainCallBack == null){
            m_adMainCallBack = new AdMainCallBack();
        }
        Point screenSize = AdMain.getInstance().getScreen();
        AdSlot adSlot =  new AdSlot.Builder()
                .setCodeId(id) //广告位ID
                /*
                 * 注：
                 *  1:单位为px
                 *  2:如果是Draw自渲染广告，设置广告图片期望的图片宽高 ，不能为0
                 *  2:如果是Draw模板广告，宽度设置为希望的宽度，高度设置为0(0为高度选择自适应参数)
                 */
                .setImageAcceptedSize(screenSize.x, 0)
                .setAdCount(1)//请求广告数量为1到3条 （优先采用平台配置的数量）
                .build();

        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(m_mainInstance.getGameCtx());
        adNativeLoader.loadExpressDrawFeedAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                //广告加载失败
                m_mainInstance.DebugPrintE("[%s] 广告加载失败 Code: %d Msg: %s", "Draw信息流", errorCode, errorMsg);
            }
            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                if (list != null && !list.isEmpty()) {
                    ad = list.get(0);
                    if(m_adMainCallBack.adLoadStatusCallBack != null){
                        m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.LOAD, list);
                    }
                }
                else{
                    m_mainInstance.DebugPrintI("[%s] 广告加载成功 但是列表中没有内容", "Draw信息流");
                }
            }
        });
        return m_adMainCallBack;
    }

    public boolean onBackPressed(){
        if(ad!=null){
            ad.destroy();
            AdMain.getInstance().getMainView().removeView(ad.getExpressAdView());
            return false;
        }
        return true;
    }

    public void ShowAd( ) {
        FrameLayout container = AdMain.getInstance().getMainView();
        Activity act = (Activity)AdMain.getInstance().getGameCtx();
        if (ad == null || container == null) {
            m_mainInstance.DebugPrintE("[%s] showDrawAdView bannerAd == null || container == null", "Draw信息流");
            return;
        }

        // 设置不喜欢按钮点击事件
        ad.setDislikeCallback(act, getDislikeInteractionCallback());

        ad.setExpressInteractionListener(getExpressAdInteractionListener());
        // 不感兴趣监听

        ad.render();
    }


    private TTAdDislike.DislikeInteractionCallback getDislikeInteractionCallback(){
        return new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {
                m_mainInstance.DebugPrintI("[%s] onShow 显示dislike弹窗","信息流广告");
            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                ad.destroy();
                AdMain.getInstance().getMainView().removeView(ad.getExpressAdView());
                //用户选择不喜欢原因后，移除广告展示
                if (enforce) {
                    m_mainInstance.DebugPrintI("[%s] 穿山甲sdk强制将view关闭了","Draw信息流广告");
                }
            }

            @Override
            public void onCancel() {
                m_mainInstance.DebugPrintI("[%s] 点击取消","Draw信息流广告");
            }
        };
    }

    private TTNativeExpressAd.ExpressAdInteractionListener getExpressAdInteractionListener(){
        return new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int i) {
                //广告点击
                m_mainInstance.DebugPrintI("[%s] 广告点击", "Draw信息流");
            }

            @Override
            public void onAdShow(View view, int i) {
                //广告展示
                m_mainInstance.DebugPrintI("[%s] 广告展示", "Draw信息流");
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
                m_mainInstance.DebugPrintE("[%s] 广告渲染失败", "Draw信息流");
            }

            @Override
            public void onRenderSuccess(View view, float w, float h) {
                //广告渲染成功
                m_mainInstance.DebugPrintI("[%s] 广告渲染成功", "Draw信息流");

                View bannerView = ad.getExpressAdView(); //获取Banner View
                if (bannerView != null) {
                    AdMain.getInstance().getMainView().addView(bannerView);
                }

            }
        };
    }
}
