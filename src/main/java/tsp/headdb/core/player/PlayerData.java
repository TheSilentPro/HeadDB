package tsp.headdb.core.player;

import java.util.*;

/**
 * @author TheSilentPro (Silent)
 */
public class PlayerData {

    private final int id;
    private final UUID uuid;
    private String lang;
    private boolean soundEnabled;
    private final Set<Integer> favorites;

    public PlayerData(int id, UUID uuid, String lang, boolean soundEnabled, int... favorites) {
        this.id = id;
        this.uuid = uuid;
        this.lang = lang != null ? lang : "en";
        this.soundEnabled = soundEnabled;
        this.favorites = new HashSet<>();
        if (favorites != null) {
            for (int favorite : favorites) {
                this.favorites.add(favorite);
            }
        }
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLang() {
        return lang;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public void setLang(String lang) {
        this.lang = lang != null ? lang : "en";
    }

    public Set<Integer> getFavorites() {
        return favorites;
    }

    public boolean addFavorite(int favorite) {
        return this.favorites.add(favorite);
    }

    public boolean removeFavorite(int favorite) {
        return this.favorites.remove(favorite);
    }

}