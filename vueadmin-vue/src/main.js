import Vue from 'vue'
import './plugins/axios'
import App from './App.vue'
import router from './router'
import store from './store'

import axios from './axios'
Vue.prototype.$http = axios   //全局$http可以使用axios库，axios是一个基于 promise 的 HTTP 库，类ajax

import global from './globalFun'

import Element from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.config.productionTip = false

// require("./mock") //引入mock数据，关闭则注释该行

Vue.use(Element)

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
