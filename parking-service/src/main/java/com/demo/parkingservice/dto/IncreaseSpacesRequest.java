package com.demo.parkingservice.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 增加空余车位请求参数
 */
@Data
public class IncreaseSpacesRequest {
    @NotNull(message = "增加数量不能为空")
    @Positive(message = "增加数量必须为正数")
    private Integer increment;
}