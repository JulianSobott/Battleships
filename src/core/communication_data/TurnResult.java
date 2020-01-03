package core.communication_data;

import core.Player;

public class TurnResult {

    private final ShotResult SHOT_RESULT;
    private final boolean TURN_AGAIN;
    private final boolean FINISHED;
    private final Error error;
    private final Player player;

    public TurnResult(Player player, ShotResult SHOT_RESULT, boolean TURN_AGAIN, boolean FINISHED, Error error) {
        assert !(FINISHED && TURN_AGAIN) : "Invalid parameters: TURN_AGAIN and FINISHED";
        this.SHOT_RESULT = SHOT_RESULT;
        this.TURN_AGAIN = TURN_AGAIN;
        this.FINISHED = FINISHED;
        this.error = error;
        this.player = player;
    }

    public TurnResult(Player player, ShotResult SHOT_RESULT, boolean TURN_AGAIN, boolean FINISHED) {
        this(player, SHOT_RESULT, TURN_AGAIN, FINISHED, Error.NONE);
    }

    public static TurnResult failure(Player player, Error error) {
        if(error == Error.NOT_YOUR_TURN)
            return new TurnResult(player, null, false, false, error);
        else
            return new TurnResult(player, null, true, false, error);
    }

    public ShotResult getSHOT_RESULT() {
        return SHOT_RESULT;
    }

    public boolean isTURN_AGAIN() {
        return TURN_AGAIN;
    }

    public boolean isFINISHED() {
        return FINISHED;
    }

    public Error getError() {
        return error;
    }

    public int getPlayerIndex() {
        return this.player.getIndex();
    }

    public enum Error {
        NONE, NOT_ON_PLAYGROUND, FIELD_ALREADY_DISCOVERED, NOT_YOUR_TURN;
    }

    @Override
    public String toString() {
        return "TurnResult{" +
                "SHOT_RESULT=" + SHOT_RESULT +
                ", TURN_AGAIN=" + TURN_AGAIN +
                ", FINISHED=" + FINISHED +
                ", error=" + error +
                '}';
    }
}