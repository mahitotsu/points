package com.mahitotsu.points.entity;

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.mahitotsu.points.Main;

@Configuration
public class EntityConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.jpa")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final EntityManagerFactoryBuilder builder,
            final DataSource dataSource) {

        final ProxyFactory pf = new ProxyFactory();
        pf.setInterfaces(DataSource.class);
        pf.setTarget(dataSource);
        pf.addAdvice(new AfterReturningAdvice() {
            @Override
            public void afterReturning(@Nullable final Object returnValue, @NonNull final Method method,
                    @NonNull final Object[] args, @Nullable final Object target) throws Throwable {
                if (returnValue == null || !Connection.class.isInstance(returnValue)
                        || !"getConnection".equals(method.getName())) {
                    return;
                }

                final Connection con = Connection.class.cast(returnValue);
                con.prepareCall("{call init_session()}").execute();
                return;
            }
        });

        return builder
                .dataSource(DataSource.class.cast(pf.getProxy()))
                .packages(Main.class)
                .build();
    }
}
