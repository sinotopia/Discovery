package com.nepxion.discovery.plugin.strategy.zuul.context;

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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.discovery.common.constant.DiscoveryConstant;
import com.nepxion.discovery.plugin.strategy.context.AbstractStrategyContextHolder;
import com.nepxion.discovery.plugin.strategy.zuul.constant.ZuulStrategyConstant;
import com.netflix.zuul.context.RequestContext;

public class ZuulStrategyContextHolder extends AbstractStrategyContextHolder {
    // 如果外界也传了相同的Header，例如，从Postman传递过来的Header，当下面的变量为true，以网关设置为优先，否则以外界传值为优先
    @Value("${" + ZuulStrategyConstant.SPRING_APPLICATION_STRATEGY_ZUUL_HEADER_PRIORITY + ":true}")
    protected Boolean zuulHeaderPriority;

    // 当以网关设置为优先的时候，网关未配置Header，而外界配置了Header，仍旧忽略外界的Header
    @Value("${" + ZuulStrategyConstant.SPRING_APPLICATION_STRATEGY_ZUUL_ORIGINAL_HEADER_IGNORED + ":true}")
    protected Boolean zuulOriginalHeaderIgnored;

    public HttpServletRequest getRequest() {
        HttpServletRequest request = ZuulStrategyContext.getCurrentContext().getRequest();
        if (request == null) {
            request = RequestContext.getCurrentContext().getRequest();
        }

        return request;
    }

    public Map<String, String> getZuulRequestHeaders() {
        Map<String, String> headers = ZuulStrategyContext.getCurrentContext().getHeaders();
        if (headers == null) {
            headers = RequestContext.getCurrentContext().getZuulRequestHeaders();
        }

        return headers;
    }

    @Override
    public String getHeader(String name) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            // LOG.warn("The HttpServletRequest object is lost for thread switched, or it is got before context filter probably");

            return null;
        }

        if (zuulHeaderPriority) {
            // 来自于Zuul Filter的Header
            String header = getZuulRequestHeaders().get(name);
            if (StringUtils.isEmpty(header)) {
                if (isRouteValue(name) && zuulOriginalHeaderIgnored) {
                    header = null;
                } else {
                    // 来自于外界的Header
                    header = request.getHeader(name);
                }
            }

            return header;
        } else {
            // 来自于外界的Header
            String header = request.getHeader(name);
            if (StringUtils.isEmpty(header)) {
                // 来自于Zuul Filter的Header
                header = getZuulRequestHeaders().get(name);
            }

            return header;
        }
    }

    private boolean isRouteValue(String name) {
        return StringUtils.equals(name, DiscoveryConstant.N_D_VERSION) ||
                StringUtils.equals(name, DiscoveryConstant.N_D_REGION) ||
                StringUtils.equals(name, DiscoveryConstant.N_D_ADDRESS) ||
                StringUtils.equals(name, DiscoveryConstant.N_D_VERSION_WEIGHT) ||
                StringUtils.equals(name, DiscoveryConstant.N_D_REGION_WEIGHT);
    }
}