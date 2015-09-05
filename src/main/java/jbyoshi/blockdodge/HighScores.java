package jbyoshi.blockdodge;

import java.util.prefs.*;

final class HighScores {
	private static final Preferences prefs;

	static {
		prefs = Preferences.userNodeForPackage(BlockDodge.class);
		try {
			prefs.sync();
		} catch (BackingStoreException e) {
			throw new Error(e);
		}
	}

	static synchronized void updateHighScore(int score) throws BackingStoreException {
		prefs.putInt("highScore", Math.max(getHighScore(), score));
		prefs.flush();
	}

	static synchronized int getHighScore() throws BackingStoreException {
		prefs.sync();
		return prefs.getInt("highScore", 0);
	}

	private HighScores() {
	}

}
