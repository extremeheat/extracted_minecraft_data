package net.minecraft.world.level.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public abstract class BiomeSource implements BiomeManager.NoiseBiomeSource {
   private static final List PLAYER_SPAWN_BIOMES;
   protected final Map supportedStructures = Maps.newHashMap();
   protected final Set surfaceBlocks = Sets.newHashSet();
   protected final Set possibleBiomes;

   protected BiomeSource(Set var1) {
      this.possibleBiomes = var1;
   }

   public List getPlayerSpawnBiomes() {
      return PLAYER_SPAWN_BIOMES;
   }

   public Set getBiomesWithin(int var1, int var2, int var3, int var4) {
      int var5 = var1 - var4 >> 2;
      int var6 = var2 - var4 >> 2;
      int var7 = var3 - var4 >> 2;
      int var8 = var1 + var4 >> 2;
      int var9 = var2 + var4 >> 2;
      int var10 = var3 + var4 >> 2;
      int var11 = var8 - var5 + 1;
      int var12 = var9 - var6 + 1;
      int var13 = var10 - var7 + 1;
      HashSet var14 = Sets.newHashSet();

      for(int var15 = 0; var15 < var13; ++var15) {
         for(int var16 = 0; var16 < var11; ++var16) {
            for(int var17 = 0; var17 < var12; ++var17) {
               int var18 = var5 + var16;
               int var19 = var6 + var17;
               int var20 = var7 + var15;
               var14.add(this.getNoiseBiome(var18, var19, var20));
            }
         }
      }

      return var14;
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int var1, int var2, int var3, int var4, List var5, Random var6) {
      int var7 = var1 - var4 >> 2;
      int var8 = var3 - var4 >> 2;
      int var9 = var1 + var4 >> 2;
      int var10 = var3 + var4 >> 2;
      int var11 = var9 - var7 + 1;
      int var12 = var10 - var8 + 1;
      int var13 = var2 >> 2;
      BlockPos var14 = null;
      int var15 = 0;

      for(int var16 = 0; var16 < var12; ++var16) {
         for(int var17 = 0; var17 < var11; ++var17) {
            int var18 = var7 + var17;
            int var19 = var8 + var16;
            if (var5.contains(this.getNoiseBiome(var18, var13, var19))) {
               if (var14 == null || var6.nextInt(var15 + 1) == 0) {
                  var14 = new BlockPos(var18 << 2, var2, var19 << 2);
               }

               ++var15;
            }
         }
      }

      return var14;
   }

   public float getHeightValue(int var1, int var2) {
      return 0.0F;
   }

   public boolean canGenerateStructure(StructureFeature var1) {
      return (Boolean)this.supportedStructures.computeIfAbsent(var1, (var1x) -> {
         return this.possibleBiomes.stream().anyMatch((var1) -> {
            return var1.isValidStart(var1x);
         });
      });
   }

   public Set getSurfaceBlocks() {
      if (this.surfaceBlocks.isEmpty()) {
         Iterator var1 = this.possibleBiomes.iterator();

         while(var1.hasNext()) {
            Biome var2 = (Biome)var1.next();
            this.surfaceBlocks.add(var2.getSurfaceBuilderConfig().getTopMaterial());
         }
      }

      return this.surfaceBlocks;
   }

   static {
      PLAYER_SPAWN_BIOMES = Lists.newArrayList(new Biome[]{Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS});
   }
}
