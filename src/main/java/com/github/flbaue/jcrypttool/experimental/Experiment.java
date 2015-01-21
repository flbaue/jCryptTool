/*
 * Copyright 2015 Florian Bauer, florian.bauer@posteo.de
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.flbaue.jcrypttool.experimental;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Florian Bauer on 14.01.15.
 */
public class Experiment {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        Experiment experiment = new Experiment();
        experiment.zip();
        experiment.add();
        experiment.unzip();
    }

    private void add() {

    }

    private void unzip() {

    }

    private void zip() throws IOException {

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream("./testzip.zip"))) {
            Path path = Paths.get(new URI("file:///Users/florian/Beispiel"));
            Files.walkFileTree(path, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    System.out.println("preVisitDirectory: " + path.relativize(dir).toString() + "/");
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println("visitFile: " + path.relativize(file).toString());
                    ZipEntry entry = new ZipEntry(path.relativize(file).toString());
                    entry.setMethod(ZipEntry.DEFLATED);
                    out.putNextEntry(entry);

                    Files.copy(file, out);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    System.out.println("visitFileFailed: " + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    System.out.println("postVisitDirectory: " + dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
