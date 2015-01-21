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

package com.github.flbaue.jcrypttool.v2.domain;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class AesEncryptionServiceTest {

    private AesEncryptionService aesEncryptionService = new AesEncryptionService();

    @Test
    public void testEncryptOutputStream() throws Exception {
        Path path = Paths.get("./testEncryptOutputStream.tmp");

        try (OutputStream out = aesEncryptionService.encryptedOutputStream(path, "password ! 12345 ? äöüß ,€")) {
            out.write("Test ! 12345 ? äöüß ,€".getBytes());
            out.flush();
        }

        try (InputStream in = new BufferedInputStream(new FileInputStream(path.toFile()))) {

            byte[] buffer = new byte[1024];
            int length = in.read(buffer);
            assertTrue(length > 32);
        }

        Files.delete(path);
    }

    @Test
    public void testEncryptString() throws Exception {
        String result = aesEncryptionService.encryptString("Test ! 12345 ? äöüß ,€", "password ! 12345 ? äöüß ,€");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.length() > 32);
    }
}