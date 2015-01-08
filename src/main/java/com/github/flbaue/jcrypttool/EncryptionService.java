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

/**
 * Created by Florian Bauer on 02.01.15.
 */
public class EncryptionService {

    public static int KEY_LENGTH = 256; // in bit
    public static int BLOCK_LENGTH = 16; // in byte
    public static int SALT_LENGTH = 16; // in byte
    public static int KEY_ITERATIONS = 20000;
    public static int STREAM_BUFFER_LENGTH = 512; // in byte

    public Progress encrypt(EncryptionSettings encryptionSettings) {
        Progress progress = new Progress();
        EncryptionRunnable encryptionRunnable = new EncryptionRunnable(encryptionSettings, progress);
        Thread encryptionThread = new Thread(encryptionRunnable);
        encryptionThread.start();
        return progress;
    }

    public Progress decrypt(EncryptionSettings encryptionSettings) {
        Progress progress = new Progress();
        DecryptionRunnable decryptionRunnable = new DecryptionRunnable(encryptionSettings, progress);
        Thread decryptionThread = new Thread(decryptionRunnable);
        decryptionThread.start();
        return progress;
    }
}
