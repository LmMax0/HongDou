<template>
    <div>
        Hello, {{username}}==to==>{{to}} <br>

        视频播放路径：{{current_video}}<br>
        视频播放当前时间:{{current_time}}s
        视频总时长:{{total_time}}s
        <button @click="sync_video_please()">与对方同步</button>
        <br/>
        <div id="mui-player"></div>

        <div v-for="src in videoList">
            <input type="text" :value=src>
            <button @click="initVideo(src)">播放本地视频</button>
        </div>
        <div>
            <input ref="networkVideo" type="text">
            <button @click="initVideo(this.$refs.networkVideo.value)">播放网络视频</button>
        </div>



        <input ref="testInput" type="text" >
        <button @click="videoTo(this.$refs.testInput.value)">跳转videoTo</button>
        <hr/>
        username: <input id="username" type="text" v-model="username" >
        <br/>
        to: <input id="to" type="text" v-model="to">
        <br>
        <button @click="connectToServer">建立连接</button>
        <br>
        <label>Message</label>
        <br>
        <textarea rows="8" cols="50" id="clientMsg"></textarea>
        <br>
        <button @click="send_msg">Send</button>
        <br>
        <label>Response from Server</label>
        <br>
        <textarea rows="8" cols="50" id="serverMsg" readonly="readonly"></textarea>


    </div>
</template>

<script>
    import 'mui-player/dist/mui-player.min.css'
    import MuiPlayer from 'mui-player'
    import Flv from 'flv.js'


    export default {
        name:'App',
        data(){
            return{
                username: '',
                to:'',
                mp: null,
                videoList:[],
                current_video: '',
                total_time: 0,
                current_time: 0,
                timer: null,
                user_token: '',
                socketConn: null
            }
        },
        methods:{
            // beginPlay(){
            //     this.flvPlayer.play()
            // },
            // initflvVideo(){
            //     console.log("播放地址==》"+ this.videoUrl)
            //     let _this = this
            //
            //     if (Flv.isSupported()) {
            //         var videoElement = document.getElementById('videoElement');
            //         this.flvPlayer = Flv.createPlayer({
            //             hasAudio: true,
            //             hasVideo: true,
            //             type: 'flv',
            //             url: 'media/xgplayer-demo-360p.flv'
            //         });
            //         this.flvPlayer.attachMediaElement(videoElement)
            //         this.flvPlayer.load()
            //     }
            //     console.log(this.flvPlayer)
            // },

            destoryMp(){
                if(this.mp!=null){
                    this.mp.destory();
                }
                setTimeout(function () {

                },2000)
            },
            initVideo(src){

                console.log("将要播放的地址："+  src);
                this.destoryMp()

                if(src === ''){
                    src = this.videoList[0]
                }

                let videoSrc = src
                this.current_video = videoSrc

                let videoTitle = videoSrc.substring(videoSrc.lastIndexOf('/')+1,videoSrc.length)

                let index1=videoSrc.lastIndexOf(".")
                let index2=videoSrc.length;
                // 获取分割播放视频的后缀
                let type=videoSrc.substring(index1+1,index2);

                console.log(type)
                if(type==='flv'){
                    // 创建flv 文件解析mp
                    // 初始化 MuiPlayer 插件，MuiPlayer 方法传递一个对象，该对象包括所有插件的配置
                    this.mp = new MuiPlayer({
                        container:'#mui-player',
                        title:videoTitle,
                        src: videoSrc,
                        parse :{
                            type:'flv',
                            loader: Flv
                        }
                    })
                }else if(type ==='mp4') {
                    this.mp = new MuiPlayer({
                        container:'#mui-player',
                        title:videoTitle,
                        src: videoSrc
                    })
                }
                this.mp.showLoading()

            },
            show_times(){
                if(this.mp!=null){
                    let videoDom = this.mp.video()
                    this.total_time =  videoDom.duration
                    this.current_time = videoDom.currentTime
                }
            },
            videoTo(newCurrentTime){
                if(this.mp!=null){
                    let videoDom = this.mp.video()
                    videoDom.currentTime = newCurrentTime
                    this.current_time = videoDom.currentTime
                    videoDom.play()
                }
            },
            connectToServer(){
                let _this  = this
                this.socketConn = new WebSocket('ws://101.35.55.105:8080/hongdouServer/socketHandler?username=' + this.username + '&to=' + this.to);

                // Recive Message
                this.socketConn.onmessage = function (event) {
                    console.log(event);

                    let JsonObj = JSON.parse(event.data)

                    if(JsonObj.type === 'message'){
                        let serverMsg = document.getElementById('serverMsg')
                        serverMsg.value = JsonObj.msg
                    }
                    if(JsonObj.type === 'sync-request'){
                        // console.log("同步操作")
                        // console.log(JsonObj)
                        //
                        // console.log("目前src:"+ _this.current_video);
                        // console.log("JsonObj"+ JsonObj.current_video);
                        // if(_this.current_video === JsonObj.current_video){
                        //     console.log("一模一样")
                        //     _this.videoTo(JsonObj.current_time)
                        // }else {
                        //     console.log("切换地址")
                        //     _this.initVideo(JsonObj.current_video)
                        //     _this.videoTo(JsonObj.current_time)
                        //
                        // }
                        // this.sync_video_please(this.current_video,this.current_time)
                        _this.do_sync_video(_this.current_video,_this.current_time)
                    }
                    if(JsonObj.type === 'sync-response') {
                        if (_this.current_video === JsonObj.current_video) {
                            console.log("一模一样")
                            _this.videoTo(JsonObj.current_time+0.5)
                        } else {
                            console.log("切换地址")
                            _this.initVideo(JsonObj.current_video)
                            _this.videoTo(JsonObj.current_time+0.5)
                        }
                    }

                }
            },
            send_msg(){ // 发送消息

                let clientMsg = document.getElementById('clientMsg');

                let msgJson = {
                    "type": "message",
                    "msg": clientMsg.value
                }

                console.log(msgJson)

                this.socketConn.send(JSON.stringify(msgJson))

            },
            sync_video_please(){ // 同步视频

                let syncJson = {
                    "type": "sync-request",
                    // "current_video": current_video,
                    // "current_time": current_time
                }
                this.socketConn.send(JSON.stringify(syncJson))

            },
            do_sync_video(current_video,current_time){
                let syncJson = {
                    "type": "sync-response",
                     "current_video": current_video,
                     "current_time": current_time
                }
                this.socketConn.send(JSON.stringify(syncJson))
            }
        },
        mounted() {
            this.videoList.push("media/468067355-1-64.flv")
            this.$nextTick(() => {
                this.initVideo('')
            })
            this.timer = setInterval(()=>{
                this.show_times()
                console.log(this.current_video);
            },1000)

        },
        computed: {

        },
        beforeUnmount() {
            if (this.timer) {
                clearInterval(this.timer); // 在Vue实例销毁前，清除我们的定时器
            }
        }
    }
</script>

<style>

</style>

