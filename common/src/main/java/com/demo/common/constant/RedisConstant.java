package com.demo.common.constant;

public class RedisConstant {
    public static final String CACHE_KEY_PAGE = "smart-park:parking-lots:page:";
    public static final String CACHE_KEY_DETAIL = "smart-park:parking-lots:detail:";
    public static final String CACHE_KEY_CAR = "smart-park:car:";
    public static final String CACHE_KEY_CAR_DEFAULT = "smart-park:car:default:";
    // 过期时间（秒）
    public static final long PAGE_EXPIRE = 300;      // 5分钟
    public static final long DETAIL_EXPIRE = 1800;   // 30分钟
    public static final long  SEARCH_PAGE_EXPIRE = 300;
    public static final long NULL_EXPIRE = 60;
    public static final long CAR_EXPIRE = 60*60*24;

}
