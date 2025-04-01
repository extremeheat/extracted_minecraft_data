package net.minecraft.world.inventory;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MineCraftingResultSlot extends Slot {
   private final MineCraftingMenu menu;

   public MineCraftingResultSlot(Container var1, int var2, int var3, int var4, MineCraftingMenu var5) {
      super(var1, var2, var3, var4);
      this.menu = var5;
   }

   public boolean mayPlace(ItemStack var1) {
      return false;
   }

   public boolean mayPickup(Player var1) {
      ItemStack var2 = this.getItem();
      boolean var3 = var2.has(DataComponents.MINE_ACTIVE);
      Boolean var4 = (Boolean)var2.get(DataComponents.MINE_COMPLETED);
      if (!var3 && var4 == null) {
         if (var1 instanceof ServerPlayer) {
            ServerPlayer var5 = (ServerPlayer)var1;
            this.menu.onOpenMine(var5, var2);
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean shouldRotate() {
      return this.menu.isMineActive();
   }
}
