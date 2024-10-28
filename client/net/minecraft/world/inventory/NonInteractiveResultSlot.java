package net.minecraft.world.inventory;

import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class NonInteractiveResultSlot extends Slot {
   public NonInteractiveResultSlot(Container var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public void onQuickCraft(ItemStack var1, ItemStack var2) {
   }

   public boolean mayPickup(Player var1) {
      return false;
   }

   public Optional<ItemStack> tryRemove(int var1, int var2, Player var3) {
      return Optional.empty();
   }

   public ItemStack safeTake(int var1, int var2, Player var3) {
      return ItemStack.EMPTY;
   }

   public ItemStack safeInsert(ItemStack var1) {
      return var1;
   }

   public ItemStack safeInsert(ItemStack var1, int var2) {
      return this.safeInsert(var1);
   }

   public boolean allowModification(Player var1) {
      return false;
   }

   public boolean mayPlace(ItemStack var1) {
      return false;
   }

   public ItemStack remove(int var1) {
      return ItemStack.EMPTY;
   }

   public void onTake(Player var1, ItemStack var2) {
   }

   public boolean isHighlightable() {
      return false;
   }

   public boolean isFake() {
      return true;
   }
}
