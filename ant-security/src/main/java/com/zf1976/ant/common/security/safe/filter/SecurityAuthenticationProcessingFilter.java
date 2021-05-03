package com.zf1976.ant.common.security.safe.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.power.common.util.Base64Util;
import com.zf1976.ant.common.encrypt.EncryptUtil;
import com.zf1976.ant.common.security.pojo.AuthenticationDetails;
import com.zf1976.ant.common.security.pojo.dto.AuthenticationDto;
import com.zf1976.ant.common.security.safe.exception.BadCredentialsException;
import com.zf1976.ant.common.security.safe.enums.AuthenticationState;
import com.zf1976.ant.common.security.safe.filter.manager.AuthenticationProviderManager;
import com.zf1976.ant.common.security.safe.handler.login.SecurityAuthenticationFailureHandler;
import com.zf1976.ant.common.security.safe.handler.login.SecurityAuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.stream.Collectors;

/**
 * 自定义校验器
 *
 * @author ant
 * Create by Ant on 2020/9/10 6:12 下午
 */
public class SecurityAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public static final Logger LOG = LoggerFactory.getLogger(SecurityAuthenticationProcessingFilter.class);
    private static final String REQUEST_METHOD = "POST";
    private final  ObjectMapper jsonMapper = new ObjectMapper();

    public SecurityAuthenticationProcessingFilter() {
        super("/auth/login");
        this.setAuthenticationManager(new AuthenticationProviderManager());
        this.setAuthenticationSuccessHandler(new SecurityAuthenticationSuccessHandler());
        this.setAuthenticationFailureHandler(new SecurityAuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!REQUEST_METHOD.equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        UsernamePasswordAuthenticationToken authenticationToken = null;
        final AuthenticationDto dto = this.parseAuthentication(request);
        if (checkObjectFieldIsNonNull(dto)) {
            AuthenticationDetails details = new AuthenticationDetails();
            String username = dto.getUsername().trim();
            String uuid = dto.getUuid();
            BeanUtils.copyProperties(dto, details);
            authenticationToken = new UsernamePasswordAuthenticationToken(username, uuid);
            authenticationToken.setDetails(details);
        } else {
            this.handlerCredentialsException(request, response,new BadCredentialsException(AuthenticationState.BAD_CREDENTIALS));
        }

        return this.getAuthenticationManager()
                   .authenticate(authenticationToken);
    }

    private AuthenticationDto parseAuthentication(HttpServletRequest request) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream());
        String base64Content = new BufferedReader(inputStreamReader).lines()
                                                                    .collect(Collectors.joining(System.lineSeparator()));
        byte[] decryptBase64 = Base64Util.decryptBASE64(base64Content);
        String result = EncryptUtil.decryptForAesByCbc(decryptBase64);
        return this.jsonMapper.readValue(result, AuthenticationDto.class);
    }

    /**
     * 校验必要字段是否为空
     *
     * @param obj object
     * @return boolean
     */
    public static boolean checkObjectFieldIsNonNull(Object obj) {

        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (ObjectUtils.isEmpty(field.get(obj))) {
                    return false;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 认证失败处理
     *
     * @param request  request
     * @param response response
     * @param e        异常
     * @throws IOException      向上抛异常
     * @throws ServletException 向上抛异常
     */
    private void handlerCredentialsException(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        this.getFailureHandler()
            .onAuthenticationFailure(request, response, e);
    }
}