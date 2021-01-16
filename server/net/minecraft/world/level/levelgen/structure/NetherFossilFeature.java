package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class NetherFossilFeature extends StructureFeature<NoneFeatureConfiguration> {
   public NetherFossilFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
      return NetherFossilFeature.FeatureStart::new;
   }

   public static class FeatureStart extends BeardedStructureStart<NoneFeatureConfiguration> {
      public FeatureStart(StructureFeature<NoneFeatureConfiguration> var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, NoneFeatureConfiguration var7) {
         ChunkPos var8 = new ChunkPos(var4, var5);
         int var9 = var8.getMinBlockX() + this.random.nextInt(16);
         int var10 = var8.getMinBlockZ() + this.random.nextInt(16);
         int var11 = var2.getSeaLevel();
         int var12 = var11 + this.random.nextInt(var2.getGenDepth() - 2 - var11);
         BlockGetter var13 = var2.getBaseColumn(var9, var10);

         for(BlockPos.MutableBlockPos var14 = new BlockPos.MutableBlockPos(var9, var12, var10); var12 > var11; --var12) {
            BlockState var15 = var13.getBlockState(var14);
            var14.move(Direction.DOWN);
            BlockState var16 = var13.getBlockState(var14);
            if (var15.isAir() && (var16.is(Blocks.SOUL_SAND) || var16.isFaceSturdy(var13, var14, Direction.UP))) {
               break;
            }
         }

         if (var12 > var11) {
            NetherFossilPieces.addPieces(var3, this.pieces, this.random, new BlockPos(var9, var12, var10));
            this.calculateBoundingBox();
         }
      }
   }
}
