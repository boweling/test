package com.markerhub.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

//配置生成验证码
@Configuration
public class KaptchaConfig {

    @Bean
    DefaultKaptcha producer(){
        Properties properties = new Properties();
        //设置验证码的一些属性，即我们要生成的验证码图片的大小规则等
        properties.put("kaptcha.border", "no");           //边框
        properties.put("kaptcha.textproducer.font.color", "blue"); //背景颜色
        properties.put("kaptcha.textproducer.char.space", "4");//每个数之间的空格数
        properties.put("kaptcha.image.height", "40");     //高
        properties.put("kaptcha.image.width", "120");     //宽
        properties.put("kaptcha.textproducer.font.size", "30");  //字体大小

        Config config = new Config(properties);  //根据设置好的属性创建配置对象
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);   //将配置对象设置进验证码信息对象
        return defaultKaptcha;

    }
}
