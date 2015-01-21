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

package com.github.flbaue.jcrypttool.v2.ui.view;

import com.github.flbaue.jcrypttool.v2.ui.JCryptApp;
import com.github.flbaue.jcrypttool.v2.ui.model.Vault;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by Florian Bauer on 19.01.15.
 */
public class VaultOverviewController {

    @FXML
    private ListView<Vault> vaultListView;

    private ObservableList<Vault> vaults;
    private JCryptApp app;

    public VaultOverviewController() {
        vaults = FXCollections.observableArrayList();
        vaults.add(new Vault("Documents"));
        vaults.add(new Vault("Photos"));
        vaults.add(new Vault("Work"));
    }

    @FXML
    private void initialize() {
        vaultListView.setCellFactory(view -> new VaultListItem());
        vaultListView.setItems(vaults);
    }

    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select vault to open...");
        File selectedFile = fileChooser.showOpenDialog(app.getPrimaryStage());

        if (selectedFile == null) {
            return;
        }

        Path path = Paths.get(selectedFile.toURI());

        Dialog<String> dialog = app.buildPasswordDialog();
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(password -> {
            try {
                Vault vault = convertVault(app.getVaultService().openVault(path, password));
                vaults.add(vault);

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error while reading file");
                alert.setContentText("The file cannot be opened");
                alert.show();
            }
        });

    }

    private Vault convertVault(com.github.flbaue.jcrypttool.v2.domain.model.Vault vault) {

        Vault uiVault = new Vault(vault.getPath().getFileName().toString());
        uiVault.setSize(vault.getSize());

        return uiVault;
    }

    public JCryptApp getApp() {
        return app;
    }

    public void setApp(JCryptApp app) {
        this.app = app;
    }

    @FXML
    private void handleNew() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select vault to open...");
        File selectedFile = fileChooser.showSaveDialog(app.getPrimaryStage());

        if (selectedFile == null) {
            return;
        }

        Path path = Paths.get(selectedFile.toURI());

        Dialog<String> dialog = app.buildPasswordDialog();
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(password -> {
            try {
                Vault vault = convertVault(app.getVaultService().createVault(path));
                vault.setPassword(password);
                vaults.add(vault);

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error while reading file");
                alert.setContentText("The file cannot be opened");
                alert.show();
            }
        });
    }
}
