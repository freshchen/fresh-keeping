package com.github.freshchen.keeping.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author darcy
 * @since 2020/06/13
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Https {

    /**
     * 返回与当前线程绑定的request对象
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    /**
     * 返回与当前线程绑定的response对象
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }

    /**
     * 返回与当前线程绑定的session对象
     *
     * @return
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 返回应用的根URL, e.g. http://localhost:3000/&lt;contextPath&gt;
     */
    public static String getRootUrl() {
        HttpServletRequest request = getRequest();
        return request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf(request.getServletPath()));
    }
}
