package com.markerhub.controller;

import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Producer;
import com.markerhub.common.lang.Const;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@RestController
@Slf4j
public class AuthController extends BaseController{

    @Autowired
    private Producer producer;

    /**
     * 图片验证码
     * @return
     * @throws IOException
     */
    @GetMapping("/captcha")
    public Result captcha() throws IOException {

        String key = UUID.randomUUID().toString();  //通过UUID工具类生成一个key值，用来传给前端
        String code = producer.createText();

        //测试
//        key = "aaaaa";
//        code = "11111";

        BufferedImage image = producer.createImage(code);//将code转为BufferedImage格式
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();//创建Byte字节输出流对象
        ImageIO.write(image,"jpg",outputStream);//将image写入输出流outputStream，以jpg格式输出


        BASE64Encoder encoder = new BASE64Encoder();//创建base64编码器对象
        String str = "data:image/jpeg;base64,";//设置  等下要返回给前端的字符串的 前缀

        String base64Image = str + encoder.encode(outputStream.toByteArray());//outputStream经过base64编码再拼接前缀

        //将key(uuid)，code(验证码)，有效时间time存入redis中
        //CAPTCHA_KEY是这个 set缓存的名称
        //方便后续过滤器去redis中获取验证码来匹配用户输入的验证码
        redisUtil.hset(Const.CAPTCHA_KEY,key,code,120);

        //将key与验证码通过result格式封装后返回给前端
        return Result.succ(
                MapUtil.builder()
                        .put("token",key)
                        .put("captchaImg",base64Image)
                        .build()
        );
    }


    /**
     * 获取用户信息接口
     * @param principal
     * @return
     */
    @GetMapping("/sys/userInfo")
    private Result userInfo(Principal principal){

        SysUser sysUser = sysUserService.getByUsername(principal.getName());
        return Result.succ(MapUtil.builder()  //通过MapUtil来构建
                .put("id",sysUser.getId())
                .put("username",sysUser.getUsername())
                .put("avatar",sysUser.getAvatar())
                .put("created",sysUser.getCreated())
                .map());//最后返回map形式的数据

    }


}
