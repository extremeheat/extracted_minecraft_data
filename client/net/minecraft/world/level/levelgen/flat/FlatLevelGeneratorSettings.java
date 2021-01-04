package net.minecraft.world.level.levelgen.flat;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.LakeConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.PillagerOutpostConfiguration;
import net.minecraft.world.level.levelgen.feature.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.VillageConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.LakeChanceDecoratorConfig;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatLevelGeneratorSettings extends ChunkGeneratorSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ConfiguredFeature<?> MINESHAFT_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> VILLAGE_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> STRONGHOLD_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> SWAMPHUT_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> DESERT_PYRAMID_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> JUNGLE_PYRAMID_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> IGLOO_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> SHIPWRECK_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> OCEAN_MONUMENT_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> WATER_LAKE_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> LAVA_LAKE_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> ENDCITY_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> WOOLAND_MANSION_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> FORTRESS_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> OCEAN_RUIN_COMPOSITE_FEATURE;
   private static final ConfiguredFeature<?> PILLAGER_OUTPOST_COMPOSITE_FEATURE;
   public static final Map<ConfiguredFeature<?>, GenerationStep.Decoration> STRUCTURE_FEATURES_STEP;
   public static final Map<String, ConfiguredFeature<?>[]> STRUCTURE_FEATURES;
   public static final Map<ConfiguredFeature<?>, FeatureConfiguration> STRUCTURE_FEATURES_DEFAULT;
   private final List<FlatLayerInfo> layersInfo = Lists.newArrayList();
   private final Map<String, Map<String, String>> structuresOptions = Maps.newHashMap();
   private Biome biome;
   private final BlockState[] layers = new BlockState[256];
   private boolean voidGen;
   private int seaLevel;

   public FlatLevelGeneratorSettings() {
      super();
   }

   @Nullable
   public static Block byString(String var0) {
      try {
         ResourceLocation var1 = new ResourceLocation(var0);
         return (Block)Registry.BLOCK.getOptional(var1).orElse((Object)null);
      } catch (IllegalArgumentException var2) {
         LOGGER.warn("Invalid blockstate: {}", var0, var2);
         return null;
      }
   }

   public Biome getBiome() {
      return this.biome;
   }

   public void setBiome(Biome var1) {
      this.biome = var1;
   }

   public Map<String, Map<String, String>> getStructuresOptions() {
      return this.structuresOptions;
   }

   public List<FlatLayerInfo> getLayersInfo() {
      return this.layersInfo;
   }

   public void updateLayers() {
      int var1 = 0;

      Iterator var2;
      FlatLayerInfo var3;
      for(var2 = this.layersInfo.iterator(); var2.hasNext(); var1 += var3.getHeight()) {
         var3 = (FlatLayerInfo)var2.next();
         var3.setStart(var1);
      }

      this.seaLevel = 0;
      this.voidGen = true;
      var1 = 0;
      var2 = this.layersInfo.iterator();

      while(var2.hasNext()) {
         var3 = (FlatLayerInfo)var2.next();

         for(int var4 = var3.getStart(); var4 < var3.getStart() + var3.getHeight(); ++var4) {
            BlockState var5 = var3.getBlockState();
            if (var5.getBlock() != Blocks.AIR) {
               this.voidGen = false;
               this.layers[var4] = var5;
            }
         }

         if (var3.getBlockState().getBlock() == Blocks.AIR) {
            var1 += var3.getHeight();
         } else {
            this.seaLevel += var3.getHeight() + var1;
            var1 = 0;
         }
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();

      int var2;
      for(var2 = 0; var2 < this.layersInfo.size(); ++var2) {
         if (var2 > 0) {
            var1.append(",");
         }

         var1.append(this.layersInfo.get(var2));
      }

      var1.append(";");
      var1.append(Registry.BIOME.getKey(this.biome));
      var1.append(";");
      if (!this.structuresOptions.isEmpty()) {
         var2 = 0;
         Iterator var3 = this.structuresOptions.entrySet().iterator();

         while(true) {
            Map var5;
            do {
               if (!var3.hasNext()) {
                  return var1.toString();
               }

               Entry var4 = (Entry)var3.next();
               if (var2++ > 0) {
                  var1.append(",");
               }

               var1.append(((String)var4.getKey()).toLowerCase(Locale.ROOT));
               var5 = (Map)var4.getValue();
            } while(var5.isEmpty());

            var1.append("(");
            int var6 = 0;
            Iterator var7 = var5.entrySet().iterator();

            while(var7.hasNext()) {
               Entry var8 = (Entry)var7.next();
               if (var6++ > 0) {
                  var1.append(" ");
               }

               var1.append((String)var8.getKey());
               var1.append("=");
               var1.append((String)var8.getValue());
            }

            var1.append(")");
         }
      } else {
         return var1.toString();
      }
   }

   @Nullable
   private static FlatLayerInfo getLayerInfoFromString(String var0, int var1) {
      String[] var2 = var0.split("\\*", 2);
      int var3;
      if (var2.length == 2) {
         try {
            var3 = Math.max(Integer.parseInt(var2[0]), 0);
         } catch (NumberFormatException var9) {
            LOGGER.error("Error while parsing flat world string => {}", var9.getMessage());
            return null;
         }
      } else {
         var3 = 1;
      }

      int var4 = Math.min(var1 + var3, 256);
      int var5 = var4 - var1;

      Block var6;
      try {
         var6 = byString(var2[var2.length - 1]);
      } catch (Exception var8) {
         LOGGER.error("Error while parsing flat world string => {}", var8.getMessage());
         return null;
      }

      if (var6 == null) {
         LOGGER.error("Error while parsing flat world string => Unknown block, {}", var2[var2.length - 1]);
         return null;
      } else {
         FlatLayerInfo var7 = new FlatLayerInfo(var5, var6);
         var7.setStart(var1);
         return var7;
      }
   }

   private static List<FlatLayerInfo> getLayersInfoFromString(String var0) {
      ArrayList var1 = Lists.newArrayList();
      String[] var2 = var0.split(",");
      int var3 = 0;
      String[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         FlatLayerInfo var8 = getLayerInfoFromString(var7, var3);
         if (var8 == null) {
            return Collections.emptyList();
         }

         var1.add(var8);
         var3 += var8.getHeight();
      }

      return var1;
   }

   public <T> Dynamic<T> toObject(DynamicOps<T> var1) {
      Object var2 = var1.createList(this.layersInfo.stream().map((var1x) -> {
         return var1.createMap(ImmutableMap.of(var1.createString("height"), var1.createInt(var1x.getHeight()), var1.createString("block"), var1.createString(Registry.BLOCK.getKey(var1x.getBlockState().getBlock()).toString())));
      }));
      Object var3 = var1.createMap((Map)this.structuresOptions.entrySet().stream().map((var1x) -> {
         return Pair.of(var1.createString(((String)var1x.getKey()).toLowerCase(Locale.ROOT)), var1.createMap((Map)((Map)var1x.getValue()).entrySet().stream().map((var1xx) -> {
            return Pair.of(var1.createString((String)var1xx.getKey()), var1.createString((String)var1xx.getValue()));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("layers"), var2, var1.createString("biome"), var1.createString(Registry.BIOME.getKey(this.biome).toString()), var1.createString("structures"), var3)));
   }

   public static FlatLevelGeneratorSettings fromObject(Dynamic<?> var0) {
      FlatLevelGeneratorSettings var1 = (FlatLevelGeneratorSettings)ChunkGeneratorType.FLAT.createSettings();
      List var2 = var0.get("layers").asList((var0x) -> {
         return Pair.of(var0x.get("height").asInt(1), byString(var0x.get("block").asString("")));
      });
      if (var2.stream().anyMatch((var0x) -> {
         return var0x.getSecond() == null;
      })) {
         return getDefault();
      } else {
         List var3 = (List)var2.stream().map((var0x) -> {
            return new FlatLayerInfo((Integer)var0x.getFirst(), (Block)var0x.getSecond());
         }).collect(Collectors.toList());
         if (var3.isEmpty()) {
            return getDefault();
         } else {
            var1.getLayersInfo().addAll(var3);
            var1.updateLayers();
            var1.setBiome((Biome)Registry.BIOME.get(new ResourceLocation(var0.get("biome").asString(""))));
            var0.get("structures").flatMap(Dynamic::getMapValues).ifPresent((var1x) -> {
               var1x.keySet().forEach((var1xx) -> {
                  var1xx.asString().map((var1x) -> {
                     return (Map)var1.getStructuresOptions().put(var1x, Maps.newHashMap());
                  });
               });
            });
            return var1;
         }
      }
   }

   public static FlatLevelGeneratorSettings fromString(String var0) {
      Iterator var1 = Splitter.on(';').split(var0).iterator();
      if (!var1.hasNext()) {
         return getDefault();
      } else {
         FlatLevelGeneratorSettings var2 = (FlatLevelGeneratorSettings)ChunkGeneratorType.FLAT.createSettings();
         List var3 = getLayersInfoFromString((String)var1.next());
         if (var3.isEmpty()) {
            return getDefault();
         } else {
            var2.getLayersInfo().addAll(var3);
            var2.updateLayers();
            Biome var4 = var1.hasNext() ? (Biome)Registry.BIOME.get(new ResourceLocation((String)var1.next())) : null;
            var2.setBiome(var4 == null ? Biomes.PLAINS : var4);
            if (var1.hasNext()) {
               String[] var5 = ((String)var1.next()).toLowerCase(Locale.ROOT).split(",");
               String[] var6 = var5;
               int var7 = var5.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  String var9 = var6[var8];
                  String[] var10 = var9.split("\\(", 2);
                  if (!var10[0].isEmpty()) {
                     var2.addStructure(var10[0]);
                     if (var10.length > 1 && var10[1].endsWith(")") && var10[1].length() > 1) {
                        String[] var11 = var10[1].substring(0, var10[1].length() - 1).split(" ");
                        String[] var12 = var11;
                        int var13 = var11.length;

                        for(int var14 = 0; var14 < var13; ++var14) {
                           String var15 = var12[var14];
                           String[] var16 = var15.split("=", 2);
                           if (var16.length == 2) {
                              var2.addStructureOption(var10[0], var16[0], var16[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               var2.getStructuresOptions().put("village", Maps.newHashMap());
            }

            return var2;
         }
      }
   }

   private void addStructure(String var1) {
      HashMap var2 = Maps.newHashMap();
      this.structuresOptions.put(var1, var2);
   }

   private void addStructureOption(String var1, String var2, String var3) {
      ((Map)this.structuresOptions.get(var1)).put(var2, var3);
      if ("village".equals(var1) && "distance".equals(var2)) {
         this.villagesSpacing = Mth.getInt(var3, this.villagesSpacing, 9);
      }

      if ("biome_1".equals(var1) && "distance".equals(var2)) {
         this.templesSpacing = Mth.getInt(var3, this.templesSpacing, 9);
      }

      if ("stronghold".equals(var1)) {
         if ("distance".equals(var2)) {
            this.strongholdsDistance = Mth.getInt(var3, this.strongholdsDistance, 1);
         } else if ("count".equals(var2)) {
            this.strongholdsCount = Mth.getInt(var3, this.strongholdsCount, 1);
         } else if ("spread".equals(var2)) {
            this.strongholdsSpread = Mth.getInt(var3, this.strongholdsSpread, 1);
         }
      }

      if ("oceanmonument".equals(var1)) {
         if ("separation".equals(var2)) {
            this.monumentsSeparation = Mth.getInt(var3, this.monumentsSeparation, 1);
         } else if ("spacing".equals(var2)) {
            this.monumentsSpacing = Mth.getInt(var3, this.monumentsSpacing, 1);
         }
      }

      if ("endcity".equals(var1) && "distance".equals(var2)) {
         this.endCitySpacing = Mth.getInt(var3, this.endCitySpacing, 1);
      }

      if ("mansion".equals(var1) && "distance".equals(var2)) {
         this.woodlandMansionSpacing = Mth.getInt(var3, this.woodlandMansionSpacing, 1);
      }

   }

   public static FlatLevelGeneratorSettings getDefault() {
      FlatLevelGeneratorSettings var0 = (FlatLevelGeneratorSettings)ChunkGeneratorType.FLAT.createSettings();
      var0.setBiome(Biomes.PLAINS);
      var0.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      var0.getLayersInfo().add(new FlatLayerInfo(2, Blocks.DIRT));
      var0.getLayersInfo().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      var0.updateLayers();
      var0.getStructuresOptions().put("village", Maps.newHashMap());
      return var0;
   }

   public boolean isVoidGen() {
      return this.voidGen;
   }

   public BlockState[] getLayers() {
      return this.layers;
   }

   public void deleteLayer(int var1) {
      this.layers[var1] = null;
   }

   static {
      MINESHAFT_COMPOSITE_FEATURE = Biome.makeComposite(Feature.MINESHAFT, new MineshaftConfiguration(0.004D, MineshaftFeature.Type.NORMAL), FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      VILLAGE_COMPOSITE_FEATURE = Biome.makeComposite(Feature.VILLAGE, new VillageConfiguration("village/plains/town_centers", 6), FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      STRONGHOLD_COMPOSITE_FEATURE = Biome.makeComposite(Feature.STRONGHOLD, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      SWAMPHUT_COMPOSITE_FEATURE = Biome.makeComposite(Feature.SWAMP_HUT, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      DESERT_PYRAMID_COMPOSITE_FEATURE = Biome.makeComposite(Feature.DESERT_PYRAMID, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      JUNGLE_PYRAMID_COMPOSITE_FEATURE = Biome.makeComposite(Feature.JUNGLE_TEMPLE, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      IGLOO_COMPOSITE_FEATURE = Biome.makeComposite(Feature.IGLOO, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      SHIPWRECK_COMPOSITE_FEATURE = Biome.makeComposite(Feature.SHIPWRECK, new ShipwreckConfiguration(false), FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      OCEAN_MONUMENT_COMPOSITE_FEATURE = Biome.makeComposite(Feature.OCEAN_MONUMENT, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      WATER_LAKE_COMPOSITE_FEATURE = Biome.makeComposite(Feature.LAKE, new LakeConfiguration(Blocks.WATER.defaultBlockState()), FeatureDecorator.WATER_LAKE, new LakeChanceDecoratorConfig(4));
      LAVA_LAKE_COMPOSITE_FEATURE = Biome.makeComposite(Feature.LAKE, new LakeConfiguration(Blocks.LAVA.defaultBlockState()), FeatureDecorator.LAVA_LAKE, new LakeChanceDecoratorConfig(80));
      ENDCITY_COMPOSITE_FEATURE = Biome.makeComposite(Feature.END_CITY, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      WOOLAND_MANSION_COMPOSITE_FEATURE = Biome.makeComposite(Feature.WOODLAND_MANSION, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      FORTRESS_COMPOSITE_FEATURE = Biome.makeComposite(Feature.NETHER_BRIDGE, FeatureConfiguration.NONE, FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      OCEAN_RUIN_COMPOSITE_FEATURE = Biome.makeComposite(Feature.OCEAN_RUIN, new OceanRuinConfiguration(OceanRuinFeature.Type.COLD, 0.3F, 0.1F), FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      PILLAGER_OUTPOST_COMPOSITE_FEATURE = Biome.makeComposite(Feature.PILLAGER_OUTPOST, new PillagerOutpostConfiguration(0.004D), FeatureDecorator.NOPE, DecoratorConfiguration.NONE);
      STRUCTURE_FEATURES_STEP = (Map)Util.make(Maps.newHashMap(), (var0) -> {
         var0.put(MINESHAFT_COMPOSITE_FEATURE, GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
         var0.put(VILLAGE_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
         var0.put(STRONGHOLD_COMPOSITE_FEATURE, GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
         var0.put(SWAMPHUT_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
         var0.put(DESERT_PYRAMID_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
         var0.put(JUNGLE_PYRAMID_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
         var0.put(IGLOO_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
         var0.put(SHIPWRECK_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
         var0.put(OCEAN_RUIN_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
         var0.put(WATER_LAKE_COMPOSITE_FEATURE, GenerationStep.Decoration.LOCAL_MODIFICATIONS);
         var0.put(LAVA_LAKE_COMPOSITE_FEATURE, GenerationStep.Decoration.LOCAL_MODIFICATIONS);
         var0.put(ENDCITY_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
         var0.put(WOOLAND_MANSION_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
         var0.put(FORTRESS_COMPOSITE_FEATURE, GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
         var0.put(OCEAN_MONUMENT_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
         var0.put(PILLAGER_OUTPOST_COMPOSITE_FEATURE, GenerationStep.Decoration.SURFACE_STRUCTURES);
      });
      STRUCTURE_FEATURES = (Map)Util.make(Maps.newHashMap(), (var0) -> {
         var0.put("mineshaft", new ConfiguredFeature[]{MINESHAFT_COMPOSITE_FEATURE});
         var0.put("village", new ConfiguredFeature[]{VILLAGE_COMPOSITE_FEATURE});
         var0.put("stronghold", new ConfiguredFeature[]{STRONGHOLD_COMPOSITE_FEATURE});
         var0.put("biome_1", new ConfiguredFeature[]{SWAMPHUT_COMPOSITE_FEATURE, DESERT_PYRAMID_COMPOSITE_FEATURE, JUNGLE_PYRAMID_COMPOSITE_FEATURE, IGLOO_COMPOSITE_FEATURE, OCEAN_RUIN_COMPOSITE_FEATURE, SHIPWRECK_COMPOSITE_FEATURE});
         var0.put("oceanmonument", new ConfiguredFeature[]{OCEAN_MONUMENT_COMPOSITE_FEATURE});
         var0.put("lake", new ConfiguredFeature[]{WATER_LAKE_COMPOSITE_FEATURE});
         var0.put("lava_lake", new ConfiguredFeature[]{LAVA_LAKE_COMPOSITE_FEATURE});
         var0.put("endcity", new ConfiguredFeature[]{ENDCITY_COMPOSITE_FEATURE});
         var0.put("mansion", new ConfiguredFeature[]{WOOLAND_MANSION_COMPOSITE_FEATURE});
         var0.put("fortress", new ConfiguredFeature[]{FORTRESS_COMPOSITE_FEATURE});
         var0.put("pillager_outpost", new ConfiguredFeature[]{PILLAGER_OUTPOST_COMPOSITE_FEATURE});
      });
      STRUCTURE_FEATURES_DEFAULT = (Map)Util.make(Maps.newHashMap(), (var0) -> {
         var0.put(MINESHAFT_COMPOSITE_FEATURE, new MineshaftConfiguration(0.004D, MineshaftFeature.Type.NORMAL));
         var0.put(VILLAGE_COMPOSITE_FEATURE, new VillageConfiguration("village/plains/town_centers", 6));
         var0.put(STRONGHOLD_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
         var0.put(SWAMPHUT_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
         var0.put(DESERT_PYRAMID_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
         var0.put(JUNGLE_PYRAMID_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
         var0.put(IGLOO_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
         var0.put(OCEAN_RUIN_COMPOSITE_FEATURE, new OceanRuinConfiguration(OceanRuinFeature.Type.COLD, 0.3F, 0.9F));
         var0.put(SHIPWRECK_COMPOSITE_FEATURE, new ShipwreckConfiguration(false));
         var0.put(OCEAN_MONUMENT_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
         var0.put(ENDCITY_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
         var0.put(WOOLAND_MANSION_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
         var0.put(FORTRESS_COMPOSITE_FEATURE, FeatureConfiguration.NONE);
         var0.put(PILLAGER_OUTPOST_COMPOSITE_FEATURE, new PillagerOutpostConfiguration(0.004D));
      });
   }
}
