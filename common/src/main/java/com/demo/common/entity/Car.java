package com.demo.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 车牌实体类，对应表 `car`
 */
@Data
@TableName("car")
public class Car {

    @TableId(type = IdType.AUTO)
    private Long id;                 // 主键ID

    private Long userId;              // 用户ID

    private String plateNumber;       // 车牌号

    @TableField("is_default")
    private Integer isDefault;        // 是否默认车牌：0-否，1-是

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
}
