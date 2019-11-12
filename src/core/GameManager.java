package core;

import core.communication_data.*;

public class GameManager implements GameManagerInterface {

    /**
     * Normally local player which controls the GUI.
     * Except at AI vs AI.
     */
    private Player player1;

    /**
     * Normally AI or network player.
     * Except local vs local.
     * <p>
     * At the remote side when played over network the players would be swapped.
     */
    private Player player2;

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
    public PlaceShipResult moveShip(ShipID id, ShipPosition pos) {
        return currentPlayer.moveShip(id, pos);
    }

    @Override
    public boolean deleteShip(ShipID id) {
        return currentPlayer.deleteShip(id);
    }

    @Override
    public TurnResult shoot(Position position) {
        TurnResult res = this.shoot(this.currentPlayer, position);
        if (!res.isTURN_AGAIN() && !res.isFINISHED())
            this.nextPlayer();
        return res;
    }

    @Override
    public TurnResult getEnemyShot() {
        return null;
    }

    /**
     * Player is shooting at position
     *
     * @param player   Player which is currently shooting
     * @param position Where the shot is placed
     * @return TurnResult
     */
    private TurnResult shoot(Player player, Position position) {
        assert !position.isOutsideOfPlayground(this.getGameSettings().getSize()) : "Shot position must be on playground";
        TurnResult res = this.otherPlayer(player).gotHit(position);
        player.update(res.getSHOT_RESULT());
        return res;
    }

    private void nextPlayer() {
        if (currentPlayer == player1) currentPlayer = player2;
        else currentPlayer = player1;
    }

    private Player otherPlayer(Player player) {
        if (player == player1) return player2;
        else return player1;
    }


}
