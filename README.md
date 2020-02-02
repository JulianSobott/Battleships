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
- *bin*: The compiled .jar file(s) for execution
- *docs*: docs for developers and for users
- *lib*: jar files of the dependencies
- *src*: java source code

# Run the game
## Compiled version

- go into the **bin** folder
- on windows: just double click the **battleships.bat** file
- all platforms: in the console write
```shell script
java -jar Battleships.jar
```
- You need java installed and the console must find it
- You can also pass the full path to the java executable

## Eclipse


# Dependencies

- Java 8+
- JavaFX2
- jackson 2.1+
- junit 5.1 (only for tests)
