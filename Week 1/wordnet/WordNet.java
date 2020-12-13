/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-12-11
 *  Description: WordNet digraph where each vertex v is an integer that
 *  represents a synset, and each directed edge v→w represents that w is a
 *  hypernym of v. The WordNet digraph is a rooted DAG: it is acyclic and has
 *  one vertex—the root—that is an ancestor of every other vertex.
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Topological;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class WordNet {

    private final Digraph digraph;
    private final SAP sap;
    private final HashMap<String, HashSet<Integer>> nouns;
    private final HashMap<Integer, String> synsets;

    public WordNet(String synsets, String hypernyms) {
        if (synsets == null) {
            throw new IllegalArgumentException("called WordNet() with a null synsets");
        }

        if (hypernyms == null) {
            throw new IllegalArgumentException("called WordNet() with a null hypernyms");
        }

        this.nouns = new HashMap<String, HashSet<Integer>>();
        this.synsets = new HashMap<>();

        In s = new In(synsets);
        while (!s.isEmpty()) {
            String[] line = s.readLine().split(",");

            int id = Integer.parseInt(line[0]);
            String[] ns = line[1].split(" ");

            this.synsets.put(id, line[1]);

            for (String noun : ns) {
                if (nouns.containsKey(noun)) {
                    nouns.get(noun).add(id);
                }
                else {
                    HashSet<Integer> ids = new HashSet<Integer>();
                    ids.add(id);
                    nouns.put(noun, ids);
                }
            }
        }

        HashMap<Integer, int[]> hdict = new HashMap<Integer, int[]>();
        In h = new In(hypernyms);
        while (!h.isEmpty()) {
            int[] line = Arrays.stream(h.readLine().split(","))
                               .mapToInt(Integer::parseInt)
                               .toArray();

            hdict.put(line[0], Arrays.copyOfRange(line, 1, line.length));
        }

        digraph = new Digraph(hdict.size());

        for (Map.Entry<Integer, int[]> hyp : hdict.entrySet()) {
            for (int edge : hyp.getValue()) {
                digraph.addEdge(hyp.getKey(), edge);
            }
        }

        Topological t = new Topological(digraph);
        if (!t.hasOrder()) {
            throw new IllegalArgumentException(
                    "provided digraph is not a rooted DAG");
        }

        sap = new SAP(digraph);
    }

    public Iterable<String> nouns() {
        return nouns.keySet();
    }

    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("called isNoun() with a null word");
        }

        return nouns.containsKey(word);
    }

    public int distance(String nounA, String nounB) {
        if (nounA == null) {
            throw new IllegalArgumentException("called distance() with a null nounA");
        }

        if (nounB == null) {
            throw new IllegalArgumentException("called distance() with a null nounB");
        }

        if (!isNoun(nounA)) {
            throw new IllegalArgumentException("called distance() with not a WordNet noun nounA");
        }

        if (!isNoun(nounB)) {
            throw new IllegalArgumentException("called distance() with not a WordNet noun nounB");
        }

        HashSet<Integer> idsA = nouns.get(nounA);
        HashSet<Integer> idsB = nouns.get(nounB);

        return sap.length(idsA, idsB);
    }

    public String sap(String nounA, String nounB) {
        if (nounA == null) {
            throw new IllegalArgumentException("called sap() with a null nounA");
        }

        if (nounB == null) {
            throw new IllegalArgumentException("called sap() with a null nounB");
        }

        if (!isNoun(nounA)) {
            throw new IllegalArgumentException("called sap() with not a WordNet noun nounA");
        }

        if (!isNoun(nounB)) {
            throw new IllegalArgumentException("called sap() with not a WordNet noun nounB");
        }

        HashSet<Integer> idsA = nouns.get(nounA);
        HashSet<Integer> idsB = nouns.get(nounB);

        int ancestor = sap.ancestor(idsA, idsB);
        return this.synsets.get(ancestor);
    }

    public static void main(String[] args) {
        WordNet w = new WordNet(args[0], args[1]);
        StdOut.printf("V: %d, E: %d\n", w.digraph.V(), w.digraph.E());
    }
}
