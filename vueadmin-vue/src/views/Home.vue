<template>

  <el-container>
    <el-aside width="200px">
      <SideMenu></SideMenu>   <!--使用侧边栏组件-->
    </el-aside>
    <el-container>
      <el-header>
        <strong>VueAdmin后台管理器</strong>
        <div class="header-avatar">
          <!--          <el-avatar size="medium" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>-->
          <el-avatar size="medium" :src="userInfo.avatar"></el-avatar>
          <el-dropdown>
            <span class="el-dropdown-link">
              {{ userInfo.username }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
<!--                ========================-->
                <router-link  :to="{name:'UserCenter'}">个人中心</router-link>
<!--                ========================-->
              </el-dropdown-item>
              <el-dropdown-item @click.native="logout"> 退出</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <el-link href="https://markerhub.com" target="_blank">网站</el-link>
          <el-link href="https://space.bilibili.com/13491144" target="_blank">B站</el-link>
        </div>

      </el-header>
      <el-main>
        <Tabs></Tabs>
        <div style="margin: 0 15px;">
          <router-view/> <!--  路由显示-->
        </div>

      </el-main>
    </el-container>
  </el-container>

</template>

<script>
import SideMenu from "./inc/SideMenu";    //引入SideMenu侧边栏组件
import Tabs from './inc/Tabs'

export default {
  name: "Home",
  components: {    //声明要使用的组件
    SideMenu, Tabs
  },
  data() {
    return {
      userInfo: {
        id: "",
        username: "",
        avatar: ""
      }
    }
  },
  created() {
    this.getUserInfo();

  },
  methods: {
    selectMenu(item){
      this.$store.commit("addTab",item)
    },
    getUserInfo() {
      this.$http.get("/sys/userInfo").then(res => {
        this.userInfo = res.data.data;
        // console.log(this.userInfo.avatar);
        // console.log(this.userInfo.username);
      })
    },
    logout() {
      this.$http.post("/logout").then(res => {
        localStorage.clear()
        sessionStorage.clear()
        this.$store.commit("resetState")
        this.$router.push("/login")
      })
    }
  }
}
</script>

<style scoped>
.header-avatar {
  float: right;
  width: 210px;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.el-container {
  padding: 0;
  margin: 0;
  height: 100%;
}

.el-header {
  background-color: #17B3A3;
  color: #333;
  text-align: center;
  line-height: 60px;
}

.el-aside {
  background-color: #D3DCE6;
  color: #333;
  text-align: center;
  line-height: 200px;
}

.el-main {
  color: #333;
  /*text-align: center;*/
  padding: 0;
  /*line-height: 160px;*/
}

a {
  text-decoration: none; /*去掉 a标签的下划线   注意：这里没有加  .   */
}

.el-dropdown-link {
  cursor: pointer;
}

</style>