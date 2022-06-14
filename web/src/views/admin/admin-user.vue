<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="登陆名">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
              <template #icon>
                <SearchOutlined/>
              </template>
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              <template #icon>
                <DingdingOutlined/>
              </template>
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <!--     ghost 将按钮样式设为幽灵状态，即中间不填充背景色       -->
            <a-button type="primary" ghost @click="resetPassword(record)">
              <template #icon>
                <GitlabFilled/>
              </template>
              重置密码
            </a-button>
            <a-button type="primary" ghost @click="edit(record)">
              <template #icon>
                <EditFilled/>
              </template>
              编辑
            </a-button>
            <a-popconfirm
                title="删除后不可恢复，确认删除?"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button danger>
                <template #icon>
                  <DeleteOutlined/>
                </template>
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
      title="用户表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="登陆名">
        <!--    使用！！绕过类型检验    -->
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="user.name"/>
      </a-form-item>
      <a-form-item label="密码" v-show="!user.id">
        <a-input v-model:value="user.password" type="password"/>
      </a-form-item>
    </a-form>
  </a-modal>

  <a-modal
      title="重置密码"
      v-model:visible="resetModalVisible"
      :confirm-loading="resetModalLoading"
      @ok="handleResetModalOk"
  >
    <a-form
        :model="user"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 18 }"
        :rules="rules"
        ref="formRef"
    >
      <a-form-item label="新密码" has-feedback name="password">
        <a-input v-model:value="user.password" type="password" autocomplete="off"/>
      </a-form-item>
      <a-form-item label="确认密码" has-feedback name="confirmPassword">
        <a-input v-model:value="user.confirmPassword" type="password" autocomplete="off"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {RuleObject,ValidateErrorEntity} from 'ant-design-vue/es/form/interface';

//告诉文件这两个变量是存在的。使用第三方的js，可以先在这定义一下，即使这里的md5是全局的。但是我好像没加也没有报错
declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'AdminUser',
  setup() {
    //用于重置密码信息校验
    const formRef = ref();

    const param = ref();
    param.value = {};
    const users = ref();
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0
    });
    const loading = ref(false);

    const columns = [
      {
        title: '登陆名',
        dataIndex: 'loginName'
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '密码',
        dataIndex: 'password'
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];

    /**
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      loading.value = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      users.value = [];
      axios.get("/user/list", {
        params: {
          page: params.page,
          size: params.size,
          loginName: param.value.loginName
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          users.value = data.content.list;

          // 重置分页按钮
          pagination.value.current = params.page;
          pagination.value.total = data.content.total;
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    // -------- 表单 ---------
    const user = ref();
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      modalLoading.value = true;
      //KEY是一个盐值，使密文更难以破解
      user.value.password = hexMd5(user.value.password + KEY);
      axios.post("/user/save", user.value).then((response) => {
        modalLoading.value = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          modalVisible.value = false;

          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 编辑
     */
    const edit = (record: any) => {
      modalVisible.value = true;
      user.value = Tool.copy(record);
    };

    /**
     * 新增
     */
    const add = () => {
      modalVisible.value = true;
      user.value = {};
    };

    const handleDelete = (id: number) => {
      axios.delete("/user/delete/" + id).then((response) => {
        const data = response.data; // data = commonResp
        if (data.success) {
          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
    };

    let validatePass = async (rule: RuleObject, value: string) => {
      if (value === '') {
        return Promise.reject('密码为空，请重新输入');
      } else if (value !== user.value.password) {
        return Promise.reject("输入密码不相同!");
      } else {
        return Promise.resolve();
      }
    };

    //登录时检验密码和账号
    //trigger:校验触发的时机(blur：失去焦点 change：发生变化）
    const rules = {
      password: [
        {required: true, message: '密码为空，请重新输入', trigger: ['change', 'blur']},
        {min: 6, max: 20, message: '密码长度最低为6位，最高为20位', trigger: ['change', 'blur']},
        {
          trigger: ['change', 'blur'],
          message: '密码强度太低，至少包含数字和字母',
          pattern: '^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$',
        }
      ],
      confirmPassword: [
        { validator: validatePass,trigger: ['change', 'blur']},
      ],
    };

    // -------- 重置密码 ---------
    const resetModalVisible = ref(false);
    const resetModalLoading = ref(false);
    const handleResetModalOk = () => {
      formRef.value.validate().then(() => {
        resetModalLoading.value = true;
        //KEY是一个盐值，使密文更难以破解
        user.value.password = hexMd5(user.value.password + KEY);
        axios.post("/user/reset-password", user.value).then((response) => {
          resetModalLoading.value = false;
          const data = response.data; // data = commonResp
          if (data.success) {
            resetModalVisible.value = false;

            // 重新加载列表
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize,
            });
          } else {
            message.error(data.message);
          }
        });
      })
          .catch((error: ValidateErrorEntity) => {
            message.error('密码输入不符号要求');
          });
    };

    /**
     * 编辑密码
     */
    const resetPassword = (record: any) => {
      resetModalVisible.value = true;
      user.value = Tool.copy(record);
      user.value.password = null;
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });
    });

    return {
      param,
      users,
      pagination,
      columns,
      loading,
      handleTableChange,
      handleQuery,

      edit,
      add,

      user,
      modalVisible,
      modalLoading,
      handleModalOk,


      handleDelete,


      resetModalVisible,
      resetModalLoading,
      handleResetModalOk,
      resetPassword,

      rules,
      formRef
    }
  }
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>
