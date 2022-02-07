package com.imagesorter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    private File inputDirectory;

    private final List<File> inputFileList = new LinkedList<File>();
    private final List<GridPane> outputGridList = new LinkedList<GridPane>();
    private final List<TextField> outputFieldsList = new LinkedList<TextField>();
    private final List<Button> outputButtonList = new LinkedList<Button>();
    private final List<File> outputDirectoryList = new LinkedList<File>();

    enum State
    {
        READY,
        RUNNING,
        PAUSED,
        FINISHED
    }

    State currentState = State.READY;

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

    public void startStop()
    {

    }

    void start()
    {

    }

    void stop()
    {

    }

    private EventHandler<ActionEvent> createOutputButtonHandler(int index)
    {
        return event -> outputDirectoryButton(index);
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

    public void chooseInputDirectory()
    {
        inputDirectory = directoryChooser.showDialog(inputDirectoryButton.getScene().getWindow());
        inputDirectoryField.setText(inputDirectory.getAbsolutePath());
    }

    private void outputDirectoryButton(int index)
    {
        if(outputFieldsList.get(index).getText().equals("")) {
            File outputDirectory = directoryChooser.showDialog(inputDirectoryButton.getScene().getWindow());
            if (outputDirectory != null) {
                outputDirectoryList.add(outputDirectory);
                outputFieldsList.get(index).setText(outputDirectory.getAbsolutePath());
                outputButtonList.get(index).setText("X");
                generateOutputDirectoryUI();
            }
        }
        else
        {
            outputDirectoryList.remove(index);
            outputDirectoryBox.getChildren().remove(outputGridList.get(index));
            outputGridList.remove(index);
            outputFieldsList.remove(index);
            outputButtonList.remove(index);

            int i = 0;
            for (Button button : outputButtonList)
            {
                button.setOnAction(createOutputButtonHandler(i));
                i++;
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generateOutputDirectoryUI();
    }
}