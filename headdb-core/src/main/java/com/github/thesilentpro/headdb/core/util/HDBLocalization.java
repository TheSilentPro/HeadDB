package com.github.thesilentpro.headdb.core.util;

import com.github.thesilentpro.headdb.core.HeadDB;
import com.github.thesilentpro.localization.paper.PaperLoader;
import com.github.thesilentpro.localization.paper.PaperLocalization;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class HDBLocalization extends PaperLocalization {

    private static final Logger LOGGER = LoggerFactory.getLogger(HDBLocalization.class);
    private final HeadDB plugin;

    public HDBLocalization(@NotNull HeadDB plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void sendTranslatedMessage(@NotNull UUID receiver, @NotNull Component message) {
        Entity entity = Bukkit.getEntity(receiver);
        if (entity == null) {
            throw new IllegalArgumentException("Invalid receiver with uuid: " + receiver.toString());
        } else {
            Compatibility.sendMessage(entity, message);
        }
    }

    public void init() {
        try {
            loadLanguages(new PaperLoader(HeadDB.class, "messages", new File(plugin.getDataFolder(), "messages")));
            //setConsoleLogFunction((level, message) -> LOGGER.atLevel(toSLF4JLevel(level)).log(ANSIComponentSerializer.ansi().serialize(message)));
        } catch (IOException ex) {
            LOGGER.error("Failed to load languages!", ex);
        }
    }

}