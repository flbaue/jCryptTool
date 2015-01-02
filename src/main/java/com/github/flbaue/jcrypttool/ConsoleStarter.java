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

/**
 * Created by Florian Bauer on 02.01.15.
 */
public class ConsoleStarter {

    public static void main(String[] args) {
        new ConsoleStarter().run(args);
    }

    private void run(String[] args) {
        if (args.length != 4) {
            printHelp();
        } else {
            String mode = args[0];
            String inputFileName = args[1];
            String outputFileName = args[2];
            String password = args[3];

            EncryptionService encryptionService = new EncryptionService();
            switch (mode) {
                case "-e":
                    encryptionService.encrypt(inputFileName, outputFileName, password);
                    break;
                case "-d":
                    encryptionService.decrypt(inputFileName, outputFileName, password);
                    break;
                default:
                    System.out.println("Unknown mode: " + mode);
                    printHelp();
                    break;
            }
        }
    }

    private void printHelp() {
        System.out.println("jCryptTool Help");
        System.out.println("==============================");
        System.out.println("1. param:\t-e (encryption) / -d (decryption)");
        System.out.println("2. param:\tinput file name");
        System.out.println("3. param:\toutput file name");
        System.out.println("4. param:\tpassword");
    }
}
