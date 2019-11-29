package core;

import core.communication_data.*;

public class GameManager implements GameManagerInterface {

    private Player player1, player2;
    private Player currentPlayer;


    @Override
    public boolean connectToServer(String ip, int port) {
        return false;
    }

    @Override
    public GameSettings getGameSettings() {
        return null;
    }

    @Override
    public NewGameResult newGame(GameSettings settings) {
        this.player1 = settings.getP1();
        this.player2 = settings.getP2();
        // TODO: Set current player properly
        this.currentPlayer = player1;
        ShipList shipList = ShipList.fromSize(settings.getPlaygroundSize());
        return new NewGameResult(shipList);
    }

    @Override
    public PlaceShipResult placeShip(ShipPosition pos) {
        return currentPlayer.placeShip(pos);
    }

    @Override
    public PlaceShipsRandomRes placeShipsRandom() {
        return this.currentPlayer.placeShipsRandom();
    }

    @Override
    public PlaceShipResult moveShip(ShipID id, ShipPosition pos) {
        return currentPlayer.moveShip(id, pos);
    }

    @Override
    public boolean deleteShip(ShipID id) {
        return currentPlayer.deleteShip(id);
    }

    @Override
    public TurnResult ownTurn(Position position){
        // P1
        if(this.allowedToShoot(this.player1)){
            ShotResult resShot = this.player2.gotHit(position);
            this.player1.update(resShot);
            boolean isFinished = this.player1.allEnemyShipsSunken();
            boolean shootAgain = resShot.getType() == Playground.FieldType.SHIP;
            TurnResult resTurn = new TurnResult(resShot, shootAgain, isFinished);
            if(!shootAgain && !isFinished){
                // TODO: start thread: makeTurn
            }
            return resTurn;
        }else{
            return
        }

    }

    private boolean allowedToShoot(Player p){
        if(this.currentPlayer != p) return false;
        return true;
    }

}
