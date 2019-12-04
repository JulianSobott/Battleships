# Logging info

## How to log a message:

{topic}.{level}(message)

e.g.
LoggerNetwork.info("Start connecting to pc: address=" + address);
LoggerState.info("TURN PLAYER " + player);

## Message structure

\[TOPIC] \[LEVEL] MESSAGE \[TIME] (CODE POSITION) (THREAD_NAME)


### LEVEL:

Priority of a message

- DEBUG
- INFO: showing completion, progress of the program. e.g. Turn start
- WARNING: error messages which do not cause a functional failure. e.g. try catch block that was handled
- ERROR: lose of important functionality

**TOPIC:**

Module where the message was logged.

- State
- System
- GUI
- Network
- Logic


# TODO List

## State

State of the program

- Start program
- Switch game state
- Turn finished
- Player which begins
- Game finished
- Saved game
- Load game

## System

- java version
- OS

## Network

- Connection status
    - Start connection to XXX
    - Successfully connected to XXX
    - Close Connection to XXX
- Message received
- Message send


## Logic

- Placements
- Shooting

## GUI

