/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-12-13
 *  Description: Class for detection of Outcast word in a given words collection
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet w;

    public Outcast(WordNet wordnet) {
        w = wordnet;
    }

    public String outcast(String[] nouns) {
        String result = "";
        int max = 0;
        for (String noun1 : nouns) {
            int d = 0;
            for (String noun2 : nouns)
                d += w.distance(noun1, noun2);
            if (max < d) {
                max = d;
                result = noun1;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
