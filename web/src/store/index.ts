import {createStore} from 'vuex'

//定义一下，使用JS变量
declare let SessionStorage: any;
const USER = "USER";

const store = createStore({
    state: {
        //加上|| {}避免空指针异常
        user: SessionStorage.get(USER) || {}
    },
    //对变量的操作，同步
    //调用方法时，不需要写第一个参数，固定state
    mutations: {
        setUser(state, user) {
            state.user = user;
            SessionStorage.set(USER,user);
        }
    },
    //异步
    actions: {},
    modules: {}
});

export default store;
