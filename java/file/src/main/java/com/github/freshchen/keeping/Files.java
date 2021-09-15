package com.github.freshchen.keeping;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author darcy
 * @since 2021/9/9
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Files {

    public static Collection<File> findFilesByDirectoryAndSuffix(String directory, String suffix) {
        Objects.requireNonNull(directory);
        Objects.requireNonNull(suffix);
        File dir = new File(directory);
        IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(suffix);
        return FileUtils.listFiles(dir, fileFilter, DirectoryFileFilter.INSTANCE);
    }

    public static boolean modifyFileContent(File file, Function<String, String> lineReplaceFunction) {
        Objects.requireNonNull(file);
        Objects.requireNonNull(lineReplaceFunction);
        try {
            List<String> lines = FileUtils.readLines(file, "utf-8").stream()
                .map(lineReplaceFunction)
                .collect(Collectors.toList());
            FileUtils.writeLines(file, "utf-8", lines, false);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
