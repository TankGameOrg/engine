package pro.trevor.tankgame.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionUtil {

    public static List<Class<?>> allClassesAnnotatedWith(Class<? extends Annotation> annotation, String packageName) {
        List<Class<?>> classes = allClassesInPackage(packageName);
        List<Class<?>> output = new ArrayList<>();

        for (Class<?> c : classes) {
            if (c.isAnnotationPresent(annotation)) {
                output.add(c);
            }
        }

        return output;
    }

    public static List<Class<?>> allClassesInPackage(String packageName) {
        List<Class<?>> classes = new ArrayList<>();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String pathToPackage = packageName.replace('.', '/');
        Enumeration<URL> urls = Collections.enumeration(new ArrayList<>());

        try {
            urls = classLoader.getResources(pathToPackage);
        } catch (IOException e) {
            System.err.println("Invalid path to package: " + pathToPackage);
        }

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            try {
                URLConnection connection = url.openConnection();
                if (connection instanceof JarURLConnection jarConnection) {
                    classes.addAll(allClassesInJar(jarConnection, packageName));
                } else {
                    // Assume we are looking at a file
                    try {
                        classes.addAll(allClassesInFile(new File(url.toURI()), packageName));
                    } catch (URISyntaxException e) {
                        System.err.println("Invalid URL: " + url);
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to open URL: " + url);
            }
        }

        return classes;
    }

    private static List<Class<?>> allClassesInFile(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(allClassesInFile(file, packageName + '.' + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - ".class".length());
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    System.err.println("Could not load class " + className);
                }
            }
        }
        return classes;
    }

    private static List<Class<?>> allClassesInJar(JarURLConnection connection, String packageName) throws IOException {
        List<Class<?>> classes = new ArrayList<>();

        JarFile jar = connection.getJarFile();
        Enumeration<JarEntry> entries = jar.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (name.endsWith(".class")) {
                String className = name.substring(0, name.length() - ".class".length()).replace('/', '.');
                if (className.contains(packageName)) {
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        System.err.println("Could not load class " + className);
                    }
                }
            }
        }

        return classes;
    }

}
