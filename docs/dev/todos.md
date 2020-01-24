# TODO's ✓

- settings for inGame sounds
- design GameOver
- design
- full screen
- tests
- to jar
- test in eclipse
- plan for presentation

- (statistics)
- (multi language support) 

## Documentation

- general documentation about the project
    - structure of the project
- java docs
- class diagram
- user manual
- (etc.)


## General

- (cleanup code)
- Check logging messages (reduce duplicates)
    - Maybe add LoggerAI


Reproduce:
- new game
    - AI vs AI: Medium + Medium
    - 20
- Main menu after gameover
- Play again with same settings

Hypotheses: Something isn't reset properly when game is finished.

Exception in thread "Main_gameLoop" java.lang.AssertionError: ShipID is not in HashMap. id=ShipID{id=136}
	at core.playgrounds.Playground.removeShipByID(Playground.java:162)
	at core.playgrounds.PlaygroundBuildUp.setShip(PlaygroundBuildUp.java:46)
	at core.playgrounds.PlaygroundEnemyBuildUp.update(PlaygroundEnemyBuildUp.java:33)
	at core.Player.update(Player.java:51)
	at player.PlayerAI.update(PlayerAI.java:219)
	at core.GameManager.turn(GameManager.java:183)
	at core.GameManager.turnLoop(GameManager.java:240)
	at core.GameManager.lambda$gameLoop$0(GameManager.java:209)
	at java.base/java.lang.Thread.run(Thread.java:834)