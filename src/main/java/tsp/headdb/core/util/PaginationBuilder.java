package tsp.headdb.core.util;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import tsp.invlib.gui.GUI;
import tsp.invlib.gui.button.control.ControlButton;
import tsp.invlib.gui.page.Page;
import tsp.invlib.gui.page.PageBuilder;

import java.util.function.BiConsumer;

/**
 * @author TheSilentPro (Silent)
 */
public class PaginationBuilder extends PageBuilder {

    public PaginationBuilder(GUI gui) {
        super(gui);
    }

    @Override
    public PaginationBuilder parentGui(GUI gui) {
        super.parentGui(gui);
        return this;
    }

    @Override
    public PaginationBuilder name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public PaginationBuilder onControlClick(BiConsumer<ControlButton, InventoryClickEvent> event) {
        super.onControlClick(event);
        return this;
    }

    @Override
    public Page build() {
        preventClick();
        includeControlButtons();
        onControlClick((button, event) -> Sounds.PAGE_CHANGE.play((Player) event.getWhoClicked()));
        return super.build();
    }

}