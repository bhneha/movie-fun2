package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.dialect.MySQL5Dialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;


import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})


public class Application {
    private String vcap_services = System.getenv("VCAP_SERVICES");

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials(@Value("${vcap.services}") String vcap_services) {
        return new DatabaseServiceCredentials(vcap_services);
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        HikariConfig config= new HikariConfig();
        config.setDataSource(dataSource);
        return new HikariDataSource(config);
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        HikariConfig config= new HikariConfig();
        config.setDataSource(dataSource);
        return new HikariDataSource(config);
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.MYSQL);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        vendorAdapter.setGenerateDdl(true);
        return vendorAdapter;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean movieEntityManagerFactoryBean(DataSource moviesDataSource, HibernateJpaVendorAdapter vendorAdapter) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("org.superbiz.moviefun.movies");
        factory.setDataSource(moviesDataSource);
        factory.setPersistenceUnitName("movie-unit");
        System.out.println(factory.toString());
        return factory;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumEntityManagerFactoryBean(DataSource albumsDataSource, HibernateJpaVendorAdapter vendorAdapter) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("org.superbiz.moviefun.albums");
        factory.setDataSource(albumsDataSource);
        factory.setPersistenceUnitName("album-unit");
        return factory;
    }

    @Bean
    public PlatformTransactionManager albumPlatformTransactionManager(EntityManagerFactory albumEntityManagerFactoryBean) {
        PlatformTransactionManager manager = new JpaTransactionManager(albumEntityManagerFactoryBean);
        return manager;
    }

    @Bean
    public PlatformTransactionManager moviePlatformTransactionManager(EntityManagerFactory movieEntityManagerFactoryBean) {
        PlatformTransactionManager manager = new JpaTransactionManager(movieEntityManagerFactoryBean);
        return manager;
    }
    /*@Bean
    public EntityManagerFactory albumEntityManagerFactory(LocalContainerEntityManagerFactoryBean albumEntityManagerFactoryBean) {

        Map<String, String> jpaProperties = new HashMap<String, String>();
        jpaProperties.put("hibernate.dialect", MySQL5Dialect.class.getName());
        //jpaProperties.put("hibernate.cache.region.factory_class", EhCacheRegionFactory.class.getName());
        //jpaProperties.put("hibernate.cache.use_second_level_cache", "true");
        //jpaProperties.put("hibernate.cache.use_query_cache", "true");
        //jpaProperties.put("hibernate.cache.use_minimal_puts", "true");
        albumEntityManagerFactoryBean.setJpaPropertyMap(jpaProperties);
        albumEntityManagerFactoryBean.afterPropertiesSet();
        return albumEntityManagerFactoryBean.getObject();

    }

    @Bean
    public EntityManagerFactory movieEntityManagerFactory(LocalContainerEntityManagerFactoryBean movieEntityManagerFactoryBean) {

        Map<String, String> jpaProperties = new HashMap<String, String>();
        jpaProperties.put("hibernate.dialect", MySQL5Dialect.class.getName());
        //jpaProperties.put("hibernate.cache.region.factory_class", EhCacheRegionFactory.class.getName());
        //jpaProperties.put("hibernate.cache.use_second_level_cache", "true");
        //jpaProperties.put("hibernate.cache.use_query_cache", "true");
        //jpaProperties.put("hibernate.cache.use_minimal_puts", "true");
        movieEntityManagerFactoryBean.setJpaPropertyMap(jpaProperties);
        movieEntityManagerFactoryBean.afterPropertiesSet();
        return movieEntityManagerFactoryBean.getObject();

    }
*/

}
