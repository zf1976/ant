package com.zf1976.mayi.upms.biz.pojo.dto.menu;

import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author Windows
 */
public class MenuDTO {

    @Null(groups = ValidationInsertGroup.class)
    @NotNull(groups = ValidationUpdateGroup.class)
    protected Long id;

    /**
     * 上级菜单ID
     */
    private Long pid;

    /**
     * 菜单类型
     */
    @NotNull
    protected Integer type;

    /**
     * 菜单标题
     */
    @NotBlank
    protected String title;

    /**
     * 排序
     */
    @NotNull
    protected Integer menuSort;

    /**
     * 组件路径
     */
    private String componentPath;

    /**
     * 组件名
     */
    private String componentName;

    /**
     * 缓存
     */
    private Boolean cache;

    /**
     * 权限
     */
    private String permission;

    /**
     * 图标
     */
    private String icon;

    /**
     * 路由地址
     */
    private String routePath;

    /**
     * 隐藏
     */
    private Boolean hidden;

    /**
     * 是否外链
     */
    private Boolean iframe;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getMenuSort() {
        return menuSort;
    }

    public void setMenuSort(Integer menuSort) {
        this.menuSort = menuSort;
    }

    public String getComponentPath() {
        return componentPath;
    }

    public void setComponentPath(String componentPath) {
        this.componentPath = componentPath;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getIframe() {
        return iframe;
    }

    public void setIframe(Boolean iframe) {
        this.iframe = iframe;
    }

    @Override
    public String toString() {
        return "MenuDTO{" +
                "id=" + id +
                ", pid=" + pid +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", menuSort=" + menuSort +
                ", componentPath='" + componentPath + '\'' +
                ", componentName='" + componentName + '\'' +
                ", cache=" + cache +
                ", permission='" + permission + '\'' +
                ", icon='" + icon + '\'' +
                ", routePath='" + routePath + '\'' +
                ", hidden=" + hidden +
                ", iframe=" + iframe +
                '}';
    }
}
