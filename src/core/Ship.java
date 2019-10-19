package core;

public class Ship extends PlaygroundElement{

    private int lives;
    private int id;

    private static int nextID = 0;

    public enum LifeStatus {
        SUNKEN, ALIVE
    }

    Ship(int lives){
        this.lives = lives;
        this.id = this.generateID();
    }

    private int generateID() {
        return Ship.nextID++;
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

    public int getId() {
        return id;
    }
}
