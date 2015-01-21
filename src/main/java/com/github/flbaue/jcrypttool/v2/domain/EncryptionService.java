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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Created by Florian Bauer on 21.01.15.
 */
public interface EncryptionService {

    OutputStream encryptedOutputStream(final Path path, final String password) throws IOException, EncryptionFailedException;

    String encryptString(final String string, final String password) throws EncryptionFailedException;

    InputStream decryptedInputStream(final Path path, final String password) throws IOException, DecryptionFailedException;

    String decryptString(final String string, final String password) throws DecryptionFailedException;
}
