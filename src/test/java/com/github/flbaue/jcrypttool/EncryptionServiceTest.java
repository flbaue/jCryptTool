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

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EncryptionServiceTest {

    @Test
    public void testEncrypt() throws Exception {
        EncryptionSettings encryptionSettings = new EncryptionSettings();
        encryptionSettings.inputFile = new File(getClass().getResource("/testIn.txt").getFile());
        encryptionSettings.outputFile = new File("testOut.txt");
        encryptionSettings.password = "test12345";

        encryptionSettings.outputFile.delete();

        EncryptionService encryptionService = new EncryptionService();
        Progress progress = encryptionService.encrypt(encryptionSettings);

        while (progress.isFinished() != true) {
            Thread.sleep(500);
        }

        assertTrue(encryptionSettings.outputFile.exists());
        assertTrue(encryptionSettings.outputFile.length() > 0);
    }

    @Test
    public void testDecrypt() throws Exception {
        EncryptionSettings encryptionSettings = new EncryptionSettings();
        encryptionSettings.inputFile = new File(getClass().getResource("/testIn.data").getFile());
        encryptionSettings.outputFile = new File("testOut.data");
        encryptionSettings.password = "password";

        encryptionSettings.outputFile.delete();

        EncryptionService encryptionService = new EncryptionService();
        Progress progress = encryptionService.decrypt(encryptionSettings);

        while (progress.isFinished() != true) {
            Thread.sleep(500);
        }

        assertTrue(encryptionSettings.outputFile.exists());
        assertTrue(encryptionSettings.outputFile.length() > 0);
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        EncryptionSettings encryptionSettings = new EncryptionSettings();
        encryptionSettings.inputFile = new File(getClass().getResource("/testIn.txt").getFile());
        encryptionSettings.outputFile = new File("testOut2.data");
        encryptionSettings.password = "password";

        encryptionSettings.outputFile.delete();

        EncryptionService encryptionService = new EncryptionService();
        Progress progress = encryptionService.encrypt(encryptionSettings);

        while (progress.isFinished() != true) {
            Thread.sleep(500);
        }

        assertTrue(encryptionSettings.outputFile.exists());
        assertTrue(encryptionSettings.outputFile.length() > 0);

        EncryptionSettings encryptionSettings2 = new EncryptionSettings();
        encryptionSettings2.inputFile = encryptionSettings.outputFile;
        encryptionSettings2.outputFile = new File("testOut3.data");
        encryptionSettings2.password = "password";

        encryptionSettings2.outputFile.delete();

        Progress progress2 = encryptionService.decrypt(encryptionSettings2);

        while (progress2.isFinished() != true) {
            Thread.sleep(500);
        }

        assertTrue(encryptionSettings2.outputFile.exists());
        assertTrue(encryptionSettings2.outputFile.length() > 0);

        assertEquals(encryptionSettings.inputFile.length(), encryptionSettings2.outputFile.length());
    }


    public void testSpeed() throws Exception {
        long dataSize = 10485760 * 10;
        File file = TestDataGenerator.generateFile(dataSize);
        EncryptionService encryptionService = new EncryptionService();
        EncryptionSettings encryptionSettings = new EncryptionSettings();

        int iterations = 5;
        long[] runtimes = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            encryptionSettings.inputFile = file;
            encryptionSettings.outputFile = new File("testSpeed.data");
            encryptionSettings.password = "test12345";
            Progress progress = encryptionService.encrypt(encryptionSettings);

            while (!progress.isFinished()) {
                Thread.sleep(500);
            }
            runtimes[i] = progress.getRuntime();
        }

        System.out.println("Buffer: " + EncryptionService.STREAM_BUFFER_LENGTH + " Byte");
        System.out.println("Data: " + dataSize / 1024 / 1024 + " MByte");
        System.out.println("Avg Runtime: " + avg(runtimes) + " msec");

    }

    private long avg(long[] runtimes) {
        long runtime = 0;
        for (long r : runtimes) {
            runtime += r;
        }
        return runtime / runtimes.length;
    }
}