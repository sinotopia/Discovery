package com.nepxion.discovery.plugin.strategy.service.context;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @author Fan Yang
 * @version 1.0
 */

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nepxion.discovery.plugin.strategy.context.AbstractStrategyContextHolder;
import com.nepxion.discovery.plugin.strategy.service.filter.ServiceStrategyRouteFilter;

public class ServiceStrategyContextHolder extends AbstractStrategyContextHolder {
    @Autowired
    private ServiceStrategyRouteFilter serviceStrategyRouteFilter;

    public ServletRequestAttributes getRestAttributes() {
        RequestAttributes requestAttributes = RestStrategyContext.getCurrentContext().getRequestAttributes();
        if (requestAttributes == null) {
            requestAttributes = RequestContextHolder.getRequestAttributes();
        }

        return (ServletRequestAttributes) requestAttributes;
    }

    public Map<String, Object> getRpcAttributes() {
        return RpcStrategyContext.getCurrentContext().getAttributes();
    }

    @Override
    public String getHeader(String name) {
        ServletRequestAttributes attributes = getRestAttributes();
        if (attributes == null) {
            // LOG.warn("The ServletRequestAttributes object is lost for thread switched probably");

            return null;
        }

        return attributes.getRequest().getHeader(name);
    }

    @Override
    public String getRouteVersion() {
        return serviceStrategyRouteFilter.getRouteVersion();
    }

    @Override
    public String getRouteRegion() {
        return serviceStrategyRouteFilter.getRouteRegion();
    }

    @Override
    public String getRouteAddress() {
        return serviceStrategyRouteFilter.getRouteAddress();
    }

    @Override
    public String getRouteVersionWeight() {
        return serviceStrategyRouteFilter.getRouteVersionWeight();
    }

    @Override
    public String getRouteRegionWeight() {
        return serviceStrategyRouteFilter.getRouteRegionWeight();
    }
}