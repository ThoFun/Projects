# Design documentation

## Initial state
At the time of the project takeover, there was a lot of duplicated code and the client only had a ChatWindowController and ClientConnectionHandler to control all the data flow. Thus, the view was not decoupled. In order to decouple the view and implement the MVC structure, a new class "Messenger" was created to represent the model. The ChatWindowController creates and fetches the data only via the Messenger. Everything that has to do with connections is done in the ClientConnectionHandler, which is used by the Messenger. The messages are listed in the ClientMessageList class. This asks for a StringProperty in the interface which keeps a string representation of all messages up to date.

The biggest changes were the mentioned changes regarding the MVC structure. Furthermore, a superclass ConnectionHandler was created, since the ClientConnectionHandler and ServerConnectionHandler have many similarities. In the following diagrams some details are missing to ensure clarity.

## Class diagram
![KlassendiagramBefore drawio](https://github.zhaw.ch/storage/user/4858/files/2971e9c4-4d7d-4d9c-a0af-bd2f44b5829c)
