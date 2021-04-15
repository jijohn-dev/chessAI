import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Openings {

    public List<String[]> openings;

    public Openings() {
        openings = new ArrayList<>();
        // read openings from file
        Path test_path = Paths.get(System.getProperty("user.dir"), "src/openingsDB.txt");
        Charset charset = StandardCharsets.ISO_8859_1;

        try {
           List<String> data = Files.readAllLines(test_path, charset);
           for (int i = 1; i < data.size(); i += 2) {
               String[] opening = data.get(i).split(" ");
               openings.add(opening);
           }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // return random move from openings db, empty string if no move
    public String getMove(List<String> moves) {
        List<String> nextMoves = new ArrayList<>();
        for (String[] opening : openings) {
            if (opening.length > moves.size()) {
                for (int i = 0; i < moves.size(); i++) {
                    if (!moves.get(i).equals(opening[i])) {
                        break;
                    }
                    if (i == moves.size() - 1) {
                        nextMoves.add(opening[i+1]);
                    }
                }
            }
        }
        if (nextMoves.size() == 0) {
            return "";
        }

        Random rand = new Random();
        int r = rand.nextInt(nextMoves.size());

        return nextMoves.get(r);
    }
}
