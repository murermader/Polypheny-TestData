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

import static org.example.Main.logger;

public class FileContentReader {
    public static List<String> readFilesFromResources(String globPath) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String resourcesDir = dotenv.get("RESOURCES_DIR");
        if (resourcesDir == null || resourcesDir.isEmpty()) {
            throw new IllegalArgumentException("RESOURCES_DIR not defined in .env file");
        }

        logger.info("Resources directory: " + resourcesDir);
        logger.info("Glob pattern: " + globPath);

        List<String> fileContents = new ArrayList<>();
        Path resourceDirPath = Path.of(resourcesDir);

        if (!Files.exists(resourceDirPath)) {
            throw new IllegalArgumentException("Resources directory does not exist: " + resourcesDir);
        }

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + resourcesDir + "/" + globPath);

        try (var paths = Files.walk(resourceDirPath)) {
            paths.forEach(path -> {
                logger.info("Checking path: " + path);
                if (matcher.matches(path)) {
                    logger.info("Matched file: " + path);
                    try (BufferedReader reader = Files.newBufferedReader(path)) {
                        String content = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                        fileContents.add(content);
                    } catch (IOException e) {
                        logger.info("Error reading file: " + path);
                        e.printStackTrace();
                    }
                } else {
                    logger.info("No match for file: " + path);
                }
            });
        }

        if (fileContents.isEmpty()) {
            logger.info("No files matched the glob pattern.");
        }

        return fileContents;
    }

    public static List<String> readFilesFromResourcesByList(List<String> relativePaths) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String resourcesDir = dotenv.get("RESOURCES_DIR");
        if (resourcesDir == null || resourcesDir.isEmpty()) {
            throw new IllegalArgumentException("RESOURCES_DIR not defined in .env file");
        }

        logger.info("Resources directory: " + resourcesDir);

        List<String> fileContents = new ArrayList<>();
        Path resourceDirPath = Path.of(resourcesDir);

        if (!Files.exists(resourceDirPath)) {
            throw new IllegalArgumentException("Resources directory does not exist: " + resourcesDir);
        }

        relativePaths.forEach(relativePath -> {
            Path fullPath = resourceDirPath.resolve(relativePath);
            logger.info("Full path: " + fullPath);
            try (BufferedReader reader = Files.newBufferedReader(fullPath)) {
                String content = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                fileContents.add(content);
            } catch (IOException e) {
                logger.info("Error reading file: " + fullPath);
                throw new RuntimeException("Error reading file: " + fullPath);
            }
        });

        if (fileContents.isEmpty()) {
            throw new RuntimeException("No files found for the provided relative paths.");
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
