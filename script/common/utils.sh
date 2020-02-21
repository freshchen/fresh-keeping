#!/usr/bin/env bash

# 只能读取key:value一对一的值
read_value_from_conf_file() {
    local key=${1}
    local conf_file=${2}
    local command="grep -v "^#" '${conf_file}' | grep -w '${key}' | sed 's/ //g' | cut -d '=' -f2 "
    eval ${command}
}

# ping地址
ping_address() {
    address=${1}
    ping -c 1 ${address}
    if [[ $? -eq 0 ]];then
        return 0
    fi
    ping6 -c 1 ${address}
    return $?
}

# 获取组中用户
get_user_by_group(){
    local group_name=$1
    if [[ -z $groupName ]];then
        return 1
    fi
    local group_info=$(grep -E "^${group_name}:" /etc/group)
    local group_id=$(echo ${group_info} | awk -F ":" '{print $3}')
    local user_list=$(grep -E "^.+:.+:.+:${group_id}:" /etc/passwd | awk -F ":" '{print $1}')
    echo ${user_list}
    return 0
}

# 安全插入一行,如果存在不添加
safe_append_line() {
    local line=$1
    local file=$2
    grep -q -e "${line}" ${file} &>/dev/null || echo ${line} >> ${file} || return 1
    return 0
}

# 将字符串转为ASCLL码作为唯一标识ID
string_to_ascll() {
    local string=${1}
    local length=${#string}
    local string_ascll=$(echo ${string} | tr -d "\n" | od -An -t dC)
    local string_ascll=$(echo ${string_ascll} | tr ' ' .)
    eval "${length}.${string_ascll}"
}
