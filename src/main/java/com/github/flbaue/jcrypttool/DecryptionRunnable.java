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

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
        PaddedBufferedBlockCipher cipher;
        byte[] key;
        byte[] iv;
        byte[] salt;

        try (InputStream fileInputStream = new BufferedInputStream(new FileInputStream(encryptionSettings.inputFile))) {

            cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
            salt = extractSalt(fileInputStream);
            iv = extractIV(fileInputStream);
            key = generateKey(encryptionSettings.password, salt);
            KeyParameter keyParam = new KeyParameter(key);
            CipherParameters params = new ParametersWithIV(keyParam, iv);
            cipher.init(false, params);

            /*
            System.out.println(getClass().getName() + " salt:\t" + Base64.toBase64String(salt) + " (" + salt.length + " byte)");
            System.out.println(getClass().getName() + " key:\t" + Base64.toBase64String(key) + " (" + key.length + " byte)");
            System.out.println(getClass().getName() + " iv:\t\t" + Base64.toBase64String(iv) + " (" + iv.length + " byte)");
            */

            InputStream in = null;
            OutputStream out = null;
            try {
                in = new GZIPInputStream(new CipherInputStream(fileInputStream, cipher));
                out = new BufferedOutputStream(new FileOutputStream(encryptionSettings.outputFile));
                processStreams(in, out);
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

    private byte[] extractSalt(InputStream fileInputStream) throws IOException {
        byte[] salt = new byte[EncryptionService.SALT_LENGTH];
        fileInputStream.read(salt);
        return salt;
    }

    private byte[] extractIV(InputStream fileInputStream) throws IOException {
        byte[] iv = new byte[EncryptionService.BLOCK_LENGTH];
        fileInputStream.read(iv);
        return iv;
    }

    private void processStreams(InputStream in, OutputStream out) throws IOException {
        long totalBytesToProcess = encryptionSettings.inputFile.length();
        long bytesProcessed = 0;

        byte[] buffer = new byte[EncryptionService.STREAM_BUFFER_LENGTH];
        int bytes;
        while ((bytes = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);

            bytesProcessed += bytes;
            progress.updateProgress((int) ((100.0 * bytesProcessed) / totalBytesToProcess));
        }
        progress.setFinished();
    }

    private byte[] generateKey(String password, byte[] salt) {
        char[] passwordChars = password.toCharArray();
        final PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, EncryptionService.KEY_ITERATIONS, EncryptionService.KEY_LENGTH);
        try {
            final SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
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
