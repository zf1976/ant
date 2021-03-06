package com.zf1976.mayi.common.security.support.signature;

import com.zf1976.mayi.common.security.support.signature.enums.SignaturePattern;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 签名认证策略接口
 *
 * @date 2021/1/29
 * @author mac
 * */
public interface SignatureAuthenticationStrategy {

    /**
     * 执行认证
     *
     * @param httpServletRequest request
     * @throws IOException exception
     */
    void onAuthenticate(HttpServletRequest httpServletRequest);

    /**
     * supports?
     *
     * @param signatureModel model
     * @return boolean
     */
    boolean supports(SignaturePattern signatureModel);
}
