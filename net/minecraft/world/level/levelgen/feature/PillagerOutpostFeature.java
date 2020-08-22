package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BeardedStructureStart;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PillagerOutpostPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class PillagerOutpostFeature extends RandomScatteredFeature {
   private static final List OUTPOST_ENEMIES;

   public PillagerOutpostFeature(Function var1) {
      super(var1);
   }

   public String getFeatureName() {
      return "Pillager_Outpost";
   }

   public int getLookupRange() {
      return 3;
   }

   public List getSpecialEnemies() {
      return OUTPOST_ENEMIES;
   }

   public boolean isFeatureChunk(BiomeManager var1, ChunkGenerator var2, Random var3, int var4, int var5, Biome var6) {
      ChunkPos var7 = this.getPotentialFeatureChunkFromLocationWithOffset(var2, var3, var4, var5, 0, 0);
      if (var4 == var7.x && var5 == var7.z) {
         int var8 = var4 >> 4;
         int var9 = var5 >> 4;
         var3.setSeed((long)(var8 ^ var9 << 4) ^ var2.getSeed());
         var3.nextInt();
         if (var3.nextInt(5) != 0) {
            return false;
         }

         if (var2.isBiomeValidStartForStructure(var6, this)) {
            for(int var10 = var4 - 10; var10 <= var4 + 10; ++var10) {
               for(int var11 = var5 - 10; var11 <= var5 + 10; ++var11) {
                  if (Feature.VILLAGE.isFeatureChunk(var1, var2, var3, var10, var11, var1.getBiome(new BlockPos((var10 << 4) + 9, 0, (var11 << 4) + 9)))) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      return false;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return PillagerOutpostFeature.FeatureStart::new;
   }

   protected int getRandomSalt() {
      return 165745296;
   }

   static {
      OUTPOST_ENEMIES = Lists.newArrayList(new Biome.SpawnerData[]{new Biome.SpawnerData(EntityType.PILLAGER, 1, 1, 1)});
   }

   public static class FeatureStart extends BeardedStructureStart {
      public FeatureStart(StructureFeature var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(ChunkGenerator var1, StructureManager var2, int var3, int var4, Biome var5) {
         BlockPos var6 = new BlockPos(var3 * 16, 90, var4 * 16);
         PillagerOutpostPieces.addPieces(var1, var2, var6, this.pieces, this.random);
         this.calculateBoundingBox();
      }
   }
}
