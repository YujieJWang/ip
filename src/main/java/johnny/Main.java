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
    private TextArea conversation;
    private TextField input;

    @Override
    public void start(Stage stage) {
        conversation = new TextArea();
        conversation.setEditable(false);
        conversation.setWrapText(true);
        conversation.setAccessibleText("Conversation with Johnny");
        conversation.setFont(Font.font("Monospaced"));

        input = new TextField();
        input.setPromptText("Enter a command, e.g. todo read book");
        input.setAccessibleText("Command input");

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> submit());
        input.setOnAction(event -> submit());

        HBox inputBar = new HBox(8, input, sendButton);
        HBox.setHgrow(input, Priority.ALWAYS);
        VBox root = new VBox(10, conversation, inputBar);
        VBox.setVgrow(conversation, Priority.ALWAYS);
        root.setPadding(new Insets(10));

        Ui guiUi = new Ui(message -> conversation.appendText(message + System.lineSeparator()));
        johnny = new Johnny(DEFAULT_FILE_PATH, guiUi);
        guiUi.showGreeting();

        stage.setTitle("Johnny");
        stage.setScene(new Scene(root, 640, 480));
        stage.setMinWidth(420);
        stage.setMinHeight(300);
        stage.show();
        input.requestFocus();
    }

    private void submit() {
        String command = input.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        conversation.appendText("You: " + command + System.lineSeparator());
        boolean shouldExit = johnny.processCommand(command);
        input.clear();
        if (shouldExit) {
            Platform.exit();
        }
    }
}
