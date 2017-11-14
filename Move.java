package nimm;

import java.util.Comparator;

public class Move {

    private int score;
    private int pick;

    public Move(int pick) {
        this.pick = pick;
    }

    public int getPick() {
        return pick;
    }
}
