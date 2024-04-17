package ads.csjAdManager2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTCustomController;


public class AdMain {
    //TODO 创建静态导出方法供JS调用

    private static AdMain instance;
    private Context m_ctx;
    private boolean m_isInit;           // 是否初始�?
    private boolean m_debugPrint;
    private boolean m_debugLog;
    private FrameLayout m_frameLayout;

    public AdMainCallBack.SDKInitCallBack callback;

    // 获取&创建单例�?
    public static AdMain getInstance(){
        if(instance == null){
            instance = new AdMain();
        }
        return instance;
    }


    /***
     * 设置游戏主类上下�?
     * @param ctx 主类上下�?
     */
    public void setGameCtx(Context ctx) {
        this.m_ctx = ctx;
    }

    public Context getGameCtx(){
        return m_ctx;
    }

    public Activity getGameCtxAsActivity(){
        return (Activity)m_ctx;
    }

    /***
     * 是否开�?调试前端输出
     * @param enable 调试输出开�?
     */
    public void setDebugPrintEnable(boolean enable){
        this.m_debugPrint = enable;
    }

    /***
     * 是否开�?调试日志输出
     * @param enable 调试输出开�?
     */
    public void setDebugLogEnable(boolean enable){
        this.m_debugLog = enable;
    }

    /***
     * 打印Debug消息�?需确保在UI线程操作
     * @param format 格式化字符串
     * @param args   格式化参�?
     */
    public void DebugPrintI(String format,Object... args){
        if(m_debugPrint){
            if(m_ctx != null){
                Toast.makeText(m_ctx, String.format(format,args), Toast.LENGTH_SHORT).show();
            }
        }
        if(m_debugLog){
            Log.i("CSJADSManager",String.format(format,args));
        }
    }

    public void DebugPrintE(String format,Object... args){
        if(m_debugPrint){
            if(m_ctx != null){
                Toast toast = Toast.makeText(m_ctx, String.format(format,args), Toast.LENGTH_LONG);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                v.setTextColor(Color.RED);
                toast.show();
            }
        }
        if(m_debugLog){
            Log.e("CSJADSManager",String.format(format,args));
        }
    }

    /***
     * 创建一个FrameLayout布局
     * @return 拿到这个布局
     */
    public FrameLayout CreateAndGetFrameLayout(){
        if(m_frameLayout == null){
            m_frameLayout = new FrameLayout(m_ctx);
        }
        return m_frameLayout;
    }

    /***
     * 获取一个撑满全屏的布局参数
     * @return 返回此参�?
     */
    public FrameLayout.LayoutParams getLayoutFull(){
        FrameLayout.LayoutParams lytp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);//布局的尺�?
        lytp.gravity = Gravity.CENTER;
        return lytp;
    }

    /***
     * 获取屏幕真实宽高
     * @return 二维对象
     */
    public Point getScreen(){

        WindowManager wm = ((WindowManager)m_ctx.getSystemService(Context.WINDOW_SERVICE));
        Display display;
        if(wm != null) {
            display = wm.getDefaultDisplay();
        }
        else{
            DebugPrintE("getSystemService wm = null");
            return null;
        }


        //Display display = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point();
        if (display != null) {
            display.getRealSize(screenSize);
        }
        else{
            DebugPrintE("getSystemService display = null");
            return null;
        }
        DebugPrintI("getscreenSize xy�?+ screenSize.x + " "+ screenSize.y);
        return screenSize;
    }

    public FrameLayout getMainView(){
        return m_frameLayout;
    }

    /***
     * SDK 初始�?
     */
    public void SDK_Init(String appId){
        if(m_isInit){
            return;
        }
        if(m_ctx == null){
            return;
        }
        TTAdSdk.init(m_ctx, new TTAdConfig.Builder().appId(appId)
                .customController(new TTCustomController(){}) // 隐私合规设置
                .build()
        );
        // 添加容器到游戏上下文Activity
        ((Activity)m_ctx).addContentView(CreateAndGetFrameLayout(), getLayoutFull());
    }

    public AdMainCallBack SDK_StartLoad(){
        // 开始加�?
        AdMainCallBack adMainCallBack = new AdMainCallBack();
        TTAdSdk.start(new TTAdSdk.Callback() {
            @Override
            public void success() {
                DebugPrintI("SDK_success");
                //初始化成�?
                m_isInit = true;
                //在初始化成功回调之后进行广告加载
                if(adMainCallBack.sdkInitCallBack != null){
                    adMainCallBack.sdkInitCallBack.onSuccess();
                }
            }

            @Override
            public void fail(int i, String s) {
                DebugPrintE("SDK_fail Code:" + i + " MSG:" + s);
                //初始化失�?
                m_isInit = false;
                if(adMainCallBack.sdkInitCallBack != null){
                    adMainCallBack.sdkInitCallBack.onError(i, s);
                }
            }
        });

    return adMainCallBack;
    }


    /***
     * 监听安卓返回�?
     * @return 是否(false)拦截返回键事�?
     */
    public boolean onBackPressed(){
        boolean ret = true;
        if(!AdDrawFeed.getInstance().onBackPressed()) ret = false;
        return ret;
    }

}
