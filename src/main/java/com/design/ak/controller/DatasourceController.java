package com.design.ak.controller;

import com.design.ak.entity.Datasource;
import com.design.ak.entity.Test;
import com.design.ak.service.DatasourceService;
import com.design.ak.utils.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.Resource;

import java.util.Date;
import java.util.Map;


/**
 * 数据源表(Datasource)表控制层
 *
 * @author ak.design 337547038
 * @since 2023-12-05 16:16:55
 */
@Tag(name = "Datasource相关")
@RestController
@RequestMapping("datasource")
public class DatasourceController {
    /**
     * 服务对象
     */
    @Resource
    private DatasourceService datasourceService;

    /**
     * 分页查询
     * 前端传参:
     * {
     *     query:{},//查询条件
     *     pageInfo:{
     *         pageNum:1,//当前第几页
     *         pageSize:20,//每页多少条记录，默认20。小于0返回全部
     *         order:"id desc"//排序
     *     }
     * }
     * @param pages 筛选条件分页对象
     * @return 查询结果
     */
    @Operation(summary ="分页列表")
    @Parameters({
            @Parameter(name = "pageInfo.pageNum",description = "当前第几页"),
            @Parameter(name = "pageInfo.pageSize",description = "每页显示多少条"),
            @Parameter(name = "query",description = "查询条件")
    })
    @PostMapping("list")
    public ResponseEntity<Map<String, Object>> queryByPage(@RequestBody Map<String, Object> pages) {
        return ResponseEntity.ok(this.datasourceService.queryByPage(pages));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary ="根据id查询数据")
    @PostMapping("get")
    public ResponseEntity<Datasource> queryById(@RequestBody Map<String, Integer> id) {
        return ResponseEntity.ok(this.datasourceService.queryById(id.get("id")));
    }

    /**
     * 新增数据
     *
     * @param datasource 实体
     * @return 新增结果Id
     */
    @Operation(summary ="新增数据")
    @PostMapping("creat")
    public ResponseEntity<Integer> add(@RequestBody Datasource datasource) {
        datasource.setCreatUserId(Utils.getCurrentUserId());
        datasource.setCreatDate(new Date());
        datasource.setUpdateDate(new Date());
        Datasource result = datasourceService.insert(datasource);
        return ResponseEntity.ok(result.getId());
    }

    /**
     * 编辑数据
     *
     * @param datasource 实体
     * @return 影响行数
     */
    @Operation(summary ="编辑数据")
    @PostMapping("edit")
    public ResponseEntity<Integer> edit(@RequestBody Datasource datasource) {
        datasource.setUpdateUserId(Utils.getCurrentUserId());
        datasource.setUpdateDate(new Date());
        return ResponseEntity.ok(this.datasourceService.updateById(datasource));
    }

    /**
     * 删除数据，删除多个时使用豆号分隔
     *
     * @param ids 主键
     * @return 删除是否成功
     */
    @Operation(summary ="根据id删除")
    @Parameter(name = "id",description = "多个id时使用豆号隔开",required = true)
    @PostMapping("delete")
    public ResponseEntity<Boolean> deleteById(@RequestBody Map<String,Object> ids) {
        String string = ids.get("id").toString();
        String[] idList = string.split(",");
        return ResponseEntity.ok(this.datasourceService.deleteById(idList));
    }

}
