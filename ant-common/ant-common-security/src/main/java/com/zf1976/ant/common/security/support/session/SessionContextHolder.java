package com.zf1976.ant.common.security.support.session;

import com.power.common.util.StringUtil;
import com.zf1976.ant.common.security.support.session.service.SessionService;
import com.zf1976.ant.common.core.util.RequestUtils;
import com.zf1976.ant.common.security.property.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 身份验证会话管理器
 *
 * @author mac
 * Create by Ant on 2020/10/4 14:11
 */
@Component
public class SessionContextHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionContextHolder.class);
    private static SecurityProperties securityProperties;
    private static SessionService service;

    public SessionContextHolder(SessionService sessionService, SecurityProperties securityProperties) {
        SessionContextHolder.service = sessionService;
        SessionContextHolder.securityProperties = securityProperties;
    }


    /**
     * 存储当前请求会话
     *
     * @param token              token
     * @param session        用户会话details
     */
    public static void storeSession(String token, Session session) {
        service.save(token, session);
    }

    /**
     * 读取当前请求session
     *
     * @date 2021-03-23 12:19:55
     * @return {@link Session}
     */
    public static Session readSession(){
        return readSession(token());
    }

    /**
     * 根据id读取会话
     *
     * @param id session id
     * @return session
     */
    public static Session readSession(Long id) {
        return service.get(id);
    }

    /**
     * 根据token读取会话
     *
     * @param token token
     * @return session
     */
    public static Session readSession(String token) {
        return service.get(token);
    }

    /**
     * 更新session
     * token续期专用/用户信息修改时更新
     * @param id id
     * @param session session
     */
    public static void refreshSession(Long id, Session session) {
        updateRequestInfo(session);
        service.update(id, session);
    }

    /**
     * 更新session
     * token续期专用/用户信息修改时更新
     * @param id id
     * @param session session
     * @param expired expiry time
     */
    public static void refreshSession(Long id, Session session, Long expired) {
        updateRequestInfo(session);
        service.update(id, session, expired);
    }

    /**
     * 更新会话
     *
     * @date 2021-03-23 12:41:44
     * @param session userDetails
     */
    public static void refreshSession(Session session) {
        updateRequestInfo(session);
        service.update(token(), session);
    }

    /**
     * 强制下线
     *
     * @param token token
     */
    public static void removeSession(String token) {
        if (token != null) {
            service.remove(token);
        }
    }

    private static void updateRequestInfo(Session session) {
        HttpServletRequest request = RequestUtils.getRequest();
        session.setIp(RequestUtils.getIpAddress(request))
               .setIpRegion(RequestUtils.getIpRegion(request))
               .setBrowser(RequestUtils.getBrowser(request))
               .setOperatingSystemType(RequestUtils.getOpsSystemType(request));
    }

    /**
     * 强制下线
     *
     * @param id id
     */
    public static void removeSession(Long id) {
        service.remove(id);
    }

    /**
     * 强制当前用户下限
     */
    public static void removeSession(){
        removeSession(token());
    }

    /**
     * 查询session过期时间
     *
     * @param id token
     * @return timestamp
     */
    public static Long getExpiredTime(Long id) {
        return service.getExpired(id);
    }

    /**
     * 获取session过期时间
     *
     * @return expired timestamp
     */
    public static Long getExpiredTime() {
        var session = readSession();
        return service.getExpired(session.getId());
    }

    /**
     * 获取当前会话id
     *
     * @return session
     */
    public static Long getSessionId(){
        return service.getId(token());
    }

    /**
     * 获取当前会话用户名
     *
     * @return username
     */
    public static String username() {
        return readSession().getUsername();
    }

    /**
     * owner
     *
     * @return boolean
     */
    public static boolean isOwner(){
        return readSession().getOwner();
    }

    /**
     * owner
     *
     * @param username 用户名
     * @return boolean
     */
    public static boolean isOwner(String username){
        return ObjectUtils.nullSafeEquals(securityProperties.getOwner(), username);
    }

    /**
     * 获取token
     *
     * @return token
     */
    private static String token(){
        var header = RequestUtils.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            throw new UnsupportedOperationException(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
        return extractToken(header);
    }

    /**
     * 提取token
     *
     * @param header request header
     * @return token
     */
    private static String extractToken(String header) {
        return header.replace("Bearer ", StringUtil.ENMPTY);
    }

}