#!/bin/bash

export PATH=/usr/sbin:${PATH}
KEYTOOL="${JAVA_HOME}/bin/keytool"
OPENSSL="openssl"

check_server_certificate_expiration() {
    local chect_port_list=$(ss -lnt | awk 'NR!=1' | awk '{print $4}' | awk -F ':' '{print $NF}' | sort | uniq)
    for port in ${chect_port_list[@]} ; do
        local address="127.0.0.1:${port}"
        local time=$(echo 'Q' | timeout 5 openssl s_client -connect ${address} 2>/dev/null | openssl x509 -noout -dates 2>/dev/null)
        if [[ ${time} == '' ]]; then
            continue
        fi
        local end_time=$(echo ${time} | awk -F '=' '{print $3}'| awk -F ' +' '{print $1,$2,$4 }' | xargs -i  date +%s -d {})
        local end_after_today=$(compute_time ${end_time})

        handle_clear_alarm ${end_after_today} "${port}" "The alarm of server certificate on port ${port}"
        handle_raise_alarm ${end_after_today} "${port}" "The server certificate on port ${port}"
    done
}

check_postgres_certificate_expiration() {
    case $LOCAL_NODE_NAME in
        1 | 2 | 3 | 4)
        handle_postgres_expire "127.0.0.1:5432"
        ;;
        *)
        return 0
        ;;
    esac
}

check_client_certificate_expiration() {

    case $LOCAL_NODE_NAME in
        1)
        handle_client_expire "conf1" "\<TrustKeyStore\>"
        handle_client_expire "conf1" "\<TrustKeyStore\>"
        ;;
        2)
        handle_client_expire  "conf2" "\<TrustKeyStore\>"
        ;;
        3)
        handle_client_expire "conf2" "\<TrustKeyStore\>"
        ;;
        4)
        handle_client_expire "conf2" "\<TrustKeyStore\>"
        ;;
        *)
        return 0
        ;;
    esac
}

handle_postgres_expire() {
    local database=${1}
    local check_script_path="./postgres_get_server_cert.py"
    local end_time=$(python ${check_script_path} ${database} | openssl x509 -noout -enddate 2>/dev/null | awk -F '=' '{print $2}'| awk -F ' +' '{print $1,$2,$4 }' | xargs -i  date +%s -d {})
    local end_after_today=$(compute_time ${end_time})

    handle_clear_alarm ${end_after_today} "5432" "The alarm of postgres certificate on port 5432"
    handle_raise_alarm ${end_after_today} "5432" "The postgres certificate on port 5432"
}

handle_client_expire() {
    local config_xml=$1
    local parameter=$2
    local path_list=$(cat "${config_xml}" | grep "${parameter}" | awk -F '<|>' '{print $3}')
    for path in ${path_list[@]} ; do
        local dates=$(echo '' | ${KEYTOOL} -v -list -keystore "${path}" 2>/dev/null | grep 'Valid from')
        local aliases=$(echo '' | ${KEYTOOL} -v -list -keystore "${path}" 2>/dev/null | grep 'Alias name')
        if [[ -n ${dates} ]]; then
            for (( index = 1; index <= "$(echo "${dates}" | wc -l)"; ++index )); do
                local end_time=$(echo "${dates}" | head "-${index}" | tail -1 | awk '{print $11,$12,$15}' | xargs -i  date +%s -d {})
                local alias_name=$(echo "${aliases}" | head "-${index}" | tail -1 | awk '{print $3}')
                local length=${#alias_name}
                local alias_name_ascll=$(echo "${alias_name}" | tr -d "\n" | od -An -t dC)
                local alias_name_ascll=$(echo ${alias_name_ascll} | tr ' ' .)
                local end_after_today=$(compute_time ${end_time})

                handle_clear_alarm ${end_after_today} "${length}.${alias_name_ascll}" "The alarm of client certificate on ${path} alias name ${alias_name}"
                handle_raise_alarm ${end_after_today} "${length}.${alias_name_ascll}" "The client certificate on ${path} alias name ${alias_name}"
            done
        fi
    done
}

handle_clear_alarm() {
    local end_after_today=$1
    local id=""$2""
    local description=""$3" has been cleared"

    if [[ ${end_after_today} -le ${EXP_DAY_4} ]]; then
        return 0
    elif [[ ${end_after_today} -le ${EXP_DAY_3} ]]; then
        clear_certificate_expiration_event "${id}" "${description}"
    elif [[ ${end_after_today} -le ${EXP_DAY_2} ]]; then
        clear_certificate_expiration_event "${id}" "${description}"
        clear_certificate_expiration_event "${id}" "${description}"
    elif [[ ${end_after_today} -le ${EXP_DAY_1} ]]; then
        clear_certificate_expiration_event "${id}" "${description}"
        clear_certificate_expiration_event "${id}" "${description}"
        clear_certificate_expiration_event "${id}" "${description}"
    else
        clear_certificate_expiration_event "${id}" "${description}"
        clear_certificate_expiration_event "${id}" "${description}"
        clear_certificate_expiration_event "${id}" "${description}"
        clear_certificate_expiration_event "${id}" "${description}"
    fi
}

handle_raise_alarm() {
    local end_after_today=$1
    local id=$2
    local description=$3
    if [[ ${end_after_today} -lt 0 ]]; then
        return 0
    elif [[ ${end_after_today} -le ${EXP_DAY_4} ]]; then
        local description="${description} will expire in ${EXP_DAY_4} days."
        raise_certificate_expiration_event "${id}" "${description}"
        echo ${description}
        return 0
    elif [[ ${end_after_today} -le ${EXP_DAY_3} ]]; then
        local description="${description} will expire in ${EXP_DAY_3} days."
        raise_certificate_expiration_event "${id}" "${description}"
        echo ${description}
        return 0
    elif [[ ${end_after_today} -le ${EXP_DAY_2} ]]; then
        local description="${description} will expire in ${EXP_DAY_2} days."
        raise_certificate_expiration_event "${id}" "${description}"
        echo ${description}
        return 0
    elif [[ ${end_after_today} -le ${EXP_DAY_1} ]]; then
        local description="${description} will expire in ${EXP_DAY_1} days."
        raise_certificate_expiration_event "${id}" "${description}"
        echo ${description}
        return 0
    fi
}

raise_certificate_expiration_event() {
    echo "$1" "$2"
}

clear_certificate_expiration_event() {
    echo "$1" "$2"
}

compute_time() {
    local time_format=$1
    local current_time=$(date | awk -F ' +'  '{print $2,$3,$6}')
    local current_time_format=$(date +%s -d "${current_time}")
    local days=$(($((time_format-current_time_format))/(60*60*24)))
    echo ${days}
}

prepare_parameters() {

    LOCAL_NODE_NAME="1"

    if [[ $# -eq 0 ]] ; then
        EXP_DAY_1="90"
        EXP_DAY_2="30"
        EXP_DAY_3="14"
        EXP_DAY_4="3"
    elif [[ ! $# -eq 4 ]] ; then
        echo "Please input 4 expiration check date levels"
        exit 1
    elif [[ $1 -le $2 ]] || [[ $2 -le $3 ]] || [[ $3 -le $4 ]]; then
        echo "Please input 4 descending expiration check date levels"
        exit 1
    else
        EXP_DAY_1=$1
        EXP_DAY_2=$2
        EXP_DAY_3=$3
        EXP_DAY_4=$4
    fi
}

main(){
    prepare_parameters $@
    check_server_certificate_expiration
    check_client_certificate_expiration
    check_postgres_certificate_expiration

    echo "End to check the certificate of server client and postgres"
}

main $@
