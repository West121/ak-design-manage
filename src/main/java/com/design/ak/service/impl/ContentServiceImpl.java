package com.design.ak.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.design.ak.config.CustomException;
import com.design.ak.dao.DatasourceDao;
import com.design.ak.utils.Utils;
import com.design.ak.dao.ContentDao;
import com.design.ak.entity.Content;
import com.design.ak.service.ContentService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.*;

/**
 * 通用内容
 *
 * @author ak.design 337547038
 * @since 2023-12-11 13:43:14
 */
@Service("contentService")
public class ContentServiceImpl implements ContentService {
    @Resource
    private ContentDao contentDao;
    @Resource
    private DatasourceDao datasourceDao;

    /**
     * 通过ID查询单条数据
     *
     * @param formId 表单id
     * @param id     主键
     * @return 实例对象
     */
    @Override
    public Map<String, Object> queryById(Integer formId, Integer id) {
        Map<String, String> dataSource = getTableNameByFormId(String.valueOf(formId));
        String tableName = dataSource.get("tableName");
        System.out.println(dataSource);
        System.out.println("dataSource");
        if (tableName == null || tableName.isEmpty()) {
            throw new CustomException("当前列表未配置有表单数据源");
        }
        return this.contentDao.queryById(tableName, id);
    }

    /**
     * 分页查询
     *
     * @param pages 筛选条件分页对象
     * @return 查询结果
     */
    @Override
    public Map<String, Object> queryByPage(Map<String, Object> pages) {
        Map<String, Object> map = Utils.pagination(pages);//处理分页信息
        JSONObject extend = JSON.parseObject(JSON.toJSONString(map.get("extend")));
        JSONObject query = JSON.parseObject(JSON.toJSONString(map.get("query")));
        Integer formId = extend.getInteger("formId");
        //1.先找出对应的数据库表名
        if (formId == null) {
            throw new CustomException("表单id不能为空");
        }
        Map<String, String> dataSource = getTableNameByFormId(String.valueOf(formId));
        String tableName = dataSource.get("tableName");

        // 从数据源里提取需要模糊搜索匹配的值
        JSONArray searchColumns = new JSONArray();
        if (!query.isEmpty()) {
            String tableData = dataSource.get("tableData");
            JSONArray jsonArray = JSON.parseArray(tableData);
            jsonArray.forEach(item -> {
                JSONObject obj = JSON.parseObject(item.toString());
                if (obj.getBoolean("search")) {
                    searchColumns.add(obj.getString("name"));
                }
            });
        }
        //查询总条数
        List<Map<String, String>> queryList = convertMapToList(query, searchColumns);
        long total = this.contentDao.count(tableName, queryList);
        //将表名添加到extend传过去
        extend.put("tableName", tableName);

        List<Map<String, Object>> list = contentDao.queryAllByLimit(queryList, extend);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);
        return response;
    }

    /**
     * 新增数据
     *
     * @param content 实例对象
     * @return 实例对象
     */
    @Override
    public Integer insert(Map<String, String> content) {
        String formId = content.get("formId");
        Map<String, String> dataSource = getTableNameByFormId(formId);
        String tableName = dataSource.get("tableName");
        String tableData = dataSource.get("tableData");
        if (tableName == null || tableName.isEmpty()) {
            throw new CustomException("当前列表未配置有表单数据源");
        }
        List<Map<String, String>> list = getFiledList(tableData, content);
        // 这里添加一个实例，用于返回新插入的id
        Content ct = new Content();
        this.contentDao.insert(tableName, list, ct);
        return ct.getId();
    }

    /**
     * 修改数据
     *
     * @param content 实例对象
     * @return 影响的行数
     */
    @Override
    public Integer updateById(Map<String, String> content) {
        String formId = content.get("formId");
        Map<String, String> dataSource = getTableNameByFormId(formId);
        String tableName = dataSource.get("tableName");
        String tableData = dataSource.get("tableData");
        List<Map<String, String>> list = getFiledList(tableData, content);
        System.out.println(list);
        return this.contentDao.updateById(tableName, list, content.get("id"));
    }

    /**
     * 通过主键删除数据
     *
     * @param id     主键
     * @param formId 表单id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String formId, String[] id) {
        Map<String, String> dataSource = getTableNameByFormId(formId);
        String tableName = dataSource.get("tableName");
        return this.contentDao.deleteById(tableName, id) > 0;
    }

    /**
     * 根据表单id返回数据源信息
     *
     * @param formId 表单id
     * @return 当前数据源信息
     */
    private Map<String, String> getTableNameByFormId(String formId) {
        Map<String, String> dataSource = this.datasourceDao.getTableNameByFormId(Integer.valueOf(formId));
        String tableName = dataSource.get("tableName");
        if (tableName == null || tableName.isEmpty()) {
            throw new CustomException("当前列表未配置有表单数据源");
        }
        return dataSource;
    }

    /**
     * 将查询参数转换以适应contentDao.xml使用foreach拼接查询条件
     * {name:"name1",id:1}转换为
     * [{key:"name",value:"name1"},{key:"id",value:1}]
     *
     * @param map           查询参数
     * @param searchColumns 支持模糊查询字段
     * @return 转换后的数据
     */
    private static List<Map<String, String>> convertMapToList(Map<String, Object> map, JSONArray searchColumns) {
        List<Map<String, String>> list = new ArrayList<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Map<String, String> item = new HashMap<>();
            String keyName = entry.getKey();
            item.put("key", keyName);
            item.put("value", entry.getValue().toString());
            if (searchColumns.contains(keyName)) {
                item.put("search", "1"); // 添加个标识即可
            }
            list.add(item);
        }
        return list;
    }

    /**
     * 从设计的表单数据里提取所有录入字段，组装成添加和编辑所需的数据
     *
     * @param tableData 设计生成的数据源
     * @param content   表单提交的内容
     * @return 组装成添加和编辑所需的数据
     */
    private static List<Map<String, String>> getFiledList(String tableData, Map<String, String> content) {
        //根据创建数据源时的配置提取字段
        JSONArray jsonArray = JSON.parseArray(tableData);
        List<Map<String, String>> list = new ArrayList<>();
        jsonArray.forEach(item -> {
            JSONObject obj = JSON.parseObject(item.toString());
            Map<String, String> map = new HashMap<>();
            String name = obj.getString("name");
            String contentName = content.get(name);
            if (contentName != null) {
                map.put("key", name); //配置的字段名
                map.put("value", contentName); // 表单提交对应的值
                list.add(map);
            }
        });
        return list;
    }
}
