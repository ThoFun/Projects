package ch.zhaw.pm2.multichat.client;

import ch.zhaw.pm2.multichat.protocol.NetworkHandler;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for the chat window. Provides for initializing the window when
 * opening it as well as for reacting to user interactions in the window, e. g.
 * clicking on buttons.
 */
public class ChatWindowController {
    private final Pattern messagePattern = Pattern.compile( "^(?:@(\\S*))?\\s*(.*)$" );

    private final WindowCloseHandler windowCloseHandler = new WindowCloseHandler();
    private Messenger messenger;
    private BooleanProperty connectedProperty;

    @FXML private Pane rootPane;
    @FXML private TextField serverAddressField;
    @FXML private TextField serverPortField;
    @FXML private TextField userNameField;
    @FXML private TextField messageField;
    @FXML private TextArea messageArea;
    @FXML private Button connectButton;
    @FXML private Button sendButton;
    @FXML private TextField filterValue;

    /**
     * Sets the server address and server port text fields to their default values
     * given by the {@link NetworkHandler}.
     */
    @FXML
    public void initialize() {
        serverAddressField.setText(NetworkHandler.DEFAULT_ADDRESS.getCanonicalHostName());
        serverPortField.setText(String.valueOf(NetworkHandler.DEFAULT_PORT));

        messenger = new Messenger();
        messageArea.textProperty().bind(messenger.getMessageBound());
        sendButton.setDisable(true);
    }

    /**
     * Makes the "username", "server port" and "server address" text fields as well as
     * the "send" and the "connect" buttons listening to the connection property of the
     * client connection handler.
     */
    public void bindConnectedProperty() {
        messenger.getConnectedProperty().addListener((observable, oldValue, newValue) -> {
            userNameField.setDisable(newValue);
            serverAddressField.setDisable(newValue);
            serverPortField.setDisable(newValue);
            sendButton.setDisable(!newValue);

            if(newValue) {
                Platform.runLater(() -> connectButton.setText("disconnect"));
            } else {
                Platform.runLater(() -> connectButton.setText("connect"));
            }
        });
    }

    /**
     * Adds a shutdown routine as well as an on-close request in order to ensure
     * that the connection gets terminated whenever the window is closed or the
     * client is terminated otherwise.
     */
    @FXML
    private void toggleConnection () {
        String fixedUserName = userNameField.getText().strip().replaceAll(" ", "-");
        userNameField.setText(fixedUserName);
        messenger.toggleConnection(fixedUserName, serverAddressField.getText().strip(), serverPortField.getText().strip());
        if(null == connectedProperty) {bindConnectedProperty();}
    }

    /**
     * Makes the connection handler sending the message, entered in the "send" field.
     */
    @FXML
    private void message() {
        String messageText = messageField.getText().strip();
        Matcher matcher = messagePattern.matcher(messageText);

        if (matcher.find() && !messageText.isEmpty()) {
            String receiver = matcher.group(1);
            String message = matcher.group(2);
            if (receiver == null || receiver.isBlank()) receiver = ClientConnectionHandler.USER_ALL;
            messenger.sendMessage(Message.MessageType.MESSAGE, receiver, message);
            messageField.setText("");
        } else {
            messenger.writeError("Not a valid message format.");
        }
    }

    @FXML
    private void applyFilter( ) {
        messenger.setFilter(filterValue.getText().strip());
    }

    /**
     * If a Client is closed without disconnecting, it will call up the applicationClose method.
     */
    class WindowCloseHandler implements EventHandler<WindowEvent> {
        public void handle(WindowEvent event) {
            applicationClose();
        }
    }

    /**
     * Disconnects the client from the server
     */
    private void applicationClose() {
        messenger.disconnect();
    }
}
