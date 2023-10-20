import {defineStore} from 'pinia'

export const useUser = defineStore("user", {
    // State 相当于组件中的 data属性
    state: () => ({
        // 账号昵称
        nickname: '',
        // 好友信息
        friendsInfo: {} as any,
        // 好友列表 + 好友消息
        friendsListInfo: [] as any,
        // WebSocket 实例
        webSocketInstance: null as any,
    }),
    // Getters 相当于组件中的 计算属性
    getters: {},
    // Actions 相当于组件中的 methods
    actions: {
        // 创建 WebSocket 实例连接
        async createWebSocket(nickname: string) {
            // 验证是否存在实例 存在则先关闭
            if (this.webSocketInstance) {
                this.webSocketInstance.close();
            }
            return new Promise((resolve, reject) => {
                if (!nickname) {
                    reject('')
                    return
                }
                // 设置用户昵称
                this.nickname = nickname
                // 建立WebSocket全双工通信链接
                this.webSocketInstance = new WebSocket('ws://127.0.0.1:8080/websocket/' + nickname)

                //<editor-fold desc="WebSocket事件监听">
                // 监听WebSocket打开事件
                this.webSocketInstance.onopen = () => {
                    console.log('WebSocket is connected');
                    resolve(true)
                };
                // 监听WebSocket消息事件
                this.webSocketInstance.onmessage = (event: any) => {
                    // 接收消息转JSON对象
                    const data = JSON.parse(event.data)
                    // console.log(data)
                    if (data.type === 'updateFriendsList') {
                        this.updateFriends(data)
                    } else if (data.type === 'messages') {
                        let findIndex = this.friendsListInfo.findIndex((object: any) => object.nickname === data.sendNickname);
                        if (findIndex !== -1) {
                            if (this.friendsListInfo[findIndex].nickname === this.friendsInfo.nickname){
                                this.friendsInfo.latestNews = data.messages
                                this.friendsInfo.messages.push({
                                    type: 'friend', // 消息类型
                                    message: data.messages// 消息内容
                                })
                            }else {
                                this.friendsListInfo[findIndex].latestNews = data.messages
                                this.friendsListInfo[findIndex].messages.push({
                                    type: 'friend', // 消息类型
                                    message: data.messages// 消息内容
                                })
                            }
                        }

                    }

                };
                // 监听WebSocket关闭事件
                this.webSocketInstance.onclose = () => {
                    console.log('WebSocket is disconnected');
                    resolve(false)
                };
                // 监听WebSocket异常事件
                this.webSocketInstance.onerror = (error: any) => {
                    console.error('WebSocket error:', error);
                    reject(error)
                };
                //</editor-fold>
            })
        },
        async updateFriends(data: any) {
            // 验证是否存在好友
            if (this.friendsListInfo.length > 0) {
                // 迭代更新的好友列表信息
                for (let i = 0; i < data.messages.length; i++) {
                    let findIndex = this.friendsListInfo.findIndex((object: any) => object.nickname === data.messages[i].nickname);
                    // 验证是否一致
                    // 不一致 新增好友
                    if (findIndex === -1) {
                        let friend = {
                            status: true, // 是否在线状态
                            nickname: data.messages[i].nickname, // 好友账号昵称
                            latestNews: '好友已经上线可以开始聊天了', // 最新消息
                            // 消息集合
                            messages: [
                                {
                                    type: 'friend', // 消息类型
                                    message: '好友已经上线可以开始聊天了' // 消息内容
                                }
                            ] as any
                        }
                        this.friendsListInfo.push(friend)
                    }
                }

                // 迭代已经存储的好友列表
                for (let i = 0; i < this.friendsListInfo.length; i++) {
                    // 查找好友
                    // 移除或者修改在线状态
                    let findIndex1 = data.messages.findIndex((object: any) => object.nickname === this.friendsListInfo[i].nickname);
                    if (findIndex1 == -1) {
                        // 不存在代表已经离线
                        this.friendsListInfo[i].status = false
                        // 验证是否为当前选择好友信息
                        if (this.friendsInfo.nickname === this.friendsListInfo[i].nickname) {
                            // 更新当前选择好友的在线状态 更新离线
                            this.friendsInfo.status = false
                        }
                    } else {
                        // 不存在代表已经上线
                        this.friendsListInfo[i].status = true
                        // 验证是否为当前选择好友信息
                        if (this.friendsInfo.nickname === data.messages[findIndex1].nickname) {
                            // 更新当前好友的在线状态 更新在线
                            this.friendsInfo.status = true
                        }
                    }

                }

            }
            // 新增初始化第一个好友
            else {
                let friend = {} as any;
                // 迭代初始化好友列表信息
                for (let i = 0; i < data.messages.length; i++) {
                    friend = {
                        status: true, // 是否在线状态
                        nickname: data.messages[i].nickname, // 好友账号昵称
                        latestNews: '好友已经上线可以开始聊天了', // 最新消息
                        // 消息集合
                        messages: [
                            {
                                type: 'friend', // 消息类型
                                message: '好友已经上线可以开始聊天了' // 消息内容
                            }
                        ] as any
                    }
                    this.friendsListInfo.push(friend)
                }
                // 默认选择第一个好友信息
                if (data.messages.length > 0) {
                    friend = {
                        status: true, // 是否在线状态
                        nickname: data.messages[0].nickname, // 好友账号昵称
                        latestNews: '好友已经上线可以开始聊天了', // 最新消息
                        // 消息集合
                        messages: [
                            {
                                type: 'friend', // 消息类型
                                message: '好友已经上线可以开始聊天了' // 消息内容
                            }
                        ] as any
                    }
                    this.friendsInfo = friend
                }
            }

        },
        async sendMessages(receiveMessage: string) {
            if (!this.friendsInfo.nickname) {
                return false
            }
            let message = {
                type: "messages",
                sendNickname: this.nickname,
                receiveNickname: this.friendsInfo.nickname,
                messages: receiveMessage
            }
            this.webSocketInstance.send(JSON.stringify(message))

            let addMessage = {
                type: 'my', // 消息类型
                message: receiveMessage // 消息内容
            }
            this.friendsInfo.messages.push(addMessage)
            this.friendsInfo.latestNews = receiveMessage
            return true
        },
    },
})