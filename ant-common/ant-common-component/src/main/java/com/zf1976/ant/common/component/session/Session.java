package com.zf1976.ant.common.component.session;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author mac
 * Create by Ant on 2020/9/28 23:35
 */
@Data
@Accessors(chain = true)
public class Session implements Serializable {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 岗位
     */
    private String department;
    /**
     * 资源所有者
     */
    private Boolean owner = Boolean.FALSE;
    /**
     * 数据权限
     */
    private List<Long> dataPermission;
    /**
     * 权限
     */
    private List<String> permission;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 操作系统类型
     */
    private String operatingSystemType;
    /**
     * IP
     */
    private String ip;
    /**
     * 地址
     */
    private String ipRegion;
    /**
     * token
     */
    private String token;
    /**
     * 登录时间
     */
    private Date loginTime;
}