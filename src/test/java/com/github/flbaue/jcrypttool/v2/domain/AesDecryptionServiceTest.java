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

import static org.junit.Assert.assertEquals;

public class AesDecryptionServiceTest {

    private AesEncryptionService aesEncryptionService = new AesEncryptionService();

    @Test
    public void testDecryptInputStream() throws Exception {

    }

    @Test
    public void testDecryptString() throws Exception {

        final String text = "Test ! 12345 ? öäüß ,€";
        final String password = "password";

        String encryptedText = aesEncryptionService.encryptString(text, password);
        String decryptedText = aesEncryptionService.decryptString(encryptedText, password);

        assertEquals(text, decryptedText);
    }
}