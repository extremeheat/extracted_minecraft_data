package net.minecraft.world.level.levelgen;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;

public class FlatLevelSource extends ChunkGenerator {
   private final Biome biomeWrapper = this.getBiomeFromSettings();
   private final PhantomSpawner phantomSpawner = new PhantomSpawner();
   private final CatSpawner catSpawner = new CatSpawner();

   public FlatLevelSource(LevelAccessor var1, BiomeSource var2, FlatLevelGeneratorSettings var3) {
      super(var1, var2, var3);
   }

   private Biome getBiomeFromSettings() {
      Biome var1 = ((FlatLevelGeneratorSettings)this.settings).getBiome();
      FlatLevelSource.FlatLevelBiomeWrapper var2 = new FlatLevelSource.FlatLevelBiomeWrapper(var1.getSurfaceBuilder(), var1.getPrecipitation(), var1.getBiomeCategory(), var1.getDepth(), var1.getScale(), var1.getTemperature(), var1.getDownfall(), var1.getWaterColor(), var1.getWaterFogColor(), var1.getParent());
      Map var3 = ((FlatLevelGeneratorSettings)this.settings).getStructuresOptions();
      Iterator var4 = var3.keySet().iterator();

      while(true) {
         ConfiguredFeature[] var6;
         int var8;
         ConfiguredFeature var11;
         do {
            if (!var4.hasNext()) {
               boolean var15 = (!((FlatLevelGeneratorSettings)this.settings).isVoidGen() || var1 == Biomes.THE_VOID) && var3.containsKey("decoration");
               if (var15) {
                  ArrayList var16 = Lists.newArrayList();
                  var16.add(GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
                  var16.add(GenerationStep.Decoration.SURFACE_STRUCTURES);
                  GenerationStep.Decoration[] var18 = GenerationStep.Decoration.values();
                  int var20 = var18.length;

                  for(var8 = 0; var8 < var20; ++var8) {
                     GenerationStep.Decoration var22 = var18[var8];
                     if (!var16.contains(var22)) {
                        Iterator var23 = var1.getFeaturesForStep(var22).iterator();

                        while(var23.hasNext()) {
                           var11 = (ConfiguredFeature)var23.next();
                           var2.addFeature(var22, var11);
                        }
                     }
                  }
               }

               BlockState[] var17 = ((FlatLevelGeneratorSettings)this.settings).getLayers();

               for(int var19 = 0; var19 < var17.length; ++var19) {
                  BlockState var21 = var17[var19];
                  if (var21 != null && !Heightmap.Types.MOTION_BLOCKING.isOpaque().test(var21)) {
                     ((FlatLevelGeneratorSettings)this.settings).deleteLayer(var19);
                     var2.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configured(new LayerConfiguration(var19, var21)).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
                  }
               }

               return var2;
            }

            String var5 = (String)var4.next();
            var6 = (ConfiguredFeature[])FlatLevelGeneratorSettings.STRUCTURE_FEATURES.get(var5);
         } while(var6 == null);

         ConfiguredFeature[] var7 = var6;
         var8 = var6.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            ConfiguredFeature var10 = var7[var9];
            var2.addFeature((GenerationStep.Decoration)FlatLevelGeneratorSettings.STRUCTURE_FEATURES_STEP.get(var10), var10);
            var11 = ((DecoratedFeatureConfiguration)var10.config).feature;
            if (var11.feature instanceof StructureFeature) {
               StructureFeature var12 = (StructureFeature)var11.feature;
               FeatureConfiguration var13 = var1.getStructureConfiguration(var12);
               FeatureConfiguration var14 = var13 != null ? var13 : (FeatureConfiguration)FlatLevelGeneratorSettings.STRUCTURE_FEATURES_DEFAULT.get(var10);
               var2.addStructureStart(var12.configured(var14));
            }
         }
      }
   }

   public void buildSurfaceAndBedrock(WorldGenRegion var1, ChunkAccess var2) {
   }

   public int getSpawnHeight() {
      ChunkAccess var1 = this.level.getChunk(0, 0);
      return var1.getHeight(Heightmap.Types.MOTION_BLOCKING, 8, 8);
   }

   protected Biome getCarvingOrDecorationBiome(BiomeManager var1, BlockPos var2) {
      return this.biomeWrapper;
   }

   public void fillFromNoise(LevelAccessor var1, ChunkAccess var2) {
      BlockState[] var3 = ((FlatLevelGeneratorSettings)this.settings).getLayers();
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      Heightmap var5 = var2.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var6 = var2.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

      for(int var7 = 0; var7 < var3.length; ++var7) {
         BlockState var8 = var3[var7];
         if (var8 != null) {
            for(int var9 = 0; var9 < 16; ++var9) {
               for(int var10 = 0; var10 < 16; ++var10) {
                  var2.setBlockState(var4.set(var9, var7, var10), var8, false);
                  var5.update(var9, var7, var10, var8);
                  var6.update(var9, var7, var10, var8);
               }
            }
         }
      }

   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3) {
      BlockState[] var4 = ((FlatLevelGeneratorSettings)this.settings).getLayers();

      for(int var5 = var4.length - 1; var5 >= 0; --var5) {
         BlockState var6 = var4[var5];
         if (var6 != null && var3.isOpaque().test(var6)) {
            return var5 + 1;
         }
      }

      return 0;
   }

   public void tickCustomSpawners(ServerLevel var1, boolean var2, boolean var3) {
      this.phantomSpawner.tick(var1, var2, var3);
      this.catSpawner.tick(var1, var2, var3);
   }

   public boolean isBiomeValidStartForStructure(Biome var1, StructureFeature var2) {
      return this.biomeWrapper.isValidStart(var2);
   }

   @Nullable
   public FeatureConfiguration getStructureConfiguration(Biome var1, StructureFeature var2) {
      return this.biomeWrapper.getStructureConfiguration(var2);
   }

   @Nullable
   public BlockPos findNearestMapFeature(Level var1, String var2, BlockPos var3, int var4, boolean var5) {
      return !((FlatLevelGeneratorSettings)this.settings).getStructuresOptions().keySet().contains(var2.toLowerCase(Locale.ROOT)) ? null : super.findNearestMapFeature(var1, var2, var3, var4, var5);
   }

   class FlatLevelBiomeWrapper extends Biome {
      protected FlatLevelBiomeWrapper(ConfiguredSurfaceBuilder var2, Biome.Precipitation var3, Biome.BiomeCategory var4, float var5, float var6, float var7, float var8, int var9, int var10, String var11) {
         super((new Biome.BiomeBuilder()).surfaceBuilder(var2).precipitation(var3).biomeCategory(var4).depth(var5).scale(var6).temperature(var7).downfall(var8).waterColor(var9).waterFogColor(var10).parent(var11));
      }
   }
}
