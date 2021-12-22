package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatLevelGeneratorSettings {
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
   }).comapFlatMap(FlatLevelGeneratorSettings::validateHeight, Function.identity()).stable();
   private final Registry<Biome> biomes;
   private final StructureSettings structureSettings;
   private final List<FlatLayerInfo> layersInfo;
   private Supplier<Biome> biome;
   private final List<BlockState> layers;
   private boolean voidGen;
   private boolean decoration;
   private boolean addLakes;

   private static DataResult<FlatLevelGeneratorSettings> validateHeight(FlatLevelGeneratorSettings var0) {
      int var1 = var0.layersInfo.stream().mapToInt(FlatLayerInfo::getHeight).sum();
      return var1 > DimensionType.Y_SIZE ? DataResult.error("Sum of layer heights is > " + DimensionType.Y_SIZE, var0) : DataResult.success(var0);
   }

   private FlatLevelGeneratorSettings(Registry<Biome> var1, StructureSettings var2, List<FlatLayerInfo> var3, boolean var4, boolean var5, Optional<Supplier<Biome>> var6) {
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
      this.layers = Lists.newArrayList();
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
      BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder();
      if (this.addLakes) {
         var3.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND);
         var3.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_SURFACE);
      }

      boolean var4 = (!this.voidGen || this.biomes.getResourceKey(var1).equals(Optional.of(Biomes.THE_VOID))) && this.decoration;
      List var5;
      int var6;
      if (var4) {
         var5 = var2.features();

         for(var6 = 0; var6 < var5.size(); ++var6) {
            if (var6 != GenerationStep.Decoration.UNDERGROUND_STRUCTURES.ordinal() && var6 != GenerationStep.Decoration.SURFACE_STRUCTURES.ordinal()) {
               List var7 = (List)var5.get(var6);
               Iterator var8 = var7.iterator();

               while(var8.hasNext()) {
                  Supplier var9 = (Supplier)var8.next();
                  var3.addFeature(var6, var9);
               }
            }
         }
      }

      var5 = this.getLayers();

      for(var6 = 0; var6 < var5.size(); ++var6) {
         BlockState var10 = (BlockState)var5.get(var6);
         if (!Heightmap.Types.MOTION_BLOCKING.isOpaque().test(var10)) {
            var5.set(var6, (Object)null);
            var3.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configured(new LayerConfiguration(var6, var10)).placed());
         }
      }

      return (new Biome.BiomeBuilder()).precipitation(var1.getPrecipitation()).biomeCategory(var1.getBiomeCategory()).temperature(var1.getBaseTemperature()).downfall(var1.getDownfall()).specialEffects(var1.getSpecialEffects()).generationSettings(var3.build()).mobSpawnSettings(var1.getMobSettings()).build();
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

   public List<BlockState> getLayers() {
      return this.layers;
   }

   public void updateLayers() {
      this.layers.clear();
      Iterator var1 = this.layersInfo.iterator();

      while(var1.hasNext()) {
         FlatLayerInfo var2 = (FlatLayerInfo)var1.next();

         for(int var3 = 0; var3 < var2.getHeight(); ++var3) {
            this.layers.add(var2.getBlockState());
         }
      }

      this.voidGen = this.layers.stream().allMatch((var0) -> {
         return var0.is(Blocks.AIR);
      });
   }

   public static FlatLevelGeneratorSettings getDefault(Registry<Biome> var0) {
      StructureSettings var1 = new StructureSettings(Optional.of(StructureSettings.DEFAULT_STRONGHOLD), Maps.newHashMap(ImmutableMap.of(StructureFeature.VILLAGE, (StructureFeatureConfiguration)StructureSettings.DEFAULTS.get(StructureFeature.VILLAGE))));
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
}
