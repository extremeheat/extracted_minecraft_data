package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BeardedStructureStart;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PillagerOutpostPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class PillagerOutpostFeature extends RandomScatteredFeature<PillagerOutpostConfiguration> {
   private static final List<Biome.SpawnerData> OUTPOST_ENEMIES;

   public PillagerOutpostFeature(Function<Dynamic<?>, ? extends PillagerOutpostConfiguration> var1) {
      super(var1);
   }

   public String getFeatureName() {
      return "Pillager_Outpost";
   }

   public int getLookupRange() {
      return 3;
   }

   public List<Biome.SpawnerData> getSpecialEnemies() {
      return OUTPOST_ENEMIES;
   }

   public boolean isFeatureChunk(ChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ChunkPos var5 = this.getPotentialFeatureChunkFromLocationWithOffset(var1, var2, var3, var4, 0, 0);
      if (var3 == var5.x && var4 == var5.z) {
         int var6 = var3 >> 4;
         int var7 = var4 >> 4;
         var2.setSeed((long)(var6 ^ var7 << 4) ^ var1.getSeed());
         var2.nextInt();
         if (var2.nextInt(5) != 0) {
            return false;
         }

         Biome var8 = var1.getBiomeSource().getBiome(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9));
         if (var1.isBiomeValidStartForStructure(var8, Feature.PILLAGER_OUTPOST)) {
            for(int var9 = var3 - 10; var9 <= var3 + 10; ++var9) {
               for(int var10 = var4 - 10; var10 <= var4 + 10; ++var10) {
                  if (Feature.VILLAGE.isFeatureChunk(var1, var2, var9, var10)) {
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
      public FeatureStart(StructureFeature<?> var1, int var2, int var3, Biome var4, BoundingBox var5, int var6, long var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      public void generatePieces(ChunkGenerator<?> var1, StructureManager var2, int var3, int var4, Biome var5) {
         BlockPos var6 = new BlockPos(var3 * 16, 90, var4 * 16);
         PillagerOutpostPieces.addPieces(var1, var2, var6, this.pieces, this.random);
         this.calculateBoundingBox();
      }
   }
}
