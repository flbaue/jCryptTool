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

package com.github.flbaue.jcrypttool.ui;

import com.github.flbaue.jcrypttool.EncryptionService;
import com.github.flbaue.jcrypttool.EncryptionSettings;
import com.github.flbaue.jcrypttool.Progress;

import javax.swing.*;
import java.io.File;

/**
 * Created by Florian Bauer on 02.01.15.
 */
public class SimpleGui {
    private JTextField outputFileTextField;
    private JTextField inputFileTextField;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton chooseOutFileButton;
    private JButton chooseInFileButton;
    private JPanel rootPanel;
    private JTextField passwordTextField;
    private JProgressBar progressBar;

    private EncryptionService encryptionService;

    public SimpleGui() {
        chooseInFileButton.addActionListener(new ChooseInButtonListener(chooseInFileButton, inputFileTextField));
        chooseOutFileButton.addActionListener(new ChooseOutButtonListener(chooseOutFileButton, outputFileTextField));
        encryptButton.addActionListener(new EncryptionButtonListener(this));
        decryptButton.addActionListener(new DecryptionButtonListener(this));

        encryptionService = new EncryptionService();
    }

    public void run() {
        JFrame frame = new JFrame("SimpleGui");
        frame.setContentPane(new SimpleGui().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public void startEncryption() {

        EncryptionSettings encryptionSettings = new EncryptionSettings();
        encryptionSettings.inputFile = new File(inputFileTextField.getText());
        encryptionSettings.outputFile = new File(outputFileTextField.getText());
        encryptionSettings.password = passwordTextField.getText();

        progressBar.getModel().setRangeProperties(0, 0, 0, 100, true);
        Progress progress = encryptionService.encrypt(encryptionSettings);
        progress.addProgressListener(progressEvent -> progressBar.setValue(progressEvent.value));
    }

    public void startDecryption() {
        EncryptionSettings encryptionSettings = new EncryptionSettings();
        encryptionSettings.inputFile = new File(inputFileTextField.getText());
        encryptionSettings.outputFile = new File(outputFileTextField.getText());
        encryptionSettings.password = passwordTextField.getText();

        progressBar.getModel().setRangeProperties(0, 0, 0, 100, true);
        Progress progress = encryptionService.decrypt(encryptionSettings);
        progress.addProgressListener(progressEvent -> progressBar.setValue(progressEvent.value));
    }
}
