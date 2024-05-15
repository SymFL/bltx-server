package com.csk2024.bltx.config.filter;

import com.csk2024.bltx.constant.Constants;
import com.csk2024.bltx.model.TUser;
import com.csk2024.bltx.result.CodeEnum;
import com.csk2024.bltx.result.R;
import com.csk2024.bltx.service.RedisService;
import com.csk2024.bltx.utils.JSONUtils;
import com.csk2024.bltx.utils.JWTUtils;
import com.csk2024.bltx.utils.ResponseUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class TokenVerifyFilter extends OncePerRequestFilter {
    @Resource
    private RedisService redisService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(Constants.LOGIN_URI)) { //如果是来自登录页面的请求，则不校验 token
            filterChain.doFilter(request, response);
        } else {
            // 获取报文头的 token
            String token = request.getHeader("Authorization");
            // token 不存在
            if (!StringUtils.hasText(token)) {
                R result = R.FAIL(CodeEnum.TOKEN_IS_EMPTY);
                String resultJson = JSONUtils.toJSON(result);
                ResponseUtils.write(response, resultJson);
                return;
            }
            // token 不正确
            if (!JWTUtils.verifyJWT(token)) {
                R result = R.FAIL(CodeEnum.TOKEN_IS_ERROR);
                String resultJson = JSONUtils.toJSON(result);
                ResponseUtils.write(response, resultJson);
                return;
            }

            // 将 JWT 中的 user 解析出来
            TUser tUser = JWTUtils.parseUserFromJWT(token);
            // 取出 redis 中存有的 token
            String redisToken = (String) redisService.getValue(Constants.REDIS_JWT_KEY + tUser.getId());

            // redis 中没有对应的 token 则说明 token 已经过期
            if (!StringUtils.hasText(redisToken)) {
                R result = R.FAIL(CodeEnum.TOKEN_IS_EXPIRED);
                String resultJson = JSONUtils.toJSON(result);
                ResponseUtils.write(response, resultJson);
                return;
            }

            // redis 中的 token 与前端的 token 不一致
            if (!redisToken.equals(token)) {
                R result = R.FAIL(CodeEnum.TOKEN_IS_NONE_MATCH);
                String resultJson = JSONUtils.toJSON(result);
                ResponseUtils.write(response, resultJson);
                return;
            }

            //jwt验证通过了，在spring security的上下文环境中要设置一下，设置当前这个人是登录过的，后续不要再拦截
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(tUser, tUser.getLoginPwd(), tUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 调用 spring boot 自带的线程池进行异步处理，实现 token 续期
            threadPoolTaskExecutor.execute(() ->{
                String rememberMe = request.getHeader("rememberMe");
                if(Boolean.parseBoolean(rememberMe)){
                    redisService.expire(Constants.REDIS_JWT_KEY + tUser.getId(),Constants.EXPIRE_TIME, TimeUnit.SECONDS);
                }else{
                    redisService.expire(Constants.REDIS_JWT_KEY + tUser.getId(),Constants.DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
                }
            });

            // 验证完成，放行
            filterChain.doFilter(request, response);
        }

    }
}
