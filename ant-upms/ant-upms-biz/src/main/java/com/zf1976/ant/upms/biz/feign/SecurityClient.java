package com.zf1976.ant.upms.biz.feign;

import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.common.security.pojo.Details;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author ant
 * Create by Ant on 2021/3/28 5:03 PM
 */
@FeignClient(name = "ant-auth")
public interface SecurityClient {

    /**
     * 远程调用服务登出处理
     *
     * @date 2021-05-07 12:03:42
     * @param token 令牌
     * @return {@link DataResult}
     */
    @PostMapping("/oauth/logout")
    @SuppressWarnings("rawtypes")
    DataResult logout(@RequestHeader(value = "Authorization") String token);

    /**
     * 根据令牌获取用户信息
     *
     * @date 2021-05-07 12:03:18
     * @param token 令牌
     * @return {@link DataResult<Details>}
     */
    @PostMapping("/oauth/info")
    DataResult<Details> getUserDetails(@RequestHeader(value = "Authorization") String token);

    /**
     * 获取当前用户信息
     *
     * @date 2021-05-07 12:04:40
     * @return {@link DataResult<Details>}
     */
    @PostMapping("/oauth/info")
    DataResult<Details> getUserDetails();
}