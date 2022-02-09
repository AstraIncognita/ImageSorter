package com.imagesorter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    private final Model model = new Model();
    State currentState = State.WAITING;

    private final List<GridPane> outputGridList = new LinkedList<>();
    private final List<TextField> outputFieldsList = new LinkedList<>();
    private final List<Button> outputButtonList = new LinkedList<>();


    @FXML
    private ImageView imageView;
    @FXML
    private TextField inputDirectoryField;
    @FXML
    private Button inputDirectoryButton;
    @FXML
    private TextField imageNameField;
    @FXML
    private Button startStopButton;
    @FXML
    private VBox outputDirectoryBox;
    @FXML
    private Pane resizeablePane;
    @FXML
    private VBox imageInfoBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generateOutputDirectoryUI();
        imageView.fitWidthProperty().bind(resizeablePane.widthProperty());
        imageView.fitHeightProperty().bind(resizeablePane.heightProperty());
    }

    public void startStopButton()
    {
        switch (currentState)
        {
            case WAITING ->
            {
                if (model.isInputDirectoryValid() && model.outputDirectoriesCount() > 0)
                {
                    model.generateFileList();
                    if (model.hasFiles())
                    {
                        toRunningState();
                    }
                    else
                    {
                        showAlert("Chosen input directory is empty.");
                    }
                }
                else
                {
                    showAlert("Some directories are invalid.");
                }
            }
            case RUNNING ->
            {
                toWaitingState();
            }
        }
    }

    public void inputDirectoryButton()
    {
        switch (currentState)
        {
            case WAITING ->
            {
                File inputDirectory = new DirectoryChooser().showDialog(inputDirectoryButton.getScene().getWindow());
                if (inputDirectory != null && inputDirectory.isDirectory())
                {
                    model.setInputDirectory(inputDirectory);
                    inputDirectoryField.setText(inputDirectory.getAbsolutePath());
                }
            }
            case RUNNING ->
            {
            }
        }
    }

    private void outputDirectoryButton(int index)
    {
        switch (currentState) {
            case WAITING ->
            {
                if (outputFieldsList.get(index).getText().equals(""))
                {
                    File outputDirectory = new DirectoryChooser().showDialog(inputDirectoryButton.getScene().getWindow());
                    if (outputDirectory != null && outputDirectory.exists() && outputDirectory.isDirectory())
                    {
                        model.addOutputDirectory(outputDirectory);
                        outputFieldsList.get(index).setText(outputDirectory.getAbsolutePath());
                        outputButtonList.get(index).setText("X");
                        generateOutputDirectoryUI();
                    }
                }
                else
                {
                    model.removeOutputDirectory(index);
                    removeOutputDirectoryUI(index);
                    regenerateOutputButtonHandlers();
                }
            }
            case RUNNING ->
            {
                boolean isFileMoved = model.tryMoveCurrentFile(imageNameField.getText(), index);
                if (isFileMoved)
                {
                    tryShowImage();
                }
                else
                {
                    showAlert("Could not move image to this folder.");
                }
            }
        }
    }

    public void skipImageButton()
    {
        switch (currentState)
        {
            case RUNNING ->
            {
                model.skipCurrentFile();
                tryShowImage();
            }
            case WAITING ->
            {
            }
        }
    }

    private void toWaitingState()
    {
        imageView.setImage(null);
        imageNameField.setText("");
        imageInfoBox.setDisable(true);
        inputDirectoryButton.setDisable(false);
        startStopButton.setText("Start");
        outputButtonList.get(outputButtonList.size() - 1).setDisable(false);
        outputFieldsList.get(outputFieldsList.size() - 1).setDisable(false);
        for (Button button : outputButtonList)
        {
            button.setText("X");
        }
        outputButtonList.get(outputButtonList.size() - 1).setText("...");
        currentState = State.WAITING;
    }

    private void toRunningState()
    {
        imageInfoBox.setDisable(false);
        inputDirectoryButton.setDisable(true);
        startStopButton.setText("Stop");
        tryShowImage();
        outputButtonList.get(outputButtonList.size() - 1).setDisable(true);
        outputFieldsList.get(outputFieldsList.size() - 1).setDisable(true);
        for (Button button : outputButtonList)
        {
            button.setText("<-");
        }
        currentState = State.RUNNING;
    }

    private EventHandler<ActionEvent> createOutputButtonHandler(int index)
    {
        return event -> outputDirectoryButton(index);
    }

    private void regenerateOutputButtonHandlers()
    {
        int i = 0;
        for (Button button : outputButtonList) {
            button.setOnAction(createOutputButtonHandler(i));
            i++;
        }
    }

    private void generateOutputDirectoryUI()
    {
        GridPane gridPane = new GridPane();
        Button button = new Button("...");
        button.setPrefWidth(75);
        button.setOnAction(createOutputButtonHandler(outputButtonList.size()));
        TextField textField = new TextField();
        textField.setEditable(false);

        gridPane.addRow(0, textField, button);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setPercentWidth(90);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(10);
        gridPane.getColumnConstraints().addAll(col0, col1);

        outputDirectoryBox.getChildren().add(gridPane);
        outputGridList.add(gridPane);
        outputFieldsList.add(textField);
        outputButtonList.add(button);
    }

    private void removeOutputDirectoryUI(int index)
    {
        outputDirectoryBox.getChildren().remove(outputGridList.get(index));
        outputGridList.remove(index);
        outputFieldsList.remove(index);
        outputButtonList.remove(index);
    }

    private void tryShowImage()
    {
        if (!model.hasFiles())
        {
            toWaitingState();
            showAlert("Image sorting was successfully finished.");
        }
        else
        {
            File currentFile = model.getCurrentFile();
            if (currentFile != null)
            {
                imageView.setImage(new Image(currentFile.getAbsolutePath()));
                String fileName = currentFile.getName();
                imageNameField.setText(fileName.substring(0, fileName.lastIndexOf(".")));
            }
            else
            {
                showAlert("Some of the files were unexpectedly removed while sorting.");
                model.removeInvalidFiles();
                tryShowImage();
            }
        }
    }

    private void showAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Image Sorter");
        alert.setHeaderText("Info");
        alert.setContentText(message);
        alert.show();
    }

    enum State
    {
        WAITING,
        RUNNING
    }
}