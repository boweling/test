import Vue from "vue"


/*定义一个全局的方法，在每个地方都可以调用*/
Vue.mixin({
    methods: {
        hasAuth(perm) {
            var authority = this.$store.state.menus.permList

            return authority.indexOf(perm) > -1  /* indexOf 方法: 获取perm在authority中的索引值
                                                    即，当结果为0就表示perm在authority的第一个位置，
                                                    所以知道表达式  >  -1 就说明perm在authority里面*/
        }
    }
})