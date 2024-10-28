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
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.slf4j.Logger;

public class FlatLevelGeneratorSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<FlatLevelGeneratorSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RegistryCodecs.homogeneousList(Registries.STRUCTURE_SET).lenientOptionalFieldOf("structure_overrides").forGetter((var0x) -> {
         return var0x.structureOverrides;
      }), FlatLayerInfo.CODEC.listOf().fieldOf("layers").forGetter(FlatLevelGeneratorSettings::getLayersInfo), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((var0x) -> {
         return var0x.addLakes;
      }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((var0x) -> {
         return var0x.decoration;
      }), Biome.CODEC.lenientOptionalFieldOf("biome").orElseGet(Optional::empty).forGetter((var0x) -> {
         return Optional.of(var0x.biome);
      }), RegistryOps.retrieveElement(Biomes.PLAINS), RegistryOps.retrieveElement(MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND), RegistryOps.retrieveElement(MiscOverworldPlacements.LAKE_LAVA_SURFACE)).apply(var0, FlatLevelGeneratorSettings::new);
   }).comapFlatMap(FlatLevelGeneratorSettings::validateHeight, Function.identity()).stable();
   private final Optional<HolderSet<StructureSet>> structureOverrides;
   private final List<FlatLayerInfo> layersInfo;
   private final Holder<Biome> biome;
   private final List<BlockState> layers;
   private boolean voidGen;
   private boolean decoration;
   private boolean addLakes;
   private final List<Holder<PlacedFeature>> lakes;

   private static DataResult<FlatLevelGeneratorSettings> validateHeight(FlatLevelGeneratorSettings var0) {
      int var1 = var0.layersInfo.stream().mapToInt(FlatLayerInfo::getHeight).sum();
      return var1 > DimensionType.Y_SIZE ? DataResult.error(() -> {
         return "Sum of layer heights is > " + DimensionType.Y_SIZE;
      }, var0) : DataResult.success(var0);
   }

   private FlatLevelGeneratorSettings(Optional<HolderSet<StructureSet>> var1, List<FlatLayerInfo> var2, boolean var3, boolean var4, Optional<Holder<Biome>> var5, Holder.Reference<Biome> var6, Holder<PlacedFeature> var7, Holder<PlacedFeature> var8) {
      this(var1, getBiome(var5, var6), List.of(var7, var8));
      if (var3) {
         this.setAddLakes();
      }

      if (var4) {
         this.setDecoration();
      }

      this.layersInfo.addAll(var2);
      this.updateLayers();
   }

   private static Holder<Biome> getBiome(Optional<? extends Holder<Biome>> var0, Holder<Biome> var1) {
      if (var0.isEmpty()) {
         LOGGER.error("Unknown biome, defaulting to plains");
         return var1;
      } else {
         return (Holder)var0.get();
      }
   }

   public FlatLevelGeneratorSettings(Optional<HolderSet<StructureSet>> var1, Holder<Biome> var2, List<Holder<PlacedFeature>> var3) {
      super();
      this.layersInfo = Lists.newArrayList();
      this.structureOverrides = var1;
      this.biome = var2;
      this.layers = Lists.newArrayList();
      this.lakes = var3;
   }

   public FlatLevelGeneratorSettings withBiomeAndLayers(List<FlatLayerInfo> var1, Optional<HolderSet<StructureSet>> var2, Holder<Biome> var3) {
      FlatLevelGeneratorSettings var4 = new FlatLevelGeneratorSettings(var2, var3, this.lakes);
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         FlatLayerInfo var6 = (FlatLayerInfo)var5.next();
         var4.layersInfo.add(new FlatLayerInfo(var6.getHeight(), var6.getBlockState().getBlock()));
         var4.updateLayers();
      }

      if (this.decoration) {
         var4.setDecoration();
      }

      if (this.addLakes) {
         var4.setAddLakes();
      }

      return var4;
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
         BiomeGenerationSettings.PlainBuilder var3 = new BiomeGenerationSettings.PlainBuilder();
         if (this.addLakes) {
            Iterator var4 = this.lakes.iterator();

            while(var4.hasNext()) {
               Holder var5 = (Holder)var4.next();
               var3.addFeature(GenerationStep.Decoration.LAKES, var5);
            }
         }

         boolean var10 = (!this.voidGen || var1.is(Biomes.THE_VOID)) && this.decoration;
         int var6;
         List var11;
         if (var10) {
            var11 = var2.features();

            for(var6 = 0; var6 < var11.size(); ++var6) {
               if (var6 != GenerationStep.Decoration.UNDERGROUND_STRUCTURES.ordinal() && var6 != GenerationStep.Decoration.SURFACE_STRUCTURES.ordinal() && (!this.addLakes || var6 != GenerationStep.Decoration.LAKES.ordinal())) {
                  HolderSet var7 = (HolderSet)var11.get(var6);
                  Iterator var8 = var7.iterator();

                  while(var8.hasNext()) {
                     Holder var9 = (Holder)var8.next();
                     var3.addFeature(var6, var9);
                  }
               }
            }
         }

         var11 = this.getLayers();

         for(var6 = 0; var6 < var11.size(); ++var6) {
            BlockState var12 = (BlockState)var11.get(var6);
            if (!Heightmap.Types.MOTION_BLOCKING.isOpaque().test(var12)) {
               var11.set(var6, (Object)null);
               var3.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, PlacementUtils.inlinePlaced(Feature.FILL_LAYER, new LayerConfiguration(var6, var12)));
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

   public static FlatLevelGeneratorSettings getDefault(HolderGetter<Biome> var0, HolderGetter<StructureSet> var1, HolderGetter<PlacedFeature> var2) {
      HolderSet.Direct var3 = HolderSet.direct(var1.getOrThrow(BuiltinStructureSets.STRONGHOLDS), var1.getOrThrow(BuiltinStructureSets.VILLAGES));
      FlatLevelGeneratorSettings var4 = new FlatLevelGeneratorSettings(Optional.of(var3), getDefaultBiome(var0), createLakesList(var2));
      var4.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      var4.getLayersInfo().add(new FlatLayerInfo(2, Blocks.DIRT));
      var4.getLayersInfo().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      var4.updateLayers();
      return var4;
   }

   public static Holder<Biome> getDefaultBiome(HolderGetter<Biome> var0) {
      return var0.getOrThrow(Biomes.PLAINS);
   }

   public static List<Holder<PlacedFeature>> createLakesList(HolderGetter<PlacedFeature> var0) {
      return List.of(var0.getOrThrow(MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND), var0.getOrThrow(MiscOverworldPlacements.LAKE_LAVA_SURFACE));
   }
}
