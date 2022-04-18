package cn.smallyoung.commonsecurityspringbootstarter.filter;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.commonsecurityspringbootstarter.util.AntPathMatcherUtil;
import cn.smallyoung.commonsecurityspringbootstarter.util.JwtConfig;
import cn.smallyoung.commonsecurityspringbootstarter.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JWT登录授权过滤器
 *
 * @author smallyoung
 * @data 2020/10/27
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private JwtConfig jwtConfig;
    @Value("${spring.application.name}")
    private String applicationName;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public final void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = request.getHeader(jwtConfig.getTokenHead());
        log.info("post-practice获取token：{}", token);
        if (StrUtil.isBlank(token) || "null".equals(token)) {
            chain.doFilter(request, response);
            return;
        }
        Claims claims = JwtTokenUtil.parseToken(jwtConfig.getPublicKey(), token);
        if (claims == null) {
            chain.doFilter(request, response);
            return;
        }
        //判断token是否过期，如果未过期则重新签发token
        Date expiredDate = claims.getExpiration();
        if (expiredDate == null || expiredDate.after(new Date())) {
            String username = claims.get("username", String.class);
            String authorities = claims.get("authorities", String.class);
            List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
            if (StrUtil.isNotBlank(authorities)) {
                grantedAuthorityList = Arrays.stream(authorities.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            }
            //根据路径鉴权
            String uri = request.getRequestURI();
            if (jwtConfig.getAuthcUrl().stream().anyMatch(url -> AntPathMatcherUtil.matches(url, uri))
                    || grantedAuthorityList.stream().map(GrantedAuthority::getAuthority).anyMatch(pattern -> AntPathMatcherUtil.matches(pattern, uri))) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, token, grantedAuthorityList);
                //是否记住密码
                boolean remember = Boolean.parseBoolean(String.valueOf(claims.get("remember")));
                String newToken = JwtTokenUtil.generateToken(username, jwtConfig.getPrivateKey(), applicationName, applicationName, jwtConfig.getExpiration(),
                        Dict.create().set("authorities", authorities), remember);
                if (newToken != null) {
                    if (jwtConfig.getSso()) {
                        if (remember) {
                            redisTemplate.opsForValue().set(jwtConfig.getRedisKey() + applicationName + ":" + username, newToken);
                        } else {
                            redisTemplate.opsForValue().set(jwtConfig.getRedisKey() + applicationName + ":" + username, newToken, jwtConfig.getExpiration(), TimeUnit.MINUTES);
                        }
                    }
                    response.setHeader(jwtConfig.getTokenHead(), newToken);
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
