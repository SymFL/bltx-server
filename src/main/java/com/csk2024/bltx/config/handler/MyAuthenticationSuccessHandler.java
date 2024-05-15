package com.csk2024.bltx.config.handler;


import com.csk2024.bltx.constant.Constants;
import com.csk2024.bltx.model.TUser;
import com.csk2024.bltx.result.R;
import com.csk2024.bltx.service.RedisService;
import com.csk2024.bltx.utils.JSONUtils;
import com.csk2024.bltx.utils.JWTUtils;
import com.csk2024.bltx.utils.ResponseUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Spring Security 登录成功处理器
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Resource
    private RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //登录成功，执行该方法，在该方法中返回json给前端，就行了
        TUser tUser = (TUser) authentication.getPrincipal();

        //将 tUser 转换为 JSON 作为 JWT 的负载
        String userJson = JSONUtils.toJSON(tUser);

        //生成 JWT
        String jwt = JWTUtils.createJWT(userJson);

        //将生成的 JWT 存储到 Redis
        redisService.setValue(Constants.REDIS_JWT_KEY + tUser.getId(),jwt);

        String rememberMe = request.getParameter("rememberMe");

        //设置 JWT 在 Redis 中的过期时间
        if(Boolean.parseBoolean(rememberMe)){
            redisService.expire(Constants.REDIS_JWT_KEY + tUser.getId(),Constants.EXPIRE_TIME, TimeUnit.SECONDS);
        }else {
            redisService.expire(Constants.REDIS_JWT_KEY + tUser.getId(),Constants.DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        //登录成功的统一结果
        R result = R.OK(jwt);

        //把R对象转成json
        String resultJSON = JSONUtils.toJSON(result);

        //把R以json返回给前端
        ResponseUtils.write(response, resultJSON);
    }
}
