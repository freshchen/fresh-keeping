package com.github.freshchen.keeping.service;

import org.springframework.stereotype.Service;

/**
 * @author darcy
 * @since 2021/1/27
 **/
@Service
public class RemoteUserService implements UserService {

    @Override
    public String getName() {
        try {
            return "hello";
        } finally {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("RemoteUserService finally getName");
        }
    }

    @Override
    public String getNameWithError() {
        try {
            int i = 0 / 0;
        } finally {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("RemoteUserService finally getNameWithError");
        }

        return "hello";
    }
}
