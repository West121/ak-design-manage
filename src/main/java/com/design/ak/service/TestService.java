package com.design.ak.service;

import com.design.ak.entity.Test;

import java.util.Map;
/**
 * (Test)表服务接口
 *
 * @author ak.design 337547038
 * @since 2023-12-08 10:42:11
 */
public interface TestService {

    /**
     * 通过ID查询单条数据
     *
     * @param query 主键和请求的列
     * @return 实例对象
     */
    Map<String,Object> queryById(Map<String,String> query);
    
    /**
     * 分页查询
     * @param pages 筛选条件 分页对象
     * @return 查询结果
     */
    Map<String,Object> queryByPage(Map<String,Object> pages);
    /**
     * 新增数据
     *
     * @param test 实例对象
     * @return 实例对象
     */
    Test insert(Test test);

    /**
     * 修改数据
     *
     * @param test 实例对象
     * @return 实例对象
     */
    Integer updateById(Test test);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(String[] id);

}
