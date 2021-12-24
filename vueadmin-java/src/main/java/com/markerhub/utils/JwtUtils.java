package com.markerhub.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
@ConfigurationProperties(prefix = "markerhub.jwt")
public class JwtUtils {

    private long expire;
    private String secret;
    private String header;  //用户给前端头部信息起一个名字，yml中配置

    //生成Jwt
    public String generateToken(String username){

        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + 1000 * expire);

        String Jwt = Jwts.builder()    //使用Jwts工具类中的构建器
                .setHeaderParam("typ", "JWT")  //头部信息，类型为JWT格式
                .setSubject(username)               //用户名（主体）
                .setIssuedAt(nowDate)               //创建时间
                .setExpiration(expireDate)          //过期时间，7天
                .signWith(SignatureAlgorithm.HS512, secret)  //HS512加密算法，secret为密钥
                .compact();

        return Jwt;
    }

    //解析Jwt
    public Claims getClaimsByToken(String jwt){

        try {
            return Jwts.parser()   //使用Jwts工具类中的解析器
                    .setSigningKey(secret)  //使用密钥
                    .parseClaimsJws(jwt)    //要解析的jwt
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    //判断Jwt是否过期
    public boolean IsTokenExpired(Claims claims){
        return claims.getExpiration().before(new Date());//如果过期时间在当前时间之前，true，说明过期了
    }

//
//    private long expire;
//    private String secret;
//    private String header;
//
//    // 生成jwt
//    public String generateToken(String username) {
//
//        Date nowDate = new Date();
//        Date expireDate = new Date(nowDate.getTime() + 1000 * expire);
//
//        return Jwts.builder()
//                .setHeaderParam("typ", "JWT")
//                .setSubject(username)
//                .setIssuedAt(nowDate)
//                .setExpiration(expireDate)// 7天過期
//                .signWith(SignatureAlgorithm.HS512, secret)
//                .compact();
//    }
//
//    // 解析jwt
//    public Claims getClaimByToken(String jwt) {
//        try {
//            return Jwts.parser()
//                    .setSigningKey(secret)
//                    .parseClaimsJws(jwt)
//                    .getBody();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    // jwt是否过期
//    public boolean isTokenExpired(Claims claims) {
//        return claims.getExpiration().before(new Date());
//    }
//

}
