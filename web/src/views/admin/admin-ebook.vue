<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <!--     查询时构造一个JSON参数对象。这里的pagination是响应式变量，在html用，就不需要去.value了；在js里要访问值就得.value       -->
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="名称">
              <template #prefix>
                <SmileTwoTone style="color: rgba(0, 0, 0, 0.25)"/>
              </template>
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
        <!--  不带具体字段的值的渲染，text和record是一样的，代表整列的值。      -->
        <!--  如果渲染绑定了dataIndex，就相当于对数据某一列元素操作，这时候text等于record.dataIndex      -->
        <!--  现在的分类不是一个普通字段，是一种组合，是自定义的显示方式。需要定义一个渲染      -->
        <template v-slot:category="{ text, record }">
          <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
        </template>
        <!--    template可以用来渲染某个字段  -->
        <!--    默认提供text和record，text表示当前要渲染的字段的值，record表示这一行记录，可以通过record.id来获得这一行的id -->
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <router-link :to="'/admin/doc?ebookId='+record.id">
              <a-button type="primary">
                文档管理
              </a-button>
            </router-link>
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="Are you sure delete this task?"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="primary" danger>
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
      <a-form-item label="分类">
        <!-- value对应的就是选中的值，是个数组       -->
        <!--   lable：显示name属性 value：实际要取得值  children：子属性   -->
        <!--    id,name,children就是level1的属性    -->
        <a-cascader
            v-model:value="categoryIds"
            :field-names="{ label: 'name', value: 'id', children: 'children' }"
            :options="level1"
        />
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
import {Tool} from "@/util/tool";

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
        title: '分类',
        slots: {customRender: 'category'}
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

      //如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，列表显示的还是编辑前的数据
      //但是我这里并没有这个问题，不清空也可以显示修改了的数据
      ebooks.value = [];

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
    /**
     * 数组【100,101】对应：前端开发/Vue
     */
    const categoryIds = ref();

    const ebook = ref();
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    //handleModalOk是点击确定按钮是触发这个方法
    const handleModalOk = () => {
      //显示loading效果
      modalLoading.value = true;
      //拆开获得两个分类值
      ebook.value.category1Id = categoryIds.value[0];
      ebook.value.category2Id = categoryIds.value[1];
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
      //在编辑时修改ebook，通过复制一个对象，对原来的值就没有影响了
      //原来这里就类似Java的引用传递，C语言的指针，修改传递的参数，会使原来的传也受影响
      ebook.value = Tool.copy(record);
      //把一二级分类组合成数组，赋给这个响应式变量，便会自动显示
      categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id]
    };

    /**
     * 新增
     */
    const add = () => {
      modalVisible.value = true;
      ebook.value = {};
      //这里清空一下级联框的值，不然如果点击了编辑按钮，然后关闭编辑框，再次点击新增按钮，会保留上次编辑的分类数据
      categoryIds.value = [];
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

    const level1 = ref();
    //定义一个全局普通变量（只在JS里用），不是响应式变量
    let categorys: any
    /**
     * 查询所有分类
     **/
    const handleQueryCategory = () => {
      loading.value = true;
      axios.get("/category/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          categorys = data.content;
          console.log("原始数组：", categorys);

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构：", level1.value);

          //加载完分类后，再添加电子书，否则如果分类树加载很慢，则电子书渲染会报错
          handleQuery({
            //这两个参数名字必须和后端PageReq中的属性对应起来。这样controller才会将参数映射进去
            page: 1,
            size: pagination.value.pageSize
          });
        } else {
          message.error(data.message);
        }
      });
    };

    const getCategoryName = (cid: number) => {
      // console.log(cid)
      let result = "";
      categorys.forEach((item: any) => {
        if (item.id === cid) {
          // return item.name; // 注意，这里直接return不起作用
          result = item.name;
        }
      });
      return result;
    };

    onMounted(() => {
      //加载分类
      handleQueryCategory();
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
      getCategoryName,

      edit,
      add,
      handleDelete,

      ebook,
      modalVisible,
      modalLoading,
      handleModalOk,
      categoryIds,
      level1
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