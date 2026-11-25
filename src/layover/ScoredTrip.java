package layover;

import java.util.Map;

/** A trip plus its final 0-100 score and each factor's 0-1 score that fed into it. */
public class ScoredTrip {
    public final TripOption option;
    public final double score;
    public final Map<String, Double> factorScores;

    public ScoredTrip(TripOption option, double score, Map<String, Double> factorScores) {
        this.option = option;
        this.score = score;
        this.factorScores = factorScores;
    }
}