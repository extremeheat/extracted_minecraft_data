package net.minecraft.client.gui.spectator.categories;

import com.google.common.base.MoreObjects;
import java.util.List;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;

public class SpectatorPage {
   public static final int NO_SELECTION = -1;
   private final List<SpectatorMenuItem> items;
   private final int selection;

   public SpectatorPage(List<SpectatorMenuItem> var1, int var2) {
      super();
      this.items = var1;
      this.selection = var2;
   }

   public SpectatorMenuItem getItem(int var1) {
      return var1 >= 0 && var1 < this.items.size() ? (SpectatorMenuItem)MoreObjects.firstNonNull((SpectatorMenuItem)this.items.get(var1), SpectatorMenu.EMPTY_SLOT) : SpectatorMenu.EMPTY_SLOT;
   }

   public int getSelectedSlot() {
      return this.selection;
   }
}
