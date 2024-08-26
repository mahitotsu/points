package com.mahitotsu.points.entity;

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.mahitotsu.points.Main;

@Configuration
public class EntityConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {

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

        final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(DataSource.class.cast(pf.getProxy()));
        emf.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        emf.setPackagesToScan(Main.class.getPackage().getName());
        return emf;
    }
}
