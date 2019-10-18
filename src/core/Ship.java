package core;

public class Ship extends PlaygroundElement{

    private int lives;
    private int id;

    public enum LifeStatus {
        SUNKEN, ALIVE
    }

    LifeStatus getStatus() {
        if(this.lives == 0)
            return LifeStatus.SUNKEN;
        else
            return LifeStatus.ALIVE;
    }

    @Override
    public void gotHit() {
        super.gotHit();
        this.lives--;
    }
}
