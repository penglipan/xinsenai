import { defineStore } from 'pinia';

export const useUser = defineStore("user", {
    // State
    state: () => ({
        nickname: '',
        friendsInfo: {} as any,
        friendsListInfo: [] as any,
        webSocketInstance: null as any,
        chatroomMessages: [] as any[], // Chatroom messages
    }),
    // Getters
    getters: {},
    // Actions
    actions: {
        async createWebSocket(nickname: string) {
            if (this.webSocketInstance) {
                this.webSocketInstance.close();
            }
            return new Promise((resolve, reject) => {
                if (!nickname) {
                    reject('');
                    return;
                }
                this.nickname = nickname;
                this.webSocketInstance = new WebSocket('ws://127.0.0.1:8080/websocket/' + nickname);

                this.webSocketInstance.onopen = () => {
                    console.log('WebSocket is connected');
                    this.createAIAvatar();
                    resolve(true);
                };

                this.webSocketInstance.onmessage = (event: any) => {
                    const data = JSON.parse(event.data);
                    if (data.type === 'updateFriendsList') {
                        this.updateFriends(data);
                    } else if (data.type === 'messages') {
                        let findIndex = this.friendsListInfo.findIndex((object: any) => object.nickname === data.sendNickname);
                        if (findIndex !== -1) {
                            if (this.friendsListInfo[findIndex].nickname === this.friendsInfo.nickname){
                                this.friendsInfo.latestNews = data.messages;
                                this.friendsInfo.messages.push({
                                    type: 'friend',
                                    message: data.messages
                                });
                            } else {
                                this.friendsListInfo[findIndex].latestNews = data.messages;
                                this.friendsListInfo[findIndex].messages.push({
                                    type: 'friend',
                                    message: data.messages
                                });
                            }
                        }
                        // For chatroom messages
                        this.receiveChatroomMessage(data);
                    }
                };

                this.webSocketInstance.onclose = () => {
                    console.log('WebSocket is disconnected');
                    resolve(false);
                };

                this.webSocketInstance.onerror = (error: any) => {
                    console.error('WebSocket error:', error);
                    reject(error);
                };
            });
        },

        createAIAvatar() {
            if (!this.nickname.endsWith("1")) {
                const aiNickname = `${this.nickname}1`;
                this.joinChatroom(aiNickname);
            }
        },

        receiveChatroomMessage(data: any) {
            this.chatroomMessages.push({
                sender: data.sendNickname,
                content: data.messages
            });
        },

        sendChatroomMessage(content: string) {
            const message = {
                type: "messages",
                sendNickname: this.nickname,
                messages: content
            };
            this.webSocketInstance.send(JSON.stringify(message));

            this.chatroomMessages.push({
                sender: this.nickname,
                content
            });
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
                // Handling the AI avatars
                this.createAIAvatar();
            }


        },
        async sendMessages(receiveMessage: string) {
            if (!this.friendsInfo.nickname) {
                return false;
            }
            let message = {
                type: "messages",
                sendNickname: this.nickname,
                receiveNickname: this.friendsInfo.nickname,
                messages: receiveMessage
            };
            this.webSocketInstance.send(JSON.stringify(message));

            let addMessage = {
                type: 'my',
                message: receiveMessage
            };
            this.friendsInfo.messages.push(addMessage);
            this.friendsInfo.latestNews = receiveMessage;
            return true;
        },
        joinChatroom(nickname: string) {
            // 模拟AI分身加入聊天室并发送一条欢迎消息
            this.sendChatroomMessage(`Hi, I'm ${nickname}, your AI avatar.`);
        },
    },
});