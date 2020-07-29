package com.nepxion.discovery.plugin.framework.configuration;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.nepxion.discovery.common.constant.DiscoveryConstant;
import com.nepxion.discovery.plugin.framework.adapter.DefaultEnvironmentRouteAdapter;
import com.nepxion.discovery.plugin.framework.adapter.EnvironmentRouteAdapter;
import com.nepxion.discovery.plugin.framework.cache.PluginCache;
import com.nepxion.discovery.plugin.framework.cache.RuleCache;
import com.nepxion.discovery.plugin.framework.context.PluginContextAware;
import com.nepxion.discovery.plugin.framework.event.PluginEventWapper;
import com.nepxion.discovery.plugin.framework.event.PluginPublisher;
import com.nepxion.discovery.plugin.framework.event.PluginSubscriber;
import com.nepxion.discovery.plugin.framework.generator.GitGenerator;
import com.nepxion.discovery.plugin.framework.generator.GroupGenerator;
import com.nepxion.discovery.plugin.framework.listener.discovery.DiscoveryListenerExecutor;
import com.nepxion.discovery.plugin.framework.listener.discovery.EnvironmentFilterDiscoveryListener;
import com.nepxion.discovery.plugin.framework.listener.discovery.HostFilterDiscoveryListener;
import com.nepxion.discovery.plugin.framework.listener.discovery.RegionFilterDiscoveryListener;
import com.nepxion.discovery.plugin.framework.listener.discovery.VersionFilterDiscoveryListener;
import com.nepxion.discovery.plugin.framework.listener.loadbalance.EnvironmentFilterLoadBalanceListener;
import com.nepxion.discovery.plugin.framework.listener.loadbalance.HostFilterLoadBalanceListener;
import com.nepxion.discovery.plugin.framework.listener.loadbalance.LoadBalanceListenerExecutor;
import com.nepxion.discovery.plugin.framework.listener.loadbalance.NotificationLoadBalanceListener;
import com.nepxion.discovery.plugin.framework.listener.loadbalance.RegionFilterLoadBalanceListener;
import com.nepxion.discovery.plugin.framework.listener.loadbalance.VersionFilterLoadBalanceListener;
import com.nepxion.discovery.plugin.framework.listener.register.CountFilterRegisterListener;
import com.nepxion.discovery.plugin.framework.listener.register.HostFilterRegisterListener;
import com.nepxion.discovery.plugin.framework.listener.register.RegisterListenerExecutor;
import com.nepxion.eventbus.annotation.EnableEventBus;

@Configuration
@EnableEventBus
public class PluginAutoConfiguration {
    @Bean
    @LoadBalanced
    public RestTemplate pluginRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PluginContextAware pluginContextAware() {
        return new PluginContextAware();
    }

    @Bean
    public PluginPublisher pluginPublisher() {
        return new PluginPublisher();
    }

    @Bean
    public PluginSubscriber pluginSubscriber() {
        return new PluginSubscriber();
    }

    @Bean
    public PluginEventWapper pluginEventWapper() {
        return new PluginEventWapper();
    }

    @Bean
    public PluginCache pluginCache() {
        return new PluginCache();
    }

    @Bean
    public RuleCache ruleCache() {
        return new RuleCache();
    }

    @Bean
    public RegisterListenerExecutor registerListenerExecutor() {
        return new RegisterListenerExecutor();
    }

    @Bean
    public DiscoveryListenerExecutor discoveryListenerExecutor() {
        return new DiscoveryListenerExecutor();
    }

    @Bean
    public LoadBalanceListenerExecutor loadBalanceListenerExecutor() {
        return new LoadBalanceListenerExecutor();
    }

    @Bean
    public HostFilterRegisterListener hostFilterRegisterListener() {
        return new HostFilterRegisterListener();
    }

    @Bean
    public CountFilterRegisterListener countFilterRegisterListener() {
        return new CountFilterRegisterListener();
    }

    @Bean
    public HostFilterDiscoveryListener hostFilterDiscoveryListener() {
        return new HostFilterDiscoveryListener();
    }

    @Bean
    public VersionFilterDiscoveryListener versionFilterDiscoveryListener() {
        return new VersionFilterDiscoveryListener();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = DiscoveryConstant.SPRING_APPLICATION_ENVIRONMENT_ISOLATION_ENABLED, matchIfMissing = false)
    public EnvironmentFilterDiscoveryListener environmentFilterDiscoveryListener() {
        return new EnvironmentFilterDiscoveryListener();
    }

    @Bean
    public RegionFilterDiscoveryListener regionFilterDiscoveryListener() {
        return new RegionFilterDiscoveryListener();
    }

    @Bean
    public HostFilterLoadBalanceListener hostFilterLoadBalanceListener() {
        return new HostFilterLoadBalanceListener();
    }

    @Bean
    public VersionFilterLoadBalanceListener versionFilterLoadBalanceListener() {
        return new VersionFilterLoadBalanceListener();
    }

    @Bean
    public RegionFilterLoadBalanceListener regionFilterLoadBalanceListener() {
        return new RegionFilterLoadBalanceListener();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = DiscoveryConstant.SPRING_APPLICATION_NO_SERVERS_NOTIFY_ENABLED, matchIfMissing = false)
    public NotificationLoadBalanceListener notificationLoadBalanceListener() {
        return new NotificationLoadBalanceListener();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = DiscoveryConstant.SPRING_APPLICATION_ENVIRONMENT_ISOLATION_ENABLED, matchIfMissing = false)
    public EnvironmentFilterLoadBalanceListener environmentFilterLoadBalanceListener() {
        return new EnvironmentFilterLoadBalanceListener();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = DiscoveryConstant.SPRING_APPLICATION_ENVIRONMENT_ISOLATION_ENABLED, matchIfMissing = false)
    public EnvironmentRouteAdapter environmentRouteAdapter() {
        return new DefaultEnvironmentRouteAdapter();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = DiscoveryConstant.SPRING_APPLICATION_GROUP_GENERATOR_ENABLED, matchIfMissing = false)
    public GroupGenerator groupGenerator() {
        return new GroupGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = DiscoveryConstant.SPRING_APPLICATION_GIT_GENERATOR_ENABLED, matchIfMissing = false)
    public GitGenerator gitGenerator() {
        return new GitGenerator();
    }
}