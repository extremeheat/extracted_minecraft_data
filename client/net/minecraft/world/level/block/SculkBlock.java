package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class SculkBlock extends DropExperienceBlock implements SculkBehaviour {
   public static final MapCodec<SculkBlock> CODEC = simpleCodec(SculkBlock::new);

   public MapCodec<SculkBlock> codec() {
      return CODEC;
   }

   public SculkBlock(BlockBehaviour.Properties var1) {
      super(ConstantInt.of(1), var1);
   }

   public int attemptUseCharge(SculkSpreader.ChargeCursor var1, LevelAccessor var2, BlockPos var3, RandomSource var4, SculkSpreader var5, boolean var6) {
      int var7 = var1.getCharge();
      if (var7 != 0 && var4.nextInt(var5.chargeDecayRate()) == 0) {
         BlockPos var8 = var1.getPos();
         boolean var9 = var8.closerThan(var3, (double)var5.noGrowthRadius());
         if (!var9 && canPlaceGrowth(var2, var8)) {
            int var10 = var5.growthSpawnCost();
            if (var4.nextInt(var10) < var7) {
               BlockPos var11 = var8.above();
               BlockState var12 = this.getRandomGrowthState(var2, var11, var4, var5.isWorldGeneration());
               var2.setBlock(var11, var12, 3);
               var2.playSound((Player)null, var8, var12.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return Math.max(0, var7 - var10);
         } else {
            return var4.nextInt(var5.additionalDecayRate()) != 0 ? var7 : var7 - (var9 ? 1 : getDecayPenalty(var5, var8, var3, var7));
         }
      } else {
         return var7;
      }
   }

   private static int getDecayPenalty(SculkSpreader var0, BlockPos var1, BlockPos var2, int var3) {
      int var4 = var0.noGrowthRadius();
      float var5 = Mth.square((float)Math.sqrt(var1.distSqr(var2)) - (float)var4);
      int var6 = Mth.square(24 - var4);
      float var7 = Math.min(1.0F, var5 / (float)var6);
      return Math.max(1, (int)((float)var3 * var7 * 0.5F));
   }

   private BlockState getRandomGrowthState(LevelAccessor var1, BlockPos var2, RandomSource var3, boolean var4) {
      BlockState var5;
      if (var3.nextInt(11) == 0) {
         var5 = (BlockState)Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, var4);
      } else {
         var5 = Blocks.SCULK_SENSOR.defaultBlockState();
      }

      return var5.hasProperty(BlockStateProperties.WATERLOGGED) && !var1.getFluidState(var2).isEmpty() ? (BlockState)var5.setValue(BlockStateProperties.WATERLOGGED, true) : var5;
   }

   private static boolean canPlaceGrowth(LevelAccessor var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1.above());
      if (var2.isAir() || var2.is(Blocks.WATER) && var2.getFluidState().is((Fluid)Fluids.WATER)) {
         int var3 = 0;
         Iterator var4 = BlockPos.betweenClosed(var1.offset(-4, 0, -4), var1.offset(4, 2, 4)).iterator();

         do {
            if (!var4.hasNext()) {
               return true;
            }

            BlockPos var5 = (BlockPos)var4.next();
            BlockState var6 = var0.getBlockState(var5);
            if (var6.is(Blocks.SCULK_SENSOR) || var6.is(Blocks.SCULK_SHRIEKER)) {
               ++var3;
            }
         } while(var3 <= 2);

         return false;
      } else {
         return false;
      }
   }

   public boolean canChangeBlockStateOnSpread() {
      return false;
   }
}
