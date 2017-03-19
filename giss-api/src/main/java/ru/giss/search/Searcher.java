package ru.giss.search;

import ru.giss.model.Address;
import ru.giss.search.score.ScoreCounter;
import ru.giss.search.score.SimpleScoreCounter;

import java.util.*;

import static ru.giss.util.StringUtil.nGrams;

/**
 * @author Ruslan Izmaylov
 */
public class Searcher {

    private final static List<Address> EMPTY_POSTING = new ArrayList<>(0);

    private Map<String, ArrayList<Address>> index;
    private ArrayList<Address> nodes;
    private ScoreCounter scoreCounter;

    private int gramLength;

    public Searcher(Map<String, ArrayList<Address>> index,
                    ArrayList<Address> nodes) {
        this(index, nodes, new SimpleScoreCounter());
    }

    public Searcher(Map<String, ArrayList<Address>> index,
                    ArrayList<Address> nodes,
                    ScoreCounter scoreCounter) {
        this.index = index;
        this.nodes = nodes;
        this.scoreCounter = scoreCounter;
        this.gramLength = index.keySet().iterator().next().length();
    }

    public ArrayList<Match> search(SearchRequest req) {
        String[] qGrams = nGrams(gramLength, req.getText());
        // Initializing stuff
        int maxError = Math.min(2, qGrams.length - 1); // TODO think about a better value
        int[] postPointers = new int[qGrams.length];
        int postingsExhausted = 0;
        List<Address>[] postings = new ArrayList[qGrams.length];
        for (int i = 0; i < qGrams.length; i++) {
            ArrayList<Address> optPosting = index.get(qGrams[i]);
            if (optPosting == null) {
                postingsExhausted++;
                postings[i] = EMPTY_POSTING;
            } else {
                postings[i] = optPosting;
            }
        }
        Arrays.fill(postPointers, -1);
        // a heap with score sorting might be better
        ArrayList<Match> matches = new ArrayList<>();
        // Traversing postings
        while (postingsExhausted <= maxError) {
            postingsExhausted = 0;
            // finding (maxError + 1)-th highest doc id that
            // is next to the pointers
            // allocating and sorting an array is not the best
            // decision - it could be optimized
            int interestingDocId;
            {
                int[] array = new int[postings.length];
                for (int i = 0; i < postings.length; i++) {
                    if (postPointers[i] < postings[i].size() - 1) {
                        array[i] = postings[i].get(postPointers[i] + 1).getId();
                    } else {
                        array[i] = Integer.MAX_VALUE;
                    }
                }
                Arrays.sort(array);
                interestingDocId = array[array.length - maxError - 1];
            }
            // traversing postings until an interesting doc
            int matchedGramCount = 0;
            for (int i = 0; i < postings.length; i++) {
                List<Address> posting = postings[i];
                // surprisingly, binary search works slower
                // it's likely because of random array accesses
                // which cause cache misses
//                List<Integer> posting = postings[i];
//                int l = postPointers[i] == -1 ? 0 : postPointers[i];
//                int r = posting.size() - 1;
//                while (l < r) {
//                    int m = l + (r - l) / 2;
//                    int postingMth = posting.get(m);
//                    if (postingMth > interestingDocId) {
//                        r = m - 1;
//                    } else if (postingMth < interestingDocId) {
//                        l = m + 1;
//                    } else {
//                        l = m;
//                        break;
//                    }
//                }
//                postPointers[i] = l;
                int cur = postPointers[i];
                while (cur < posting.size() - 1 && posting.get(cur + 1).getId() <= interestingDocId) {
                    cur++;
                }
                if (cur >= posting.size() - 1) {
                    postingsExhausted++;
                }
                if (cur != -1 && posting.get(cur).getId() == interestingDocId) {
                    matchedGramCount++;
                }
                postPointers[i] = cur;
            }
            if (qGrams.length - matchedGramCount <= maxError) {
                Address doc = nodes.get(interestingDocId);
                matches.add(new Match(doc, scoreCounter.count(req, matchedGramCount, doc)));
            }
        }
        matches.sort((m1, m2) -> m2.getScore() - m1.getScore());
        return matches;
    }
}
