import axios from 'axios';
import router from "./router";
import Element from "element-ui";


//自定义axios里的拦截设置
//默认请求前缀
axios.defaults.baseURL="http://localhost:8081";
//创建axios实例
const  request=axios.create({
    timeout: 5000,
    headers: {
        'Content-Type' : "application/json; charset=utf-8"
    }
})

//axios请求的前置、后置拦截处理
request.interceptors.request.use(config =>{
    config.headers['Authorization'] = localStorage.getItem("token");
    return config;
})



request.interceptors.response.use(
    response => {
        console.log("response"+response);
        let res = response.data;
        if(res.code === 200){
            return response;
        }else {
            Element.Message.error(!res.msg ? '系统异常' : res.msg)   //有返回msg就弹出msg异常，没有就默认是系统异常
            return Promise.reject(response.data.msg)
        }
    },
    error => {
        console.log(error);
        if(error.response.data){
            error.message = error.response.data.msg;
        }
        if (error.response.status === 401) {//没有权限
            router.push("/login");
        }

        Element.Message.error(error.message, {duration: 3000})
        return Promise.reject(error)
    }
)

export  default request

