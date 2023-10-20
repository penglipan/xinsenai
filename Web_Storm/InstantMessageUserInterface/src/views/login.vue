<template>
  <!--登录-->
  <div class="login">
    <!--容器-->
    <div class="container">
      <!--布局-->
      <el-row class="layout">
        <el-col class="col" :span="15">
          <!-- 产品效果图 -->
          <div class="product-show">
            <img src="@/assets/image/theme/login/Product-Demo-Photo.png" alt="">
          </div>
        </el-col>
        <el-col :span="9">
          <!--表单容器-->
          <div class="form-container">
            <!--LOGO-->
            <div class="logo">
              <!--图标-->
              <div class="ico">
                <img src="@/assets/image/theme/login/logo.png" alt="">
              </div>
              <!--标题-->
              <div class="title">
                即时通讯
              </div>
            </div>
            <!--表单-->
            <div class="form">
              <!--头像-->
              <div class="avatar">
                <img src="@/assets/image/theme/login/avatar.png" alt="">
              </div>
              <!--账号/昵称-->
              <div class="nickname">
                <el-input
                    v-model="nickname"
                    placeholder="请输入账号/昵称..."
                    :prefix-icon="Avatar"
                    @keyup.enter="submitCreate"
                />
              </div>
              <!--提交-->
              <div class="submit">
                <el-button color="#446DFF" type="primary" @click="submitCreate">登录</el-button>
              </div>
              <!--自动登录/忘记密码-->
              <div class="automatic-login-forgot-password">
                <!--自动登录-->
                <div class="automatic-login">
                  <el-checkbox v-model="automaticLoginCheckBox" size="large">
                    记住自动登录
                  </el-checkbox>
                </div>
                <!--忘记密码-->
                <div class="forgot-password">
                  <el-link :underline="false">忘记账号昵称？</el-link>
                </div>
              </div>
              <!--脚页-->
              <div class="footer">
                <div>© 2023 LiuZhengQuan.cn</div>
                <div>Cookie与隐私</div>
                <div>使用条款</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue'
import {useUser} from '../store/user';
import {useRouter} from "vue-router";
import {Avatar} from "@element-plus/icons-vue";

const nickname = ref('')
const automaticLoginCheckBox = ref(false)

const router = useRouter()
const userStore = useUser()
import {ElNotification} from 'element-plus'

/**
 * 提交创建登录
 */
const submitCreate = () => {
  if (nickname.value) {
    // 创建连接
    userStore.createWebSocket(nickname.value).then(async (res: any) => {
      if (res) {
        ElNotification({
          title: 'Success',
          message: '欢迎 ' + nickname.value + ' 回家~',
          type: 'success',
        })
        await router.push({
          name: 'home'
        })
        return
      } else {
        ElNotification({
          title: 'Warning',
          message: '登录失败，请重新尝试',
          type: 'warning',
        })
      }
    }).catch(async (error: any) => {
      ElNotification({
        title: 'Error',
        message: error,
        type: 'error',
      })
    })
  } else {
    ElNotification({
      title: 'Warning',
      message: '请输入用户账号或昵称',
      type: 'warning',
    })
  }
}

</script>

<style lang="scss" scoped>
// 容器
.container {
  display: flex;
  width: 100%;
  // 布局
  .layout {

    width: 100%;

    .col {
      display: flex;
      align-items: center;
      justify-content: center;
    }

    // 产品效果图
    .product-show {
      width: 869px;
      height: 547px;

      img {
        width: 100%;
        height: 100%;
      }
    }

    //  表单容器
    .form-container {
      position: relative;
      display: flex;
      align-items: center;
      flex-direction: column;
      justify-content: center;

      // logo
      .logo {
        display: flex;
        align-items: center;
        // 图标
        .ico {
          width: 48px;
          height: 48px;

          img {
            width: 100%;
            height: 100%;
          }
        }
      }

      // 标题
      .title {
        color: #000;
        font-size: 32px;
        font-family: "Alimama ShuHeiTi Bold", serif;
      }


      //  表单
      .form {
        position: relative;
        display: flex;
        width: 400px;
        height: 400px;
        background: rgba(255, 255, 255, .7);
        margin-top: 30px;
        align-items: center;
        border-radius: 20px;
        flex-direction: column;
        justify-content: center;
        backdrop-filter: blur(3px);

        // 头像
        .avatar {
          width: 84px;
          height: 84px;
          cursor: pointer;
          margin-bottom: 20px;

          img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
          }
        }

        // 账号/昵称
        .nickname {
          margin-bottom: 10px;

          :deep(.el-input__inner) {
            color: #000;
            font-weight: 600;
            font-family: "Alimama DongFangDaKai", serif;
          }
        }

        // 提交
        .submit {
          margin-bottom: 20px;

          button {
            width: 223px;
          }

          font-family: "Alimama DongFangDaKai", serif;
        }

        // 自动登录/忘记密码
        .automatic-login-forgot-password {
          display: flex;
          width: 80%;
          align-items: center;
          margin-bottom: 20px;
          justify-content: space-between;
        }

        // 自动登录
        .automatic-login {
          :deep(.el-checkbox) {
            --el-checkbox-border-radius: 8px;
          }

          :deep(.el-checkbox__label) {
            font-size: 12px;
            color: rgba(37, 38, 43, .6);
          }
        }

        // 忘记密码
        .forgot-password {
          :deep(.el-link__inner) {
            font-size: 12px;
            color: rgba(37, 38, 43, .6);
          }

          :deep(.el-link__inner:hover) {
            color: #409eff;
          }
        }
      }
    }

    // 脚页
    .footer {
      position: absolute;
      display: flex;
      width: 80%;
      color: rgba(37, 38, 43, .8);
      font-size: 12px;
      bottom: 6px;
      align-items: center;
      justify-content: space-between;
    }
  }


}

// 登录
.login {
  display: flex;
  width: 100%;
  height: 100vh;
  align-items: center;
  justify-content: center;
  // background: #ecefff;
  background: #ECEFFF;
}
</style>