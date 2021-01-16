package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class EndCityFeature extends StructureFeature<NoneFeatureConfiguration> {
   public EndCityFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected boolean linearSeparation() {
      return false;
   }

   protected boolean isFeatureChunk(ChunkGenerator var1, BiomeSource var2, long var3, WorldgenRandom var5, int var6, int var7, Biome var8, ChunkPos var9, NoneFeatureConfiguration var10) {
      return getYPositionForFeature(var6, var7, var1) >= 60;
   }

   public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
      return EndCityFeature.EndCityStart::new;
   }

   private static int getYPositionForFeature(int var0, int var1, ChunkGenerator var2) {
      Random var3 = new Random((long)(var0 + var1 * 10387313));
      Rotation var4 = Rotation.getRandom(var3);
      byte var5 = 5;
      byte var6 = 5;
      if (var4 == Rotation.CLOCKWISE_90) {
         var5 = -5;
      } else if (var4 == Rotation.CLOCKWISE_180) {
         var5 = -5;
         var6 = -5;
      } else if (var4 == Rotation.COUNTERCLOCKWISE_90) {
         var6 = -5;
      }

      int var7 = (var0 << 4) + 7;
      int var8 = (var1 << 4) + 7;
      int var9 = var2.getFirstOccupiedHeight(var7, var8, Heightmap.Types.WORLD_SURFACE_WG);
      int var10 = var2.getFirstOccupiedHeight(var7, var8 + var6, Heightmap.Types.WORLD_SURFACE_WG);
      int var11 = var2.getFirstOccupiedHeight(var7 + var5, var8, Heightmap.Types.WORLD_SURFACE_WG);
      int var12 = var2.getFirstOccupiedHeight(var7 + var5, var8 + var6, Heightmap.Types.WORLD_SURFACE_WG);
      return Math.min(Math.min(var9, var10), Math.min(var11, var12));
   }

   public static class EndCityStart extends StructureStart<NoneFeatureConfiguration> {
      public EndCityStart(StructureFeature<NoneFeatureConfiguration> var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, NoneFeatureConfiguration var7) {
         Rotation var8 = Rotation.getRandom(this.random);
         int var9 = EndCityFeature.getYPositionForFeature(var4, var5, var2);
         if (var9 >= 60) {
            BlockPos var10 = new BlockPos(var4 * 16 + 8, var9, var5 * 16 + 8);
            EndCityPieces.startHouseTower(var3, var10, var8, this.pieces, this.random);
            this.calculateBoundingBox();
         }
      }
   }
}
