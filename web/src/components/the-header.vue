<template>
  <a-layout-header class="header">
    <div class="logo">LiKe知识库</div>
    <div>
      <a-popconfirm
          title="确认退出登录?"
          ok-text="是"
          cancel-text="否"
          @confirm="logout()"
      >
        <!--  a标签支持点击，按钮也可以，但是会有个边框    -->
        <!--  做一个互斥，只显示一个    -->
        <a class="login-menu" v-show="user.id">
          <span>退出登录</span>
        </a>
      </a-popconfirm>
      <a class="login-menu" v-show="user.id">
        <span>您好：{{ user.name }}</span>
      </a>
      <a class="login-menu" v-show="!user.id" @click="showLoginModal">
        <span>登录</span>
      </a>
    </div>
    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/">
        <router-link to="/">
          首页
        </router-link>
      </a-menu-item>
      <a-menu-item key="/admin/user" v-if="user.id">
        <router-link to="/admin/user">
          用户管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/admin/ebook" v-if="user.id">
        <router-link to="/admin/ebook">
          电子书管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/admin/category" v-if="user.id">
        <router-link to="/admin/category">
          分类管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">
          关于我们
        </router-link>
      </a-menu-item>
    </a-menu>

    <a-modal
        title="登录"
        v-model:visible="loginModalVisible"
        :confirm-loading="loginModalLoading"
        @ok="login"
    >
      <a-form
          :model="loginUser"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 18 }"
          :rules="rules"
          ref="formRef"
      >
        <a-form-item has-feedback label="登录名" name="loginName">
          <a-input v-model:value="loginUser.loginName"/>
        </a-form-item>
        <a-form-item has-feedback label="密码" name="password">
          <a-input v-model:value="loginUser.password" type="password"/>
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout-header>
</template>

<script lang="ts">
import {computed, defineComponent, ref} from 'vue';
import {useRouter} from "vue-router";
import axios from 'axios';
import {message} from 'ant-design-vue';
import store from "@/store";
import {ValidateErrorEntity} from 'ant-design-vue/es/form/interface';

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'the-header',
  setup() {
    //用于登录信息校验
    const formRef = ref();

    //用于退出登录时跳转到首页
    const router = useRouter();

    //登录后保存
    const user = computed(() =>
        store.state.user
    );

    //用来登录
    const loginUser = ref({
      loginName: "xhxh",
      password: "wxhtobe1"
    });

    const loginModalVisible = ref(false);
    const loginModalLoading = ref(false);
    const showLoginModal = () => {
      loginModalVisible.value = true;
    };

    //登录时检验密码和账号
    const rules = {
      loginName: [
        {required: true, message: '名称为空，请重新输入', trigger: 'blur'},
      ],
      password: [
        {required: true, message: '密码为空，请重新输入', trigger: ['change', 'blur']},
        {min: 6, max: 20, message: '密码长度最低为6位，最高为20位', trigger: ['change', 'blur']},
        {
          trigger: ['change', 'blur'],
          message: '密码强度太低，至少包含数字和字母',
          pattern: '^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$',
        }
      ],
    };

    // 登录
    const login = () => {
      formRef.value.validate().then(() => {
        console.log("开始登录");
        loginModalLoading.value = true;
        //这个位置可以加一些前端的校验
        loginUser.value.password = hexMd5(loginUser.value.password + KEY);
        axios.post('/user/login', loginUser.value).then((response) => {
          loginModalLoading.value = false;
          const data = response.data;
          if (data.success) {
            loginModalVisible.value = false;
            message.success("登录成功！");
            store.commit("setUser", data.content);
          } else {
            message.error(data.message);
          }
        });
      })
          .catch((error: ValidateErrorEntity) => {
            message.error('登录信息不符号要求');
          });
    };

    /*// 登录
    const login = () => {
      console.log("开始登录");
      loginModalLoading.value = true;
      loginUser.value.password = hexMd5(loginUser.value.password + KEY);
      axios.post('/user/login', loginUser.value).then((response) => {
        loginModalLoading.value = false;
        const data = response.data;
        if (data.success) {
          loginModalVisible.value = false;
          message.success("登录成功！");
          store.commit("setUser", data.content);
        } else {
          message.error(data.message);
        }
      });
    };*/

    // 退出登录
    const logout = () => {
      console.log("退出登录");
      axios.get('/user/logout/' + user.value.token).then((response) => {
        const data = response.data;
        if (data.success) {
          message.success("退出登录成功！");
          store.commit("setUser", {});
          router.push("/");
        } else {
          message.error(data.message);
        }
      });
    };

    return {
      loginModalVisible,
      loginModalLoading,
      showLoginModal,
      loginUser,
      login,
      user,
      logout,

      rules,
      formRef
    }
  }
});
</script>

<!--登录标签放到右边-->
<style>
.logo {
  width: 120px;
  height: 31px;
  /*background: rgba(255, 255, 255, 0.2);*/
  /*margin: 16px 28px 16px 0;*/
  float: left;
  color: white;
  font-size: 18px;
}
.login-menu {
  float: right;
  color: white;
  padding-left: 10px;
}
</style>