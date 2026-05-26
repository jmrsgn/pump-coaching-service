package com.johnmartin.coaching.security.filter;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnmartin.coaching.constants.SecurityConstants;
import com.johnmartin.coaching.utilities.LoggerUtility;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CorrelationIdFilter extends BaseFilter {

    private static final Class<CorrelationIdFilter> clazz = CorrelationIdFilter.class;

    public CorrelationIdFilter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected void doFilterAction(HttpServletRequest request, HttpServletResponse response) {
        String requestId = request.getHeader(SecurityConstants.HttpHeaders.REQUEST_ID);

        if (StringUtils.isBlank(requestId)) {
            requestId = UUID.randomUUID().toString();
        }

        LoggerUtility.d(clazz, String.format("requestId: [%s]", requestId));

        MDC.put(SecurityConstants.HttpHeaders.REQUEST_ID, requestId);
        request.setAttribute(SecurityConstants.HttpHeaders.REQUEST_ID, requestId);
        response.setHeader(SecurityConstants.HttpHeaders.REQUEST_ID, requestId);
    }

    @Override
    protected void afterRequest() {
        MDC.remove(SecurityConstants.HttpHeaders.REQUEST_ID);
    }
}
