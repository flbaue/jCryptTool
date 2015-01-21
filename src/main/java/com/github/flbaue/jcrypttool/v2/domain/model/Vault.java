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

package com.github.flbaue.jcrypttool.v2.domain.model;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Florian Bauer on 19.01.15.
 */
public class Vault {

    private final Path path;
    private final List<VaultEntry> vaultEntries = new LinkedList<>();

    public Vault(Path path) {
        this.path = path;
    }

    public void addEntry(VaultEntry vaultEntry) {
        vaultEntries.add(vaultEntry);
    }

    public Path getPath() {
        return path;
    }

    public long getSize() {
        long size = 0;
        for (VaultEntry entry : vaultEntries) {
            size += entry.getSize();
        }
        return size;
    }

}
