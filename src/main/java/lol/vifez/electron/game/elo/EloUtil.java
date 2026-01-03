package lol.vifez.electron.game.elo;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;

// NOTE: This elo calculation method was not coded by me since I am lazy af
public class EloUtil {

	private static final KFactor[] K_FACTORS = {
			new KFactor(0, 1000, 25),
			new KFactor(1001, 1400, 20),
			new KFactor(1401, 1800, 15),
			new KFactor(1801, 2200, 10)
	};

	private static final int DEFAULT_K_FACTOR = 25;
	private static final int WIN = 1;
	private static final int LOSS = 0;
	private static final int DEFAULT_ELO = 1000;

	public static int getNewRating(int rating, int opponentRating, boolean won) {
		return getNewRating(rating, opponentRating, won ? WIN : LOSS);
	}

	public static int getNewRating(int rating, int opponentRating, int score) {
		double kFactor = getKFactor(rating);
		double expectedScore = getExpectedScore(rating, opponentRating);
		int newRating = calculateNewRating(rating, score, expectedScore, kFactor);

		if (score == WIN && newRating == rating) {
			newRating++;
		}
		return newRating;
	}

	private static int calculateNewRating(int oldRating, int score, double expectedScore, double kFactor) {
		return oldRating + (int) (kFactor * (score - expectedScore));
	}

	private static double getKFactor(int rating) {
		for (KFactor kFactor : K_FACTORS) {
			if (rating >= kFactor.getStartIndex() && rating <= kFactor.getEndIndex()) {
				return kFactor.getValue();
			}
		}
		return DEFAULT_K_FACTOR;
	}

	private static double getExpectedScore(int rating, int opponentRating) {
		return 1 / (1 + Math.pow(10, ((double) (opponentRating - rating) / 400)));
	}

	public static int getGlobalElo(Profile profile) {
		int highest = DEFAULT_ELO;
		boolean found = false;

		for (Kit kit : Practice.getInstance().getKitManager().getKits().values()) {
			if (!kit.isRanked()) continue;

			int elo = profile.getElo(kit);

			if (elo == DEFAULT_ELO) continue;

			highest = Math.max(highest, elo);
			found = true;
		}

		return found ? highest : DEFAULT_ELO;
	}

	public static void setElo(Profile profile, int amount) {
		for (Kit kit : Practice.getInstance().getKitManager().getKits().values()) {
			if (kit.isRanked()) {
				profile.setElo(kit, amount);
			}
		}
	}

	public static void addElo(Profile profile, int amount) {
		for (Kit kit : Practice.getInstance().getKitManager().getKits().values()) {
			if (kit.isRanked()) {
				int newElo = profile.getElo(kit) + amount;
				profile.setElo(kit, newElo);
			}
		}
	}

	public static void resetElo(Profile profile) {
		setElo(profile, DEFAULT_ELO);
	}
}