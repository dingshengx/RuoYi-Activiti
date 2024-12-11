package com.ruoyi.framework.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 条件类
 * 用于判断当前环境是否为 development 环境。
 * 如果是 development 环境，则允许特定的组件被 Spring 容器注入。
 *
 * @author dingsheng
 * @date 2024/8/16
 */
public class DevelopmentCondition implements Condition {

    private static final String DEVELOPMENT_ENVIRONMENT = "dev";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 获取环境名称
        String environmentName = context.getEnvironment()
                                        .getProperty("spring.profiles.active");
        // 判断环境是否为 dev
        return DEVELOPMENT_ENVIRONMENT.equals(environmentName);
    }

}
