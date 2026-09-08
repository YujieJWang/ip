package johnny;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import johnny.ui.Ui;

/**
 * Provides a fit-for-purpose JavaFX interface for Johnny.
 */
public class Main extends Application {

    private static final String DEFAULT_FILE_PATH = "./data/johnny.txt";

    private Johnny johnny;
    private TextArea conversationArea;
    private TextField inputField;

    @Override
    public void start(Stage stage) {
        conversationArea = new TextArea();
        conversationArea.setEditable(false);
        conversationArea.setWrapText(true);
        conversationArea.setAccessibleText("Conversation with Johnny");
        conversationArea.setFont(Font.font("Monospaced"));

        inputField = new TextField();
        inputField.setPromptText("Enter a command, e.g. todo read book");
        inputField.setAccessibleText("Command input");

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> submit());
        inputField.setOnAction(event -> submit());

        HBox inputBar = new HBox(8, inputField, sendButton);
        HBox.setHgrow(inputField, Priority.ALWAYS);
        VBox root = new VBox(10, conversationArea, inputBar);
        VBox.setVgrow(conversationArea, Priority.ALWAYS);
        root.setPadding(new Insets(10));

        Ui guiUi = new Ui(message -> conversationArea.appendText(message + System.lineSeparator()));
        johnny = new Johnny(DEFAULT_FILE_PATH, guiUi);
        guiUi.showGreeting();

        stage.setTitle("Johnny");
        stage.setScene(new Scene(root, 640, 480));
        stage.setMinWidth(420);
        stage.setMinHeight(300);
        stage.show();
        inputField.requestFocus();
    }

    private void submit() {
        assert johnny != null : "Johnny must be initialized before command submission";

        String command = inputField.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        conversationArea.appendText("You: " + command + System.lineSeparator());
        boolean shouldExit = johnny.processCommand(command);
        inputField.clear();
        if (shouldExit) {
            Platform.exit();
        }
    }
}
