package com.nepxion.discovery.plugin.strategy.zuul.filter;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.nepxion.discovery.common.constant.DiscoveryConstant;
import com.nepxion.discovery.common.entity.RuleEntity;
import com.nepxion.discovery.common.entity.StrategyCustomizationEntity;
import com.nepxion.discovery.common.entity.StrategyHeaderEntity;
import com.nepxion.discovery.plugin.framework.adapter.PluginAdapter;
import com.nepxion.discovery.plugin.strategy.context.StrategyContextHolder;
import com.nepxion.discovery.plugin.strategy.zuul.constant.ZuulStrategyConstant;
import com.nepxion.discovery.plugin.strategy.zuul.monitor.ZuulStrategyMonitor;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public abstract class AbstractZuulStrategyRouteFilter extends ZuulFilter implements ZuulStrategyRouteFilter {
    @Autowired
    protected PluginAdapter pluginAdapter;

    @Autowired
    protected StrategyContextHolder strategyContextHolder;

    @Autowired(required = false)
    private ZuulStrategyMonitor zuulStrategyMonitor;

    // 如果外界也传了相同的Header，例如，从Postman传递过来的Header，当下面的变量为true，以网关设置为优先，否则以外界传值为优先
    @Value("${" + ZuulStrategyConstant.SPRING_APPLICATION_STRATEGY_ZUUL_HEADER_PRIORITY + ":true}")
    protected Boolean zuulHeaderPriority;

    // 当以网关设置为优先的时候，网关未配置Header，而外界配置了Header，仍旧忽略外界的Header
    @Value("${" + ZuulStrategyConstant.SPRING_APPLICATION_STRATEGY_ZUUL_ORIGINAL_HEADER_IGNORED + ":true}")
    protected Boolean zuulOriginalHeaderIgnored;

    @Value("${" + ZuulStrategyConstant.SPRING_APPLICATION_STRATEGY_ZUUL_ROUTE_FILTER_ORDER + ":" + ZuulStrategyConstant.SPRING_APPLICATION_STRATEGY_ZUUL_ROUTE_FILTER_ORDER_VALUE + "}")
    protected Integer filterOrder;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return filterOrder;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RuleEntity ruleEntity = pluginAdapter.getRule();
        if (ruleEntity != null) {
            StrategyCustomizationEntity strategyCustomizationEntity = ruleEntity.getStrategyCustomizationEntity();
            if (strategyCustomizationEntity != null) {
                StrategyHeaderEntity strategyHeaderEntity = strategyCustomizationEntity.getStrategyHeaderEntity();
                if (strategyHeaderEntity != null) {
                    Map<String, String> headerMap = strategyHeaderEntity.getHeaderMap();
                    for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();

                        ZuulStrategyFilterResolver.setHeader(key, value, zuulHeaderPriority);
                    }
                }
            }
        }

        String routeVersion = getRouteVersion();
        String routeRegion = getRouteRegion();
        String routeAddress = getRouteAddress();
        String routeVersionWeight = getRouteVersionWeight();
        String routeRegionWeight = getRouteRegionWeight();

        // 通过过滤器设置路由Header头部信息，并全链路传递到服务端
        if (StringUtils.isNotEmpty(routeVersion)) {
            ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_VERSION, routeVersion, zuulHeaderPriority);
        } else {
            ZuulStrategyFilterResolver.ignoreHeader(DiscoveryConstant.N_D_VERSION, zuulHeaderPriority, zuulOriginalHeaderIgnored);
        }
        if (StringUtils.isNotEmpty(routeRegion)) {
            ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_REGION, routeRegion, zuulHeaderPriority);
        } else {
            ZuulStrategyFilterResolver.ignoreHeader(DiscoveryConstant.N_D_REGION, zuulHeaderPriority, zuulOriginalHeaderIgnored);
        }
        if (StringUtils.isNotEmpty(routeAddress)) {
            ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_ADDRESS, routeAddress, zuulHeaderPriority);
        } else {
            ZuulStrategyFilterResolver.ignoreHeader(DiscoveryConstant.N_D_ADDRESS, zuulHeaderPriority, zuulOriginalHeaderIgnored);
        }
        if (StringUtils.isNotEmpty(routeVersionWeight)) {
            ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_VERSION_WEIGHT, routeVersionWeight, zuulHeaderPriority);
        } else {
            ZuulStrategyFilterResolver.ignoreHeader(DiscoveryConstant.N_D_VERSION_WEIGHT, zuulHeaderPriority, zuulOriginalHeaderIgnored);
        }
        if (StringUtils.isNotEmpty(routeRegionWeight)) {
            ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_REGION_WEIGHT, routeRegionWeight, zuulHeaderPriority);
        } else {
            ZuulStrategyFilterResolver.ignoreHeader(DiscoveryConstant.N_D_REGION_WEIGHT, zuulHeaderPriority, zuulOriginalHeaderIgnored);
        }

        // 对于服务A -> 网关 -> 服务B调用链
        // 域网关下(zuulHeaderPriority=true)，只传递网关自身的group，不传递服务A的group，起到基于组的网关端服务调用隔离
        // 非域网关下(zuulHeaderPriority=false)，优先传递服务A的group，基于组的网关端服务调用隔离不生效，但可以实现基于相关参数的熔断限流等功能
        ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_SERVICE_GROUP, pluginAdapter.getGroup(), zuulHeaderPriority);
        // 网关只负责传递服务A的相关参数（例如：serviceId），不传递自身的参数，实现基于相关参数的熔断限流等功能
        ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_SERVICE_TYPE, pluginAdapter.getServiceType(), false);
        String serviceAppId = pluginAdapter.getServiceAppId();
        if (StringUtils.isNotEmpty(serviceAppId)) {
            ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_SERVICE_APP_ID, serviceAppId, false);
        }
        ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_SERVICE_ID, pluginAdapter.getServiceId(), false);
        ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_SERVICE_ADDRESS, pluginAdapter.getHost() + ":" + pluginAdapter.getPort(), false);
        ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_SERVICE_VERSION, pluginAdapter.getVersion(), false);
        ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_SERVICE_REGION, pluginAdapter.getRegion(), false);
        ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.N_D_SERVICE_ENVIRONMENT, pluginAdapter.getEnvironment(), false);

        extendFilter();

        // 调用链监控
        RequestContext context = RequestContext.getCurrentContext();
        if (zuulStrategyMonitor != null) {
            zuulStrategyMonitor.monitor(context);
        }

        // 拦截侦测请求
        String path = context.getRequest().getServletPath();
        if (path.contains(DiscoveryConstant.INSPECTOR_ENDPOINT_URL)) {
            ZuulStrategyFilterResolver.setHeader(DiscoveryConstant.INSPECTOR_ENDPOINT_HEADER, pluginAdapter.getPluginInfo(null), true);
        }

        return null;
    }

    protected void extendFilter() {

    }

    public PluginAdapter getPluginAdapter() {
        return pluginAdapter;
    }

    public StrategyContextHolder getStrategyContextHolder() {
        return strategyContextHolder;
    }
}