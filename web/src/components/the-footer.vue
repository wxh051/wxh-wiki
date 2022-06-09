<template>
  <a-layout-footer style="text-align: center;height: 80px;width: 100%">
    xh电子书<span v-show="user.id">，欢迎：{{ user.name }}</span>
  </a-layout-footer>
</template>

<script lang="ts">
import {defineComponent, computed} from 'vue';
import store from "@/store";

export default defineComponent({
  name: 'the-footer',
  setup() {
    //通过computed去监听store里面的user
    const user = computed(() =>
        store.state.user
    );
    return {
      user
    }
  }
});
</script>