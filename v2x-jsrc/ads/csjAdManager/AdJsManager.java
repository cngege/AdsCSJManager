package ads.csjAdManager;
// 穿山甲 - Cocos2.4.x

//import static com.cocos.lib.GlobalObject.runOnUiThread;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;

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

    public static void InitJsManager(){

    }


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
        }
        else if(Type.equals(ADTYPE_ADFEED)){
            // 信息流
        }
        else if(Type.equals(ADTYPE_ADDRAWFEED)){
            // REWA信息流
        }
    }


    public static void ShowAdIO(String Type, int id, String extra){
        if(Type.equals(ADTYPE_ADBANNER)){
            // 横幅
            try{
                JSONObject jsonObj = new JSONObject(extra);
                if(jsonObj.has("pos")){
                    AdBanner.getInstance().setShowPosition(jsonObj.get("pos").toString());
                }
                AdBanner.getInstance().ShowAd();

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        else if(Type.equals(ADTYPE_ADFULLSCREEN)){
            // 插全屏
            showAdFullScreen(id);
        }
        else if(Type.equals(ADTYPE_ADREWARD)){
            // 激励视频
        }
        else if(Type.equals(ADTYPE_ADFEED)){
            // 信息流
        }
        else if(Type.equals(ADTYPE_ADDRAWFEED)){
            // REWA信息流
        }
    }

    private static void showAdFullScreen(final int id){
        AdFullScreenVideo.getInstance().buildListen().RewardHandle(new AdFullScreenVideo.RewardCallBackListen() {
            @Override
            public void onSuccess(TTFullScreenVideoAd ad) {
                // 回传id
                ((Cocos2dxActivity)AdMain.getInstance().getGameCtx()).runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        Cocos2dxJavascriptJavaBridge.evalString("window.callADShowSuccess("+ id +");"); // 成功
                    }
                });
            }

            @Override
            public void onError(TTFullScreenVideoAd ad) {
                ((Cocos2dxActivity)AdMain.getInstance().getGameCtx()).runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        Cocos2dxJavascriptJavaBridge.evalString("window.callADShowFail("+ id +");"); // 失败
                    }
                });
            }
        });
        AdFullScreenVideo.getInstance().ShowAd();
    }

}
