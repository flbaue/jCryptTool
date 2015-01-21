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

package com.github.flbaue.jcrypttool.v2.newui.view;

import com.github.flbaue.jcrypttool.v2.newui.model.Vault;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;

/**
 * Created by Florian Bauer on 19.01.15.
 */
public class VaultListItem extends ListCell<Vault> {

    private final DecimalFormat decimalFormat;
    private VBox box = new VBox();
    private Label head = new Label();
    private Label sub = new Label();

    public VaultListItem() {
        box.getChildren().addAll(head, sub);
        box.setPrefWidth(150);
        setGraphic(box);
        decimalFormat = new DecimalFormat("#0.00");
    }

    @Override
    protected void updateItem(Vault item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            head.setText(item.getName());
            sub.setText(decimalFormat.format(item.getSize()) + " MB");
        }
    }
}
