package net.minecraft.client.gui.spectator.categories;

import com.google.common.base.MoreObjects;
import java.util.List;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;

public class SpectatorPage {
   private final SpectatorMenuCategory category;
   private final List items;
   private final int selection;

   public SpectatorPage(SpectatorMenuCategory var1, List var2, int var3) {
      this.category = var1;
      this.items = var2;
      this.selection = var3;
   }

   public SpectatorMenuItem getItem(int var1) {
      return var1 >= 0 && var1 < this.items.size() ? (SpectatorMenuItem)MoreObjects.firstNonNull(this.items.get(var1), SpectatorMenu.EMPTY_SLOT) : SpectatorMenu.EMPTY_SLOT;
   }

   public int getSelectedSlot() {
      return this.selection;
   }
}
