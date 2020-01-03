# Thoughts

- Some player need notification that they have to make a turn
    - need notification: AI
    - no need for notification: GUI, network
    - getShotPosition (Main_gameLoop)
        - either wait for input in Queue
        - or call make Turn


## Shooting

### GUI:

GUI.click [GUI] 
-> GameManager.addShot(Position, Player) [GUI] 
-> GameManger.getNextShotPosition [Main_gameLoop] 
-> GameManager.executeShot(Position, Player) [Main_gameLoop]
-> GameManager.saveTurnResult(Player, TurnResult) [Main_gameLoop]

-> GameManager.getTurnResult(Player) [GUI_playgrounds]
-> GUI.updatePlayground(Player, TurnResult) [GUI_playgrounds]

### AI

GameManager.getNextShotPosition [Main_gameLoop]
    -> Player.makeTurn [Main_gameLoop]
-> GameManager.executeShot(Position, Player) [Main_gameLoop]
-> GameManager.saveTurnResult(Player, TurnResult) [Main_gameLoop]

### Network

...

## Methods

### GameManager.addShot(Position, Player) [GUI] 

- Set Object
- Notify

### GameManager.executeShot(Position, Player) [Main_gameLoop]

- Only called when player is the current player (assert?)

-> Player.enemyPlayground.canShootAt(Position)
-> OtherPlayer.gotHit(Position) --> ShotResult
-> Player.enemyPlayground.update(TurnResult)

### GameManager.getNextShotPosition [Main_gameLoop]

-> if player.needsTrigger: player.makeTurn
-> else: wait till Object available

### GameManager.saveTurnResult(Player, TurnResult) [Main_gameLoop]

- Append TurnResult to Queue
- Notify