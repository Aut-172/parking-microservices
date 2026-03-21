package com.demo.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IncrementRequest {
    private Long lotId;
    // getter/setter
}