package core.serialization;

import core.Player;
import core.utils.logging.LoggerLogic;
import player.PlayerAI;
import player.PlayerHuman;
import player.PlayerNetwork;

public enum PlayerType {
    HUMAN, AI, NETWORK;

    public static PlayerType fromPlayer(Player player) {
        Class cls = player.getClass();
        if (cls == PlayerHuman.class) return HUMAN;
        else if(cls == PlayerNetwork.class) return NETWORK;
        else if(cls == PlayerAI.class) return AI;
        LoggerLogic.error("Invalid player class (HUMAN was taken as default): class=" + cls + ", player=" + player);
        return HUMAN;
    }
}
