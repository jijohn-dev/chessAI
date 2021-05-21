import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("jChessAI Engine");

        int depth = 5;
        boolean running = true;
        Scanner input = new Scanner(System.in).useDelimiter("\n");

        Position position = new Position();
        Engine engine = new Engine(depth);

        while (running) {
            System.out.print(">");
            String cmd = input.next();

            // play vs Engine
            switch (cmd) {
                case "help":
                    System.out.println("Available commands:");
                    System.out.println("[play] [pos] [reset] [eval] [depth] [test] [quit]");
                    break;
                // play against engine
                case "play":
                    Game game = new Game(5, position);
                    game.play();
                    break;
                // load a position
                case "pos":
                    System.out.print("Enter FEN: ");
                    String fen = input.next();
                    position = new Position(fen);
                    position.printBoard();
                    break;
                // evaluate current position
                case "eval":
                    System.out.println(engine.eval(position, Engine.MINIMAX_ABO));
                    break;
                // testing
                case "test":
                    System.out.print("Generation depth: ");
                    int generationDepth = Math.min(5, Integer.parseInt(input.next()));

                    System.out.print("Number of test positions: ");
                    int numTestPositions = Integer.parseInt(input.next());

                    System.out.print("Engine evaluation depth: ");
                    int evalDepth = Math.min(5, Integer.parseInt(input.next()));

                    System.out.print("Minimax versions [mpo]: ");
                    String versions = input.next();

                    Test.runTests(generationDepth, numTestPositions, evalDepth, versions);
                    System.out.println("Tests completed");
                    break;
                case "reset":
                    position.reset();
                    break;
                // quit
                case "quit":
                    running = false;
                    break;
                // invalid command
                default:
                    System.out.println("Command not recognized");
                    System.out.println("use <help> to show available commands");
            }
        }
    }
}
