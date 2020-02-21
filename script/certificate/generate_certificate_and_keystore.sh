#!/bin/bash

CURRENT_PATH=$(cd "$(dirname "$0")"; pwd)

PASSWORD="QWDQWDQSAVFDS"
KEYSTORE_PASSWORD="123456"

KEYTOOL="${JAVA_HOME}/bin/keytool"
OPENSSL="openssl"
EXPIRATION_TIME="1095"
KEY_LENGTH="2048"
CERTIFICATE_TYPE="pkcs12"
KEY_TYPE="aes256"
TEMP_KEYSTORE_PATH="${CURRENT_PATH}/tmp/"
KEYSTORE_PREFIX="${CURRENT_PATH}/keystores/"
CA_KEY_PATH="${CURRENT_PATH}/ca/root.key"
CA_CRT_PATH="${CURRENT_PATH}/ca/root.crt"

KEYSTORE_PATH_LIST=(\
"${KEYSTORE_PREFIX}/11111.keystore" \
"${KEYSTORE_PREFIX}/22222.keystore" \
"${KEYSTORE_PREFIX}/33333.keystore" \
)

ALIAS_NAME_LIST=(\
"111111" \
"222222" \
"333333" \
)

COMMON_NAME_LIST=(\
"www.1.com" \
"www.2.com" \
"www.3.com" \
)
TRUST_KEYSTORE_PATH_LIST=(\
"${KEYSTORE_PREFIX}/111111.trustkeystore" \
"${KEYSTORE_PREFIX}/222222.trustkeystore" \
"${KEYSTORE_PREFIX}/333333.trustkeystore" \
)

TRUST_ALIAS_NAME_LIST=(\
"111111" \
"222222" \
"333333" \
)

POSTGRES_SSL_LIST=(\
"${CURRENT_PATH}/postgresql/ssl/" \
)

generate_ca_keystore() {

    mkdir -p "${TEMP_KEYSTORE_PATH}"
    mkdir -p "${CA_KEY_PATH%/*}"

    local certificate_name="root"
    local ca_key_path="${TEMP_KEYSTORE_PATH}${certificate_name}.key"
    local ca_crt_path="${TEMP_KEYSTORE_PATH}${certificate_name}.crt"
    local ca_csr_path="${TEMP_KEYSTORE_PATH}${certificate_name}.csr"
    local subject_info="/C=CN/ST=SH/L=SH/O=OWN/OU=OWN/CN=domain_name/emailAddress=email"

    ${OPENSSL} genrsa -${KEY_TYPE} -passout pass:${PASSWORD} -out ${ca_key_path} ${KEY_LENGTH} || exit 1
    ${OPENSSL} req -new -key ${ca_key_path} -out ${ca_csr_path} -subj "${subject_info}" -passin pass:${PASSWORD} || exit 1
    ${OPENSSL} x509 -req -sha256 -days ${EXPIRATION_TIME} -in ${ca_csr_path} -out ${ca_crt_path} -signkey ${ca_key_path} -CAcreateserial -passin pass:${PASSWORD} || exit 1

    /bin/cp -f ${ca_key_path} ${CA_KEY_PATH}
    /bin/cp -f ${ca_crt_path} ${CA_CRT_PATH}
}

generate_server_ssl_and_keystore() {
    for (( index = 0; index < ${#ALIAS_NAME_LIST[*]}; ++index )); do
        if [[ ! -d "${KEYSTORE_PATH_LIST[${index}]%/*}" ]] ; then
            continue
        fi
        local password=${PASSWORD}
        local destination=${KEYSTORE_PATH_LIST[${index}]}
        local certificate_name=$(echo ${destination} | awk -F '.' '{print $1F}' | awk -F '/' '{print $NF}')
        local common_name=${COMMON_NAME_LIST[${index}]}
        local alias_name=${ALIAS_NAME_LIST[${index}]}
        local key_path="${TEMP_KEYSTORE_PATH}${certificate_name}.key"
        local csr_path="${TEMP_KEYSTORE_PATH}${certificate_name}.csr"
        local p12_path="${TEMP_KEYSTORE_PATH}${certificate_name}.p12"
        local keystore_path="${TEMP_KEYSTORE_PATH}${certificate_name}.keystore"
        local crt_path="${TEMP_KEYSTORE_PATH}${certificate_name}.crt"
        local subject_info="/C=CN/ST=SH/L=SH/O=OWN/OU=OWN/CN=${common_name}/emailAddress=email"

        ${OPENSSL} genrsa -${KEY_TYPE} -passout pass:${PASSWORD} -out ${key_path} ${KEY_LENGTH} || exit 1
        ${OPENSSL} req -new -key ${key_path} -out ${csr_path} -subj "${subject_info}" -passin pass:${PASSWORD} || exit 1
        ${OPENSSL} x509 -req -sha256 -days ${EXPIRATION_TIME} -in ${csr_path} -out ${crt_path} -signkey ${key_path} -CAkey ${CA_KEY_PATH} -CA ${CA_CRT_PATH} -CAcreateserial -passin pass:${PASSWORD} || exit 1
        ${OPENSSL} ${CERTIFICATE_TYPE} -export -clcerts -in ${crt_path} -inkey ${key_path} -out ${p12_path} -name ${alias_name} -passin pass:${PASSWORD} -password pass:${PASSWORD} || exit 1
        ${KEYTOOL} -importkeystore -trustcacerts -noprompt -deststoretype ${CERTIFICATE_TYPE} -srcstoretype ${CERTIFICATE_TYPE} -srckeystore ${p12_path} -destkeystore ${keystore_path} -alias ${alias_name} -deststorepass ${KEYSTORE_PASSWORD} -destkeypass ${KEYSTORE_PASSWORD} -srcstorepass ${PASSWORD} || exit 1

        /bin/cp -f ${keystore_path} ${destination}
    done
}

generate_postgres_ssl() {
    local certificate_name="server"
    local key_path="${TEMP_KEYSTORE_PATH}${certificate_name}.key"
    local csr_path="${TEMP_KEYSTORE_PATH}${certificate_name}.csr"
    local crt_path="${TEMP_KEYSTORE_PATH}${certificate_name}.crt"
    local der_path="${TEMP_KEYSTORE_PATH}${certificate_name}.crt.der"
    local subject_info="/C=CN/ST=SH/L=SH/O=OWN/OU=OWN/CN=CN/emailAddress=email"

    ${OPENSSL} genrsa -out ${key_path} ${KEY_LENGTH} || exit 1
    ${OPENSSL} req -new -key ${key_path} -out ${csr_path} -subj "${subject_info}" || exit 1
    ${OPENSSL} x509 -req -sha256 -days ${EXPIRATION_TIME} -in ${csr_path} -out ${crt_path} -signkey ${key_path} -CAkey ${CA_KEY_PATH} -CA ${CA_CRT_PATH} -CAcreateserial -passin pass:${PASSWORD} || exit 1
    cat ${CA_CRT_PATH} >> ${crt_path}
    ${OPENSSL} x509 -inform pem -outform der -in ${CA_CRT_PATH} -out ${der_path}

    for path in ${POSTGRES_SSL_LIST[@]} ; do
        if [[ ! -d "${path}" ]] ; then
            continue
        fi
        /bin/cp -f ${key_path} ${path}
        /bin/cp -f ${crt_path} ${path}
        /bin/cp -f ${der_path} ${path}
    done
}

generate_trust_ssl_and_keystore() {
    for (( index = 0; index < ${#TRUST_ALIAS_NAME_LIST[*]}; ++index )); do
        if [[ ! -d "${TRUST_KEYSTORE_PATH_LIST[${index}]%/*}" ]] ; then
            continue
        fi
        ${KEYTOOL} -import -trustcacerts -noprompt -alias ${TRUST_ALIAS_NAME_LIST[${index}]} -file ${CA_CRT_PATH} -keystore ${TRUST_KEYSTORE_PATH_LIST[${index}]} -storepass ${KEYSTORE_PASSWORD} || exit 1
    done
}

prepare() {
    if [[ ! -f ${CA_KEY_PATH} ]] || [[ ! -f ${CA_CRT_PATH} ]] ; then
        echo "CA files do not exist, Please run -ca"
        print_usage
        exit 1
    fi

    mkdir -p "${TEMP_KEYSTORE_PATH}"
    mkdir -p "${CA_KEY_PATH%/*}"

    for path in ${POSTGRES_SSL_LIST[@]} ; do
        if [[ ! -d "${path}" ]] ; then
            mkdir -p "${path}"
        fi
    done
    for path in ${KEYSTORE_PATH_LIST[@]} ; do
        if [[ ! -d "${path%/*}" ]] ; then
            mkdir -p "${path%/*}"
        fi
    done
    for path in ${TRUST_KEYSTORE_PATH_LIST[@]} ; do
        if [[ ! -d "${path%/*}" ]] ; then
            mkdir -p "${path%/*}"
        fi
    done
}

print_usage() {
    echo "Usage: $0 [-ca|-a|-s|-c|-p]"
    echo "-ca indicates generate ca root certificate"
    echo "-a indicates generate all defined keystore"
    echo "-s <keystore_name> <domain_name> <alias_name> indicates generate custom server keystore"
    echo "-c <keystore_name> <alias_name> indicates generate custom client keystore"
    echo "-p indicates generate postgres certificate"
}

generate_certificate() {

    if [[ $# -eq 1 ]] && [[ "${1}" == "-ca"  ]];then
        generate_ca_keystore
        exit 0
    fi

    prepare

    if [[ $# -eq 1 ]] && [[ "${1}" == "-p"  ]];then
        generate_postgres_ssl
    elif [[ $# -eq 1 ]] && [[ "${1}" == "-a"  ]];then
        generate_server_ssl_and_keystore
        generate_trust_ssl_and_keystore
        generate_postgres_ssl
    elif [[ $# -eq 3 ]] && [[ "${1}" == "-c"  ]];then
        TRUST_KEYSTORE_PATH_LIST=("${CURRENT_PATH}/${2}")
        TRUST_ALIAS_NAME_LIST=("${3}")
        generate_trust_ssl_and_keystore
    elif [[ $# -eq 4 ]] && [[ "${1}" == "-s"  ]];then
        KEYSTORE_PATH_LIST=("${CURRENT_PATH}/${2}")
        ALIAS_NAME_LIST=("${3}")
        COMMON_NAME_LIST=("${4}")
        generate_server_ssl_and_keystore
    else
        print_usage
    fi
}

generate_certificate "$@"
