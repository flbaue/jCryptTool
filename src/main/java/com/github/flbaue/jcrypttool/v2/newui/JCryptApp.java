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

package com.github.flbaue.jcrypttool.v2.newui;

import com.github.flbaue.jcrypttool.v2.domain.AesEncryptionService;
import com.github.flbaue.jcrypttool.v2.domain.VaultService;
import com.github.flbaue.jcrypttool.v2.newui.view.ContentOverviewController;
import com.github.flbaue.jcrypttool.v2.newui.view.VaultOverviewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Florian Bauer on 16.01.15.
 */
public class JCryptApp extends Application {
    private final VaultService vaultService;
    private Stage primaryStage;
    private SplitPane mainWindowLayout;

    public JCryptApp() {
        vaultService = new VaultService(new AesEncryptionService());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public VaultService getVaultService() {
        return vaultService;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("jCryptTool");

        initMainWindow();
        initVaultOverview();
        initContentOverview();
    }

    private void initContentOverview() {
        try {
            FXMLLoader loader = new FXMLLoader(JCryptApp.class.getResource("view/content_overview.fxml"));
            BorderPane vaultOverviewLayout = loader.load();
            mainWindowLayout.getItems().add(vaultOverviewLayout);
            ContentOverviewController controller = loader.getController();
            controller.setApp(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initVaultOverview() {
        try {
            FXMLLoader loader = new FXMLLoader(JCryptApp.class.getResource("view/vault_overview.fxml"));
            BorderPane vaultOverviewLayout = loader.load();
            mainWindowLayout.getItems().add(vaultOverviewLayout);
            VaultOverviewController controller = loader.getController();
            controller.setApp(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(JCryptApp.class.getResource("view/main_window.fxml"));
            mainWindowLayout = loader.load();
            Scene scene = new Scene(mainWindowLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();

    }

    public Dialog<String> buildPasswordDialog() {
        // Create the custom dialog.
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Password Dialog");
        //dialog.setHeaderText("Look, a Custom Login Dialog");
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

        // Set the button types.
        ButtonType openButtonType = new ButtonType("Open", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(openButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node openButton = dialog.getDialogPane().lookupButton(openButtonType);
        openButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            openButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> password.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == openButtonType) {
                return password.getText();
            }
            return null;
        });

        return dialog;
    }
}
