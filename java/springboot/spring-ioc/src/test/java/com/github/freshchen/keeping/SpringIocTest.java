package com.github.freshchen.keeping;

import com.github.freshchen.keeping.di.Doctor;
import com.github.freshchen.keeping.di.Patient;
import com.github.freshchen.keeping.di.User;
import com.github.freshchen.keeping.life.ApplicationContextUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public class SpringIocTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void test() {
        User user = context.getBean(User.class);
        user.getIntro().intro();

        Doctor doctor = context.getBean(Doctor.class);
        doctor.getUser().getIntro().intro();

        Patient patient = context.getBean(Patient.class);
        patient.getUser().getIntro().intro();

        ApplicationContextUtils.hello();
    }

}
