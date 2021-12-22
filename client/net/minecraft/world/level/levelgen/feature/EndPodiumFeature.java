package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndPodiumFeature extends Feature<NoneFeatureConfiguration> {
   public static final int PODIUM_RADIUS = 4;
   public static final int PODIUM_PILLAR_HEIGHT = 4;
   public static final int RIM_RADIUS = 1;
   public static final float CORNER_ROUNDING = 0.5F;
   public static final BlockPos END_PODIUM_LOCATION;
   private final boolean active;

   public EndPodiumFeature(boolean var1) {
      super(NoneFeatureConfiguration.CODEC);
      this.active = var1;
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      Iterator var4 = BlockPos.betweenClosed(new BlockPos(var2.getX() - 4, var2.getY() - 1, var2.getZ() - 4), new BlockPos(var2.getX() + 4, var2.getY() + 32, var2.getZ() + 4)).iterator();

      while(true) {
         BlockPos var5;
         boolean var6;
         do {
            if (!var4.hasNext()) {
               for(int var7 = 0; var7 < 4; ++var7) {
                  this.setBlock(var3, var2.above(var7), Blocks.BEDROCK.defaultBlockState());
               }

               BlockPos var8 = var2.above(2);
               Iterator var9 = Direction.Plane.HORIZONTAL.iterator();

               while(var9.hasNext()) {
                  Direction var10 = (Direction)var9.next();
                  this.setBlock(var3, var8.relative(var10), (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, var10));
               }

               return true;
            }

            var5 = (BlockPos)var4.next();
            var6 = var5.closerThan(var2, 2.5D);
         } while(!var6 && !var5.closerThan(var2, 3.5D));

         if (var5.getY() < var2.getY()) {
            if (var6) {
               this.setBlock(var3, var5, Blocks.BEDROCK.defaultBlockState());
            } else if (var5.getY() < var2.getY()) {
               this.setBlock(var3, var5, Blocks.END_STONE.defaultBlockState());
            }
         } else if (var5.getY() > var2.getY()) {
            this.setBlock(var3, var5, Blocks.AIR.defaultBlockState());
         } else if (!var6) {
            this.setBlock(var3, var5, Blocks.BEDROCK.defaultBlockState());
         } else if (this.active) {
            this.setBlock(var3, new BlockPos(var5), Blocks.END_PORTAL.defaultBlockState());
         } else {
            this.setBlock(var3, new BlockPos(var5), Blocks.AIR.defaultBlockState());
         }
      }
   }

   static {
      END_PODIUM_LOCATION = BlockPos.ZERO;
   }
}
