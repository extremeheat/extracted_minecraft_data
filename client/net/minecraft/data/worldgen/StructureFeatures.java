package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.RuinedPortalFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;

public class StructureFeatures {
   private static final ConfiguredStructureFeature<JigsawConfiguration, ? extends StructureFeature<JigsawConfiguration>> PILLAGER_OUTPOST;
   private static final ConfiguredStructureFeature<MineshaftConfiguration, ? extends StructureFeature<MineshaftConfiguration>> MINESHAFT;
   private static final ConfiguredStructureFeature<MineshaftConfiguration, ? extends StructureFeature<MineshaftConfiguration>> MINESHAFT_MESA;
   private static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> WOODLAND_MANSION;
   private static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> JUNGLE_TEMPLE;
   private static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> DESERT_PYRAMID;
   private static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> IGLOO;
   private static final ConfiguredStructureFeature<ShipwreckConfiguration, ? extends StructureFeature<ShipwreckConfiguration>> SHIPWRECK;
   private static final ConfiguredStructureFeature<ShipwreckConfiguration, ? extends StructureFeature<ShipwreckConfiguration>> SHIPWRECK_BEACHED;
   private static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> SWAMP_HUT;
   public static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> STRONGHOLD;
   private static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> OCEAN_MONUMENT;
   private static final ConfiguredStructureFeature<OceanRuinConfiguration, ? extends StructureFeature<OceanRuinConfiguration>> OCEAN_RUIN_COLD;
   private static final ConfiguredStructureFeature<OceanRuinConfiguration, ? extends StructureFeature<OceanRuinConfiguration>> OCEAN_RUIN_WARM;
   private static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> NETHER_BRIDGE;
   private static final ConfiguredStructureFeature<RangeConfiguration, ? extends StructureFeature<RangeConfiguration>> NETHER_FOSSIL;
   private static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> END_CITY;
   private static final ConfiguredStructureFeature<ProbabilityFeatureConfiguration, ? extends StructureFeature<ProbabilityFeatureConfiguration>> BURIED_TREASURE;
   private static final ConfiguredStructureFeature<JigsawConfiguration, ? extends StructureFeature<JigsawConfiguration>> BASTION_REMNANT;
   private static final ConfiguredStructureFeature<JigsawConfiguration, ? extends StructureFeature<JigsawConfiguration>> VILLAGE_PLAINS;
   private static final ConfiguredStructureFeature<JigsawConfiguration, ? extends StructureFeature<JigsawConfiguration>> VILLAGE_DESERT;
   private static final ConfiguredStructureFeature<JigsawConfiguration, ? extends StructureFeature<JigsawConfiguration>> VILLAGE_SAVANNA;
   private static final ConfiguredStructureFeature<JigsawConfiguration, ? extends StructureFeature<JigsawConfiguration>> VILLAGE_SNOWY;
   private static final ConfiguredStructureFeature<JigsawConfiguration, ? extends StructureFeature<JigsawConfiguration>> VILLAGE_TAIGA;
   private static final ConfiguredStructureFeature<RuinedPortalConfiguration, ? extends StructureFeature<RuinedPortalConfiguration>> RUINED_PORTAL_STANDARD;
   private static final ConfiguredStructureFeature<RuinedPortalConfiguration, ? extends StructureFeature<RuinedPortalConfiguration>> RUINED_PORTAL_DESERT;
   private static final ConfiguredStructureFeature<RuinedPortalConfiguration, ? extends StructureFeature<RuinedPortalConfiguration>> RUINED_PORTAL_JUNGLE;
   private static final ConfiguredStructureFeature<RuinedPortalConfiguration, ? extends StructureFeature<RuinedPortalConfiguration>> RUINED_PORTAL_SWAMP;
   private static final ConfiguredStructureFeature<RuinedPortalConfiguration, ? extends StructureFeature<RuinedPortalConfiguration>> RUINED_PORTAL_MOUNTAIN;
   private static final ConfiguredStructureFeature<RuinedPortalConfiguration, ? extends StructureFeature<RuinedPortalConfiguration>> RUINED_PORTAL_OCEAN;
   private static final ConfiguredStructureFeature<RuinedPortalConfiguration, ? extends StructureFeature<RuinedPortalConfiguration>> RUINED_PORTAL_NETHER;

   public StructureFeatures() {
      super();
   }

   public static ConfiguredStructureFeature<?, ?> bootstrap() {
      return MINESHAFT;
   }

   private static <FC extends FeatureConfiguration, F extends StructureFeature<FC>> ConfiguredStructureFeature<FC, F> register(String var0, ConfiguredStructureFeature<FC, F> var1) {
      return (ConfiguredStructureFeature)BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, (String)var0, var1);
   }

   private static void register(BiConsumer<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> var0, ConfiguredStructureFeature<?, ?> var1, Set<ResourceKey<Biome>> var2) {
      var2.forEach((var2x) -> {
         var0.accept(var1, var2x);
      });
   }

   private static void register(BiConsumer<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> var0, ConfiguredStructureFeature<?, ?> var1, ResourceKey<Biome> var2) {
      var0.accept(var1, var2);
   }

   public static void registerStructures(BiConsumer<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> var0) {
      ImmutableSet var1 = ImmutableSet.builder().add(Biomes.DEEP_FROZEN_OCEAN).add(Biomes.DEEP_COLD_OCEAN).add(Biomes.DEEP_OCEAN).add(Biomes.DEEP_LUKEWARM_OCEAN).build();
      ImmutableSet var2 = ImmutableSet.builder().add(Biomes.FROZEN_OCEAN).add(Biomes.OCEAN).add(Biomes.COLD_OCEAN).add(Biomes.LUKEWARM_OCEAN).add(Biomes.WARM_OCEAN).addAll(var1).build();
      ImmutableSet var3 = ImmutableSet.builder().add(Biomes.BEACH).add(Biomes.SNOWY_BEACH).build();
      ImmutableSet var4 = ImmutableSet.builder().add(Biomes.RIVER).add(Biomes.FROZEN_RIVER).build();
      ImmutableSet var5 = ImmutableSet.builder().add(Biomes.MEADOW).add(Biomes.FROZEN_PEAKS).add(Biomes.JAGGED_PEAKS).add(Biomes.STONY_PEAKS).add(Biomes.SNOWY_SLOPES).build();
      ImmutableSet var6 = ImmutableSet.builder().add(Biomes.BADLANDS).add(Biomes.ERODED_BADLANDS).add(Biomes.WOODED_BADLANDS).build();
      ImmutableSet var7 = ImmutableSet.builder().add(Biomes.WINDSWEPT_HILLS).add(Biomes.WINDSWEPT_FOREST).add(Biomes.WINDSWEPT_GRAVELLY_HILLS).build();
      ImmutableSet var8 = ImmutableSet.builder().add(Biomes.TAIGA).add(Biomes.SNOWY_TAIGA).add(Biomes.OLD_GROWTH_PINE_TAIGA).add(Biomes.OLD_GROWTH_SPRUCE_TAIGA).build();
      ImmutableSet var9 = ImmutableSet.builder().add(Biomes.BAMBOO_JUNGLE).add(Biomes.JUNGLE).add(Biomes.SPARSE_JUNGLE).build();
      ImmutableSet var10 = ImmutableSet.builder().add(Biomes.FOREST).add(Biomes.FLOWER_FOREST).add(Biomes.BIRCH_FOREST).add(Biomes.OLD_GROWTH_BIRCH_FOREST).add(Biomes.DARK_FOREST).add(Biomes.GROVE).build();
      ImmutableSet var11 = ImmutableSet.builder().add(Biomes.NETHER_WASTES).add(Biomes.BASALT_DELTAS).add(Biomes.SOUL_SAND_VALLEY).add(Biomes.CRIMSON_FOREST).add(Biomes.WARPED_FOREST).build();
      register(var0, BURIED_TREASURE, (Set)var3);
      register(var0, DESERT_PYRAMID, Biomes.DESERT);
      register(var0, IGLOO, Biomes.SNOWY_TAIGA);
      register(var0, IGLOO, Biomes.SNOWY_PLAINS);
      register(var0, IGLOO, Biomes.SNOWY_SLOPES);
      register(var0, JUNGLE_TEMPLE, Biomes.BAMBOO_JUNGLE);
      register(var0, JUNGLE_TEMPLE, Biomes.JUNGLE);
      register(var0, MINESHAFT, (Set)var2);
      register(var0, MINESHAFT, (Set)var4);
      register(var0, MINESHAFT, (Set)var3);
      register(var0, MINESHAFT, Biomes.STONY_SHORE);
      register(var0, MINESHAFT, (Set)var5);
      register(var0, MINESHAFT, (Set)var7);
      register(var0, MINESHAFT, (Set)var8);
      register(var0, MINESHAFT, (Set)var9);
      register(var0, MINESHAFT, (Set)var10);
      register(var0, MINESHAFT, Biomes.MUSHROOM_FIELDS);
      register(var0, MINESHAFT, Biomes.ICE_SPIKES);
      register(var0, MINESHAFT, Biomes.WINDSWEPT_SAVANNA);
      register(var0, MINESHAFT, Biomes.DESERT);
      register(var0, MINESHAFT, Biomes.SAVANNA);
      register(var0, MINESHAFT, Biomes.SNOWY_PLAINS);
      register(var0, MINESHAFT, Biomes.PLAINS);
      register(var0, MINESHAFT, Biomes.SUNFLOWER_PLAINS);
      register(var0, MINESHAFT, Biomes.SWAMP);
      register(var0, MINESHAFT, Biomes.SAVANNA_PLATEAU);
      register(var0, MINESHAFT, Biomes.DRIPSTONE_CAVES);
      register(var0, MINESHAFT, Biomes.LUSH_CAVES);
      register(var0, MINESHAFT_MESA, (Set)var6);
      register(var0, OCEAN_MONUMENT, (Set)var1);
      register(var0, OCEAN_RUIN_COLD, Biomes.FROZEN_OCEAN);
      register(var0, OCEAN_RUIN_COLD, Biomes.COLD_OCEAN);
      register(var0, OCEAN_RUIN_COLD, Biomes.OCEAN);
      register(var0, OCEAN_RUIN_COLD, Biomes.DEEP_FROZEN_OCEAN);
      register(var0, OCEAN_RUIN_COLD, Biomes.DEEP_COLD_OCEAN);
      register(var0, OCEAN_RUIN_COLD, Biomes.DEEP_OCEAN);
      register(var0, OCEAN_RUIN_WARM, Biomes.LUKEWARM_OCEAN);
      register(var0, OCEAN_RUIN_WARM, Biomes.WARM_OCEAN);
      register(var0, OCEAN_RUIN_WARM, Biomes.DEEP_LUKEWARM_OCEAN);
      register(var0, PILLAGER_OUTPOST, Biomes.DESERT);
      register(var0, PILLAGER_OUTPOST, Biomes.PLAINS);
      register(var0, PILLAGER_OUTPOST, Biomes.SAVANNA);
      register(var0, PILLAGER_OUTPOST, Biomes.SNOWY_PLAINS);
      register(var0, PILLAGER_OUTPOST, Biomes.TAIGA);
      register(var0, PILLAGER_OUTPOST, (Set)var5);
      register(var0, PILLAGER_OUTPOST, Biomes.GROVE);
      register(var0, RUINED_PORTAL_DESERT, Biomes.DESERT);
      register(var0, RUINED_PORTAL_JUNGLE, (Set)var9);
      register(var0, RUINED_PORTAL_OCEAN, (Set)var2);
      register(var0, RUINED_PORTAL_SWAMP, Biomes.SWAMP);
      register(var0, RUINED_PORTAL_MOUNTAIN, (Set)var6);
      register(var0, RUINED_PORTAL_MOUNTAIN, (Set)var7);
      register(var0, RUINED_PORTAL_MOUNTAIN, Biomes.SAVANNA_PLATEAU);
      register(var0, RUINED_PORTAL_MOUNTAIN, Biomes.WINDSWEPT_SAVANNA);
      register(var0, RUINED_PORTAL_MOUNTAIN, Biomes.STONY_SHORE);
      register(var0, RUINED_PORTAL_MOUNTAIN, (Set)var5);
      register(var0, RUINED_PORTAL_STANDARD, Biomes.MUSHROOM_FIELDS);
      register(var0, RUINED_PORTAL_STANDARD, Biomes.ICE_SPIKES);
      register(var0, RUINED_PORTAL_STANDARD, (Set)var3);
      register(var0, RUINED_PORTAL_STANDARD, (Set)var4);
      register(var0, RUINED_PORTAL_STANDARD, (Set)var8);
      register(var0, RUINED_PORTAL_STANDARD, (Set)var10);
      register(var0, RUINED_PORTAL_STANDARD, Biomes.DRIPSTONE_CAVES);
      register(var0, RUINED_PORTAL_STANDARD, Biomes.LUSH_CAVES);
      register(var0, RUINED_PORTAL_STANDARD, Biomes.SAVANNA);
      register(var0, RUINED_PORTAL_STANDARD, Biomes.SNOWY_PLAINS);
      register(var0, RUINED_PORTAL_STANDARD, Biomes.PLAINS);
      register(var0, RUINED_PORTAL_STANDARD, Biomes.SUNFLOWER_PLAINS);
      register(var0, SHIPWRECK_BEACHED, (Set)var3);
      register(var0, SHIPWRECK, (Set)var2);
      register(var0, SWAMP_HUT, Biomes.SWAMP);
      register(var0, VILLAGE_DESERT, Biomes.DESERT);
      register(var0, VILLAGE_PLAINS, Biomes.PLAINS);
      register(var0, VILLAGE_PLAINS, Biomes.MEADOW);
      register(var0, VILLAGE_SAVANNA, Biomes.SAVANNA);
      register(var0, VILLAGE_SNOWY, Biomes.SNOWY_PLAINS);
      register(var0, VILLAGE_TAIGA, Biomes.TAIGA);
      register(var0, WOODLAND_MANSION, Biomes.DARK_FOREST);
      register(var0, NETHER_BRIDGE, (Set)var11);
      register(var0, NETHER_FOSSIL, Biomes.SOUL_SAND_VALLEY);
      register(var0, BASTION_REMNANT, Biomes.CRIMSON_FOREST);
      register(var0, BASTION_REMNANT, Biomes.NETHER_WASTES);
      register(var0, BASTION_REMNANT, Biomes.SOUL_SAND_VALLEY);
      register(var0, BASTION_REMNANT, Biomes.WARPED_FOREST);
      register(var0, RUINED_PORTAL_NETHER, (Set)var11);
      register(var0, END_CITY, Biomes.END_HIGHLANDS);
      register(var0, END_CITY, Biomes.END_MIDLANDS);
   }

   static {
      PILLAGER_OUTPOST = register("pillager_outpost", StructureFeature.PILLAGER_OUTPOST.configured(new JigsawConfiguration(() -> {
         return PillagerOutpostPools.START;
      }, 7)));
      MINESHAFT = register("mineshaft", StructureFeature.MINESHAFT.configured(new MineshaftConfiguration(0.004F, MineshaftFeature.Type.NORMAL)));
      MINESHAFT_MESA = register("mineshaft_mesa", StructureFeature.MINESHAFT.configured(new MineshaftConfiguration(0.004F, MineshaftFeature.Type.MESA)));
      WOODLAND_MANSION = register("mansion", StructureFeature.WOODLAND_MANSION.configured(NoneFeatureConfiguration.INSTANCE));
      JUNGLE_TEMPLE = register("jungle_pyramid", StructureFeature.JUNGLE_TEMPLE.configured(NoneFeatureConfiguration.INSTANCE));
      DESERT_PYRAMID = register("desert_pyramid", StructureFeature.DESERT_PYRAMID.configured(NoneFeatureConfiguration.INSTANCE));
      IGLOO = register("igloo", StructureFeature.IGLOO.configured(NoneFeatureConfiguration.INSTANCE));
      SHIPWRECK = register("shipwreck", StructureFeature.SHIPWRECK.configured(new ShipwreckConfiguration(false)));
      SHIPWRECK_BEACHED = register("shipwreck_beached", StructureFeature.SHIPWRECK.configured(new ShipwreckConfiguration(true)));
      SWAMP_HUT = register("swamp_hut", StructureFeature.SWAMP_HUT.configured(NoneFeatureConfiguration.INSTANCE));
      STRONGHOLD = register("stronghold", StructureFeature.STRONGHOLD.configured(NoneFeatureConfiguration.INSTANCE));
      OCEAN_MONUMENT = register("monument", StructureFeature.OCEAN_MONUMENT.configured(NoneFeatureConfiguration.INSTANCE));
      OCEAN_RUIN_COLD = register("ocean_ruin_cold", StructureFeature.OCEAN_RUIN.configured(new OceanRuinConfiguration(OceanRuinFeature.Type.COLD, 0.3F, 0.9F)));
      OCEAN_RUIN_WARM = register("ocean_ruin_warm", StructureFeature.OCEAN_RUIN.configured(new OceanRuinConfiguration(OceanRuinFeature.Type.WARM, 0.3F, 0.9F)));
      NETHER_BRIDGE = register("fortress", StructureFeature.NETHER_BRIDGE.configured(NoneFeatureConfiguration.INSTANCE));
      NETHER_FOSSIL = register("nether_fossil", StructureFeature.NETHER_FOSSIL.configured(new RangeConfiguration(UniformHeight.method_24(VerticalAnchor.absolute(32), VerticalAnchor.belowTop(2)))));
      END_CITY = register("end_city", StructureFeature.END_CITY.configured(NoneFeatureConfiguration.INSTANCE));
      BURIED_TREASURE = register("buried_treasure", StructureFeature.BURIED_TREASURE.configured(new ProbabilityFeatureConfiguration(0.01F)));
      BASTION_REMNANT = register("bastion_remnant", StructureFeature.BASTION_REMNANT.configured(new JigsawConfiguration(() -> {
         return BastionPieces.START;
      }, 6)));
      VILLAGE_PLAINS = register("village_plains", StructureFeature.VILLAGE.configured(new JigsawConfiguration(() -> {
         return PlainVillagePools.START;
      }, 6)));
      VILLAGE_DESERT = register("village_desert", StructureFeature.VILLAGE.configured(new JigsawConfiguration(() -> {
         return DesertVillagePools.START;
      }, 6)));
      VILLAGE_SAVANNA = register("village_savanna", StructureFeature.VILLAGE.configured(new JigsawConfiguration(() -> {
         return SavannaVillagePools.START;
      }, 6)));
      VILLAGE_SNOWY = register("village_snowy", StructureFeature.VILLAGE.configured(new JigsawConfiguration(() -> {
         return SnowyVillagePools.START;
      }, 6)));
      VILLAGE_TAIGA = register("village_taiga", StructureFeature.VILLAGE.configured(new JigsawConfiguration(() -> {
         return TaigaVillagePools.START;
      }, 6)));
      RUINED_PORTAL_STANDARD = register("ruined_portal", StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.STANDARD)));
      RUINED_PORTAL_DESERT = register("ruined_portal_desert", StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.DESERT)));
      RUINED_PORTAL_JUNGLE = register("ruined_portal_jungle", StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.JUNGLE)));
      RUINED_PORTAL_SWAMP = register("ruined_portal_swamp", StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.SWAMP)));
      RUINED_PORTAL_MOUNTAIN = register("ruined_portal_mountain", StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.MOUNTAIN)));
      RUINED_PORTAL_OCEAN = register("ruined_portal_ocean", StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.OCEAN)));
      RUINED_PORTAL_NETHER = register("ruined_portal_nether", StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.NETHER)));
   }
}
