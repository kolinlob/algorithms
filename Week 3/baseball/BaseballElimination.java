/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-12-27
 *  Description: Immutable data type that represents a sports division and
 *  determines which teams are mathematically eliminated
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BaseballElimination {
    private final HashMap<String, Standing> division;
    private final HashMap<String, ArrayList<String>> eliminations;
    private final String[] teams;

    public BaseballElimination(String filename) {
        In file = new In(filename);
        int divisionSize = Integer.parseInt(file.readLine());
        division = new HashMap<>();
        eliminations = new HashMap<>();
        teams = new String[divisionSize];

        while (!file.isEmpty()) {
            String raw = file.readLine();
            processInputLine(raw.trim().split(" +"));
        }
    }

    private void processInputLine(String[] line) {
        int[] parsed = Arrays.stream(Arrays.copyOfRange(line, 1, line.length))
                             .mapToInt(Integer::parseInt)
                             .toArray();

        int[] games = Arrays.copyOfRange(parsed, 3, line.length);

        teams[division.size()] = line[0];
        division.put(line[0],
                     new Standing(division.size(), parsed[0], parsed[1], parsed[2], games));
    }

    public int numberOfTeams() {
        return division.size();
    }

    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }

    public int wins(String team) {
        validate(team);
        return division.get(team).wins;
    }

    public int losses(String team) {
        validate(team);
        return division.get(team).losses;
    }

    public int remaining(String team) {
        validate(team);
        return division.get(team).remained;
    }

    public int against(String team1, String team2) {
        validate(team1);
        validate(team2);

        Standing standing1 = division.get(team1);
        Standing standing2 = division.get(team2);
        return standing1.remainingGames[standing2.index];
    }

    public boolean isEliminated(String team) {
        validate(team);
        return trivially(team) || nonTrivially(team);
    }

    public Iterable<String> certificateOfElimination(String team) {
        validate(team);

        if (!eliminations.containsKey(team) && !division.get(team).checked)
            isEliminated(team);

        return eliminations.get(team);
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team))
                    StdOut.print(t + " ");
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

    private void validate(String team) {
        if (!division.containsKey(team))
            throw new IllegalArgumentException("Team " + team + " not found");
    }

    private boolean trivially(String team) {
        Standing std = division.get(team);
        std.markChecked();

        int bestChance = std.wins + std.remained;
        boolean result = false;
        for (String other : teams()) {
            if (other.equalsIgnoreCase(team))
                continue;

            Standing oth = division.get(other);
            if (oth.wins > bestChance) {
                result = true;
                addEliminationCert(team, other);
            }
        }
        return result;
    }

    private boolean nonTrivially(String team) {
        int teamsCount = division.size() - 1;
        int games = (int) choose(division.size() - 1, 2);

        FlowNetwork network = new FlowNetwork(2 + games + teamsCount);
        Standing x = division.get(team);

        int vc = 1;
        int tc = 0;
        for (String t : teams()) {
            if (team.equalsIgnoreCase(t))
                continue;

            Standing i = division.get(t);
            for (int j = 0; j < teamsCount; j++) {
                if (j == x.index || j >= i.index)
                    continue;

                network.addEdge(new FlowEdge(0, vc, i.remainingGames[j]));

                int t1 = 1 + games + (i.index > x.index ? i.index - 1 : i.index);
                int t2 = 1 + games + (j > x.index ? j - 1 : j);
                network.addEdge(new FlowEdge(vc, t1, Double.POSITIVE_INFINITY));
                network.addEdge(new FlowEdge(vc, t2, Double.POSITIVE_INFINITY));
                vc++;
            }

            network.addEdge(new FlowEdge(1 + games + tc,
                                         network.V() - 1,
                                         x.wins + x.remained - i.wins));
            tc++;
        }

        FordFulkerson flow = new FordFulkerson(network, 0, network.V() - 1);
        boolean result = false;

        for (FlowEdge e : network.adj(0))
            if (e.capacity() > e.flow())
                result = true;

        if (result) {
            for (FlowEdge e : network.adj(network.V() - 1)) {
                if (flow.inCut(e.from())) {
                    int ix = (e.from() - games) > division.get(team).index
                             ? e.from() - games
                             : e.from() - games - 1;
                    addEliminationCert(team, teams[ix]);
                }
            }
        }

        return result;
    }

    private void addEliminationCert(String team, String other) {
        if (eliminations.containsKey(team))
            eliminations.get(team).add(other);
        else {
            ArrayList<String> n = new ArrayList<String>();
            n.add(other);
            eliminations.put(team, n);
        }
    }

    private long choose(long n, long k) {
        long numerator = 1;
        long denominator = 1;

        for (long i = n; i >= (n - k + 1); i--)
            numerator *= i;

        for (long i = k; i >= 1; i--)
            denominator *= i;

        return (numerator / denominator);
    }

    private class Standing {
        private final int index;
        private final int wins;
        private final int losses;
        private final int remained;
        private final int[] remainingGames;
        private boolean checked = false;

        public Standing(int index, int wins, int losses, int remCount, int[] games) {
            this.index = index;
            this.wins = wins;
            this.losses = losses;
            this.remained = remCount;
            this.remainingGames = Arrays.copyOf(games, games.length);
        }

        public void markChecked() {
            this.checked = true;
        }
    }
}
