/****************************************************************************
Copyright (c) 2015-2016 Chukong Technologies Inc.
Copyright (c) 2017-2018 Xiamen Yaji Software Co., Ltd.

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package com.cocos.game;

import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import com.cocos.service.SDKWrapper;
import com.cocos.lib.CocosActivity;
import ads.csjAdManager3.AdJsManager;
import ads.csjAdManager3.AdMain;
import ads.csjAdManager3.AdMainCallBack;
import ads.csjAdManager3.AdNative;

public class AppActivity extends CocosActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // DO OTHER INITIALIZATION BELOW
        SDKWrapper.shared().init(this);

        //AD初始化部分
        AdMain adManager = AdMain.getInstance();
        // 是否开启Debug打印输出
        adManager.setDebugPrintEnable(true);
        // 是否开启Debug日志输出
        adManager.setDebugLogEnable(true);
        // 向SDK设置游戏上下文活动句柄
        adManager.setGameCtx(this);
        // 初始化SDK信息
        adManager.SDK_Init("5516602");
        // 监听前端JS传回的数据
        AdJsManager.InitJsManager();
        // 开启载入SDK包
        adManager.SDK_StartLoad().Handler(new AdMainCallBack.SDKInitCallBack() {
            @Override
            public void onSuccess() {
                AdNative.getInstance().LoadAd("889094689").Handler(new AdMainCallBack.AdLoadStatusCallBack() {
                    @Override
                    public void onSuccess(AdMainCallBack.LoadStatusType type, Object obj) {
                        AdMainCallBack.AdLoadStatusCallBack.super.onSuccess(type, obj);
                        if(type == AdMainCallBack.LoadStatusType.RENDER){
                            //必须是渲染成功才进行广告的加载
                            AdNative.getInstance().ShowAd();
                        }
                    }
                    @Override
                    public void onError(AdMainCallBack.LoadStatusType type, Object obj, int i, String e) {
                        AdMainCallBack.AdLoadStatusCallBack.super.onError(type, obj, i, e);
                    }
                });
            }

            @Override
            public void onError(int i ,String e) {
                adManager.DebugPrintE("加载SDK错误 Code: %d, msg: %s",i, e);

            }
        });

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(10000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                } catch (InterruptedException e) {
                    adManager.DebugPrintE("出现异常:"+e.getMessage());
                }
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(!AdMain.getInstance().onBackPressed()){
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        SDKWrapper.shared().onBackPressed();
        super.onBackPressed();
    }


    @Override
    protected void onResume() {
        super.onResume();
        SDKWrapper.shared().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SDKWrapper.shared().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Workaround in https://stackoverflow.com/questions/16283079/re-launch-of-activity-on-home-button-but-only-the-first-time/16447508
        if (!isTaskRoot()) {
            return;
        }
        SDKWrapper.shared().onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SDKWrapper.shared().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SDKWrapper.shared().onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SDKWrapper.shared().onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SDKWrapper.shared().onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        SDKWrapper.shared().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        SDKWrapper.shared().onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SDKWrapper.shared().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        SDKWrapper.shared().onStart();
        super.onStart();
    }

    @Override
    public void onLowMemory() {
        SDKWrapper.shared().onLowMemory();
        super.onLowMemory();
    }
}
