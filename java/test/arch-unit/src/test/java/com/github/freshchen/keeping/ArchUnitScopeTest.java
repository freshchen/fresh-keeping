package com.github.freshchen.keeping;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeJars;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

@AnalyzeClasses(packages = {"com.github.freshchen.keeping"},
    // 跳过依赖jar包，跳过测试文件
    importOptions = {DoNotIncludeJars.class, DoNotIncludeTests.class})
class ArchUnitScopeTest {

    /**
     * service 目录只能被 controller 目录和 service 目录访问
     *
     * @param javaClasses
     */
    @ArchTest
    public void service(JavaClasses javaClasses) {
    }

}
