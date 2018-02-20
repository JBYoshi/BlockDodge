package jbyoshi.blockdodge.util;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public abstract class HighScoreTracker {
    public static final HighScoreTracker INSTANCE;

    static {
        int fixed = Integer.getInteger("jbyoshi.blockdodge.dev.fixed-high-score", -1);
        if (fixed > 0) {
            INSTANCE = new HighScoreTracker() {
                @Override public int getHighScore() {
                    return fixed;
                }

                @Override public void setHighScore(int highScore) {
                    System.out.println("Not changing high score in development mode");
                }
            };
        } else {
            Preferences prefs = Preferences.userRoot().node("jbyoshi/blockdodge");
            INSTANCE = new HighScoreTracker() {
                @Override public int getHighScore() throws IOException {
                    try {
                        prefs.sync();
                        return prefs.getInt("highScore", 0);
                    } catch (BackingStoreException e) {
                        throw new IOException(e);
                    }
                }

                @Override public void setHighScore(int highScore) throws IOException {
                    try {
                        prefs.putInt("highScore", Math.max(getHighScore(), highScore));
                        prefs.flush();
                    } catch (BackingStoreException e) {
                        throw new IOException(e);
                    }
                }
            };
        }
    }

    public abstract int getHighScore() throws IOException;

    public abstract void setHighScore(int highScore) throws IOException;
}
