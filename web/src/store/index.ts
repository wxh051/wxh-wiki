import {createStore} from 'vuex'

const store = createStore({
    state: {
        user: {}
    },
    //对变量的操作，同步
    mutations: {
        setUser(state, user) {
            state.user = user;
        }
    },
    //异步
    actions: {},
    modules: {}
});

export default store;
