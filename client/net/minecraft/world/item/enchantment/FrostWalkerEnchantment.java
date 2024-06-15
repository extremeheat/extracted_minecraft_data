package net.minecraft.world.item.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class FrostWalkerEnchantment extends Enchantment {
   public FrostWalkerEnchantment(Enchantment.EnchantmentDefinition var1) {
      super(var1);
   }

   @Override
   public boolean isTreasureOnly() {
      return true;
   }

   public static void onEntityMoved(LivingEntity var0, Level var1, BlockPos var2, int var3) {
      if (var0.onGround()) {
         BlockState var4 = Blocks.FROSTED_ICE.defaultBlockState();
         int var5 = Math.min(16, 2 + var3);
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

         for (BlockPos var8 : BlockPos.betweenClosed(var2.offset(-var5, -1, -var5), var2.offset(var5, -1, var5))) {
            if (var8.closerToCenterThan(var0.position(), (double)var5)) {
               var6.set(var8.getX(), var8.getY() + 1, var8.getZ());
               BlockState var9 = var1.getBlockState(var6);
               if (var9.isAir()) {
                  BlockState var10 = var1.getBlockState(var8);
                  if (var10 == FrostedIceBlock.meltsInto() && var4.canSurvive(var1, var8) && var1.isUnobstructed(var4, var8, CollisionContext.empty())) {
                     var1.setBlockAndUpdate(var8, var4);
                     var1.scheduleTick(var8, Blocks.FROSTED_ICE, Mth.nextInt(var0.getRandom(), 60, 120));
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean checkCompatibility(Enchantment var1) {
      return super.checkCompatibility(var1) && var1 != Enchantments.DEPTH_STRIDER;
   }
}
