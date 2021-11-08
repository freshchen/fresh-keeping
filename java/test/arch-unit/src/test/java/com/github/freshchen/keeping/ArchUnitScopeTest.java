package com.github.freshchen.keeping;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(packages = {"com.github.freshchen.keeping"},
        // 跳过依赖jar包，跳过测试文件
        importOptions = {ImportOption.DoNotIncludeJars, ImportOption.DoNotIncludeTests})
class ArchUnitScopeTest {



    /**
     * service 目录只能被 controller 目录和 service 目录访问
     *
     * @param javaClasses
     */
    @ArchTest
    public void service(JavaClasses javaClasses) {
        ArchRuleDefinition.classes()
                .that()
                .areAnnotatedWith(RestController.class)
                .and()
                .areAnnotatedWith(Controller.class)
                .should()
                .check(javaClasses);
    }

}
