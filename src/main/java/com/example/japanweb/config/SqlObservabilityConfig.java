package com.example.japanweb.config;

import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = "app.observability.sql", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SqlObservabilityConfig {

    @Bean
    public BeanPostProcessor dataSourceProxyPostProcessor(SqlQueryMetricsListener queryMetricsListener) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (!(bean instanceof DataSource dataSource)) {
                    return bean;
                }
                if (bean instanceof ProxyDataSource) {
                    return bean;
                }

                return ProxyDataSourceBuilder.create(dataSource)
                        .name(beanName)
                        .listener(queryMetricsListener)
                        .build();
            }
        };
    }
}
