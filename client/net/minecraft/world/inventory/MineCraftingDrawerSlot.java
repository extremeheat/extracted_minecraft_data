package net.minecraft.world.inventory;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WorldModifiers;

public class MineCraftingDrawerSlot extends Slot {
   final MineCraftingMenu menu;

   public MineCraftingDrawerSlot(Container var1, int var2, int var3, int var4, MineCraftingMenu var5) {
      super(var1, var2, var3, var4);
      this.menu = var5;
   }

   public boolean mayPlace(ItemStack var1) {
      return true;
   }

   public boolean mayPickup(Player var1) {
      boolean var2 = this.couldBeAddedToMine();
      if (!var2) {
         this.menu.setCarried(ItemStack.EMPTY);
      }

      return super.mayPickup(var1) && var2;
   }

   public boolean couldBeAddedToMine() {
      if (this.getItem().isEmpty()) {
         return true;
      } else {
         WorldModifiers var1 = (WorldModifiers)this.getItem().get(DataComponents.WORLD_MODIFIERS);
         if (var1 == null) {
            return false;
         } else {
            return this.menu.getCraftingEffects().allMatch((var1x) -> var1x.isValidWith(var1.effects())) && this.menu.getCraftingEffects().noneMatch((var1x) -> var1.effects().contains(var1x));
         }
      }
   }

   public void onTake(Player var1, ItemStack var2) {
      this.set(var2.copyWithCount(1));
   }

   public void setByPlayer(ItemStack var1, ItemStack var2) {
      if (!var2.isEmpty()) {
         this.set(var2.copyWithCount(1));
      }

   }

   public boolean isActive() {
      return super.isActive() && (!this.menu.isBossMine() || this.menu.isMineCompleted());
   }
}
