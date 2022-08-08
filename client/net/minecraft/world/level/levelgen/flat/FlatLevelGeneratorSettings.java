package net.minecraft.world.level.levelgen.flat;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.slf4j.Logger;

public class FlatLevelGeneratorSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<FlatLevelGeneratorSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter((var0x) -> {
         return var0x.biomes;
      }), RegistryCodecs.homogeneousList(Registry.STRUCTURE_SET_REGISTRY).optionalFieldOf("structure_overrides").forGetter((var0x) -> {
         return var0x.structureOverrides;
      }), FlatLayerInfo.CODEC.listOf().fieldOf("layers").forGetter(FlatLevelGeneratorSettings::getLayersInfo), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((var0x) -> {
         return var0x.addLakes;
      }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((var0x) -> {
         return var0x.decoration;
      }), Biome.CODEC.optionalFieldOf("biome").orElseGet(Optional::empty).forGetter((var0x) -> {
         return Optional.of(var0x.biome);
      })).apply(var0, FlatLevelGeneratorSettings::new);
   }).comapFlatMap(FlatLevelGeneratorSettings::validateHeight, Function.identity()).stable();
   private final Registry<Biome> biomes;
   private final Optional<HolderSet<StructureSet>> structureOverrides;
   private final List<FlatLayerInfo> layersInfo;
   private Holder<Biome> biome;
   private final List<BlockState> layers;
   private boolean voidGen;
   private boolean decoration;
   private boolean addLakes;

   private static DataResult<FlatLevelGeneratorSettings> validateHeight(FlatLevelGeneratorSettings var0) {
      int var1 = var0.layersInfo.stream().mapToInt(FlatLayerInfo::getHeight).sum();
      return var1 > DimensionType.Y_SIZE ? DataResult.error("Sum of layer heights is > " + DimensionType.Y_SIZE, var0) : DataResult.success(var0);
   }

   private FlatLevelGeneratorSettings(Registry<Biome> var1, Optional<HolderSet<StructureSet>> var2, List<FlatLayerInfo> var3, boolean var4, boolean var5, Optional<Holder<Biome>> var6) {
      this(var2, var1);
      if (var4) {
         this.setAddLakes();
      }

      if (var5) {
         this.setDecoration();
      }

      this.layersInfo.addAll(var3);
      this.updateLayers();
      if (var6.isEmpty()) {
         LOGGER.error("Unknown biome, defaulting to plains");
         this.biome = var1.getOrCreateHolderOrThrow(Biomes.PLAINS);
      } else {
         this.biome = (Holder)var6.get();
      }

   }

   public FlatLevelGeneratorSettings(Optional<HolderSet<StructureSet>> var1, Registry<Biome> var2) {
      super();
      this.layersInfo = Lists.newArrayList();
      this.biomes = var2;
      this.structureOverrides = var1;
      this.biome = var2.getOrCreateHolderOrThrow(Biomes.PLAINS);
      this.layers = Lists.newArrayList();
   }

   public FlatLevelGeneratorSettings withLayers(List<FlatLayerInfo> var1, Optional<HolderSet<StructureSet>> var2) {
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

   public BiomeGenerationSettings adjustGenerationSettings(Holder<Biome> var1) {
      if (!var1.equals(this.biome)) {
         return ((Biome)var1.value()).getGenerationSettings();
      } else {
         BiomeGenerationSettings var2 = ((Biome)this.getBiome().value()).getGenerationSettings();
         BiomeGenerationSettings.Builder var3 = new BiomeGenerationSettings.Builder();
         if (this.addLakes) {
            var3.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND);
            var3.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_SURFACE);
         }

         boolean var4 = (!this.voidGen || var1.is(Biomes.THE_VOID)) && this.decoration;
         List var5;
         int var6;
         if (var4) {
            var5 = var2.features();

            for(var6 = 0; var6 < var5.size(); ++var6) {
               if (var6 != GenerationStep.Decoration.UNDERGROUND_STRUCTURES.ordinal() && var6 != GenerationStep.Decoration.SURFACE_STRUCTURES.ordinal()) {
                  HolderSet var7 = (HolderSet)var5.get(var6);
                  Iterator var8 = var7.iterator();

                  while(var8.hasNext()) {
                     Holder var9 = (Holder)var8.next();
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
               var3.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, PlacementUtils.inlinePlaced(Feature.FILL_LAYER, new LayerConfiguration(var6, var10)));
            }
         }

         return var3.build();
      }
   }

   public Optional<HolderSet<StructureSet>> structureOverrides() {
      return this.structureOverrides;
   }

   public Holder<Biome> getBiome() {
      return this.biome;
   }

   public void setBiome(Holder<Biome> var1) {
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

   public static FlatLevelGeneratorSettings getDefault(Registry<Biome> var0, Registry<StructureSet> var1) {
      HolderSet.Direct var2 = HolderSet.direct(var1.getHolderOrThrow(BuiltinStructureSets.STRONGHOLDS), var1.getHolderOrThrow(BuiltinStructureSets.VILLAGES));
      FlatLevelGeneratorSettings var3 = new FlatLevelGeneratorSettings(Optional.of(var2), var0);
      var3.biome = var0.getOrCreateHolderOrThrow(Biomes.PLAINS);
      var3.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      var3.getLayersInfo().add(new FlatLayerInfo(2, Blocks.DIRT));
      var3.getLayersInfo().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      var3.updateLayers();
      return var3;
   }
}
