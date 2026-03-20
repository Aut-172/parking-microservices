package com.demo.parkingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增/更新停车场请求参数
 */
@Data
public class ParkingLotCreateRequest {
    @NotBlank(message = "停车场名称不能为空")
    private String name;

    @NotBlank(message = "地址不能为空")
    private String address;

    @NotNull(message = "总车位数不能为空")
    @PositiveOrZero(message = "总车位数必须大于等于0")
    private Integer totalSpaces;

    @NotNull(message = "剩余车位数不能为空")
    @PositiveOrZero(message = "剩余车位数必须大于等于0")
    private Integer availableSpaces;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(message = "费率不能为空")
    private BigDecimal feeRate;
}