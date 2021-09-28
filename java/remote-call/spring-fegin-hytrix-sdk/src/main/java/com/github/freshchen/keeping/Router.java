package com.github.freshchen.keeping;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author darcy
 * @since 2020/06/25
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Router {

    public static final String API = "/api";

    public static final String USER = API + "/user";

    public static final String USER_DELETE = API + "/user/{id}";

}
