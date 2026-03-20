
package com.demo.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类，对应表 `parking_order`
 */
@Data
@TableName("parking_order")
public class ParkingOrder {

    @TableId(type = IdType.AUTO)
    private Long id;                 // 订单ID

    private Long userId;              // 用户ID
    private Long lotId;               // 停车场ID
    private String plateNumber;       // 车牌号

    private LocalDateTime enterTime;  // 入场时间
    private LocalDateTime exitTime;   // 离场时间

    private BigDecimal amount;        // 应付金额

    private Integer status;           // 订单状态：0-进行中，1-已完成，2-已取消

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间
}