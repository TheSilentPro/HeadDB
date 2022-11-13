package tsp.headdb.core.hook;

import org.bukkit.Bukkit;

public class Hooks {

    public static final PluginHook PAPI = new PluginHook(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null);

}
