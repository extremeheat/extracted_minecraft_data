package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CoralMushroomFeature extends CoralFeature {
   public CoralMushroomFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected boolean placeFeature(LevelAccessor var1, RandomSource var2, BlockPos var3, BlockState var4) {
      int var5 = var2.nextInt(3) + 3;
      int var6 = var2.nextInt(3) + 3;
      int var7 = var2.nextInt(3) + 3;
      int var8 = var2.nextInt(3) + 1;
      BlockPos.MutableBlockPos var9 = var3.mutable();

      for(int var10 = 0; var10 <= var6; ++var10) {
         for(int var11 = 0; var11 <= var5; ++var11) {
            for(int var12 = 0; var12 <= var7; ++var12) {
               var9.set(var10 + var3.getX(), var11 + var3.getY(), var12 + var3.getZ());
               var9.move(Direction.DOWN, var8);
               if ((var10 != 0 && var10 != var6 || var11 != 0 && var11 != var5) && (var12 != 0 && var12 != var7 || var11 != 0 && var11 != var5) && (var10 != 0 && var10 != var6 || var12 != 0 && var12 != var7) && (var10 == 0 || var10 == var6 || var11 == 0 || var11 == var5 || var12 == 0 || var12 == var7) && !(var2.nextFloat() < 0.1F) && !this.placeCoralBlock(var1, var2, var9, var4)) {
               }
            }
         }
      }

      return true;
   }
}
