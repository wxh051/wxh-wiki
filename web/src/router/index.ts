import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router'
import Home from '../views/home.vue'
import About from '../views/about.vue'
import Changan from '../views/changan.vue'
import Doc from '../views/doc.vue'
import AdminUser from '../views/admin/admin-user.vue'
import AdminEbook from '../views/admin/admin-ebook.vue'
import AdminCategory from '../views/admin/admin-category.vue'
import AdminDoc from '../views/admin/admin-doc.vue'
import store from "@/store";
import {Tool} from "@/util/tool";

const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'Home',
        component: Home
    },
    {
        path: '/doc',
        name: 'Doc',
        component: Doc
    },
    {
        path: '/about',
        name: 'About',
        component: About
        // route level code-splitting
        // this generates a separate chunk (about.[hash].js) for this route
        // which is lazy-loaded when the route is visited.
        // component: () => import(/* webpackChunkName: "about" */ '../views/about.vue')
    },
    {
        path: '/changan',
        name: 'Changan',
        component: Changan
    },
    {
        path: '/admin/user',
        name: 'AdminUser',
        component: AdminUser,
        meta: {
            loginRequire: true
        }
    },
    {
        path: '/admin/ebook',
        name: 'AdminEbook',
        component: AdminEbook,
        //    meta：元数据，可以自己定义一些信息
        meta: {
            loginRequire: true
        }
    },
    {
        path: '/admin/category',
        name: 'AdminCategory',
        component: AdminCategory,
        meta: {
            loginRequire: true
        }
    },
    {
        path: '/admin/doc',
        name: 'AdminDoc',
        component: AdminDoc,
        meta: {
            loginRequire: true
        }
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 路由登录拦截
//to：新路由 from：旧路由
router.beforeEach((to, from, next) => {
    // 要不要对meta.loginRequire属性做监控拦截
    if (to.matched.some(function (item) {
        console.log(item, "是否需要登录校验：", item.meta.loginRequire);
        return item.meta.loginRequire
    })) {
        const loginUser = store.state.user;
        if (Tool.isEmpty(loginUser)) {
            console.log("用户未登录！");
            next('/');
        } else {
            //不带参数，就是不拦截
            next();
        }
    } else {
        next();
    }
});

export default router
