package com.demo.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 统一 API 响应结果封装
 *
 * @param <T> 响应数据类型
 */
@Data
@Accessors(chain = true) // 开启链式调用
public class Response<T> {

    /** 状态码，200 表示成功，其他表示失败 */
    private int code;

    /** 提示信息 */
    private String msg;

    /** 响应数据 */
    private T data;

    // ========== 静态工厂方法 ==========

    /**
     * 成功响应（无数据）
     */
    public static <T> Response<T> success() {
        return new Response<T>()
                .setCode(200)
                .setMsg("操作成功");
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Response<T> success(T data) {
        return new Response<T>()
                .setCode(200)
                .setMsg("操作成功")
                .setData(data);
    }

    /**
     * 成功响应（自定义消息和数据）
     */
    public static <T> Response<T> success(String msg, T data) {
        return new Response<T>()
                .setCode(200)
                .setMsg(msg)
                .setData(data);
    }

    /**
     * 失败响应（默认错误码 500）
     */
    public static <T> Response<T> error(String msg) {
        return new Response<T>()
                .setCode(500)
                .setMsg(msg);
    }

    /**
     * 失败响应（自定义错误码和消息）
     */
    public static <T> Response<T> error(int code, String msg) {
        return new Response<T>()
                .setCode(code)
                .setMsg(msg);
    }

    // ========== 链式调用示例 ==========
    // 由于使用了 @Accessors(chain = true)，setter 方法会自动返回 this，例如：
    // Response<String> response = new Response<String>().setCode(200).setMsg("ok").setData("hello");
}
