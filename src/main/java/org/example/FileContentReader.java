package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileContentReader {
    public static List<String> readFilesFromResources(String globPath) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String resourcesDir = dotenv.get("RESOURCES_DIR");
        if (resourcesDir == null || resourcesDir.isEmpty()) {
            throw new IllegalArgumentException("RESOURCES_DIR not defined in .env file");
        }

        System.out.println("Resources directory: " + resourcesDir);
        System.out.println("Glob pattern: " + globPath);

        List<String> fileContents = new ArrayList<>();
        Path resourceDirPath = Path.of(resourcesDir);

        if (!Files.exists(resourceDirPath)) {
            throw new IllegalArgumentException("Resources directory does not exist: " + resourcesDir);
        }

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + resourcesDir + "/" + globPath);

        try (var paths = Files.walk(resourceDirPath)) {
            paths.forEach(path -> {
                System.out.println("Checking path: " + path);
                if (matcher.matches(path)) {
                    System.out.println("Matched file: " + path);
                    try (BufferedReader reader = Files.newBufferedReader(path)) {
                        String content = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                        fileContents.add(content);
                    } catch (IOException e) {
                        System.err.println("Error reading file: " + path);
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("No match for file: " + path);
                }
            });
        }

        if (fileContents.isEmpty()) {
            System.out.println("No files matched the glob pattern.");
        }

        return fileContents;
    }

    public static String readFileFromResources(String filePath) throws IOException {
        ClassLoader classLoader = FileContentReader.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("File not found: " + filePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }
}
