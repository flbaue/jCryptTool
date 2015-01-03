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
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

/**
 * Created by Florian Bauer on 02.01.15.
 */
public class DecryptionRunnable implements Runnable {

    private final EncryptionSettings encryptionSettings;
    private final Progress progress;

    public DecryptionRunnable(final EncryptionSettings encryptionSettings, final Progress progress) {
        this.encryptionSettings = encryptionSettings;
        this.progress = progress;
    }

    @Override
    public void run() {
        byte[] key = generateKey(encryptionSettings.password);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher;

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[cipher.getBlockSize()]);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new GZIPInputStream(new CipherInputStream(new FileInputStream(encryptionSettings.inputFile), cipher)));
            out = new BufferedOutputStream(new FileOutputStream(encryptionSettings.outputFile));

            processStreams(in, out, cipher.getBlockSize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }

    private void processStreams(InputStream in, OutputStream out, int bufferSize) throws IOException {
        long totalBytesToProcess = encryptionSettings.inputFile.length();
        long bytesProcessed = 0;

        byte[] buffer = new byte[bufferSize];
        int bytes;
        while ((bytes = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);

            bytesProcessed += bytes;
            progress.updateProgress((int) ((100.0 * bytesProcessed) / totalBytesToProcess));
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
