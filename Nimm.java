package nimm;

// This is an adapted version of code written by Ron Grau, University of Sussex

import java.util.*;

/**
 * NIMM!
 * 
 * @author  Ron Grau
 * @version 1.1
 * @since 25/10/2015 
 * 
 * This game works as follows:
 * The human player picks a pile size. The program then calculates a sensible maximum number of tokens 
 * that may be taken by any player in any single turn.
 * The players take turns in grabbing tokens from the pile. Any player can take any quantity that is less or equal than the maximum. 
 * So for instance, if the maximum bound turns out as 4,  player can take 4,3,2, or 1 token(s).
 * However, throughout the game, each distinct quantity may only be taken once. In other words, one cannot repeatedly take 
 * the same quantity in several turns of the same game
 * Whoever can make the last pick wins, such that the pile is empty, or the opponent is stuck and cannot move anymore. 
 */
public class Nimm {
    private GameState currentGameState;
    static int[] humanHistory; // previous turns of the human player
    static int[] AIHistory; // previous turns of the AI player
    static ArrayList<MoveAndScore> successorMoves;

    private int getAIMove() {
        successorMoves = new ArrayList<>();
        simulate();
        return getBestMove();
    }

    private int getBestMove() {
        Move best = Collections.min(successorMoves).getMove();
        return best.getPick();
    }

    private void simulate() {
        minimax(0, 2);
    }

    public int minimax(int depth, int player) {
        if (player == 1 && (currentGameState.getCount() == 0 || playerStuck(currentGameState.getHumanState()))) {
            return -10;
        } else if (player == 2 && (currentGameState.getCount() == 0 || playerStuck(currentGameState.getAiState()))) {
            return 10;
        }

        ArrayList<Move> moves = getPossibleMoves(player);
        ArrayList<Integer> scores = new ArrayList<>();

      // System.out.println("empty: " + moves.isEmpty());
      // System.out.println("size: " + moves.size());
      // System.out.println("depth: " + depth);
      // System.out.println();
        if (!moves.isEmpty()) {
            moves.forEach((move) -> {
                int score;

                if (player == 1) {
                    makeMove(move, 1);
                    score = minimax(depth+1, 2);

                    scores.add(score);

                    if (depth == 0) {
                        successorMoves.add(new MoveAndScore(move, score));
                    }
                } else if (player == 2) {
                    makeMove(move, 2);
                    score = minimax(depth+1,1);

                    scores.add(score);

                    if (depth == 0) {
                        successorMoves.add(new MoveAndScore(move, score));
                    }
                }

                currentGameState.undoMove(move, player);
            });
        }
        return player == 1 ? Collections.max(scores) : Collections.min(scores);
    }

    private void makeMove(Move move, int player) {
        if (player == 1) {
            currentGameState.update(move, player);
        } else {
            currentGameState.update(move, player);
        }
    }

    private ArrayList<Move> getPossibleMoves(int player) {
        int[] playerHistory = player == 1 ?
                currentGameState.getHumanState() :
                currentGameState.getAiState();

        ArrayList<Move> possibleMoves = new ArrayList<>();

        for (int i = 0; i < playerHistory.length; i++) {
            if (playerHistory[i] == 0 && i <= currentGameState.getCount()) {
                if (player == 1) {
                    possibleMoves.add(new Move(i+1));
                } else {
                    possibleMoves.add(new Move(i+1)) ;
                }
            }
        }
        return possibleMoves;
    }

    private String getUserCommand() {
        System.out.print("Please enter the number of tokens in the pile, or 'q' to quit: ");
        Scanner scanner = new Scanner(System.in);
        String s = scanner.next();
        return s;
    }
    
    public void processUserCommand(String c) {
        try {
            int count = Integer.parseInt(c);
            currentGameState = new GameState(count, count);
            humanHistory = new int[currentGameState.getBound()+1];
            AIHistory = new int[currentGameState.getBound()+1];
            System.out.println();
            System.out.println("The pile has " +count+ " tokens.");
            System.out.print("The maximum you may take in any turn is "+currentGameState.getBound());
        } catch(Exception e) {
            System.out.print("Good Bye!");
            System.exit(0);
        }  
    }
    
    private boolean playerStuck(int[] history) {
         for (int i=1;i < history.length; i++) {
                // if there is any valid move possible, go on
                if(currentGameState.getCount() - i >= 0 && 1 == Math.abs(history[i]-1)) {
                   // Hint: // System.out.println("Player could take " +i+ " tokens.");
                   return false;
                }
            }
            return true;
    }
    
    private void play(String humanName) {
        Scanner humanInput = new Scanner(System.in);
        int humanMove = 0;
        int AImove = -1;
        
        while(currentGameState.getCount() > 0) {
            
            // ** HUMAN MOVE **
            // check if a move can be made at all
            if(playerStuck(currentGameState.getHumanState())) {
                System.out.println("\n* "+humanName+" can't move anymore. \n\n** The AI wins! **");
                break;
            }
            // a move can be made; elicit input
            System.out.print("\nHow many do you want? ");
            humanMove = humanInput.nextInt();
            
            // input validity checks
            if( humanMove < 1) {
                System.out.println("You have to take at least one token per turn.");
                continue;
            }
            if( humanMove > currentGameState.getBound()) {
                System.out.println("You can't take that many tokens! (Maximum "+currentGameState.getBound()+")");
                continue;
            }
            if( currentGameState.getCount()-humanMove < 0) {
                System.out.println("There aren't that many tokens left in the pile!");
                continue;
            }
            if(humanHistory[humanMove] == 1) {
                System.out.println("You cannot take the same amount twice!");
                continue;
            }
            // move appears to  be valid; make move
            currentGameState.update(new Move(humanMove), 1);
            System.out.println(currentGameState.getCount() + " tokens left");
            System.out.println("* "+humanName+" takes " + humanMove);

            // terminal test for human player
            if ( currentGameState.getCount() == 0 ) {
                System.out.println("\n* The pile is empty. \n\n** "+humanName+" wins! **");
                break;
            }
            
            // ** AI MOVE **
            // stuck?
            if(playerStuck(currentGameState.getAiState())) {
               System.out.println("\n* The AI player can't move anymore. \n\n** "+humanName+" wins! **");
               break;
            }
            
            AImove = getAIMove();
            currentGameState.update(new Move(AImove), 2);
            System.out.println("* AI takes " + AImove);
            System.out.println(currentGameState.getCount() + " tokens left");
            AIHistory[AImove] = 1;
            
            // terminal test for AI player
            if ( currentGameState.getCount() == 0 ) {
                System.out.println("\n* The pile is empty. \n\n** The AI wins! **");
                break;
            }
        }
        System.out.println("\nWould you like to play again?");
        processUserCommand(getUserCommand());
        play(humanName);
    }
      
    public static void main(String[] args) {
        Scanner humanInput = new Scanner(System.in);
        System.out.println("\n**********************");
        System.out.println("Hello, welcome to Nimm!");
        System.out.print("What is your name? ");
        String humanName = humanInput.next();
        Nimm nimm = new Nimm();
        nimm.processUserCommand(nimm.getUserCommand());
        nimm.play(humanName);
    }
}