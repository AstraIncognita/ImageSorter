package com.imagesorter;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    private File inputDirectory;
    List<File> inputFileList = new LinkedList<>();

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

    @FXML
    private Pane resizeablePane;

    public void startStop()
    {
        start();
    }

    void start()
    {
        if (inputDirectory.exists() && inputDirectory.isDirectory()) {
            inputFileList = findAllImages();
            if (inputFileList.size() > 0)
            {
                showNextImage();
                currentState = State.RUNNING;
            }
        }
    }

    private void showNextImage() {
        imageView.setImage(new Image(inputFileList.get(0).getAbsolutePath()));
        imageNameField.setText(inputFileList.get(0).getName());
    }

    private LinkedList<File> findAllImages() {

        String[] extensions = {"jpg", "jpeg", "bmp", "png"};


        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                for(String extension : extensions)
                {
                    if (name.endsWith("."+extension))
                    {
                        return true;
                    }
                }
                return false;
            }
        };

        LinkedList<File> imageList;
        File[] imageArray = inputDirectory.listFiles(filter);
        if (imageArray != null) {
            imageList = new LinkedList<File>(List.of(imageArray));
        }
        else
        {
            imageList = new LinkedList<File>();
        }

        return imageList;
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
        switch (currentState) {
            case READY:
                if (outputFieldsList.get(index).getText().equals("")) {
                    File outputDirectory = directoryChooser.showDialog(inputDirectoryButton.getScene().getWindow());
                    if (outputDirectory != null) {
                        outputDirectoryList.add(outputDirectory);
                        outputFieldsList.get(index).setText(outputDirectory.getAbsolutePath());
                        outputButtonList.get(index).setText("X");
                        generateOutputDirectoryUI();
                    }
                } else
                {
                    outputDirectoryList.remove(index);
                    outputDirectoryBox.getChildren().remove(outputGridList.get(index));
                    outputGridList.remove(index);
                    outputFieldsList.remove(index);
                    outputButtonList.remove(index);

                    int i = 0;
                    for (Button button : outputButtonList) {
                        button.setOnAction(createOutputButtonHandler(i));
                        i++;
                    }
                }
                break;
            case RUNNING:
                String newPath = outputDirectoryList.get(index).getAbsolutePath() + "/" + imageNameField.getText();
                if (inputFileList.get(0).renameTo(new File(newPath)))
                {
                    inputFileList.remove(0);
                    if (inputFileList.size() <= 0)
                    {
                        imageView.setImage(null);
                        imageNameField.setText("");
                        currentState = State.FINISHED;
                        return;
                    }
                    imageView.setImage(new Image(inputFileList.get(0).getAbsolutePath()));
                    imageNameField.setText(inputFileList.get(0).getName());

                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning!");
                    alert.setContentText("Could not move image to this folder.");
                    alert.show();
                }
                break;
            case FINISHED:
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Info!");
                alert.setContentText("All images are already sorted.");
                alert.show();
                break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generateOutputDirectoryUI();
        imageView.fitWidthProperty().bind(resizeablePane.widthProperty());
        imageView.fitHeightProperty().bind(resizeablePane.heightProperty());
    }
}