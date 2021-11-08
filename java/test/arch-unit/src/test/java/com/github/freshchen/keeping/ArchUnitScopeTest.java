package com.github.freshchen.keeping;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeJars;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.DisplayName;

@AnalyzeClasses(packages = {"com.github.freshchen.keeping"},
    // 跳过依赖jar包，跳过测试文件
    importOptions = {DoNotIncludeJars.class, DoNotIncludeTests.class})
class ArchUnitScopeTest {

    @ArchTest
    @DisplayName("controller 只能被 controller 访问")
    public void test1(JavaClasses javaClasses) {
        ArchRuleDefinition.classes()
            .that()
            .resideInAPackage("com.github.freshchen.keeping.controller..")
            .should()
            .onlyBeAccessed().byAnyPackage("com.github.freshchen.keeping.controller..")
            .check(javaClasses);
    }

    @ArchTest
    @DisplayName("application 只能被 application controller 访问")
    public void test2(JavaClasses javaClasses) {
        ArchRuleDefinition.classes()
            .that()
            .resideInAPackage("com.github.freshchen.keeping.application..")
            .should()
            .onlyBeAccessed()
            .byAnyPackage(
                "com.github.freshchen.keeping.application..",
                "com.github.freshchen.keeping.controller.."
            )
            .check(javaClasses);
    }

    @ArchTest
    @DisplayName("domain 只能被 domain application 访问")
    public void test3(JavaClasses javaClasses) {
        ArchRuleDefinition.classes()
            .that()
            .resideInAPackage("com.github.freshchen.keeping.domain..")
            .should()
            .onlyBeAccessed()
            .byAnyPackage(
                "com.github.freshchen.keeping.application..",
                "com.github.freshchen.keeping.domain.."
            )
            .check(javaClasses);
    }

    @ArchTest
    @DisplayName("infrastructure 只能被 domain application infrastructure 访问")
    public void test4(JavaClasses javaClasses) {
        ArchRuleDefinition.classes()
            .that()
            .resideInAPackage("com.github.freshchen.keeping.domain..")
            .should()
            .onlyBeAccessed()
            .byAnyPackage(
                "com.github.freshchen.keeping.application..",
                "com.github.freshchen.keeping.domain..",
                "com.github.freshchen.keeping.infrastructure.."
            )
            .check(javaClasses);
    }

}
