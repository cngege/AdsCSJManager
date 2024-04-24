package ads.csjAdManager;
// 穿山甲 - Cocos2.4.x

//import static com.cocos.lib.GlobalObject.runOnUiThread;
import android.app.Activity;

import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;
import org.json.JSONException;
import org.json.JSONObject;


public class AdJsManager {

    public static final String ADTYPE_ADBANNER = "ADBANNER";
    public static final String ADTYPE_ADFULLSCREEN = "ADFULLSCREEN";
    public static final String ADTYPE_ADREWARD = "ADREWARD";
    public static final String ADTYPE_ADFEED = "ADFEED";
    public static final String ADTYPE_ADDRAWFEED = "ADDRAWFEED";

    public static void LoadAdIO(String adId,int preAdNum, String Type){
        if(Type.equals(ADTYPE_ADBANNER)){
            // 横幅
            AdBanner.getInstance().EnablePreLoad(true,preAdNum);
            AdBanner.getInstance().LoadAd(adId);
        }
        else if(Type.equals(ADTYPE_ADFULLSCREEN)){
            // 插全屏
            AdFullScreenVideo.getInstance().EnablePreLoad(true,preAdNum);
            AdFullScreenVideo.getInstance().LoadAd(adId);
        }
        else if(Type.equals(ADTYPE_ADREWARD)){
            // 激励视频
            ((Activity)AdMain.getInstance().getGameCtx()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ADReward.getInstance().EnablePreLoad(true,3);
                    ADReward.getInstance().LoadAd(adId);
                }
            });
        }
        else if(Type.equals(ADTYPE_ADFEED)){
            // 信息流
        }
        else if(Type.equals(ADTYPE_ADDRAWFEED)){
            // REWA信息流
        }
    }


    public static void ShowAdIO(String Type,final int id, String extra){
        if(Type.equals(ADTYPE_ADBANNER)){
            // 横幅
            AdBanner.getInstance().buildListen().adDislikeHandle(new AdBanner.AdDislikeListener() {
                @Override
                public void onShow() {

                }

                @Override
                public void onSelected(int position, String value, boolean enforce) {
                    clickCloseAD(id);
                }

                @Override
                public void onCancel() {

                }
            });
            try{
                JSONObject jsonObj = new JSONObject(extra);
                if(jsonObj.has("pos")){
                    AdBanner.getInstance().setShowPosition(jsonObj.get("pos").toString());
                }
                ((Activity)AdMain.getInstance().getGameCtx()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdBanner.getInstance().ShowAd();
                    }
                });

            } catch (JSONException e) {
                AdMain.getInstance().DebugPrintE("ShowAdIO AD-TYPE:%s JSONException: %s",Type, e.toString());
                throw new RuntimeException(e);
            }
        }
        else if(Type.equals(ADTYPE_ADFULLSCREEN)){
            // 插全屏
            showAdFullScreen(id);
        }
        else if(Type.equals(ADTYPE_ADREWARD)){
            // 激励视频
            showAdReward(id);
        }
        else if(Type.equals(ADTYPE_ADFEED)){
            // 信息流
        }
        else if(Type.equals(ADTYPE_ADDRAWFEED)){
            // REWA信息流
        }
    }

    private static void clickCloseAD(final int id){
        ((Cocos2dxActivity)AdMain.getInstance().getGameCtx()).runOnGLThread(new Runnable() {
            @Override
            public void run() {
                Cocos2dxJavascriptJavaBridge.evalString("cc.game.emit('callADClickClose',"+ id +");"); // 成功
            }
        });
    }

    private static void showAdFullScreen(final int id){
        AdFullScreenVideo.getInstance().buildListen().RewardHandle(new AdFullScreenVideo.RewardCallBackListen() {
            @Override
            public void onSuccess(TTFullScreenVideoAd ad) {
                // 回传id
                ((Cocos2dxActivity)AdMain.getInstance().getGameCtx()).runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        Cocos2dxJavascriptJavaBridge.evalString("cc.game.emit('callADShowSuccess',"+ id +");"); // 成功
                    }
                });
            }

            @Override
            public void onError(TTFullScreenVideoAd ad) {
                ((Cocos2dxActivity)AdMain.getInstance().getGameCtx()).runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        Cocos2dxJavascriptJavaBridge.evalString("cc.game.emit('callADShowFail',"+ id +");"); // 失败
                    }
                });
            }
        });
        ((Activity)AdMain.getInstance().getGameCtx()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AdFullScreenVideo.getInstance().ShowAd();
            }
        });
    }
    //激励视频
    private static void showAdReward(final int id){
        ADReward.getInstance().buildListen().RewardHandle(new ADReward.RewardCallBackListen() {
            @Override
            public void onSuccess(TTRewardVideoAd ad) {
                ((Cocos2dxActivity)AdMain.getInstance().getGameCtx()).runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        Cocos2dxJavascriptJavaBridge.evalString("cc.game.emit('callADShowSuccess',"+ id +");"); // 成功
                    }
                });
            }

            @Override
            public void onError(TTRewardVideoAd ad) {
                ((Cocos2dxActivity)AdMain.getInstance().getGameCtx()).runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        Cocos2dxJavascriptJavaBridge.evalString("cc.game.emit('callADShowFail',"+ id +");"); // 失败
                    }
                });
            }
        });
        ((Activity)AdMain.getInstance().getGameCtx()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ADReward.getInstance().ShowAd();
            }
        });
    }

}
