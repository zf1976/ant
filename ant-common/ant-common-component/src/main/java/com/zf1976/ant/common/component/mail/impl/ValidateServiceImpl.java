package com.zf1976.ant.common.component.mail.impl;

import com.power.common.util.RandomUtil;
import com.power.common.util.ValidateUtil;
import com.zf1976.ant.common.component.mail.MailSenderProvider;
import com.zf1976.ant.common.component.mail.ValidateEmailService;
import com.zf1976.ant.common.component.mail.ValidateMobileService;
import com.zf1976.ant.common.component.mail.pojo.ToolEmailConfig;
import com.zf1976.ant.common.core.foundation.exception.BusinessException;
import com.zf1976.ant.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.ant.common.core.util.RedisUtil;
import com.zf1976.ant.common.component.property.EmailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证服务
 *
 * @author mac
 */
@Service("ValidateServiceImpl")
public class ValidateServiceImpl implements ValidateEmailService, ValidateMobileService {

    private final static String EMAIL_KEY_PREFIX = "email:";
    private final Logger log = LoggerFactory.getLogger("[ValidateService]");
    private final EmailProperties properties;
    private final TemplateEngine engine;
    private final static String MOBILE_KEY_PREFIX = "mobile:";
    private static ValidateServiceImpl validateService;

    public ValidateServiceImpl(EmailProperties properties, TemplateEngine engine) {
        this.properties = properties;
        this.engine = engine;
        validateService = this;
        Assert.notNull(validateService, "init validateService cannot been null!");
    }

    /**
     * 获取单实例
     *
     * @return {@link ValidateServiceImpl}
     */
    public static ValidateServiceImpl getInstance() {
        Assert.notNull(validateService, "init validateService is null!");
        return validateService;
    }

    @Override
    public Void sendVerifyCode(String key) {
        if (StringUtils.isEmpty(key) || !ValidateUtil.isEmail(key)) {
            throw new BusinessException(BusinessMsgState.EMAIL_LOW);
        }
        final String validateCode = RandomUtil.randomString(properties.getLength())
                                              .toUpperCase();
        final Map<ToolEmailConfig, JavaMailSender> mailSenderMap = MailSenderProvider.getMailSenderMap();
        Context context = new Context();
        context.setVariable(properties.getName(), validateCode);
        String process = engine.process("email", context);
        // 轮询 配置失效 继续下一个
        boolean isSend = mailSenderMap.keySet()
                                      .stream()
                                      .anyMatch(config -> {
                                          JavaMailSender mailSender = mailSenderMap.get(config);
                                          MimeMessage mimeMessage = mailSender.createMimeMessage();
                                          MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
                                          try {
                                              helper.setFrom(config.getFromUser());
                                              helper.setTo(key);
                                              helper.setText(process, true);
                                              helper.setSubject(properties.getSubject());
                                              helper.setValidateAddresses(true);
                mailSender.send(mimeMessage);
            } catch (MessagingException e) {
                                              log.error("send error:", e);
                return false;
            }
            return true;
        });
        if (!isSend) {
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
        // 保存验证码
        RedisUtil.set(properties.getKeyPrefix(), key, validateCode, properties.getExpired(), TimeUnit.MILLISECONDS);
        return null;

    }


    @Override
    public Boolean validateVerifyCode(String key, String code) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(code)) {
            throw new BusinessException(BusinessMsgState.CODE_NOT_FOUNT);
        }
        final String rawCode = RedisUtil.get(properties.getKeyPrefix(), key);
        if (!StringUtils.isEmpty(rawCode)) {
            return ObjectUtils.nullSafeEquals(rawCode, code);
        }
        return false;
    }

    @Override
    public void clearVerifyCode(String key) {
        RedisUtil.delete(properties.getKeyPrefix(), key);
    }

    @Override
    public Void sendEmailVerifyCode(String key) {
        return this.sendVerifyCode(EMAIL_KEY_PREFIX.concat(key));
    }

    @Override
    public Boolean validateEmailVerifyCode(String key, String code) {
        return this.validateVerifyCode(EMAIL_KEY_PREFIX.concat(key), code);
    }

    @Override
    public void clearEmailVerifyCode(String key) {
        this.clearVerifyCode(EMAIL_KEY_PREFIX.concat(key));
    }

    @Override
    public Void sendMobileVerifyCode(String mobile) {
        return this.sendVerifyCode(MOBILE_KEY_PREFIX.concat(mobile));
    }

    @Override
    public boolean validateMobileVerifyCode(String mobile, String code) {
        return this.validateVerifyCode(MOBILE_KEY_PREFIX.concat(mobile), code);
    }

    @Override
    public void clearMobileVerifyCode(String mobile) {
        this.clearVerifyCode(MOBILE_KEY_PREFIX.concat(mobile));
    }
}
