#!/bin/bash

OLD_PASSWORD="123456"


input_old_password(){
	read -t 10 -p "Please input old password: " -s password
	echo ""
	return 0
}

check_password(){
	if [[ ! "${password}" == "${OLD_PASSWORD}" ]]; then
		echo "The password is not right !"
		exit 1
	fi
	return 0
}

input_new_password(){
	while [[ "X" == "X" ]]; do
        read -p "Please enter new keystore password: " -s password
        echo ""
        if [[ "X${password}" == "X${OLD_PASSWORD}" ]];then
            echo "New password must differ from original password,please try again."
            continue
        fi
        read -p "Please re-enter new keystore password: " -s confirmed_password
        echo ""
        if [[ "X${password}" == "X${confirmed_password}" ]];then
            NEW_PASSWORD=${password}
            break
        fi
        echo "The two new passwords are not same,please try again."
    done
}

main(){
	input_old_password
	check_password
	input_new_password
}

main
