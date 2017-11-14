package nimm;

public class MoveAndScore implements Comparable<MoveAndScore>{

    private final Move move;
    private final int score;

    public MoveAndScore(Move move, int score) {
        this.move = move;
        this.score = score;
    }

    public Move getMove() {
        return move;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(MoveAndScore o) {
        MoveAndScore other = o;
        if (this.score < other.score) {
            return -1;
        } else if (this.score > other.score) {
            return 1;
        }
        return 0;
    }
}
