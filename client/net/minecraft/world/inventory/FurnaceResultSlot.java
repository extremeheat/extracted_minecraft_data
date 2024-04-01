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

   @Override
   public boolean mayPlace(ItemStack var1) {
      return false;
   }

   @Override
   public ItemStack remove(int var1) {
      if (this.hasItem()) {
         this.removeCount += Math.min(var1, this.getItem().getCount());
      }

      return super.remove(var1);
   }

   @Override
   public void onTake(Player var1, ItemStack var2) {
      this.checkTakeAchievements(var2);
      super.onTake(var1, var2);
   }

   @Override
   protected void onQuickCraft(ItemStack var1, int var2) {
      this.removeCount += var2;
      this.checkTakeAchievements(var1);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void checkTakeAchievements(ItemStack var1) {
      var1.onCraftedBy(this.player.level(), this.player, this.removeCount);
      Player var4 = this.player;
      if (var4 instanceof ServerPlayer var2) {
         Container var5 = this.container;
         if (var5 instanceof AbstractFurnaceBlockEntity var3) {
            var3.awardUsedRecipesAndPopExperience((ServerPlayer)var2);
         }
      }

      this.removeCount = 0;
   }
}
