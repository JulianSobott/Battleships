package core.communication_data;

public class NewGameResult {

    private final ShipList SHIP_LIST;

    public NewGameResult(ShipList shipList){
        this.SHIP_LIST = shipList;
    }

    public ShipList getSHIP_LIST() {
        return SHIP_LIST;
    }
}
