package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class IceBlock extends HalfTransparentBlock {
   public static final MapCodec<IceBlock> CODEC = simpleCodec(IceBlock::new);

   public MapCodec<? extends IceBlock> codec() {
      return CODEC;
   }

   public IceBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public static BlockState meltsInto() {
      return Blocks.WATER.defaultBlockState();
   }

   public void playerDestroy(Level var1, Player var2, BlockPos var3, BlockState var4, @Nullable BlockEntity var5, ItemStack var6) {
      super.playerDestroy(var1, var2, var3, var4, var5, var6);
      if (!EnchantmentHelper.hasTag(var6, EnchantmentTags.PREVENTS_ICE_MELTING)) {
         if (var1.dimensionType().ultraWarm()) {
            var1.removeBlock(var3, false);
            return;
         }

         BlockState var7 = var1.getBlockState(var3.below());
         if (var7.blocksMotion() || var7.liquid()) {
            var1.setBlockAndUpdate(var3, meltsInto());
         }
      }

   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.getBrightness(LightLayer.BLOCK, var3) > 11 - var1.getLightBlock(var2, var3)) {
         this.melt(var1, var2, var3);
      }

   }

   protected void melt(BlockState var1, Level var2, BlockPos var3) {
      if (var2.dimensionType().ultraWarm()) {
         var2.removeBlock(var3, false);
      } else {
         var2.setBlockAndUpdate(var3, meltsInto());
         var2.neighborChanged(var3, meltsInto().getBlock(), var3);
      }
   }
}
