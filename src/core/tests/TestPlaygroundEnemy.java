package core.tests;

import core.PlaygroundEnemy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPlaygroundEnemy {

    private PlaygroundEnemy playground;

    @BeforeEach
    void cleanPlayground(){
        playground = new PlaygroundEnemy(6);
    }

    @Test
    void updateField_normal(){
        // TODO
    }
}
