package core.communication_data;

public class TurnResult {

    private final ShotResult SHOT_RESULT;
    private final boolean TURN_AGAIN;
    private final boolean FINISHED;
    private final Error error;

    public TurnResult(ShotResult SHOT_RESULT, boolean TURN_AGAIN, boolean FINISHED, Error error) {
        this.SHOT_RESULT = SHOT_RESULT;
        this.TURN_AGAIN = TURN_AGAIN;
        this.FINISHED = FINISHED;
        this.error = error;
    }

    public TurnResult(ShotResult SHOT_RESULT, boolean TURN_AGAIN, boolean FINISHED) {
        this(SHOT_RESULT, TURN_AGAIN, FINISHED, Error.NONE);
    }

    public static TurnResult failure(Error error) {
        return new TurnResult(null, true, false, error);
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

    public enum Error {
        NONE, NOT_ON_PLAYGROUND, FIELD_ALREADY_DISCOVERED;
    }
}