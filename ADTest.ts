// Learn TypeScript:
//  - https://docs.cocos.com/creator/2.4/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/2.4/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/2.4/manual/en/scripting/life-cycle-callbacks.html

import Msg from "./Framework/Msg";

const {ccclass, property} = cc._decorator;


export class AdEvent{
    private msg : string;
    /**
     * 仅在插全屏-激励视频视频有效, 广告是否完整的播放没有跳过
     */
    isSuccess : boolean = false;
    /**
     * 广告是否正在关闭
     */
    isClose : boolean = false;

    /**
     * 是否是播放广告出错,尝试使用 getMsg 获取错误内容
     */
    isError : boolean = false;
    
    AdEvent(msg = ""){
        this.msg = msg;
    }

    getMsg() : string{
        return this.msg;
    }
}

/**
 * 回调函数第一个参数的参数类型
 */
type ADEventCALL = (arg1: AdEvent) => void;

/**
 * 调用 watchAD添加事件列表的列表项内容
 */
export interface AdEventListParams {
    id : number;
    event : ADEventCALL;
}

/**
 * watchAD函数的第二个参数,播放广告时传入一些可选项，以JSON的方式
 */
interface ADwatchADExtra{
    adType ?: string,
    extra ?: string
}

//// 不包括开屏 开屏在java里面写
export enum ADType {
    ADBANNER = "ADBANNER",
    ADFULLSCREEN = "ADFULLSCREEN",
    ADREWARD = "ADREWARD",
    ADFEED = "ADFEED",
    ADDRAWFEED = "ADDRAWFEED"
}


export default class ADTest {
    private static instance: ADTest = null;
    
    private _Event: AdEventListParams[] = [];

    private static registerEvent(){


        // 定义事件类型和监听器
        // 广告载入成功
        cc.game.on('callADLoadSuccess', (id : number) => {
            for(let item of ADTest.getInstance()._Event){
                if(item.id == id){
                    //item?.success();
                }
            }
        });

        // 广告载入错误
        cc.game.on('callADLoadFail', (id : number) => {
            for(let item of ADTest.getInstance()._Event){
                if(item.id == id){
                    //item?.error();
                }
            }
        });
        // 广告显示成功
        cc.game.on('callADShowSuccess', (id : number) => {
            for(let item of ADTest.getInstance()._Event){
                if(item.id == id){
                    let requests : AdEvent = new AdEvent();
                    requests.isSuccess = true;
                    item.event && item.event(requests);
                    ADTest.instance.removeEvent(id);
                    break;
                }
            }
        });

        // 广告显示错误-激励广告没有达到要求
        cc.game.on('callADShowFail', (id : number) => {
            for(let item of ADTest.getInstance()._Event){
                if(item.id == id){
                    let requests : AdEvent = new AdEvent();
                    requests.isSuccess = false;
                    item.event && item.event(requests);
                    ADTest.instance.removeEvent(id);
                    break;
                }
            }
        });

        // 点击DisLike 不感兴趣按钮关闭
        cc.game.on('callADClickClose', (id : number) => {
            for(let item of ADTest.getInstance()._Event){
                if(item.id == id){
                    let requests : AdEvent = new AdEvent();
                    requests.isClose = true;
                    item.event && item.event(requests);
                    ADTest.instance.removeEvent(id);
                    break;
                }
            }
        });
    }

    public static getInstance(): ADTest {
        if(ADTest.instance == null){
            ADTest.instance = new ADTest();
            ADTest.registerEvent();
        }
        return ADTest.instance;
    }

    PreLoadAD(adId : string, num : number, adType : ADType = ADType.ADFULLSCREEN){
        if(num > 3) num = 3;
        if(num < 1) num = 1;
        jsb.reflection.callStaticMethod("ads/csjAdManager/AdJsManager", "LoadAdIO", "(Ljava/lang/String;ILjava/lang/String;)V", adId, num,adType);
    }

    watchAD(event : ADEventCALL,extra : ADwatchADExtra = {}) : number{
        let _id : number = (new Date()).getMilliseconds();  // 三位数毫秒数,不能用getTime() 因为值比int大
        this._Event.push({
            id : _id,
            event: event
        });
        if(!extra.adType) extra.adType = ADType.ADFULLSCREEN;
        if(!extra.extra) extra.extra = "{}"
        jsb.reflection.callStaticMethod("ads/csjAdManager/AdJsManager", "ShowAdIO", "(Ljava/lang/String;ILjava/lang/String;)V", extra.adType, _id,extra.extra);
        return _id;
    }
    
    removeEvent(id : number){
        for (var i = 0; i < this._Event.length; i++) {
            if (this._Event[i].id == id) {
                this._Event.splice(i, 1);
            }
        }
    }

    gameClose(){
        jsb.reflection.callStaticMethod("java/lang/System", "exit", "(I)V", 0);
    }
}
