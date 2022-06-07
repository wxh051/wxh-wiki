<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <!--     查询时构造一个JSON参数对象。这里的pagination是响应式变量，在html用，就不需要去.value了       -->
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="名称">
              <template #prefix><SmileTwoTone  style="color: rgba(0, 0, 0, 0.25)" /></template>
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <!--  pagination:分页 loading：等待框，true的话有个等待的效果    -->
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar"/>
        </template>
        <!--        template可以用来渲染某个字段-->
        <!--        默认提供text和record，text表示当前要渲染的字段的值，record表示这一行记录，可以通过record.id来获得这一行的id-->
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="Are you sure delete this task?"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <!--  a-modal这个标签可以放在template根目录下，也可以放在上面a-layout中（vue2中，template下只能有一个节点）-->
  <a-modal
      title="电子书表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover"/>
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name"/>
      </a-form-item>
      <a-form-item label="分类一">
        <a-input v-model:value="ebook.category1Id"/>
      </a-form-item>
      <a-form-item label="分类二">
        <a-input v-model:value="ebook.category2Id"/>
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" type="textarea"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import {message} from "ant-design-vue";

export default defineComponent({
  name: 'AdminEbook',
  setup() {
    //在上面按名字查询时作为参数
    const param = ref();
    //初始给一个空对象，不加会报错
    param.value = {};
    const ebooks = ref();
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0
    });
    const loading = ref(false);

    //dataIndex就是用来映射到你的后端返回的数据里的属性字段
    const columns = [
      {
        title: '封面',
        dataIndex: 'cover',
        slots: {customRender: 'cover'}
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '分类一',
        key: 'category1Id',
        dataIndex: 'category1Id'
      },
      {
        title: '分类二',
        dataIndex: 'category2Id'
      },
      {
        title: '文档数',
        dataIndex: 'docCount'
      },
      {
        title: '阅读数',
        dataIndex: 'viewCount'
      },
      {
        title: '点赞数',
        dataIndex: 'voteCount'
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
      //loading是一个变量，在table组件里用到，true时，表格就会有个加载框，false时，加载框就消失
      loading.value = true;
      axios.get("/ebook/list", {
        //还可以通过拼接URL的方式添加参数
        params: {
          page: params.page,
          size: params.size,
          name: param.value.name
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          ebooks.value = data.content.list;

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
     * 这里的pagination只是一个普通的参数
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    // -------- 表单 ---------
    //前面三行是定义响应式变量，handleModalOk是点击确定按钮是触发这个方法
    const ebook = ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      //显示loading效果
      modalLoading.value = true;
      //PSOT请求不需要params参数
      axios.post("/ebook/save", ebook.value).then((response) => {
        //只要后端返回，就去掉loading
        modalLoading.value = false;
        const data = response.data;//data=commonResponse
        if (data.success) {
          modalVisible.value = false;

          //重新加载列表，查询当前页
          handleQuery({
            //这两个参数名字必须和后端PageReq中的属性对应起来。这样controller才会将参数映射进去
            page: pagination.value.current,
            size: pagination.value.pageSize
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
      ebook.value = record
    };

    /**
     * 新增
     */
    const add = () => {
      modalVisible.value = true;
      ebook.value = {};
    };

    /**
     * 删除
     * 这个参数类型本来写的是number，改成any（6-9评论里老师建议-因为精度丢失问题）
     */
    const handleDelete = (id: any) => {
      axios.delete("/ebook/delete/" + id).then((response) => {
        const data = response.data;//data=commonResponse
        if (data.success) {
          //重新加载列表，查询当前页
          handleQuery({
            // page: pagination.value.current,
            page: 1,
            size: pagination.value.pageSize
          });
        }
      });
    };

    onMounted(() => {
      handleQuery({
        //这两个参数名字必须和后端PageReq中的属性对应起来。这样controller才会将参数映射进去
        page: 1,
        size: pagination.value.pageSize
      });
    });

    return {
      param,
      ebooks,
      pagination,
      columns,
      loading,
      handleTableChange,
      //handleQuery因为在按名字查询时HTML用到了，所以需要return出去
      handleQuery,

      edit,
      add,
      handleDelete,

      ebook,
      modalVisible,
      modalLoading,
      handleModalOk
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