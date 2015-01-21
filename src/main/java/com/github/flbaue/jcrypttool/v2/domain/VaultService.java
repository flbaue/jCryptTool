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

import com.github.flbaue.jcrypttool.v2.domain.model.Vault;
import com.github.flbaue.jcrypttool.v2.domain.model.VaultEntry;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Florian Bauer on 19.01.15.
 */
public class VaultService {

    private EncryptionService encryptionService;

    public VaultService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public Vault createVault(final Path path) throws IOException {

        //validations
        Objects.requireNonNull(path, "vault path must not be null");
        Files.deleteIfExists(path);

        //create empty file
        try (OutputStream out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))) {
            out.flush();
        }

        //return empty vault
        return new Vault(path);
    }

    public Vault openVault(final Path path, final String password) throws IOException {

        //validations
        Objects.requireNonNull(path, "vault path must not be null");

        //open zip file
        ZipInputStream in = new ZipInputStream(new BufferedInputStream(Files.newInputStream(path)));
        ZipFile zipFile = new ZipFile(path.toFile());

        //iterate over zip file entries and convert them to vault entries
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        Vault vault = new Vault(path);
        while (entries.hasMoreElements()) {
            try {
                ZipEntry zipEntry = entries.nextElement();
                VaultEntry vaultEntry = new VaultEntry();
                vaultEntry.setName(encryptionService.decryptString(zipEntry.getName(), password));
                vaultEntry.setLastModifiedTime(zipEntry.getLastModifiedTime().toInstant());
                vaultEntry.setSize(zipEntry.getSize());
                vault.addEntry(vaultEntry);
            } catch (DecryptionFailedException e) {
                //TODO what if a filename cannot be decrypted?
            }
        }

        //return vault
        return vault;
    }

    public void addFileToVault(final Vault vault, final Path externalFilePath,
                               final Path internalFilePath, final String password) throws IOException {
        //validations
        Objects.requireNonNull(vault, "Vault must not be null");
        Objects.requireNonNull(externalFilePath, "external file path must not be null");
        Objects.requireNonNull(internalFilePath, "internal file path must not be null");

        //Prepare vault entry
        VaultEntry vaultEntry = new VaultEntry();
        vaultEntry.setName(internalFilePath.getFileName().toString());
        vaultEntry.setLastModifiedTime(Files.getLastModifiedTime(externalFilePath).toInstant());
        vaultEntry.setSize(Files.size(externalFilePath));


        //TODO better thread handling
        //copy encrypted file into zip file system
        Thread copyThread = new Thread(() -> {
            URI vaultUri = URI.create("jar:file:" + vault.getPath().toString());
            FileSystem zipFs = null;
            OutputStream fileOut = null;
            try {
                String encryptedPath = encryptionService.encryptString(internalFilePath.toString(), password);
                zipFs = FileSystems.newFileSystem(vaultUri, null);
                fileOut = encryptionService.encryptedOutputStream(zipFs.getPath(encryptedPath), password);

                Files.copy(externalFilePath, fileOut);
            } catch (IOException | EncryptionFailedException e) {
                vaultEntry.setError("File could not be added to the vault");
            } finally {
                closeCloseable(zipFs);
                closeCloseable(fileOut);
            }
        });
        copyThread.start();

        //add entry to vault
        vault.addEntry(vaultEntry);
    }

    public void removeFileFromVault(final Vault vault, final VaultEntry entry) {

        //validations
        Objects.requireNonNull(vault, "Vault must not be null");
        Objects.requireNonNull(entry, "vault entry must not be null");

        //TODO
    }

    public void extractFileFromVault(final Vault vault, final VaultEntry entry) {

        //validations
        Objects.requireNonNull(vault, "Vault must not be null");
        Objects.requireNonNull(entry, "vault entry must not be null");

        //TODO
    }

    private void closeCloseable(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
