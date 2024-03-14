package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BowlFoodItem extends Item {
   public BowlFoodItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      ItemStack var4 = super.finishUsingItem(var1, var2, var3);
      if (var3 instanceof Player var5 && var5.hasInfiniteMaterials()) {
         return var4;
      }

      return new ItemStack(Items.BOWL);
   }
}
