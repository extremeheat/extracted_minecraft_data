package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.OceanMonumentPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class OceanMonumentFeature extends StructureFeature<NoneFeatureConfiguration> {
   private static final List<Biome.SpawnerData> MONUMENT_ENEMIES;

   public OceanMonumentFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(ChunkGenerator<?> var1, Random var2, int var3, int var4, int var5, int var6) {
      int var7 = var1.getSettings().getMonumentsSpacing();
      int var8 = var1.getSettings().getMonumentsSeparation();
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0 ? var9 - var7 + 1 : var9;
      int var12 = var10 < 0 ? var10 - var7 + 1 : var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((WorldgenRandom)var2).setLargeFeatureWithSalt(var1.getSeed(), var13, var14, 10387313);
      var13 *= var7;
      var14 *= var7;
      var13 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      var14 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      return new ChunkPos(var13, var14);
   }

   public boolean isFeatureChunk(ChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ChunkPos var5 = this.getPotentialFeatureChunkFromLocationWithOffset(var1, var2, var3, var4, 0, 0);
      if (var3 == var5.x && var4 == var5.z) {
         Set var6 = var1.getBiomeSource().getBiomesWithin(var3 * 16 + 9, var4 * 16 + 9, 16);
         Iterator var7 = var6.iterator();

         Biome var8;
         do {
            if (!var7.hasNext()) {
               Set var10 = var1.getBiomeSource().getBiomesWithin(var3 * 16 + 9, var4 * 16 + 9, 29);
               Iterator var11 = var10.iterator();

               Biome var9;
               do {
                  if (!var11.hasNext()) {
                     return true;
                  }

                  var9 = (Biome)var11.next();
               } while(var9.getBiomeCategory() == Biome.BiomeCategory.OCEAN || var9.getBiomeCategory() == Biome.BiomeCategory.RIVER);

               return false;
            }

            var8 = (Biome)var7.next();
         } while(var1.isBiomeValidStartForStructure(var8, Feature.OCEAN_MONUMENT));

         return false;
      } else {
         return false;
      }
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return OceanMonumentFeature.OceanMonumentStart::new;
   }

   public String getFeatureName() {
      return "Monument";
   }

   public int getLookupRange() {
      return 8;
   }

   public List<Biome.SpawnerData> getSpecialEnemies() {
      return MONUMENT_ENEMIES;
   }

   static {
      MONUMENT_ENEMIES = Lists.newArrayList(new Biome.SpawnerData[]{new Biome.SpawnerData(EntityType.GUARDIAN, 1, 2, 4)});
   }

   public static class OceanMonumentStart extends StructureStart {
      private boolean isCreated;

      public OceanMonumentStart(StructureFeature<?> var1, int var2, int var3, Biome var4, BoundingBox var5, int var6, long var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      public void generatePieces(ChunkGenerator<?> var1, StructureManager var2, int var3, int var4, Biome var5) {
         this.generatePieces(var3, var4);
      }

      private void generatePieces(int var1, int var2) {
         int var3 = var1 * 16 - 29;
         int var4 = var2 * 16 - 29;
         Direction var5 = Direction.Plane.HORIZONTAL.getRandomDirection(this.random);
         this.pieces.add(new OceanMonumentPieces.MonumentBuilding(this.random, var3, var4, var5));
         this.calculateBoundingBox();
         this.isCreated = true;
      }

      public void postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
         if (!this.isCreated) {
            this.pieces.clear();
            this.generatePieces(this.getChunkX(), this.getChunkZ());
         }

         super.postProcess(var1, var2, var3, var4);
      }
   }
}
