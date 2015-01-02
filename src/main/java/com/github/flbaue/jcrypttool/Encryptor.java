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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Florian Bauer on 02.01.15.
 */
public class Encryptor {
    public void run(String inputFileName, String outputFileName, String password) {
        byte[] key = generateKey(password);
        byte[] inputFileBytes = readFile(inputFileName);
        byte[] encryptedFileContent = encrypt(inputFileBytes, key);
        writeFile(encryptedFileContent, outputFileName);
    }

    private void writeFile(byte[] encryptedFileContent, String outputFileName) {
        File file = new File(outputFileName);
        file.delete();
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            out.write(encryptedFileContent);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write output file", e);
        }
    }

    private byte[] encrypt(byte[] inputFileBytes, byte[] key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(inputFileBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readFile(String inputFileName) {
        File file = new File(inputFileName);
        byte[] fileContent = new byte[(int) file.length()];
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            in.read(fileContent);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read input file", e);
        }
        return fileContent;
    }

    private byte[] generateKey(String password) {
        try {
            byte[] passwordBytes = password.getBytes("UTF-8");
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            byte[] passwordHash = messageDigest.digest(passwordBytes);
            return Arrays.copyOf(passwordHash, 16);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
