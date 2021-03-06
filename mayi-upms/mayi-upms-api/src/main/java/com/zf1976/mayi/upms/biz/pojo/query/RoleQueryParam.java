package com.zf1976.mayi.upms.biz.pojo.query;

import com.zf1976.mayi.upms.biz.pojo.query.annotation.Param;
import com.zf1976.mayi.upms.biz.pojo.query.enmus.Type;

import java.util.Date;
import java.util.List;

/**
 * @author mac
 * @date 2020/11/21
 **/
public class RoleQueryParam extends AbstractQueryParam {

    @Param(type = Type.LIKE, fields = {"name", "description"})
    private String blurry;

    @Param(type = Type.BETWEEN)
    private List<Date> createTime;

    public String getBlurry() {
        return blurry;
    }

    public void setBlurry(String blurry) {
        this.blurry = blurry;
    }

    public List<Date> getCreateTime() {
        return createTime;
    }

    public void setCreateTime(List<Date> createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "RoleQueryParam{" +
                "blurry='" + blurry + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
