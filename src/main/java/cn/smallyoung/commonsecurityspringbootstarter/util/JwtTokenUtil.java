package cn.smallyoung.commonsecurityspringbootstarter.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtToken 工具类
 * @author SmallYoung
 * @date 2021/10/21
 */

@Slf4j
public class JwtTokenUtil {

    /**
     * 根据负责生成JWT的token
     * @param username  用户名称
     * @param privateKey 私钥
     * @param issuer 发行者
     * @param subject 签发平台
     * @param offset 过期时长
     * @param claims 其他信息
     * @param remember 记住密码
     * @return token
     */
    public static String generateToken(String username, String privateKey, String subject, String issuer,
                                       int offset, Map<String, Object> claims, boolean remember) {
        if(claims == null){
            claims = new HashMap<>();
        }
        claims.put("username", username);
        claims.put("remember", remember);
        try {
            byte[] keyBytes = Base64.getMimeDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey0 = keyFactory.generatePrivate(keySpec);
            JwtBuilder jwtBuilder = Jwts.builder()
                    .setClaims(claims)
                    .setIssuer(issuer)
                    .setSubject(subject)
                    .setIssuedAt(DateTime.now())
                    .signWith(SignatureAlgorithm.RS256, privateKey0);
            if (!remember) {
                jwtBuilder.setExpiration(DateUtil.offsetMinute(DateTime.now(), offset));
            }
            return jwtBuilder.compact();
        }catch (Exception e){
            log.error("生成token出错:", e);
            return null;
        }
    }

    public static Claims parseToken(String publicKey, String token){
        //密钥错误会抛出SignatureException，说明该token是伪造的
        //如果过期时间exp字段已经早于当前时间，会抛出ExpiredJwtException，说明token已经失效
        try {
            byte[] keyBytes = Base64.getMimeDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey0 = keyFactory.generatePublic(keySpec);
            return Jwts.parser().setSigningKey(publicKey0).parseClaimsJws(token).getBody();
        }catch (Exception e){
            log.error("token错误:{}", e.getMessage());
            return null;
        }
    }
}
