# NeedAHand  🛠️
### The application for repairing issues


## Project information

- View [Issues](https://github.zhaw.ch/IT21a/pm3-hs22-it21a_win-team2/issues)
- View [Project](https://github.zhaw.ch/IT21a/pm3-hs22-it21a_win-team2/projects/1)

## Authors
We are a team of Computer Science students from the Zurich University of Applied Sciences (ZHAW).

- [@arndom01](https://github.zhaw.ch/arndom01) / Software Developer
- [@funhotho](https://github.zhaw.ch/funhotho) / Software Developer
- [@gabricyr](https://github.zhaw.ch/gabricyr) / Vice Project Manager, Software Developer
- [@quachvan](https://github.zhaw.ch/quachvan) / Software Developer
- [@unveryoh](https://github.zhaw.ch/unveryoh) / Project Manager, Software Developer

## Description
NeedAHand is an application that allows the customer to repair a damage that occurred at home.

The customer can create an advertisement and the application will give him a selection of craftsmen who can fix the problem.
<br><br>
For this purpose, an algorithm is used that takes into account the necessary experience, reputation, availability and budget, the distance between customer and craftsmen.

Moreover, the customer can accept the offer and the application will show the selected craftsmen the contact details of the customer.
After the job is done, the customer and the craftsmen can rate each other.



## Documentation

- [Class Diagram](./docs/ClassDiagram.md)

## Installation
1. Install:
    - OpenJDK version 17 or higher
    - Gradle 7.4: [Installation Manual](https://docs.gradle.org/current/userguide/installation.html)
      <br><br>
2. Clone the repository
   <br><br>
3. Configure Gradle in IntelliJ or in VS Code
    - IntelliJ: Gradle & Gradle Extension is installed and enabled by default.
    - For VS Code: Use the [Gradle Task](https://marketplace.visualstudio.com/items?itemName=richardwillis.vscode-gradle) Extension.
<br><br>
4. Run the build from the IDE or the terminal:

    ```Shell
    $ ./gradlew build
    ```


## Usage
After the installation you can run the application with the following command:
```Shell
$ ./gradle run
```