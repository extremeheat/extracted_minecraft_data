package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.BeardedStructureStart;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class JigsawFeature extends StructureFeature<JigsawConfiguration> {
   private final int startY;
   private final boolean doExpansionHack;
   private final boolean projectStartToHeightmap;

   public JigsawFeature(Codec<JigsawConfiguration> var1, int var2, boolean var3, boolean var4) {
      super(var1);
      this.startY = var2;
      this.doExpansionHack = var3;
      this.projectStartToHeightmap = var4;
   }

   public StructureFeature.StructureStartFactory<JigsawConfiguration> getStartFactory() {
      return (var1, var2, var3, var4, var5, var6) -> {
         return new JigsawFeature.FeatureStart(this, var2, var3, var4, var5, var6);
      };
   }

   public static class FeatureStart extends BeardedStructureStart<JigsawConfiguration> {
      private final JigsawFeature feature;

      public FeatureStart(JigsawFeature var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
         this.feature = var1;
      }

      public void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, JigsawConfiguration var7) {
         BlockPos var8 = new BlockPos(var4 * 16, this.feature.startY, var5 * 16);
         Pools.bootstrap();
         JigsawPlacement.addPieces(var1, var7, PoolElementStructurePiece::new, var2, var3, var8, this.pieces, this.random, this.feature.doExpansionHack, this.feature.projectStartToHeightmap);
         this.calculateBoundingBox();
      }
   }
}
