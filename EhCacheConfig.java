package com.chunfytseng.alm.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.cache.Caching;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class EhCacheConfig extends CachingConfigurerSupport {
	
	@Bean
	@Override
	public CacheManager cacheManager() {
		
		CacheConfiguration<String, String> cacheConfiguration = 
	            CacheConfigurationBuilder
	            .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10000L))
	            .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofDays(2L)))
	    			.withDispatcherConcurrency(4)
	            .build();
		Map<String, org.ehcache.config.CacheConfiguration<?, ?>> cacheMap = new HashMap<>();
		cacheMap.put("vsk", cacheConfiguration);
		EhcacheCachingProvider ehcacheCachingProvider = (EhcacheCachingProvider) Caching.getCachingProvider();
		 DefaultConfiguration defaultConfiguration = 
				 new DefaultConfiguration(cacheMap, ehcacheCachingProvider.getDefaultClassLoader());
		 javax.cache.CacheManager cacheManager = 
				 ehcacheCachingProvider.getCacheManager(ehcacheCachingProvider.getDefaultURI(), defaultConfiguration);
	    
		return new JCacheCacheManager(cacheManager);
	}

	@Bean
	@Override
	public CacheResolver cacheResolver() {
		//return new SimpleCacheResolver();
		return new SimpleCacheResolver(cacheManager());
	}

	@Bean
	@Override
	public KeyGenerator keyGenerator() {
		return new SimpleKeyGenerator();
	}

	@Bean
	@Override
	public CacheErrorHandler errorHandler() {
		return new SimpleCacheErrorHandler();
	}
}
