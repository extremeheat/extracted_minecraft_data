package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;

public record BasaltPillarFeature() implements Feature {
   public static final BasaltPillarFeature INSTANCE = new BasaltPillarFeature();
   public static final MapCodec<BasaltPillarFeature> CODEC;

   public BasaltPillarFeature() {
      super();
   }

   public MapCodec<BasaltPillarFeature> codec() {
      return CODEC;
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      if (level.isEmptyBlock(origin) && !level.isEmptyBlock(origin.above())) {
         BlockPos.MutableBlockPos pos = origin.mutable();
         BlockPos.MutableBlockPos tmpPos = origin.mutable();
         boolean placeNorthHangoff = true;
         boolean placeSouthHangoff = true;
         boolean placeWestHangoff = true;
         boolean placeEastHangoff = true;

         while(level.isEmptyBlock(pos)) {
            if (level.isOutsideBuildHeight(pos)) {
               return true;
            }

            level.setBlock(pos, Blocks.BASALT.defaultBlockState(), 2);
            placeNorthHangoff = placeNorthHangoff && this.placeHangOff(level, random, tmpPos.setWithOffset(pos, (Direction)Direction.NORTH));
            placeSouthHangoff = placeSouthHangoff && this.placeHangOff(level, random, tmpPos.setWithOffset(pos, (Direction)Direction.SOUTH));
            placeWestHangoff = placeWestHangoff && this.placeHangOff(level, random, tmpPos.setWithOffset(pos, (Direction)Direction.WEST));
            placeEastHangoff = placeEastHangoff && this.placeHangOff(level, random, tmpPos.setWithOffset(pos, (Direction)Direction.EAST));
            pos.move(Direction.DOWN);
         }

         pos.move(Direction.UP);
         this.placeBaseHangOff(level, random, tmpPos.setWithOffset(pos, (Direction)Direction.NORTH));
         this.placeBaseHangOff(level, random, tmpPos.setWithOffset(pos, (Direction)Direction.SOUTH));
         this.placeBaseHangOff(level, random, tmpPos.setWithOffset(pos, (Direction)Direction.WEST));
         this.placeBaseHangOff(level, random, tmpPos.setWithOffset(pos, (Direction)Direction.EAST));
         pos.move(Direction.DOWN);
         BlockPos.MutableBlockPos basePos = new BlockPos.MutableBlockPos();

         for(int dx = -3; dx < 4; ++dx) {
            for(int dz = -3; dz < 4; ++dz) {
               int probability = Mth.abs(dx) * Mth.abs(dz);
               if (random.nextInt(10) < 10 - probability) {
                  basePos.set(pos.offset(dx, 0, dz));
                  int maxDrop = 3;

                  while(level.isEmptyBlock(tmpPos.setWithOffset(basePos, (Direction)Direction.DOWN))) {
                     basePos.move(Direction.DOWN);
                     --maxDrop;
                     if (maxDrop <= 0) {
                        break;
                     }
                  }

                  if (!level.isEmptyBlock(tmpPos.setWithOffset(basePos, (Direction)Direction.DOWN))) {
                     level.setBlock(basePos, Blocks.BASALT.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void placeBaseHangOff(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      if (random.nextBoolean()) {
         level.setBlock(pos, Blocks.BASALT.defaultBlockState(), 2);
      }

   }

   private boolean placeHangOff(final LevelAccessor level, final RandomSource random, final BlockPos hangOffPos) {
      if (random.nextInt(10) != 0) {
         level.setBlock(hangOffPos, Blocks.BASALT.defaultBlockState(), 2);
         return true;
      } else {
         return false;
      }
   }

   static {
      CODEC = MapCodec.unit(INSTANCE);
   }
}
