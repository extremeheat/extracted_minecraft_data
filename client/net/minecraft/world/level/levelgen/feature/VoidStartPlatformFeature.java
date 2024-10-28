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

   public VoidStartPlatformFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   private static int checkerboardDistance(int var0, int var1, int var2, int var3) {
      return Math.max(Math.abs(var0 - var2), Math.abs(var1 - var3));
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      ChunkPos var3 = new ChunkPos(var1.origin());
      if (checkerboardDistance(var3.x, var3.z, PLATFORM_ORIGIN_CHUNK.x, PLATFORM_ORIGIN_CHUNK.z) > 1) {
         return true;
      } else {
         BlockPos var4 = PLATFORM_OFFSET.atY(var1.origin().getY() + PLATFORM_OFFSET.getY());
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

         for(int var6 = var3.getMinBlockZ(); var6 <= var3.getMaxBlockZ(); ++var6) {
            for(int var7 = var3.getMinBlockX(); var7 <= var3.getMaxBlockX(); ++var7) {
               if (checkerboardDistance(var4.getX(), var4.getZ(), var7, var6) <= 16) {
                  var5.set(var7, var4.getY(), var6);
                  if (var5.equals(var4)) {
                     var2.setBlock(var5, Blocks.COBBLESTONE.defaultBlockState(), 2);
                  } else {
                     var2.setBlock(var5, Blocks.STONE.defaultBlockState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }

   static {
      PLATFORM_ORIGIN_CHUNK = new ChunkPos(PLATFORM_OFFSET);
   }
}
