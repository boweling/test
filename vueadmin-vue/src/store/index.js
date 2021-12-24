import Vue from 'vue'
import Vuex from 'vuex'
import menus from './modules/menus'
Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        token: ''
    },
    mutations: {
        SET_TOKEN: (state, token) => {  //通过mutation中的方法来操作state里的数据
            state.token = token;    //将jwt赋给state中的token
            localStorage.setItem("token", token);   //再把token存储到本地存储中去(即将jwt存储到本地中去)
        },


    },
    actions: {},
    modules: {
        menus
    }
})
