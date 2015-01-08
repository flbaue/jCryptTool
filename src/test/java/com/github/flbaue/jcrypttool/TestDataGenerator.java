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

package com.github.flbaue.jcrypttool;

import java.io.*;
import java.util.Random;

/**
 * Created by Florian Bauer on 05.01.15.
 */
public class TestDataGenerator {

    public static File generateFile(long sizeInByte) {
        File file = new File(System.getProperty("java.io.tmpdir") + "/testFile.data");
        file.delete();

        Random random = new Random();
        byte[] buffer = new byte[1];

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            for (long i = 0; i < sizeInByte; i++) {
                random.nextBytes(buffer);
                out.write(buffer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }
}
