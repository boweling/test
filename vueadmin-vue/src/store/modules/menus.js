import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default{
    state: {

        menuList : [],
        permList:[],

        hasRoutes: false,

        editableTabsValue: 'Index',
        editableTabs: [{
            title: '首页',
            name: 'Index',
        }],
    },
    mutations: {
        setMenuList(state,menus){
            state.menuList = menus
        },
        setPermList(state,perms){
            state.permList = perms
        },
        changeRouteStatus(state, hasRoutes) {
            state.hasRoutes = hasRoutes

            // sessionStorage.setItem("hasRoute",hasRoute)
        },

        addTab(state,tab) {

            let index = state.editableTabs.findIndex(e => e.name === tab.name)//e表示editableTabs数组里的每个数据，遍历
                                                                //判断数组中是否已经有和tab.name相同的标签名，没有就返回 -1
            if (index === -1) {  //等于  -1 就说明数组中没有同样的标签，就push进数组
                state.editableTabs.push({
                    title: tab.title,
                    name: tab.name,
                });
            }
            state.editableTabsValue = tab.name;
        },

        resetState: (state) => {
            state.menuList = []
            state.permList = []

            state.hasRoutes = false
            state.editableTabsValue = 'Index'
            state.editableTabs = [{
                title: '首页',
                name: 'Index',
            }]
        }
    },
    actions: {

    },
}
