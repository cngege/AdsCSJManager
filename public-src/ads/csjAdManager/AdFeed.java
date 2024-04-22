package ads.csjAdManager;
// 信息流插入广告, 插入，隐藏在文章、新闻中
// 参考：https://www.bilibili.com/video/BV1n441117Jn/?spm_id_from=..search-card.all.click&vd_source=4f9c5288dc87968656f37cab722ccfe6

import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;

public class AdFeed {
    private static AdFeed instance;
    private AdMain m_mainInstance;
    private TTNativeExpressAd ad;

    private AdMainCallBack m_adMainCallBack;


    public static AdFeed getInstance(){
        if(instance == null){
            instance = new AdFeed();
            instance.m_mainInstance = AdMain.getInstance();
        }
        return instance;
    }

    public AdMainCallBack LoadAd(String id){
        if(m_adMainCallBack == null){
            m_adMainCallBack = new AdMainCallBack();
        }

        Point screenSize = AdMain.getInstance().getScreen();
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(id) //广告位ID
                /**
                 * 注：
                 *  1:单位为px
                 *  2:如果是信息流自渲染广告，设置广告图片期望的图片宽高 ，不能为0
                 *  2:如果是信息流模板广告，宽度设置为希望的宽度，高度设置为0(0为高度选择自适应参数)
                 */
                .setImageAcceptedSize(screenSize.x, 0)
                .setAdCount(1)
                .build();

        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(m_mainInstance.getGameCtx());
        adNativeLoader.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                //广告加载失败
                m_mainInstance.DebugPrintE("[%s] 广告加载失败 %d, %s","信息流广告",errorCode, errorMsg);
                if(m_adMainCallBack.adLoadStatusCallBack != null){
                    m_adMainCallBack.adLoadStatusCallBack.onError(AdMainCallBack.LoadStatusType.NONE, null,errorCode,errorMsg);
                }
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
                //广告加载成功
                //信息流广告渲染具体参考demo
                //如果是自渲染下载类广告可以通过以下api获取下载六要素
                if (list != null && !list.isEmpty()) {
                    ad = list.get(0);
                    if(m_adMainCallBack.adLoadStatusCallBack != null){
                        m_adMainCallBack.adLoadStatusCallBack.onSuccess(AdMainCallBack.LoadStatusType.LOAD, list);
                    }
                }
                else{
                    AdMain.getInstance().DebugPrintE("[%s] 载入成功,但列表中没有内容","信息流广告");
                }
            }
        });
        return m_adMainCallBack;
    }
    public void ShowAd() {
        Activity act = (Activity)AdMain.getInstance().getGameCtx();
        FrameLayout container = AdMain.getInstance().getMainView();
        //https://www.csjplatform.com/supportcenter/5402
        if (ad == null || container == null) {
            m_mainInstance.DebugPrintE("[%s] Feed广告 showBannerView bannerAd == null || container == null","信息流广告");
            return;
        }
        // 设置不喜欢按钮点击事件
        ad.setDislikeCallback((Activity) m_mainInstance.getGameCtx(), new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {
                m_mainInstance.DebugPrintI("[%s] onShow 显示dislike弹窗","信息流广告");
            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                //TToast.show(mContext, "点击 " + value);
                //mExpressContainer.removeAllViews();
                ad.destroy();
                container.removeView(ad.getExpressAdView());
                //用户选择不喜欢原因后，移除广告展示
                if (enforce) {
                    m_mainInstance.DebugPrintI("[%s] 穿山甲sdk强制将view关闭了","信息流广告");
                }
            }

            @Override
            public void onCancel() {
                m_mainInstance.DebugPrintI("[%s] 点击取消","信息流广告");
            }

//            默认dislike样式，用户重复点击dislike按钮回调。3600版本废除
//            @Override
//            public void onRefuse() {
//
//            }

        });

        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            //广告点击回调
            @Override
            public void onAdClicked(View view, int type) {
                m_mainInstance.DebugPrintI("[%s] 广告点击回调","信息流广告");
            }

            //广告展示回调
            @Override
            public void onAdShow(View view, int type) {
                m_mainInstance.DebugPrintI("[%s] 广告展示回调","信息流广告");
            }

            //广告渲染失败回调
            @Override
            public void onRenderFail(View view, String msg, int code) {
                m_mainInstance.DebugPrintE("[%s] 广告渲染失败回调 Code: %d, MSG: %s","信息流广告", code , msg);
            }

            //广告渲染成功回调
            @Override
            public void onRenderSuccess(View view, float width, float height) {
                m_mainInstance.DebugPrintI("[%s] 广告渲染成功回调","信息流广告");
                View bannerView = ad.getExpressAdView(); //获取Banner View
                if (bannerView != null) {
                    container.addView(bannerView);
                }
            }
        });
        ad.render();
    }

}
