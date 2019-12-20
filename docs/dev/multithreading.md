# Multithreading

## Threads

- Main_gameLoop
    - waiting for shots
    - execute shots
    - write TurnResults
- GUI (Thread that starts other threads)
    - Handles user input
- GUI_PlaygroundUpdater
    - Updates all playgrounds
    - Permanently waiting for a new TurnResult (from both players)
    - Needed information: TurnResult, Player

 
 ## Thread communication
 
 ### GUI
 
 - GUI [Thread writer] -> nextShot [Object] -> Main_gameLoop [Thread reader]
 - Main_gameLoop [Thread writer] -> lastTurns [Object] -> GUI_PlaygroundUpdater [Thread reader]

### Network

 - Network_in [Thread writer] -> nextShot [Object] -> Main_gameLoop [Thread reader]
 - Main_gameLoop [Thread writer] -> lastTurns [Object] -> Network_out [Thread reader]
 
 ## Notice:
 
 - GUI thread is never a reader -> GUI never stalls
 