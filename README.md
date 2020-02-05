# Battleships
Battlehips game with ai and network

# Features
<ul>
    <li>Play vs an AI</li>
    <li>Play over the network</li>
    <li>Simulate AI vs AI</li>
    <li>3 different AI difficulty levels</li>
    <li>Cheatmode: Get tips where to shoot</li>
</ul>

# Repository structure

- *assets*: images, music, stylesheets, csv_data
- *binaries*: The compiled .jar file(s) for execution
- *docs*: docs for developers and for users + class diagrams
- *lib*: jar files of the dependencies
- *src*: java source code

# Run the game
## Compiled version

- go into the **binaries** folder
- on windows: just double click the **battleships.bat** file
- all platforms: in the console write:
```shell script
java -jar Battleships.jar
```
- You need java installed and the console must find it
- You can also pass the full path to the java executable

## Eclipse
- File -> Import -> Projects from Folder or Archive
- Select this repository
- click finish (It is normal, that there will occur Errors. followings steps should solve them)
- goto src/core.test.TestCommunicationData 
- click on the error sign on the left side and select:
    - Add **JUnit 5** library to the build path
- (Probably delete problems)
- right click **assets** folder -> Build Path -> Use as Source Folder
- Start src/gui.Main

# Dependencies

- Java 8+
- JavaFX2 (Included in Java 8)
- jackson 2.1+ (.jar files are in lib folder)
- junit 5.1 (only for tests)
