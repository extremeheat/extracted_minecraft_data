package net.minecraft.world.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class FurnaceResultSlot extends Slot {
   private final Player player;
   private int removeCount;

   public FurnaceResultSlot(Player var1, Container var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.player = var1;
   }

   public boolean mayPlace(ItemStack var1) {
      return false;
   }

   public ItemStack remove(int var1) {
      if (this.hasItem()) {
         this.removeCount += Math.min(var1, this.getItem().getCount());
      }

      return super.remove(var1);
   }

   public void onTake(Player var1, ItemStack var2) {
      this.checkTakeAchievements(var2);
      super.onTake(var1, var2);
   }

   protected void onQuickCraft(ItemStack var1, int var2) {
      this.removeCount += var2;
      this.checkTakeAchievements(var1);
   }

   protected void checkTakeAchievements(ItemStack var1) {
      var1.onCraftedBy(this.player.level, this.player, this.removeCount);
      if (this.player instanceof ServerPlayer && this.container instanceof AbstractFurnaceBlockEntity) {
         ((AbstractFurnaceBlockEntity)this.container).awardUsedRecipesAndPopExperience((ServerPlayer)this.player);
      }

      this.removeCount = 0;
   }
}
