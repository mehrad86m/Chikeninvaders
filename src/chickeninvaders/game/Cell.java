package chickeninvaders.game;

public class Cell {
    public final int row;
    public final int col;
    public final int type;
    private int counter;
    private boolean occupied;

    public Cell(int row, int col, int type, int counter) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.counter = counter;
        this.occupied = true;
    }
    public int getType() {
        return type;
    }
    public int getCounter() { return counter; }

    public boolean onEnemyKilled() {
        counter--;
        occupied = false;
        return counter > 0;
    }

    public boolean isCleared() { return counter <= 0; }
    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
}
