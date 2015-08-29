package jbyoshi.blockdodge;

import java.util.prefs.*;

final class Data {
	private final Preferences system = Preferences.userNodeForPackage(BlockDodge.class);

	Data() {
		try {
			system.sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	synchronized void updateHighScore(int score) throws BackingStoreException {
		system.putInt("highScore", Math.max(getHighScore(), score));
		system.flush();
	}

	synchronized int getHighScore() throws BackingStoreException {
		system.sync();
		return system.getInt("highScore", 0);
	}
}
