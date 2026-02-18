package dev.oblivion.client.addon;

import dev.oblivion.client.OblivionClient;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Compiles Java source files from addon directories at runtime using javax.tools.
 * Requires a JDK (not just JRE) for the Java compiler to be available.
 */
public class AddonCompiler {

    /**
     * Compiles all .java files in the given addon directory.
     * Returns a URLClassLoader for the compiled classes, or null if no source files found.
     */
    public URLClassLoader compile(Path addonDir) throws AddonException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new AddonException("No Java compiler available. A JDK (not JRE) is required for Java addons.");
        }

        List<File> sourceFiles;
        try (var walk = Files.walk(addonDir)) {
            sourceFiles = walk
                .filter(p -> p.toString().endsWith(".java"))
                .map(Path::toFile)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new AddonException("Failed to scan addon directory: " + e.getMessage());
        }

        if (sourceFiles.isEmpty()) return null;

        Path outputDir = addonDir.resolve("classes");
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            throw new AddonException("Failed to create output directory: " + e.getMessage());
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            List<String> options = List.of(
                "-d", outputDir.toString(),
                "-classpath", System.getProperty("java.class.path")
            );

            Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles(sourceFiles);

            boolean success = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits).call();

            if (!success) {
                StringBuilder errors = new StringBuilder("Compilation failed:\n");
                for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
                    if (d.getKind() == Diagnostic.Kind.ERROR) {
                        errors.append("  Line ").append(d.getLineNumber())
                              .append(": ").append(d.getMessage(null)).append("\n");
                    }
                }
                throw new AddonException(errors.toString());
            }

            return new URLClassLoader(
                new URL[]{ outputDir.toUri().toURL() },
                AddonCompiler.class.getClassLoader()
            );
        } catch (IOException e) {
            throw new AddonException("Compiler I/O error: " + e.getMessage());
        }
    }
}
