# gruppe3-GUnit-projekt2-Checkit
Version 1.3 13.05.2022

@authors: Valentin Egger, Thomas Funhoff
## Checkit
A software for hotels, which enables hotel customers to check themselves in and out .
The focus is on optimizing the process from check-in to check-out. This is to remove work from the hotel staff
by allowing the hotel customer to check themselves in and out . A so-called self-service solution.
### Project Information
This project is the prototype version of Checkit. It allows users too check-in and check-out by themselves and to create a new booking. To gain a better understanding of this project you can review the [design documentation](./docs/design-documentation.md). It consists of the following parts:

#### Self check-in and self check-out
Self check-in and self check-out is the main part of this project. 
A hotel guest is able too check-in and -out of a hotel with a generated code, which he receives after booking a room.
If the hotel guest hasn't created a booking, the terminal will provide the guest with a simple-to-use interface, where he may provide his information.
A guest will not be able to create a booking request, without providing the necessary information. 
The dates can only be from now until somewhere in the future. Booking in the past is prohibited. The check-out date needs to be after the check-in date.
As soon as the user changes the preferred amount of nights, the datepicker updates and disables reserved time-slots. Currently, we assume that there is only one Room available, to minimize complexity.

#### Secure and flawless processing of bookings
The guest's booking information is registered in the system and a room will automatically be reserved. 
For the prototype only one room is available. This allows for a clearer overview in the calendar.

#### Features in the prototyp
Some of the already implemented features:
 - Create Booking and prevent overbooking
 - Checkin only if the the starttime is today or in the past and the endtime in the future
 - Disable reserved dates based on preferred amount of nights
 - Checkout dialog

#### Features for in the future 
As it is a prototype, many more features will be added until the final product is created.
These features consist of:
- Admin view for hotel staff
- Algorithm for optimal utilization of rooms
- Automatic generation of cleaning schedules
- Various payment options
- Verification of hotel customers
- Room management
- Analysis tool to evaluate collected data

### Installation
1. Install prerequisites:
    - OpenJDK version 16+ or higher
    - Optional: Gradle 7.4 => [Installation Manual](https://docs.gradle.org/current/userguide/installation.html)

2. Clone the repository

3. Configure your IDE to use Gradle
    - IntelliJ: Gradle & Gradle Extension is installed and enabled by default. [Additional Help](https://www.jetbrains.com/help/idea/gradle.html#gradle_import_project_start)
    - VS CODE: Use the [Gradle Task](https://marketplace.visualstudio.com/items?itemName=richardwillis.vscode-gradle) Extension
    
4. Run the client from the IDE or the terminal:

    ```Shell
    $ ./gradlew run --no-daemon
    ```

   or in a windows command line.

    ```Shell
    $ ./gradlew.bat run --no-daemon
    ```
### Usage
Once the installation is done you can execute the application from the IDE you're using or via the Gradle Wrapper:

```Shell
$ ./gradlew run
```

### Branching
Branch naming conventions: Types -> enhancement, bugfix, refactoring
- \<type\>-\<issueNumber\>/\<individual name\>
  e.g. bugfix-2/multi-threaded-server

Commit message guidelines:

- "#issueNumber: <commit message>" e.g. "#2: Missing Javadoc"

 ### Sample pull requests
 PR 6: https://github.zhaw.ch/PM2-IT21aWIN-fame-rayi-wahl/IT21aWIN-gruppe3-GUnit-projekt2-Checkit/pull/6 
 
 PR 12: https://github.zhaw.ch/PM2-IT21aWIN-fame-rayi-wahl/IT21aWIN-gruppe3-GUnit-projekt2-Checkit/pull/12
