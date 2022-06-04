<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          v-model:selectedKeys="selectedKeys2"
          v-model:openKeys="openKeys"
          :style="{ height: '100%', borderRight: 0 }"
      >
        <a-sub-menu key="sub1">
          <template #title>
            <span>
              <user-outlined/>
              subnav 1
            </span>
          </template>
          <a-menu-item key="1">option1</a-menu-item>
          <a-menu-item key="2">option2</a-menu-item>
          <a-menu-item key="3">option3</a-menu-item>
          <a-menu-item key="4">option4</a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="sub2">
          <template #title>
            <span>
              <laptop-outlined/>
              subnav 2
            </span>
          </template>
          <a-menu-item key="5">option5</a-menu-item>
          <a-menu-item key="6">option6</a-menu-item>
          <a-menu-item key="7">option7</a-menu-item>
          <a-menu-item key="8">option8</a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="sub3">
          <template #title>
            <span>
              <notification-outlined/>
              subnav 3
            </span>
          </template>
          <a-menu-item key="9">option9</a-menu-item>
          <a-menu-item key="10">option10</a-menu-item>
          <a-menu-item key="11">option11</a-menu-item>
          <a-menu-item key="12">option12</a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <pre>
{{ ebooks }}
{{ ebooks2 }}
      </pre>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref, reactive, toRef} from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'Home',
  //Vue3新增的初始化方法。这个组件加载完后会去加载这个方法
  //setup就放一些参数定义，方法定义
  setup() {
    console.log("setup");
    //ref():响应式数据，在js里动态修改里面的值，实时反馈到页面上
    const ebooks = ref();
    //定义一个变量，实际上返回的是里面的属性，将这个属性变成一个响应式变量
    const ebooks1 = reactive({books: []});


    //一般要初始化的一些逻辑，建议都写到生命周期函数里。
    // 如果写在setup方法里，有时候setup执行的时候界面还没渲染好，这时候如果去操作界面元素会报错
    onMounted(() => {
      console.log("onMounted");
      axios.get("http://localhost:8888/ebook/list?name=Spring").then((response) => {
        const data = response.data;
        ebooks.value = data.content
        ebooks1.books = data.content;
        console.log(response);
      });
    })

    //HTML代码拿到响应式变量，需要在setup最后return
    return {
      ebooks,
      //返回，只需要返回books这段就可以；通过toref变成响应式变量
      //另有一个torefs，可以将所有属性变成响应式变量
      ebooks2: toRef(ebooks1, "books")
    }
  }
});
</script>
