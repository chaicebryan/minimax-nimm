package nimm;

public class GameState {

    private int[] humanState;
    private int[] aiState;
    private int count;
    private int pile;
    private int bound;

    public GameState(int count, int pile) {
        this.count = count;
        this.pile = pile;
        calcUpperBound();
        humanState = new int[bound];
        aiState = new int[bound];
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void update(Move move, int player) {
        if (player == 1)    {
            humanState[move.getPick()-1] = 1;
        } else {
            aiState[move.getPick()-1] = 1;
        }
        count -= move.getPick();
    }

    public void undoMove(Move move, int player) {
        if (player == 1)    {
            humanState[move.getPick()-1] = 0;
        } else {
            aiState[move.getPick()-1] = 0;
        }
        count += move.getPick();
    }

    public int getBound() {
        return bound;
    }

    public void setBound(int bound) {
        this.bound = bound;
    }

    public int[] getHumanState() {
        return humanState;
    }

    public void setHumanState(int[] humanState) {
        this.humanState = humanState;
    }

    public int[] getAiState() {
        return aiState;
    }

    public void setAiState(int[] aiState) {
        this.aiState = aiState;
    }

    public int getPile() {
        return pile;
    }

    public void setPile(int pile) {
        this.pile = pile;
    }

    private void calcUpperBound() {
        int temp =0;
        while(temp + bound + 1 < pile ) {
            bound++;
            temp+=bound;
        }
    }
}
