package com.myalc.fxrates.config;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.myalc.fxrates.exception.CustomException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;


@EnableCaching
@Configuration
@ConfigurationProperties(prefix = "cache")
public class CacheManagerConfig extends CachingConfigurerSupport implements BeanFactoryAware {
    
    private String KEY_SEPERATOR = "#";

    private List<String> configs = new ArrayList<>();

    private BeanFactory beanFactory;

    private final Logger logger = LoggerFactory.getLogger(CacheManagerConfig.class);


    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            public Object generate(Object target, Method method, Object... params) {

                StringBuilder sb = new StringBuilder();
                String className = target.getClass().getSimpleName();
                if (className.contains("$$"))
                    className = className.substring(0, className.indexOf("$$"));
                sb.append(className);
                sb.append(KEY_SEPERATOR);
                sb.append(method.getName());
                sb.append(KEY_SEPERATOR);
                for (Object param : params) {
                    if (param != null && StringUtils.isNotBlank(param.toString())) {
                        sb.append(param.toString());
                        sb.append(KEY_SEPERATOR);
                    }
                }
                String str = sb.toString().substring(0, sb.toString().length() - KEY_SEPERATOR.length());;
                logger.debug("Cache key is: {}", str);
                return str;
            }
        };
    }


    @Bean
    @Override
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    public List<String> getConfigs() {
        return configs;
    }

    public void setConfigs(List<String> configs) {
        this.configs = configs;
    }
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    public void configure() {
        
        // properties: cache.configs[0]=MyCache|10|1200|6000|caffeine (<name>|<expiration>|<capacity>|<maxSize>|<type>)
        Assert.state(beanFactory instanceof ConfigurableBeanFactory, "wrong bean factory type");
        ConfigurableBeanFactory configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
        logger.info("Configure cache managers");

        // prevent duplicate cache managers
        List<String> configured = new ArrayList<>();

        for (String config : configs) {
            String[] parts = config.split("\\|");
            if (parts.length == 5 || parts.length == 4) {
                String name = parts[0];
                if (configured.contains(name)) {
                    logger.warn("Duplicate configuration for cache manager name {}", name);
                    continue;
                }
                Integer expiration = Integer.valueOf(parts[1]);
                Integer capacity = Integer.valueOf(parts[2]);
                Integer maxSize = Integer.valueOf(parts[3]);
                
                String type = "na";
                if (parts.length == 5)
                    type = parts[4];

                CacheManager manager = createCacheManager(name, expiration, capacity, maxSize, type);
                // register bean
                configurableBeanFactory.registerSingleton(name, manager);
                logger.info("Bean registered. Name: {}, expiration: {}, capacity: {}, maxSize: {}, type: {} - {}", name, expiration, capacity, maxSize, type, manager.toString());
                configured.add(name);
            }
        }
    }

    private CacheManager createCacheManager(String name, Integer expiration, Integer capacity, Integer maxSize, String type) {
        
        CacheManager manager;

        if ("caffeine".equals(type)) {
            CaffeineCacheManager cmgr = new CaffeineCacheManager();
            cmgr.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(capacity)
                .maximumSize(maxSize)
                .recordStats()
                .expireAfterWrite(Duration.ofSeconds(expiration))
            );
            manager = (CacheManager) cmgr;

        } else if ("redis".equals(type)) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Redis cache manager not implemented!");

        } else {
            logger.warn("Cannot find type {}. Using ConcurrentMapCacheManager.", type);
            manager = new ConcurrentMapCacheManager();
        }

        return manager;
    }

}
