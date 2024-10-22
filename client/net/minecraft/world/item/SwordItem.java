package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SwordItem extends Item {
   public SwordItem(ToolMaterial var1, float var2, float var3, Item.Properties var4) {
      super(var1.applySwordProperties(var4, var2, var3));
   }

   @Override
   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      return !var4.isCreative();
   }

   @Override
   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      return true;
   }

   @Override
   public void postHurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(1, var3, EquipmentSlot.MAINHAND);
   }
}
