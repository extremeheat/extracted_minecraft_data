package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.Features;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatLevelGeneratorSettings implements LevelHeightAccessor {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Codec<FlatLevelGeneratorSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((var0x) -> {
         return var0x.biomes;
      }), StructureSettings.CODEC.fieldOf("structures").forGetter(FlatLevelGeneratorSettings::structureSettings), FlatLayerInfo.CODEC.listOf().fieldOf("layers").forGetter(FlatLevelGeneratorSettings::getLayersInfo), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((var0x) -> {
         return var0x.addLakes;
      }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((var0x) -> {
         return var0x.decoration;
      }), Biome.CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter((var0x) -> {
         return Optional.of(var0x.biome);
      })).apply(var0, FlatLevelGeneratorSettings::new);
   }).stable();
   private static final Map<StructureFeature<?>, ConfiguredStructureFeature<?, ?>> STRUCTURE_FEATURES = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put(StructureFeature.MINESHAFT, StructureFeatures.MINESHAFT);
      var0.put(StructureFeature.VILLAGE, StructureFeatures.VILLAGE_PLAINS);
      var0.put(StructureFeature.STRONGHOLD, StructureFeatures.STRONGHOLD);
      var0.put(StructureFeature.SWAMP_HUT, StructureFeatures.SWAMP_HUT);
      var0.put(StructureFeature.DESERT_PYRAMID, StructureFeatures.DESERT_PYRAMID);
      var0.put(StructureFeature.JUNGLE_TEMPLE, StructureFeatures.JUNGLE_TEMPLE);
      var0.put(StructureFeature.IGLOO, StructureFeatures.IGLOO);
      var0.put(StructureFeature.OCEAN_RUIN, StructureFeatures.OCEAN_RUIN_COLD);
      var0.put(StructureFeature.SHIPWRECK, StructureFeatures.SHIPWRECK);
      var0.put(StructureFeature.OCEAN_MONUMENT, StructureFeatures.OCEAN_MONUMENT);
      var0.put(StructureFeature.END_CITY, StructureFeatures.END_CITY);
      var0.put(StructureFeature.WOODLAND_MANSION, StructureFeatures.WOODLAND_MANSION);
      var0.put(StructureFeature.NETHER_BRIDGE, StructureFeatures.NETHER_BRIDGE);
      var0.put(StructureFeature.PILLAGER_OUTPOST, StructureFeatures.PILLAGER_OUTPOST);
      var0.put(StructureFeature.RUINED_PORTAL, StructureFeatures.RUINED_PORTAL_STANDARD);
      var0.put(StructureFeature.BASTION_REMNANT, StructureFeatures.BASTION_REMNANT);
   });
   private final Registry<Biome> biomes;
   private final StructureSettings structureSettings;
   private final List<FlatLayerInfo> layersInfo;
   private Supplier<Biome> biome;
   private final BlockState[] layers;
   private boolean voidGen;
   private boolean decoration;
   private boolean addLakes;

   public FlatLevelGeneratorSettings(Registry<Biome> var1, StructureSettings var2, List<FlatLayerInfo> var3, boolean var4, boolean var5, Optional<Supplier<Biome>> var6) {
      this(var2, var1);
      if (var4) {
         this.setAddLakes();
      }

      if (var5) {
         this.setDecoration();
      }

      this.layersInfo.addAll(var3);
      this.updateLayers();
      if (!var6.isPresent()) {
         LOGGER.error("Unknown biome, defaulting to plains");
         this.biome = () -> {
            return (Biome)var1.getOrThrow(Biomes.PLAINS);
         };
      } else {
         this.biome = (Supplier)var6.get();
      }

   }

   public FlatLevelGeneratorSettings(StructureSettings var1, Registry<Biome> var2) {
      super();
      this.layersInfo = Lists.newArrayList();
      this.biomes = var2;
      this.structureSettings = var1;
      this.biome = () -> {
         return (Biome)var2.getOrThrow(Biomes.PLAINS);
      };
      this.layers = new BlockState[this.getHeight()];
   }

   public FlatLevelGeneratorSettings withStructureSettings(StructureSettings var1) {
      return this.withLayers(this.layersInfo, var1);
   }

   public FlatLevelGeneratorSettings withLayers(List<FlatLayerInfo> var1, StructureSettings var2) {
      FlatLevelGeneratorSettings var3 = new FlatLevelGeneratorSettings(var2, this.biomes);
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         FlatLayerInfo var5 = (FlatLayerInfo)var4.next();
         var3.layersInfo.add(new FlatLayerInfo(var5.getHeight(), var5.getBlockState().getBlock()));
         var3.updateLayers();
      }

      var3.setBiome(this.biome);
      if (this.decoration) {
         var3.setDecoration();
      }

      if (this.addLakes) {
         var3.setAddLakes();
      }

      return var3;
   }

   public void setDecoration() {
      this.decoration = true;
   }

   public void setAddLakes() {
      this.addLakes = true;
   }

   public Biome getBiomeFromSettings() {
      Biome var1 = this.getBiome();
      BiomeGenerationSettings var2 = var1.getGenerationSettings();
      BiomeGenerationSettings.Builder var3 = (new BiomeGenerationSettings.Builder()).surfaceBuilder(var2.getSurfaceBuilder());
      if (this.addLakes) {
         var3.addFeature(GenerationStep.Decoration.LAKES, Features.LAKE_WATER);
         var3.addFeature(GenerationStep.Decoration.LAKES, Features.LAKE_LAVA);
      }

      Iterator var4 = this.structureSettings.structureConfig().entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         var3.addStructureStart(var2.withBiomeConfig((ConfiguredStructureFeature)STRUCTURE_FEATURES.get(var5.getKey())));
      }

      boolean var10 = (!this.voidGen || this.biomes.getResourceKey(var1).equals(Optional.of(Biomes.THE_VOID))) && this.decoration;
      int var6;
      if (var10) {
         List var11 = var2.features();

         for(var6 = 0; var6 < var11.size(); ++var6) {
            if (var6 != GenerationStep.Decoration.UNDERGROUND_STRUCTURES.ordinal() && var6 != GenerationStep.Decoration.SURFACE_STRUCTURES.ordinal()) {
               List var7 = (List)var11.get(var6);
               Iterator var8 = var7.iterator();

               while(var8.hasNext()) {
                  Supplier var9 = (Supplier)var8.next();
                  var3.addFeature(var6, var9);
               }
            }
         }
      }

      BlockState[] var12 = this.getLayers();

      for(var6 = 0; var6 < var12.length; ++var6) {
         BlockState var13 = var12[var6];
         if (var13 != null && !Heightmap.Types.MOTION_BLOCKING.isOpaque().test(var13)) {
            this.layers[var6] = null;
            int var14 = this.getMinBuildHeight() + var6;
            var3.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configured(new LayerConfiguration(var14, var13)));
         }
      }

      return (new Biome.BiomeBuilder()).precipitation(var1.getPrecipitation()).biomeCategory(var1.getBiomeCategory()).depth(var1.getDepth()).scale(var1.getScale()).temperature(var1.getBaseTemperature()).downfall(var1.getDownfall()).specialEffects(var1.getSpecialEffects()).generationSettings(var3.build()).mobSpawnSettings(var1.getMobSettings()).build();
   }

   public StructureSettings structureSettings() {
      return this.structureSettings;
   }

   public Biome getBiome() {
      return (Biome)this.biome.get();
   }

   public void setBiome(Supplier<Biome> var1) {
      this.biome = var1;
   }

   public List<FlatLayerInfo> getLayersInfo() {
      return this.layersInfo;
   }

   public BlockState[] getLayers() {
      return this.layers;
   }

   public void updateLayers() {
      Arrays.fill(this.layers, 0, this.layers.length, (Object)null);
      int var1 = this.getMinBuildHeight();

      FlatLayerInfo var3;
      for(Iterator var2 = this.layersInfo.iterator(); var2.hasNext(); var1 += var3.getHeight()) {
         var3 = (FlatLayerInfo)var2.next();
         var3.setStart(var1);
      }

      this.voidGen = true;
      Iterator var5 = this.layersInfo.iterator();

      while(var5.hasNext()) {
         FlatLayerInfo var6 = (FlatLayerInfo)var5.next();

         for(int var7 = var6.getStart(); var7 < var6.getStart() + var6.getHeight(); ++var7) {
            BlockState var4 = var6.getBlockState();
            if (!var4.is(Blocks.AIR)) {
               this.voidGen = false;
               this.layers[this.getLayerIndex(var7)] = var4;
            }
         }
      }

   }

   public static FlatLevelGeneratorSettings getDefault(Registry<Biome> var0) {
      StructureSettings var1 = new StructureSettings(Optional.of(StructureSettings.DEFAULT_STRONGHOLD), Maps.newHashMap(ImmutableMap.of(StructureFeature.VILLAGE, StructureSettings.DEFAULTS.get(StructureFeature.VILLAGE))));
      FlatLevelGeneratorSettings var2 = new FlatLevelGeneratorSettings(var1, var0);
      var2.biome = () -> {
         return (Biome)var0.getOrThrow(Biomes.PLAINS);
      };
      var2.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      var2.getLayersInfo().add(new FlatLayerInfo(2, Blocks.DIRT));
      var2.getLayersInfo().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      var2.updateLayers();
      return var2;
   }

   public int getLayerIndex(int var1) {
      return var1 - this.getMinBuildHeight();
   }

   public int getSectionsCount() {
      return 16;
   }

   public int getMinSection() {
      return 0;
   }
}
