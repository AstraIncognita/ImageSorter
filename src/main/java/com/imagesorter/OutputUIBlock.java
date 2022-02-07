package com.imagesorter;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class OutputUIBlock {

    private Node parent;
    private GridPane gridPane;
    private TextField textField;
    private Button button;
    private int index;

    OutputUIBlock(int index)
    {
        GridPane gridPane = new GridPane();
        Button button = new Button("...");
        button.setPrefWidth(75);
    }



}
