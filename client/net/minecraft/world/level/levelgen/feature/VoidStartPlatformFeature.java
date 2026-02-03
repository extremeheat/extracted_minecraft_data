package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class VoidStartPlatformFeature extends Feature<NoneFeatureConfiguration> {
   private static final BlockPos PLATFORM_OFFSET = new BlockPos(8, 3, 8);
   private static final ChunkPos PLATFORM_ORIGIN_CHUNK;
   private static final int PLATFORM_RADIUS = 16;
   private static final int PLATFORM_RADIUS_CHUNKS = 1;

   public VoidStartPlatformFeature(final Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   private static int checkerboardDistance(final int xa, final int za, final int xb, final int zb) {
      return Math.max(Math.abs(xa - xb), Math.abs(za - zb));
   }

   public boolean place(final FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      ChunkPos currentChunkPos = ChunkPos.containing(context.origin());
      if (checkerboardDistance(currentChunkPos.x(), currentChunkPos.z(), PLATFORM_ORIGIN_CHUNK.x(), PLATFORM_ORIGIN_CHUNK.z()) > 1) {
         return true;
      } else {
         BlockPos platformOrigin = PLATFORM_OFFSET.atY(context.origin().getY() + PLATFORM_OFFSET.getY());
         BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

         for(int z = currentChunkPos.getMinBlockZ(); z <= currentChunkPos.getMaxBlockZ(); ++z) {
            for(int x = currentChunkPos.getMinBlockX(); x <= currentChunkPos.getMaxBlockX(); ++x) {
               if (checkerboardDistance(platformOrigin.getX(), platformOrigin.getZ(), x, z) <= 16) {
                  blockPos.set(x, platformOrigin.getY(), z);
                  if (blockPos.equals(platformOrigin)) {
                     level.setBlock(blockPos, Blocks.COBBLESTONE.defaultBlockState(), 2);
                  } else {
                     level.setBlock(blockPos, Blocks.STONE.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }

   static {
      PLATFORM_ORIGIN_CHUNK = ChunkPos.containing(PLATFORM_OFFSET);
   }
}
