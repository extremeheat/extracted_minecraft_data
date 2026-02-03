package net.minecraft.world.inventory;

import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class NonInteractiveResultSlot extends Slot {
   public NonInteractiveResultSlot(final Container container, final int id, final int x, final int y) {
      super(container, id, x, y);
   }

   public void onQuickCraft(final ItemStack picked, final ItemStack original) {
   }

   public boolean mayPickup(final Player player) {
      return false;
   }

   public Optional<ItemStack> tryRemove(final int amount, final int maxAmount, final Player player) {
      return Optional.empty();
   }

   public ItemStack safeTake(final int amount, final int maxAmount, final Player player) {
      return ItemStack.EMPTY;
   }

   public ItemStack safeInsert(final ItemStack stack) {
      return stack;
   }

   public ItemStack safeInsert(final ItemStack inputStack, final int inputAmount) {
      return this.safeInsert(inputStack);
   }

   public boolean allowModification(final Player player) {
      return false;
   }

   public boolean mayPlace(final ItemStack itemStack) {
      return false;
   }

   public ItemStack remove(final int amount) {
      return ItemStack.EMPTY;
   }

   public void onTake(final Player player, final ItemStack carried) {
   }

   public boolean isHighlightable() {
      return false;
   }

   public boolean isFake() {
      return true;
   }
}
