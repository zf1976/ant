package com.zf1976.mayi.upms.biz.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * @date 2021/1/20
 **/
@Component
public class PageUtil {

    @Autowired
    private RedisTemplate<Object, Object> template;
    /**
     * 存放单个hash缓存
     * @param key 键
     * @param hKey 键
     * @param value 值
     * @return /
     */
    public boolean hPut(String key, String hKey, Object value) {
        try {
            template.opsForHash().put(key, hKey, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 分页存取数据
     * @param key  hash存取的key
     * @param hKey hash存取的hKey
     * @param score 指定字段排序
     * @param value value
     * @return /
     */
    public Boolean setPage(String key, String hKey, double score, String value){
        Boolean result = false;
        try {
            result = template.opsForZSet().add(key + ":page", hKey, score);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置辅助分页的过期时间
        template.expire(key+":page",1800000 , TimeUnit.MILLISECONDS);
        return result;
    }

    /**
     * 分页取出 hash中hKey值
     *
     * @param key key
     * @param offset 位置
     * @param count 总数
     * @return /
     */
    public Set<Object> getPage(String key, int offset, int count){
        Set<Object> result = null;
        try {
            result = template.opsForZSet().rangeByScore(key+":page", 1, 100000, (long) (offset - 1) *count, count);
            //1 100000代表score的排序氛围值，即从1-100000的范围
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 计算key值对应的数量
     *
     * @param key key
     * @return /
     */
    public Integer getSize(String key){
        Integer num = 0;
        try {
            Long size = template.opsForZSet().zCard(key+":page");
            assert size != null;
            return size.intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }
}
