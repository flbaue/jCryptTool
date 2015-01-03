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

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Florian Bauer on 02.01.15.
 */
public class EncryptionRunnable implements Runnable {

    private EncryptionSettings encryptionSettings;
    private Progress progress;

    public EncryptionRunnable(EncryptionSettings encryptionSettings, Progress progress) {
        this.encryptionSettings = encryptionSettings;
        this.progress = progress;
    }

    @Override
    public void run() {
        final byte[] key = generateKey(encryptionSettings.password);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        final Cipher cipher;
        final byte[] startVector;

        try (OutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(encryptionSettings.outputFile))) {

            try {
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                startVector = initStartVector(cipher.getBlockSize(), fileOutputStream);
                final IvParameterSpec ivParameterSpec = new IvParameterSpec(startVector);
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }


            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(new FileInputStream(encryptionSettings.inputFile));

                out = new GZIPOutputStream(new CipherOutputStream(fileOutputStream, cipher));

                processStreams(in, out, cipher.getBlockSize());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                closeStream(in);
                closeStream(out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] initStartVector(int blockSize, OutputStream fileOutputStream) throws NoSuchAlgorithmException, IOException {
        byte[] vector = new byte[blockSize];
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.nextBytes(vector);

        fileOutputStream.write(vector);
        return vector;
    }

    private void processStreams(InputStream in, OutputStream out, int bufferSize) throws IOException {
        long totalBytesTpProcess = encryptionSettings.inputFile.length();
        long bytesProcessed = 0;

        byte[] buffer = new byte[bufferSize];
        int bytes;
        while ((bytes = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);

            bytesProcessed += bytes;
            progress.updateProgress((int) ((100.0 * bytesProcessed) / totalBytesTpProcess));
        }
        progress.setFinished();
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

    private void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
