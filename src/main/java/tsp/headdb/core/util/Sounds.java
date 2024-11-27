package tsp.headdb.core.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * @author TheSilentPro (Silent)
 */
public enum Sounds {

    FAIL(Sound.BLOCK_ANVIL_LAND, 0.3f, 0.5f),
    SUCCESS(Sound.ENTITY_PLAYER_LEVELUP, 2f),
    PAGE_CHANGE(Sound.BLOCK_LEVER_CLICK, 0.5f, 1f),
    PAGE_OPEN(Sound.ENTITY_BAT_TAKEOFF, 1f),
    FAVORITE(Sound.ENTITY_ARROW_HIT_PLAYER, 1f),
    FAVORITE_REMOVE(Sound.ENTITY_ARROW_HIT_PLAYER, 2f);

    private final Sound sound;
    private final float volume;
    private final float pitch;

    Sounds(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    Sounds(Sound sound, float pitch) {
        this(sound, 1f, pitch);
    }

    public void play(Player player) {
        player.playSound(player, sound, volume, pitch);
    }

}
