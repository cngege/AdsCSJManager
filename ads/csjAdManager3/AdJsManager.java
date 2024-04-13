package ads.csjAdManager3;

import static com.cocos.lib.GlobalObject.runOnUiThread;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.cocos.lib.JsbBridge;

public class AdJsManager {


    public static void InitJsManager(){
        JsbBridge.setCallback(new JsbBridge.ICallback() {
            @Override
            public void onScript(String arg0, String arg1) {
                final String jsonStr = arg1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AdMain.getInstance().DebugPrintI("arg0:%s, arg1: %s",arg0, arg1);

                        if(arg0.equals("CLOSEGAME")){
                            //关闭游戏按钮
                            System.exit(0);
                        }

                        try{
                            JSONObject jsonObj = new JSONObject(jsonStr);

                            if(!jsonObj.has("Type")){
                                AdMain.getInstance().DebugPrintE("JSON中缺少相关参数 %s","Type");
                                return;
                            }
                            String Type = jsonObj.getString("Type");

                            String CodeId = null;
                            {
                                if(Type.equals("LOAD")){
                                    if(!jsonObj.has("CodeId")){
                                        AdMain.getInstance().DebugPrintE("JSON中缺少相关参数 %s","CodeId");
                                        return;
                                    }
                                    CodeId = jsonObj.getString("CodeId");
                                }
                            }

                            //开屏广告 注意,需要SDK初始化完成
                            if(arg0.equals("ADNATIVE")){
                                AdMain.getInstance().DebugPrintI("前端载入开屏广告，此条已废弃");
                            }

                            //横幅广告
                            if(arg0.equals("ADBANNER")){
                                if(Type.equals("LOAD")){
                                    AdBanner.getInstance().LoadAd(CodeId).Handler(new AdMainCallBack.AdLoadStatusCallBack() {
                                        @Override
                                        public void onSuccess(AdMainCallBack.LoadStatusType type, Object obj) {
                                            AdMainCallBack.AdLoadStatusCallBack.super.onSuccess(type, obj);
                                            JsbBridge.sendToScript("ADBANNER", "LOADED");   //发生到ts消息 横幅广告加载完成可以显示
                                        }

                                        @Override
                                        public void onError(AdMainCallBack.LoadStatusType type, Object obj, int i, String e) {
                                            AdMainCallBack.AdLoadStatusCallBack.super.onError(type, obj, i, e);
                                        }
                                    });
                                }else{
                                    if(!jsonObj.has("ViewPos") || !jsonObj.has("Size")){
                                        AdMain.getInstance().DebugPrintE("JSON中缺少相关参数");
                                        return;
                                    }
                                    String viewPos = jsonObj.getString("ViewPos");
                                    String size = jsonObj.getString("Size");    // 创建代码位时的size, 大小将根据size大小同比缩放

                                    // 横幅 "up","center","down"
                                    AdBanner.getInstance().ShowAd();
                                }
                            }

                            // 插全屏 0 1
                            if(arg0.equals("ADFULLSCREEN")){
                                if(Type.equals("LOAD")){
                                    AdFullScreenVideo.getInstance().LoadAd(CodeId).Handler(new AdMainCallBack.AdLoadStatusCallBack() {
                                        @Override
                                        public void onSuccess(AdMainCallBack.LoadStatusType type, Object obj) {
                                            AdMainCallBack.AdLoadStatusCallBack.super.onSuccess(type, obj);
                                            JsbBridge.sendToScript("ADFULLSCREEN", "LOADED");   //发送到ts消息 横幅广告加载完成可以显示
                                        }

                                        @Override
                                        public void onError(AdMainCallBack.LoadStatusType type, Object obj, int i, String e) {
                                            AdMainCallBack.AdLoadStatusCallBack.super.onError(type, obj, i, e);
                                        }
                                    });
                                }else{
                                    if(!jsonObj.has("isFull") ){
                                        AdMain.getInstance().DebugPrintE("JSON中缺少相关参数:isFull");
                                        return;
                                    }
                                    AdFullScreenVideo.getInstance().ShowAd();
                                }
                            }

                            //激励视频
                            if(arg0.equals("ADREWARD")){
                                if(Type.equals("LOAD")){
                                    ADReward.getInstance().LoadAd(CodeId).Handler(new AdMainCallBack.AdLoadStatusCallBack() {
                                        @Override
                                        public void onSuccess(AdMainCallBack.LoadStatusType type, Object obj) {
                                            AdMainCallBack.AdLoadStatusCallBack.super.onSuccess(type, obj);
                                            JsbBridge.sendToScript("ADREWARD", "LOADED");   //发送到ts消息 横幅广告加载完成可以显示
                                        }

                                        @Override
                                        public void onError(AdMainCallBack.LoadStatusType type, Object obj, int i, String e) {
                                            AdMainCallBack.AdLoadStatusCallBack.super.onError(type, obj, i, e);
                                        }
                                    });
                                }
                                else{
                                    ADReward.getInstance().ShowAd();
                                }
                            }

                            //信息流 ADFEED
                            if(arg0.equals("ADFEED")){
                                if(Type.equals("LOAD")){
                                    AdFeed.getInstance().LoadAd(CodeId).Handler(new AdMainCallBack.AdLoadStatusCallBack() {
                                        @Override
                                        public void onSuccess(AdMainCallBack.LoadStatusType type, Object obj) {
                                            AdMainCallBack.AdLoadStatusCallBack.super.onSuccess(type, obj);
                                            JsbBridge.sendToScript("ADFEED", "LOADED");   //发送到ts消息 横幅广告加载完成可以显示
                                        }

                                        @Override
                                        public void onError(AdMainCallBack.LoadStatusType type, Object obj, int i, String e) {
                                            AdMainCallBack.AdLoadStatusCallBack.super.onError(type, obj, i, e);
                                        }
                                    });
                                }else{
                                    AdFeed.getInstance().ShowAd();
                                }
                            }

                            //Draw 信息流 ADDRAWFEED
                            if(arg0.equals("ADDRAWFEED")){
                                if(Type.equals("LOAD")){
                                    AdDrawFeed.getInstance().LoadAd(CodeId).Handler(new AdMainCallBack.AdLoadStatusCallBack() {
                                        @Override
                                        public void onSuccess(AdMainCallBack.LoadStatusType type, Object obj) {
                                            AdMainCallBack.AdLoadStatusCallBack.super.onSuccess(type, obj);
                                            JsbBridge.sendToScript("ADDRAWFEED", "LOADED");   //发送到ts消息 横幅广告加载完成可以显示
                                        }

                                        @Override
                                        public void onError(AdMainCallBack.LoadStatusType type, Object obj, int i, String e) {
                                            AdMainCallBack.AdLoadStatusCallBack.super.onError(type, obj, i, e);
                                        }
                                    });
                                }else{
                                    AdDrawFeed.getInstance().ShowAd();
                                }
                            }

                        }catch (JSONException e){
                            AdMain.getInstance().DebugPrintE("[%s]onScript arg1必须为JSON字符串:%s",arg0,e.getMessage());
                        }
                    }
                });
                //ADBANNER  "up","center","down"

                //ADFULLSCREEN  "0", "1"
            }
        });
    }

}
