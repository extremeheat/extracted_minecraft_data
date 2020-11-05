package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;

public class IceBlock extends HalfTransparentBlock {
   public IceBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public void playerDestroy(Level var1, Player var2, BlockPos var3, BlockState var4, @Nullable BlockEntity var5, ItemStack var6) {
      super.playerDestroy(var1, var2, var3, var4, var5, var6);
      if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, var6) == 0) {
         if (var1.dimensionType().ultraWarm()) {
            var1.removeBlock(var3, false);
            return;
         }

         Material var7 = var1.getBlockState(var3.below()).getMaterial();
         if (var7.blocksMotion() || var7.isLiquid()) {
            var1.setBlockAndUpdate(var3, Blocks.WATER.defaultBlockState());
         }
      }

   }

   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (var2.getBrightness(LightLayer.BLOCK, var3) > 11 - var1.getLightBlock(var2, var3)) {
         this.melt(var1, var2, var3);
      }

   }

   protected void melt(BlockState var1, Level var2, BlockPos var3) {
      if (var2.dimensionType().ultraWarm()) {
         var2.removeBlock(var3, false);
      } else {
         var2.setBlockAndUpdate(var3, Blocks.WATER.defaultBlockState());
         var2.neighborChanged(var3, Blocks.WATER, var3);
      }
   }

   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.NORMAL;
   }
}
