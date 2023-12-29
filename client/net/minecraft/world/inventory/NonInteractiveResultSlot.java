package net.minecraft.world.inventory;

import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class NonInteractiveResultSlot extends Slot {
   public NonInteractiveResultSlot(Container var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void onQuickCraft(ItemStack var1, ItemStack var2) {
   }

   @Override
   public boolean mayPickup(Player var1) {
      return false;
   }

   @Override
   public Optional<ItemStack> tryRemove(int var1, int var2, Player var3) {
      return Optional.empty();
   }

   @Override
   public ItemStack safeTake(int var1, int var2, Player var3) {
      return ItemStack.EMPTY;
   }

   @Override
   public ItemStack safeInsert(ItemStack var1) {
      return var1;
   }

   @Override
   public ItemStack safeInsert(ItemStack var1, int var2) {
      return this.safeInsert(var1);
   }

   @Override
   public boolean allowModification(Player var1) {
      return false;
   }

   @Override
   public boolean mayPlace(ItemStack var1) {
      return false;
   }

   @Override
   public ItemStack remove(int var1) {
      return ItemStack.EMPTY;
   }

   @Override
   public void onTake(Player var1, ItemStack var2) {
   }

   @Override
   public boolean isHighlightable() {
      return false;
   }

   @Override
   public boolean isFake() {
      return true;
   }
}
