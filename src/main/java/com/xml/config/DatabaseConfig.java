package com.xml.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.properties" }, ignoreResourceNotFound = true)
public class DatabaseConfig {
	
	@Value("${db.driver}")
	private String dbDriver;
	@Value("${db.password}")
	private String dbPassword;
	@Value("${db.url}")
	private String dbUrl;
	@Value("${db.username}")
	private String dbUserName;
	@Value("${hibernate.dialect}")
	private String hibernateDialect;
	@Value("${hibernate.show_sql}")
	private String hibernateShowSql;
	@Value("${hibernate.hbm2ddl.auto}")
	private String hibernateHbm2ddlAuto;
	@Value("${entitymanager.packagesToScan}")
	private String entityManagerPackagesToScan;
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		try {
			dataSource.setDriverClassName(dbDriver);
			dataSource.setUrl(dbUrl);
			dataSource.setUsername(dbUserName);
			dataSource.setPassword(dbPassword);
		} catch (Exception e) {
			System.out.println(e);
		}

		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		try {
			sessionFactoryBean.setDataSource(dataSource());
			sessionFactoryBean.setPackagesToScan(entityManagerPackagesToScan);
			Properties hibernateProperties = new Properties();
			hibernateProperties.put("hibernate.dialect", hibernateDialect);
			hibernateProperties.put("hibernate.show_sql", hibernateShowSql);
			hibernateProperties.put("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
			sessionFactoryBean.setHibernateProperties(hibernateProperties);
		} catch (Exception e) {
			System.out.println(e);
		}
		return sessionFactoryBean;
	}

	@Bean
	public HibernateTransactionManager transactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		try {
			transactionManager.setSessionFactory(sessionFactory().getObject());
		} catch (Exception e) {
			System.out.println(e);
		}

		return transactionManager;
	}
	
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/resources/**")
          .addResourceLocations("/resources/"); 
    }
}
