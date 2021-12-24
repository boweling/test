<template>
  <div>
    <el-row type="flex" class="row-bg" justify="center">
      <el-col :xl="6" :gl="7">
        <h2>欢迎来到VueAdmin管理系统</h2>
        <el-image :src="require('@/assets/MarkerHub.jpg')" style="height: 180px;width: 180px"></el-image>
        <p>公众号MarkerHub</p>
        <p>扫码二维码，回复【VueAdmin
          】获取登录密码</p>
      </el-col>

      <el-col :span="1">
        <el-divider direction="vertical"></el-divider>
      </el-col>

      <el-col :xl="6" :gl="7">
        <!--  ref： 表示为这个form表单注册一个实例， 以便下面可以通过 this.$refs[表单实例名称] 获取表单数据进行校验      -->
        <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="80px" class="demo-loginForm">
          <el-form-item label="用户名" prop="username" style="width: 380px">
            <el-input v-model="loginForm.username"></el-input>
          </el-form-item>

          <el-form-item label="密码" prop="password" style="width: 380px">
            <el-input v-model="loginForm.password" type="password"></el-input>
          </el-form-item>

          <el-form-item label="验证码" prop="code" style="width: 380px">
            <el-input v-model="loginForm.code" style="width: 172px; float:left" ></el-input>
            <el-image :src="captchaImg" class="captchaImg" @click="getCaptcha"></el-image>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitForm('loginForm')" style="width: 100px; float:left">立即登录</el-button><!--submitForm方法里的参数就是form表单实例名称-->
            <el-button @click="resetForm('loginForm')" style="float:left">重置</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </div>

</template>

<script>
import qs from 'qs'
export default {
  name: "Login",
  data() {
    return {
      loginForm: {
        username: 'admin',
        password: '111111',
        code: '11111',
        token: '',    /* 随机码 */
      },
      rules: {
        username: [
            /*required  表示必填选项*/
          { required: true, message: '请输入用户名', trigger: 'blur' },   /*blur失去焦点.比如输入框里。校验文本框是否为空*/
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ],
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
            /* min : 最小长度  max: 最大长度 */
          { min: 5, max: 5, message: '长度为 5 个字符', trigger: 'blur' }
        ],
      },
      captchaImg: null
    };
  },
  methods: {
    submitForm(formName) {
      /*  上面 的form表单里  通过ref 注册个一个form表单实例，
      这里直接获取表单里的内容来进行校验*/
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$http.post('/login?'+qs.stringify(this.loginForm)).then(res =>{   //这里统一都是直接用  axios.get/post来发起请求的
          // axios.post('/login',this.loginForm).then(res =>{
            const jwt = res.headers['authorization']  //从后端响应数据的请求头中获取 生成的jwt
            this.$store.commit('SET_TOKEN',jwt)   // 同步调用 SET_TOKEN方法，参数为jwt
            this.$router.push('/index')

          })
          // alert('submit!');
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    getCaptcha(){
      this.$http.get('/captcha').then(res =>{
      // axios.get('/captcha').then(res =>{

        console.log("/captcha")
        console.log(res)

        this.loginForm.token = res.data.data.token  //即uuid
        this.captchaImg = res.data.data.captchaImg  //验证码图片
        this.loginForm.code = ''

      })
    }
  },
  created() {
    this.getCaptcha()
  }
}
</script>

<style scoped>
   .el-row{
     background-color: #fafafa;  /*背景颜色*/
     height: 100vh;          /*总体高度*/
     display: flex;         /*横轴居中*/
     align-items: center;   /*主轴居中*/
     text-align: center;   /*文字居中*/
   }

   .el-divider{
     height: 200px;      /*  分割线高度*/
   }

   .captchaImg {
     float: left;         /*左对齐*/
     margin-left: 8px;     /*左边距*/
     border-radius: 4px;  /*图片圆角*/
   }

</style>