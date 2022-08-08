package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CoralClawFeature extends CoralFeature {
   public CoralClawFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected boolean placeFeature(LevelAccessor var1, RandomSource var2, BlockPos var3, BlockState var4) {
      if (!this.placeCoralBlock(var1, var2, var3, var4)) {
         return false;
      } else {
         Direction var5 = Direction.Plane.HORIZONTAL.getRandomDirection(var2);
         int var6 = var2.nextInt(2) + 2;
         List var7 = Util.toShuffledList(Stream.of(var5, var5.getClockWise(), var5.getCounterClockWise()), var2);
         List var8 = var7.subList(0, var6);
         Iterator var9 = var8.iterator();

         while(var9.hasNext()) {
            Direction var10 = (Direction)var9.next();
            BlockPos.MutableBlockPos var11 = var3.mutable();
            int var12 = var2.nextInt(2) + 1;
            var11.move(var10);
            int var13;
            Direction var14;
            if (var10 == var5) {
               var14 = var5;
               var13 = var2.nextInt(3) + 2;
            } else {
               var11.move(Direction.UP);
               Direction[] var15 = new Direction[]{var10, Direction.UP};
               var14 = (Direction)Util.getRandom((Object[])var15, var2);
               var13 = var2.nextInt(3) + 3;
            }

            int var16;
            for(var16 = 0; var16 < var12 && this.placeCoralBlock(var1, var2, var11, var4); ++var16) {
               var11.move(var14);
            }

            var11.move(var14.getOpposite());
            var11.move(Direction.UP);

            for(var16 = 0; var16 < var13; ++var16) {
               var11.move(var5);
               if (!this.placeCoralBlock(var1, var2, var11, var4)) {
                  break;
               }

               if (var2.nextFloat() < 0.25F) {
                  var11.move(Direction.UP);
               }
            }
         }

         return true;
      }
   }
}
