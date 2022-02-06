package com.github.freshchen.keeping.common.lib.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;

/**
 * @author darcy
 * @since 2022/01/20
 **/
public class Id {

    public static String genId() {
        long ipv4ToLong = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
        long work = ipv4ToLong % 32;
        long i = "dev".hashCode() % 32;

        Snowflake snowflake = IdUtil.createSnowflake(work, i);

        return snowflake.nextIdStr();
    }
}
