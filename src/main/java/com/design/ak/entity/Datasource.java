package com.design.ak.entity;

import java.util.Date;
import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;


/**
 * 数据源表(Datasource)实体类
 *
 * @author ak.design 337547038
 * @since 2023-12-05 16:16:55
 */
@Data
public class Datasource implements Serializable {
    @Serial
    private static final long serialVersionUID = -55938615676147511L;

    private Integer id;
    /**
     * 数据源名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;
    /**
     * 表名
     */
    @NotBlank(message = "表名不能为空")
    private String tableName;
    /**
     * 分类
     */
    private Integer category;

    private Integer status;

    private Date creatDate;

    private Date updateDate;

    private Integer creatUserId;
    /**
     * 最后修改人
     */
    private Integer updateUserId;
    /**
     * 表注释
     */
    private String remark;
    /**
     * 表结构数据
     */
    @NotBlank(message = "表结构数据不能为空")
    private String tableData;


}
