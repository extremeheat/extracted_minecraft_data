package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CoralTreeFeature extends CoralFeature {
   public CoralTreeFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected boolean placeFeature(LevelAccessor var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockPos.MutableBlockPos var5 = var3.mutable();
      int var6 = var2.nextInt(3) + 1;

      for(int var7 = 0; var7 < var6; ++var7) {
         if (!this.placeCoralBlock(var1, var2, var5, var4)) {
            return true;
         }

         var5.move(Direction.UP);
      }

      BlockPos var16 = var5.immutable();
      int var8 = var2.nextInt(3) + 2;
      List var9 = Direction.Plane.HORIZONTAL.shuffledCopy(var2);
      List var10 = var9.subList(0, var8);
      Iterator var11 = var10.iterator();

      while(var11.hasNext()) {
         Direction var12 = (Direction)var11.next();
         var5.set(var16);
         var5.move(var12);
         int var13 = var2.nextInt(5) + 2;
         int var14 = 0;

         for(int var15 = 0; var15 < var13 && this.placeCoralBlock(var1, var2, var5, var4); ++var15) {
            ++var14;
            var5.move(Direction.UP);
            if (var15 == 0 || var14 >= 2 && var2.nextFloat() < 0.25F) {
               var5.move(var12);
               var14 = 0;
            }
         }
      }

      return true;
   }
}
