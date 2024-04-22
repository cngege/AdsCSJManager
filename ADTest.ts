// Learn TypeScript:
//  - https://docs.cocos.com/creator/2.4/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/2.4/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/2.4/manual/en/scripting/life-cycle-callbacks.html

const {ccclass, property} = cc._decorator;

interface AdEventParams {
    id : number;
    success : Function;
    error : Function;
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
    
    private _Event: AdEventParams[] = [];

    
    public static getInstance(): ADTest {
        if(ADTest.instance == null){
            ADTest.instance = new ADTest();
            // 注册监听
            if (typeof window["callADLoadSuccess"] !== "function") {
                window["callADLoadSuccess"] = function (id : number) {
                    for(let item of ADTest.getInstance()._Event){
                        if(item.id == id){
                            //item?.success();
                        }
                    }
                }
            }
            if (typeof window["callADLoadFail"] !== "function") {
                window["callADLoadSuccess"] = function (id : number) {
                    for(let item of ADTest.getInstance()._Event){
                        if(item.id == id){
                            //item?.error();
                        }
                    }
                }
            }
            if (typeof window["callADShowSuccess"] !== "function") {
                window["callADShowSuccess"] = function (id : number) {
                    for(let item of ADTest.getInstance()._Event){
                        if(item.id == id){
                            item?.success();
                        }
                    }
                }
            }
            if (typeof window["callADShowFail"] !== "function") {
                window["callADShowFail"] = function (id : number) { 
                    for(let item of ADTest.getInstance()._Event){
                        if(item.id == id){
                            item?.error();
                        }
                    }
                }
            }

        }
        return ADTest.instance;
    }

    PreLoadAD(adId : string, num : number, adType : ADType = ADType.ADFULLSCREEN){
        if(num > 3) num = 3;
        if(num < 1) num = 1;
        jsb.reflection.callStaticMethod("ads/csjAdManager/AdJsManager", "LoadAdIO", "(Ljava/lang/String;ILjava/lang/String;)V", adId, num,adType);
    }

    watchAD(event_succ: Function, event_err : Function = null, adType : ADType = ADType.ADFULLSCREEN) : number {
        let _id : number = (new Date()).getMilliseconds();
        this._Event.push({
            id : _id,
            success: event_succ,
            error: event_err
        });
        // TODO: 然后发送播放广告, 把id带过去
        jsb.reflection.callStaticMethod("ads/csjAdManager/AdJsManager", "ShowAdIO", "(Ljava/lang/String;I)V", adType, _id);
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
