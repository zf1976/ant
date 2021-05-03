package com.zf1976.ant.common.component.load.annotation;

import java.lang.annotation.*;

/**
 * @author WINDOWS
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CaffeinePut {

    /**
     * 缓存命名空间
     *
     * @return namespace
     */
    String namespace();

    /**
     * key
     *
     * @return key
     */
    String key();

    /**
     * 开启动态key，根据用户分组
     * @return /
     */
    boolean dynamicsKey() default false;

    /**
     * 超时时间 - 单位:秒
     *
     * @return time
     */
    long expired() default -1;
}