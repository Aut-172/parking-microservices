package com.demo.userapi.fallback;

import com.demo.common.dto.RegisterRequest;
import com.demo.common.entity.User;
import com.demo.common.exception.BusinessException;
import com.demo.userapi.feign.UserFeignClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserFeignFallbackFactory implements FallbackFactory<UserFeignClient> {
    @Override
    public UserFeignClient create(Throwable cause) {
        return new UserFeignClient() {

            @Override
            public User getByPhone(String phone) {
                // 记录详细降级原因
                if (cause instanceof FeignException) {
                    FeignException fe = (FeignException) cause;
                    log.error("调用 user-service 失败，状态码：{}，原因：{}", fe.status(), fe.getMessage());
                } else {
                    log.error("调用 user-service 失败，原因：{}", cause.getMessage(), cause);
                }
                // 降级逻辑：抛出业务异常，由调用方全局处理器统一返回
                throw new BusinessException(500,"登录服务暂不可用，请稍后重试");
            }

            @Override
            public User register(RegisterRequest registerRequest) {
                // 记录详细降级原因
                if (cause instanceof FeignException) {
                    FeignException fe = (FeignException) cause;
                    log.error("调用 user-service 失败，状态码：{}，原因：{}", fe.status(), fe.getMessage());
                } else {
                    log.error("调用 user-service 失败，原因：{}", cause.getMessage(), cause);
                }
                // 降级逻辑：抛出业务异常，由调用方全局处理器统一返回
                throw new BusinessException(500,"注册服务暂不可用，请稍后重试");
            }
        };
    }
}
