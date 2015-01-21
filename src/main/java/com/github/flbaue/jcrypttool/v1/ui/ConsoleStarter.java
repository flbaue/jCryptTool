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

package com.github.flbaue.jcrypttool.v1.ui;

import com.github.flbaue.jcrypttool.v1.*;

import java.io.File;

/**
 * Created by Florian Bauer on 02.01.15.
 */
public class ConsoleStarter implements ProgressListener {

    public static void main(String[] args) {
        new ConsoleStarter().run(args);
    }

    private void run(String[] args) {
        if (args.length != 4 && args.length != 1) {
            printHelp();
        } else if (args.length == 4) {
            String mode = args[0];

            EncryptionSettings encryptionSettings = new EncryptionSettings();
            encryptionSettings.inputFile = new File(args[1]);
            encryptionSettings.outputFile = new File(args[2]);
            encryptionSettings.password = args[3];
            Progress progress;

            EncryptionService encryptionService = new EncryptionService();
            switch (mode) {
                case "-e":
                    progress = encryptionService.encrypt(encryptionSettings);
                    break;
                case "-d":
                    progress = encryptionService.decrypt(encryptionSettings);
                    break;
                default:
                    System.out.println("Unknown mode: " + mode);
                    printHelp();
                    return;
            }
            progress.addProgressListener(this);
        } else if (args.length == 1) {
            String mode = args[0];
            if (mode.equals("-g")) {
                new SimpleGui().run();
            }
        }
    }

    private void printHelp() {
        System.out.println("jCryptTool Help");
        System.out.println("==============================");
        System.out.println("1. param:\t-e (encryption) / -d (decryption) / -g (GUI, without other parameters)");
        System.out.println("2. param:\tinput file name");
        System.out.println("3. param:\toutput file name");
        System.out.println("4. param:\tpassword");
    }

    @Override
    public void progressUpdate(ProgressEvent progressEvent) {
        System.out.println(progressEvent.value + "%");
    }
}
