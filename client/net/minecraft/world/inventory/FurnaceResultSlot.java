package net.minecraft.world.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class FurnaceResultSlot extends Slot {
   private final Player player;
   private int removeCount;

   public FurnaceResultSlot(final Player player, final Container container, final int slot, final int x, final int y) {
      super(container, slot, x, y);
      this.player = player;
   }

   public boolean mayPlace(final ItemStack itemStack) {
      return false;
   }

   public ItemStack remove(final int amount) {
      if (this.hasItem()) {
         this.removeCount += Math.min(amount, this.getItem().getCount());
      }

      return super.remove(amount);
   }

   public void onTake(final Player player, final ItemStack carried) {
      this.checkTakeAchievements(carried);
      super.onTake(player, carried);
   }

   protected void onQuickCraft(final ItemStack picked, final int count) {
      this.removeCount += count;
      this.checkTakeAchievements(picked);
   }

   protected void checkTakeAchievements(final ItemStack carried) {
      carried.onCraftedBy(this.player, this.removeCount);
      Player var4 = this.player;
      if (var4 instanceof ServerPlayer serverPlayer) {
         Container var5 = this.container;
         if (var5 instanceof AbstractFurnaceBlockEntity abstractFurnaceBlockEntity) {
            abstractFurnaceBlockEntity.awardUsedRecipesAndPopExperience(serverPlayer);
         }
      }

      this.removeCount = 0;
   }
}
