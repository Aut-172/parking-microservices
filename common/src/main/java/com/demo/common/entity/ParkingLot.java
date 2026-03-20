package com.demo.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 停车场实体类，对应表 `parking_lot`
 */
@Data
@TableName("parking_lot")
public class ParkingLot {

    @TableId(type = IdType.AUTO)
    private Long id;                     // 停车场ID

    private String name;                  // 停车场名称
    private String address;                // 地址
    private Integer totalSpaces;           // 总车位数
    private Integer availableSpaces;       // 剩余车位数
    private BigDecimal latitude;           // 纬度
    private BigDecimal longitude;          // 经度
    private BigDecimal feeRate;            // 每小时费率（元）

    @Version
    private Integer version;               // 乐观锁版本号

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;       // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;       // 更新时间
}
