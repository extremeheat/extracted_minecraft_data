package net.minecraft.world.inventory;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WorldModifiers;

public class MineCraftingSlot extends Slot {
   public MineCraftingMenu menu;
   public boolean randomSlot;

   public MineCraftingSlot(Container var1, int var2, int var3, int var4, MineCraftingMenu var5, boolean var6) {
      super(var1, var2, var3, var4);
      this.randomSlot = var6;
      this.menu = var5;
   }

   public boolean mayPlace(ItemStack var1) {
      if (!this.menu.isMineActive() && !this.menu.isMineCompleted() && !this.menu.isBossMine() && !this.randomSlot) {
         if (!var1.is(Items.MINE_INGREDIENT)) {
            return false;
         } else {
            WorldModifiers var2 = (WorldModifiers)var1.get(DataComponents.WORLD_MODIFIERS);
            if (var2 == null) {
               return false;
            } else {
               List var3 = this.menu.getCraftingEffects().toList();
               return var2.effects().stream().allMatch((var1x) -> var1x.isValidWith(var3));
            }
         }
      } else {
         return false;
      }
   }

   public boolean mayPickup(Player var1) {
      return !this.menu.isMineActive() && !this.menu.isMineCompleted() && !this.menu.isBossMine() && !this.randomSlot ? super.mayPickup(var1) : false;
   }

   public void setChanged() {
      super.setChanged();
      this.menu.slotsChanged(this.container);
   }

   public boolean shouldMove() {
      return this.menu.isMineActive();
   }

   public boolean shouldRotate() {
      return this.menu.isMineActive();
   }

   public boolean isActive() {
      return super.isActive() && !this.menu.isMineCompleted() && !this.menu.isBossMine();
   }
}
