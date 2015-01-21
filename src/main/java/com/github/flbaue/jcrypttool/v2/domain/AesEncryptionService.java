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

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Florian Bauer on 19.01.15.
 */
public class AesEncryptionService implements EncryptionService {
    public static final int KEY_LENGTH = 256; //in bit
    public static final int KEY_ITERATIONS = 100000;
    public static final int BLOCK_LENGTH = 16; //in byte
    public static final int SALT_LENGTH = 16; //in byte
    public static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    public static final String SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";

    @Override
    public OutputStream encryptedOutputStream(final Path path, final String password) throws IOException,
            EncryptionFailedException {
        try {
            final byte[] salt = generateSalt();
            final byte[] key = generateKey(password, salt);
            final byte[] iv = generateIV();
            final byte[] fileInitBlock = generateOutputInitBlock(salt, iv);

            final PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new AESEngine()), new PKCS7Padding());

            final KeyParameter keyParam = new KeyParameter(key);
            final CipherParameters params = new ParametersWithIV(keyParam, iv);
            cipher.init(true, params);

            final BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(path));
            out.write(fileInitBlock);

            return new CipherOutputStream(out, cipher);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new EncryptionFailedException(e);
        }
    }

    @Override
    public String encryptString(final String string, final String password) throws EncryptionFailedException {
        try {
            final byte[] salt = generateSalt();
            final byte[] key = generateKey(password, salt);
            final byte[] iv = generateIV();
            final byte[] outputInitBlock = generateOutputInitBlock(salt, iv);

            final PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new AESEngine()), new PKCS7Padding());

            final KeyParameter keyParam = new KeyParameter(key);
            final CipherParameters params = new ParametersWithIV(keyParam, iv);
            cipher.init(true, params);

            final byte in[] = string.getBytes();
            final byte out[] = new byte[cipher.getOutputSize(in.length)];
            final int len1 = cipher.processBytes(in, 0, in.length, out, 0);

            cipher.doFinal(out, len1);

            final byte[] result = Arrays.concatenate(outputInitBlock, out);

            return Base64.toBase64String(result);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidCipherTextException e) {
            throw new EncryptionFailedException(e);
        }
    }

    @Override
    public InputStream decryptedInputStream(final Path path, final String password) throws IOException,
            DecryptionFailedException {
        try {
            InputStream in = new BufferedInputStream(Files.newInputStream(path));
            byte[] initBlock = readInitBlock(in);
            byte[] salt = extractSalt(initBlock);
            byte[] iv = extractIV(initBlock);
            byte[] key = generateKey(password, salt);

            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()),
                    new PKCS7Padding());
            KeyParameter keyParam = new KeyParameter(key);
            CipherParameters params = new ParametersWithIV(keyParam, iv);
            cipher.init(false, params);

            return new CipherInputStream(in, cipher);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new DecryptionFailedException(e);
        }
    }

    @Override
    public String decryptString(final String string, final String password) throws DecryptionFailedException {
        try {
            byte[] inputBytes = Base64.decode(string);
            byte[] salt = extractSalt(inputBytes);
            byte[] iv = extractIV(inputBytes);
            byte[] key = generateKey(password, salt);
            byte[] encryptedData = extractEncryptedData(inputBytes);

            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()),
                    new PKCS7Padding());
            KeyParameter keyParam = new KeyParameter(key);
            CipherParameters params = new ParametersWithIV(keyParam, iv);
            cipher.init(false, params);

            final byte out[] = new byte[cipher.getOutputSize(encryptedData.length)];
            int length = cipher.processBytes(encryptedData, 0, encryptedData.length, out, 0);
            byte[] result;

            length += cipher.doFinal(out, length);
            result = new byte[length];
            System.arraycopy(out, 0, result, 0, length);

            return new String(result);

        } catch (InvalidCipherTextException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new DecryptionFailedException(e);
        }
    }


    //////////////////////////////////////////////
    /// Decryption helpers
    /////////////////////////////////////////////

    private byte[] readInitBlock(InputStream in) throws IOException {
        byte[] initBlock = new byte[SALT_LENGTH + BLOCK_LENGTH];
        int length = in.read(initBlock);
        if (length != initBlock.length) {
            throw new IOException("Cannot read from file");
        }
        return initBlock;
    }

    private byte[] extractEncryptedData(byte[] inputBytes) {
        int offset = SALT_LENGTH + BLOCK_LENGTH;
        byte[] data = new byte[inputBytes.length - offset];
        System.arraycopy(inputBytes, offset, data, 0, data.length);
        return data;
    }

    private byte[] extractSalt(byte[] bytes) {
        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(bytes, 0, salt, 0, SALT_LENGTH);
        return salt;
    }

    private byte[] extractIV(byte[] bytes) {
        byte[] iv = new byte[BLOCK_LENGTH];
        System.arraycopy(bytes, SALT_LENGTH, iv, 0, BLOCK_LENGTH);
        return iv;
    }


    //////////////////////////////////////////////
    /// Enrcyption helpers
    /////////////////////////////////////////////

    private byte[] generateOutputInitBlock(byte[] salt, byte[] iv) {
        return Arrays.concatenate(salt, iv);
    }

    private byte[] generateSalt() throws NoSuchAlgorithmException {
        final SecureRandom sr = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        final byte[] salt = new byte[SALT_LENGTH];
        sr.nextBytes(salt);
        return salt;
    }

    private byte[] generateKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final char[] passwordChars = password.toCharArray();
        final PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, KEY_ITERATIONS, KEY_LENGTH);
        final SecretKeyFactory skf = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    private byte[] generateIV() throws NoSuchAlgorithmException {
        final byte[] vector = new byte[BLOCK_LENGTH];
        final SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        secureRandom.nextBytes(vector);
        return vector;
    }
}
