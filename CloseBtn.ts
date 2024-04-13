import { _decorator, Component, game, native, Node } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('CloseBtn')
export class CloseBtn extends Component {
    AD : any = {
        ADNATIVE : "ADNATIVE",              // 开屏
        ADBANNER : "ADBANNER",              // 横幅
        ADFULLSCREEN : "ADFULLSCREEN",      // 插全屏
        ADREWARD : "ADREWARD",              // 激励视频
        ADFEED : "ADFEED",                  // 信息流
        ADDRAWFEED : "ADDRAWFEED"           // Draw信息流
    }
    


    start() {
        // 接收Java层传回的消息
        native.bridge.onNative = (arg0:string, arg1: string):void=>{
            if(arg0 == 'SDKONLOAD'){    // SDK加载完成 废弃 监听时机太慢，监听不到
                let json = {
                    Type: "LOAD"
                }
                native.bridge.sendToNative(this.AD.ADNATIVE, JSON.stringify(json)); //通知开屏
                return;
            }
            if(arg0 == this.AD.ADNATIVE){ //开屏
                let json = {
                    Type: "SHOW",
                }
                native.bridge.sendToNative(this.AD.ADNATIVE, JSON.stringify(json));
                return;
            }
            if(arg0 == this.AD.ADBANNER){ //横幅广告
                let pos : Array<string> = ["up","center","down"];
                let poss : string = pos[this.getRandomNumber(0,2)];
                
                let json = {
                    Type: "SHOW",
                    ViewPos: poss,
                    Size: "300*150"
                }
                native.bridge.sendToNative(this.AD.ADBANNER, JSON.stringify(json));
                return;
            }
            if(arg0 == this.AD.ADFULLSCREEN){ //插全屏
                let json = {
                    Type: "SHOW",
                    isFull: this.getRandomNumber(0,1).toString()
                }
                native.bridge.sendToNative(this.AD.ADFULLSCREEN, JSON.stringify(json));
                return;
            }
            if(arg0 == this.AD.ADREWARD){ //激励视频
                let json = {
                    Type: "SHOW"
                }
                native.bridge.sendToNative(this.AD.ADREWARD, JSON.stringify(json));
                return;
            }
            if(arg0 == this.AD.ADFEED){ //信息流
                let json = {
                    Type: "SHOW"
                }
                native.bridge.sendToNative(this.AD.ADFEED, JSON.stringify(json));
                return;
            }
            if(arg0 == this.AD.ADDRAWFEED){ //Draw信息流
                let json = {
                    Type: "SHOW"
                }
                native.bridge.sendToNative(this.AD.ADDRAWFEED, JSON.stringify(json));
                return;
            }


            
        }
    }
    getRandomNumber(min : number, max : number) : number {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    /**
     * 游戏关闭按钮点击
     */
    click(){
        game.end();
        native.bridge.sendToNative("CLOSEGAME", "");
    }


    bannerClick(){
        let json = {
            CodeId: "956932217",
            Type: "LOAD",
        }

        native.bridge.sendToNative("ADBANNER", JSON.stringify(json));
    }

    fullScreenClick(){
        let json = {
            CodeId: "956954384",
            Type: "LOAD",
        }

        native.bridge.sendToNative("ADFULLSCREEN", JSON.stringify(json));
    }

    //激励视频按钮
    reward(){
        let json = {
            CodeId: "956956182",
            Type: "LOAD",
        }
        native.bridge.sendToNative("ADREWARD", JSON.stringify(json));
    }

    //信息流
    feed(){
        let json = {
            CodeId: "956962042",
            Type: "LOAD",
        }
        native.bridge.sendToNative("ADFEED", JSON.stringify(json));
    }

    //Draw信息流
    drawFeed(){
        let json = {
            CodeId: "956963156",
            Type: "LOAD",
        }
        native.bridge.sendToNative("ADDRAWFEED", JSON.stringify(json));
    }

}


