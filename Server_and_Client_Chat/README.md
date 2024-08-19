# Uebung-hk1-eggerval-funhotho
Version 1.0 15.04.2022 

@authors: Valentin Egger, Thomas Funhoff
## Multichat

### Project Information
Multichat is a project designed to allow multiple clients to communicate together. 
To gain a better understanding of this project you can review the [design documentation](./docs/design-documentation.md).
It consists of three main parts:

#### Client
The client is built using [JavaFX](https://openjfx.io). It allows a user to connect to a server and send and receive messages.

Messages can both be sent to every client connected to the server, or to a client connected with a certain user name. This allows for both public and private messaging. They can also be filtered, which helps to keep an overview on particularly noisy servers.

#### Server
The server component is designed to serve as many connections to clients as the hardware it is running on can provide. It works fully concurrently – it allows for the parallel forwarding of as many messages as needed. This allows for great performance even under high loads.

#### Protocol
This component consists of elements used by both the client and the server and of elements used in the communication between them.

### Installation
1. Install prerequisites:
    - OpenJDK version 16+ or higher
    - Optional: Gradle 7.4 => [Installation Manual](https://docs.gradle.org/current/userguide/installation.html)

2. Clone the repository

3. Configure your IDE to use Gradle
    - IntelliJ: Gradle & Gradle Extension is installed and enabled by default. [Additional Help](https://www.jetbrains.com/help/idea/gradle.html#gradle_import_project_start)
    - VS CODE: Use the [Gradle Task](https://marketplace.visualstudio.com/items?itemName=richardwillis.vscode-gradle) Extension

4. Run the server from the IDE or the terminal:

    ```Shell
    $ ./gradlew server:run --no-daemon
    ```

   or in a windows command line.

    ```Shell
    $ ./gradlew.bat server:run --no-daemon
    ```

5. Run the client from the IDE or the terminal:

    ```Shell
    $ ./gradlew client:run --no-daemon
    ```

   or in a windows command line.

    ```Shell
    $ ./gradlew.bat client:run --no-daemon
    ```
### Usage
Once the installation is done you can execute the application from the IDE you're using or via the Gradle Wrapper:

```Shell
$ ./gradlew run
```

### Branching
Branch naming conventions: Types -> feature, bugfix, refactoring
- \<type\>-\<issueNumber\>/\<individual name\>
    e.g. bugfix-2/multi-threaded-server
    
Commit message guidelines:

- "#issueNumber: <commit message>" e.g. "#2: Missing Javadoc"

### Issues
The issues have been split into two main categories:

- [Structural Issues](https://github.zhaw.ch/PM2-IT21aWIN-fame-rayi-wahl/Uebung-hk1-eggerval-funhotho/issues?q=is%3Aissue+is%3Aclosed+label%3A%22structural+problems%22) - All issues that have to do with architecture, clean-code, JavaDoc, etc.
- [Bugs](https://github.zhaw.ch/PM2-IT21aWIN-fame-rayi-wahl/Uebung-hk1-eggerval-funhotho/issues?q=is%3Aissue+is%3Aclosed+label%3Abug) - Issues related to functionality that doesn't work as specified.

View All [Open Issues](https://github.zhaw.ch/PM2-IT21aWIN-fame-rayi-wahl/Uebung-hk1-eggerval-funhotho/issues)

#### Documented issues
A list of our 5 fixed structural issues and 5 fixed bugs can be found [here](https://github.zhaw.ch/PM2-IT21aWIN-fame-rayi-wahl/Uebung-hk1-eggerval-funhotho/blob/dev/docs/fixed-issues.md).
