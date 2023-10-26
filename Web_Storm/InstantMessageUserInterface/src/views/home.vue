<template>
  <!--主页-->
  <div class="home">
    <!--容器-->
    <div class="container">
      <!--布局-->
      <el-row class="layout">
        <el-col :span="2">
          <!--选项-->
          <div class="grid-content options">
            <!--网络-->
            <div v-if="isOnline" class="network">
              <div></div>
              <div></div>
              <div></div>
            </div>
            <div v-else class="network-offline">
              <div></div>
              <div></div>
              <div></div>
            </div>
            <!--头像-->
            <div class="avatar">
              <img src="@/assets/image/theme/login/avatar.png" alt="">
              <div class="online-state"></div>
              <div class="nickname">{{ nickname }}</div>
            </div>
            <!--功能-->
            <div class="feature">
              <!--选项-->
              <div class="option">
                <el-icon>
                  <Comment/>
                </el-icon>
              </div>
              <div class="option">
                <el-icon>
                  <Avatar/>
                </el-icon>
              </div>
              <div class="option">
                <el-icon>
                  <WalletFilled/>
                </el-icon>
              </div>
              <div class="settings">
                <el-icon>
                  <Tools/>
                </el-icon>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <!--好友-->
          <div class="grid-content friends">
            <!--导航-->
            <div class="navigation">
              <!--搜索-->
              <div class="search">
                <el-input v-model="search" placeholder="" :prefix-icon="Search" size="small"/>
              </div>
              <!--添加-->
              <div class="add">
                <el-icon>
                  <CirclePlusFilled/>
                </el-icon>
              </div>
            </div>
            <!--好友列表无限滚动-->
            <div class="infinite-scroll" style="overflow: auto" v-infinite-scroll="loadInfiniteScroll">
              <!--好友-->
              <div class="friend" @click="friendsInfo=friend" v-for="(friend,index) in friendsListInfo" :key="index">
                <!--头像-->
                <div class="avatar">
                  <img src="@/assets/image/theme/login/other.png" alt="">
                </div>
                <!--好友消息-->
                <div class="message">
                  <!--昵称-->
                  <div class="nickname">
                    <div>{{ friend.nickname }}</div>
                    <div class="badge">
                      {{ friend.messages.length }}
                    </div>
                  </div>

                  <!--最新通知-->
                  <div class="latest-notice">
                    {{ friend.latestNews }}
                  </div>

                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="16">
          <!--聊天-->
          <div class="grid-content chats">
            <!--顶部-->
            <div class="top">
              <!--昵称-->
              <div class="nickname">{{ friendsInfo.nickname }}</div>
              <!--在线状态-->
              <div class="status">
                <!--状态-->
                <div class="state" :style="{background: friendsInfo.status ? '#77FD59' : '#A8ABB2'}"></div>
                <!--在线/离线-->
                <div class="online-and-offline">{{ friendsInfo.status ? '在线' : '离线' }}</div>
              </div>
            </div>
            <!--聊天-->
            <div class="chat">
              <!--无限滚动-->
              <div class="infinite-scroll" style="overflow: auto" v-infinite-scroll="loadInfiniteScroll">
                <div v-for="(message,index) in friendsInfo.messages" :key="index">
                  <!--左边-->
                  <div class="left" v-if="message.type === 'friend'">
                    <!--头像-->
                    <div class="avatar">
                      <img src="@/assets/image/theme/login/other.png" alt="">
                    </div>
                    <!--消息-->
                    <div class="message">
                      {{ message.message }}
                      <!--三角形-->
                      <div class="triangle"></div>
                    </div>
                  </div>
                  <!--右边-->
                  <div class="right" v-else>
                    <!--消息-->
                    <div class="message">
                      {{ message.message }}
                      <!--三角形-->
                      <div class="triangle"></div>
                    </div>
                    <!--头像-->
                    <div class="avatar">
                      <img src="@/assets/image/theme/login/avatar.png" alt="">
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!--底部-->
            <div class="bottom">
              <!--左边-->
              <div class="left">
                <!--表情包-->
                <div class="emoji"><img src="@/assets/image/theme/home/svg/smile.svg" alt=""></div>
                <!--文件-->
                <div class="file">
                  <el-icon>
                    <FolderOpened/>
                  </el-icon>
                </div>
              </div>
              <div class="middle">
                <el-input @keyup.enter="sendMessages" v-model="message" placeholder="现在可以开始聊天了。"/>
              </div>
              <div class="right" @click="sendMessages">
                <el-icon>
                  <Promotion/>
                </el-icon>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ref, reactive, toRefs} from 'vue'
import {useNetwork} from '@vueuse/core'

import {
  Promotion,
  FolderOpened,
  CirclePlusFilled,
  Search,
  WalletFilled,
  Avatar,
  Comment,
  Tools
} from '@element-plus/icons-vue'

import {useUser} from '../store/user';

const userStore = useUser()
import {storeToRefs} from 'pinia'

const {nickname, friendsInfo, friendsListInfo} = storeToRefs(userStore)

const network = reactive(useNetwork())
const {isOnline} = toRefs(network)
// console.log(isOnline.value)

const search = ref('')
const message = ref('')
const loadInfiniteScroll = () => {

}

const sendMessages = async () => {
  if (message.value) {
    if (await userStore.sendMessages(message.value)) {
      // 模拟AI分身回复
      setTimeout(() => {
        const aiReply = `Hi ${nickname.value}, I'm your AI avatar. You said: "${message.value}"`;
        userStore.receiveChatroomMessage({
          sendNickname: `${nickname.value}1`,
          messages: aiReply
        });
      }, 1000); // 延迟1秒回复
      message.value = '';
    }
  }
}
</script>

<style lang="scss" scoped>
// 布局
.layout {
  position: relative;
  width: 100%;
  height: 100%;

  .grid-content {
    height: 100%;
    background: #ecefff;
  }

  // 选项
  .options {
    display: flex;
    width: 100%;
    align-items: center;
    flex-direction: column;
    border-radius: 4px 0 0 4px;
    background: #131824;

    // 状态
    .network {
      display: flex;
      width: 100%;
      margin-top: 20px;
      margin-bottom: 30px;
      justify-content: space-evenly;

      div {
        width: 11px;
        height: 11px;
        border-radius: 50%;
      }

      div:nth-child(1) {
        background: #f1f0ed;
      }

      div:nth-child(2) {
        background: #feba07;
      }

      div:nth-child(3) {
        background: #007bff;
      }
    }

    .network-offline {
      display: flex;
      width: 100%;
      margin-top: 20px;
      margin-bottom: 30px;
      justify-content: space-evenly;

      div {
        width: 11px;
        height: 11px;
        border-radius: 50%;
        background: red;
      }
    }

    // 头像
    .avatar {
      position: relative;
      width: 84px;
      height: 84px;
      margin-bottom: 35px;

      img {
        width: 100%;
        height: 100%;
        border-radius: 50px;
      }

      // 在线状态
      .online-state {
        position: absolute;
        width: 14px;
        height: 14px;
        right: 6px;
        bottom: 6px;
        background: #007bff;
        border-radius: 50%;
      }

      // 用户昵称
      .nickname {
        color: #fff;
        font-size: 14px;
        font-family: "Alimama ShuHeiTi Bold", serif;
      }


    }

    // 功能
    .feature {
      position: relative;
      display: flex;
      width: 100%;
      height: 100%;
      flex-direction: column;
      // 选项
      .option {
        display: flex;
        height: 48px;
        font-size: 24px;
        color: #A6B6C3;
        cursor: pointer;
        align-items: center;
        justify-content: center;
        border-left: 3px solid #131824;
      }

      .option:nth-child(1) {
        color: #fff;
        background: #007bff;
        border-left: 3px solid #131824;
      }

      // 选项鼠标悬浮
      .option:hover {
        color: #fff;
        background: #007bff;
        border-left: 3px solid #131824;
      }

      // 设置
      .settings {
        position: absolute;
        width: 100%;
        display: flex;
        color: #A6B6C3;
        cursor: pointer;
        height: 38px;
        font-size: 24px;
        align-items: center;
        justify-content: center;
        bottom: 20px;
      }

      // 设置鼠标悬浮
      .settings:hover {
        color: #fff;
      }
    }
  }

  // 好友
  .friends {
    display: flex;
    width: 100%;
    align-items: center;
    flex-direction: column;
    background: #363F48;
    // 导航
    .navigation {
      display: flex;
      width: 100%;
      height: 40px;
      background: #3A434A;
      align-items: center;
      margin-bottom: 12px;
      justify-content: space-evenly;
      // 搜索
      .search {


        :deep(.el-input__inner) {
          color: #fff;
          font-weight: 600;
          font-family: "Alimama DongFangDaKai", serif;
        }

        :deep(.el-input__wrapper) {
          background: #303842;
          border-radius: 50px;
        }

      }

      // 添加
      .add {
        display: flex;
        cursor: pointer;
        font-size: 24px;
        align-items: center;
        justify-content: center;
        color: #606266;
      }

      .add:hover {
        color: #ffffff;
      }
    }

    // 好友列表无限滚动
    .infinite-scroll {
      width: 100%;
      height: 530px;
      //border: 1px solid red;
      // 好友
      .friend {
        display: flex;
        height: 60px;
        cursor: pointer;
        align-items: center;
        justify-content: space-evenly;
        // 头像
        .avatar {
          width: 38px;
          height: 38px;

          img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
          }
        }

        // 好友消息
        .message {
          display: flex;
          width: 210px;
          text-align: left;
          flex-direction: column;
          // 昵称
          .nickname {
            display: flex;
            color: #fff;
            font-size: 12px;
            font-family: "Alimama DongFangDaKai", serif;
            justify-content: space-between;

            .badge {
              display: flex;
              color: #ffffff;
              width: 18px;
              height: 18px;
              font-size: 8px;
              font-family: "Alimama ShuHeiTi Bold", serif;
              background: red;
              border-radius: 50%;
              text-align: center;
              align-items: center;
              justify-content: center;
            }
          }

          // 最新通知
          .latest-notice {
            width: 210px;
            color: #C0C4CC;
            font-size: 9px;
            font-family: "Alimama DongFangDaKai", serif;
            text-overflow: ellipsis; /* ellipsis:显示省略符号来代表被修剪的文本  string:使用给定的字符串来代表被修剪的文本*/
            white-space: nowrap; /* nowrap:规定段落中的文本不进行换行   */
            overflow: hidden; /*超出部分隐藏*/
          }
        }
      }

      // 好友鼠标悬浮
      .friend:hover {
        background: #444C56;
      }
    }
  }

  // 聊天
  .chats {
    display: flex;
    width: 100%;
    align-items: center;
    flex-direction: column;
    border-radius: 0 4px 4px 0;
    background: #EFF3F6;
    // 顶部
    .top {
      display: flex;
      width: 100%;
      height: 48px;
      font-size: 14px;
      align-items: center;
      justify-content: center;
      background: #F7FCFF;
      border-radius: 0 4px 4px 0;
      // 昵称
      .nickname {
        color: #000;
        font-family: "Alimama ShuHeiTi Bold", serif;
      }

      // 状态
      .status {
        display: flex;
        align-items: center;
        justify-content: center;
        // 状态
        .state {
          width: 10px;
          height: 10px;
          margin: 0 8px;
          border-radius: 50px;
        }

        // 在线或离线
        .online-and-offline {
          color: #606266;
          font-size: 12px;
          font-family: "Alimama DongFangDaKai", serif;
        }
      }
    }

    // 聊天
    .chat {
      display: flex;
      width: 100%;

      // 无限滚动
      .infinite-scroll {
        width: 100%;
        height: 484px;
      }

      // 左边
      .left {
        position: relative;
        display: flex;
        margin-top: 10px;
        align-items: center;
        padding-left: 6px;
        // 头像
        .avatar {
          width: 32px;
          height: 32px;
          margin-right: 6px;
          transform: scaleX(-1);

          img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
          }
        }

        // 消息
        .message {
          position: relative;
          display: flex;
          color: #000;
          height: 24px;
          padding: 3px 8px;
          font-size: 14px;
          background: #ffffff;
          font-family: "Alimama ShuHeiTi Bold", serif;
          border-radius: 4px;
          align-items: center;
          margin-top: 10px;
          margin-left: 8px;
          // 三角形
          .triangle {
            position: absolute;
            left: -6px;
            top: 6px;
            border-top: 6px solid transparent;
            border-right: 8px solid #fff;
            border-bottom: 6px solid transparent;
          }

          box-shadow: 0 0 12px rgba(0, 0, 0, .12);
        }
      }

      // 右边
      .right {
        position: relative;
        display: flex;
        margin-top: 10px;
        padding-right: 6px;
        align-items: center;
        /* 终点对齐 */
        justify-content: flex-end;
        // 消息
        .message {
          position: relative;
          display: flex;
          color: #000;
          height: 24px;
          padding: 3px 8px;
          font-size: 14px;
          background: #55D58B;
          font-family: "Alimama ShuHeiTi Bold", serif;
          border-radius: 4px;
          align-items: center;
          margin-top: 10px;
          margin-right: 8px;

          // 三角形
          .triangle {
            position: absolute;
            right: -6px;
            top: 6px;
            border-top: 6px solid transparent;
            border-left: 8px solid #55D58B;
            border-bottom: 6px solid transparent;
            //background-color: #ccc;
          }

          box-shadow: 0 0 12px rgba(0, 0, 0, .12);
        }

        // 头像
        .avatar {
          width: 32px;
          height: 32px;
          margin-left: 6px;
          transform: scaleX(-1);

          img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
          }
        }
      }
    }

    // 底部
    .bottom {
      display: flex;
      width: 100%;
      height: 48px;
      font-size: 14px;
      align-items: center;
      justify-content: space-evenly;
      background: #F7FCFF;
      border-radius: 0 0 4px 0;
      // 左边
      .left {
        display: flex;
        align-items: center;
        justify-content: center;
        // 表情包
        .emoji {
          width: 28px;
          height: 28px;
          cursor: pointer;

          img {
            width: 100%;
            height: 100%;
          }

          margin-right: 12px;
        }

        // 文件
        .file {
          display: flex;
          color: #a9acaf;
          cursor: pointer;
          font-size: 28px;
          font-weight: 400;
          align-items: center;
          justify-content: center;
        }
      }

      // 中间
      .middle {
        width: 80%;

        :deep(.el-input__inner) {
          color: #000;
          font-weight: 600;
          font-family: "Alimama DongFangDaKai", serif;
        }
      }

      // 右边
      .right {
        display: flex;
        color: #a9acaf;
        cursor: pointer;
        font-size: 28px;
        font-weight: 400;
        align-items: center;
        justify-content: center;
      }

      // 右边鼠标悬浮
      .right:hover {
        color: #409EFF;
      }
    }
  }

}

/*容器*/
.container {
  width: 1180px;
  height: 580px;
  border-radius: 4px;
  //background: #ffffff;
  box-shadow: 0 3px 10px rgba(0, 0, 0, .92);
}

/*主页*/
.home {
  display: flex;
  width: 100%;
  height: 100vh;
  align-items: center;
  justify-content: center;
  background: #ECEFFF;
}
</style>