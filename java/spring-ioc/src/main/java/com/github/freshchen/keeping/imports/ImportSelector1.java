package com.github.freshchen.keeping.imports;

import com.github.freshchen.keeping.imports.selector.EnableDataConfig;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author darcy
 * @since 2021/2/5
 **/
public class ImportSelector1 implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata
                .getAnnotationAttributes(EnableDataConfig.class.getName()));
        return new String[0];
    }
}
