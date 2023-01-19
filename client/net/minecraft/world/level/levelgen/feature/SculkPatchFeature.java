package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;

public class SculkPatchFeature extends Feature<SculkPatchConfiguration> {
   public SculkPatchFeature(Codec<SculkPatchConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<SculkPatchConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      if (!this.canSpreadFrom(var2, var3)) {
         return false;
      } else {
         SculkPatchConfiguration var4 = (SculkPatchConfiguration)var1.config();
         RandomSource var5 = var1.random();
         SculkSpreader var6 = SculkSpreader.createWorldGenSpreader();
         int var7 = var4.spreadRounds() + var4.growthRounds();

         for(int var8 = 0; var8 < var7; ++var8) {
            for(int var9 = 0; var9 < var4.chargeCount(); ++var9) {
               var6.addCursors(var3, var4.amountPerCharge());
            }

            boolean var13 = var8 < var4.spreadRounds();

            for(int var10 = 0; var10 < var4.spreadAttempts(); ++var10) {
               var6.updateCursors(var2, var3, var5, var13);
            }

            var6.clear();
         }

         BlockPos var12 = var3.below();
         if (var5.nextFloat() <= var4.catalystChance() && var2.getBlockState(var12).isCollisionShapeFullBlock(var2, var12)) {
            var2.setBlock(var3, Blocks.SCULK_CATALYST.defaultBlockState(), 3);
         }

         int var14 = var4.extraRareGrowths().sample(var5);

         for(int var15 = 0; var15 < var14; ++var15) {
            BlockPos var11 = var3.offset(var5.nextInt(5) - 2, 0, var5.nextInt(5) - 2);
            if (var2.getBlockState(var11).isAir() && var2.getBlockState(var11.below()).isFaceSturdy(var2, var11.below(), Direction.UP)) {
               var2.setBlock(var11, Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, Boolean.valueOf(true)), 3);
            }
         }

         return true;
      }
   }

   private boolean canSpreadFrom(LevelAccessor var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      if (var3.getBlock() instanceof SculkBehaviour) {
         return true;
      } else {
         return !var3.isAir() && (!var3.is(Blocks.WATER) || !var3.getFluidState().isSource())
            ? false
            : Direction.stream().map(var2::relative).anyMatch(var1x -> var1.getBlockState(var1x).isCollisionShapeFullBlock(var1, var1x));
      }
   }
}
