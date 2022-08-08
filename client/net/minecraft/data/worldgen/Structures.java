package net.minecraft.data.worldgen;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasureStructure;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import net.minecraft.world.level.levelgen.structure.structures.EndCityStructure;
import net.minecraft.world.level.levelgen.structure.structures.IglooStructure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.structures.JungleTempleStructure;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilStructure;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinStructure;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure;
import net.minecraft.world.level.levelgen.structure.structures.ShipwreckStructure;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdStructure;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutStructure;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure;

public class Structures {
   public static final Holder<Structure> PILLAGER_OUTPOST;
   public static final Holder<Structure> MINESHAFT;
   public static final Holder<Structure> MINESHAFT_MESA;
   public static final Holder<Structure> WOODLAND_MANSION;
   public static final Holder<Structure> JUNGLE_TEMPLE;
   public static final Holder<Structure> DESERT_PYRAMID;
   public static final Holder<Structure> IGLOO;
   public static final Holder<Structure> SHIPWRECK;
   public static final Holder<Structure> SHIPWRECK_BEACHED;
   public static final Holder<Structure> SWAMP_HUT;
   public static final Holder<Structure> STRONGHOLD;
   public static final Holder<Structure> OCEAN_MONUMENT;
   public static final Holder<Structure> OCEAN_RUIN_COLD;
   public static final Holder<Structure> OCEAN_RUIN_WARM;
   public static final Holder<Structure> FORTRESS;
   public static final Holder<Structure> NETHER_FOSSIL;
   public static final Holder<Structure> END_CITY;
   public static final Holder<Structure> BURIED_TREASURE;
   public static final Holder<Structure> BASTION_REMNANT;
   public static final Holder<Structure> VILLAGE_PLAINS;
   public static final Holder<Structure> VILLAGE_DESERT;
   public static final Holder<Structure> VILLAGE_SAVANNA;
   public static final Holder<Structure> VILLAGE_SNOWY;
   public static final Holder<Structure> VILLAGE_TAIGA;
   public static final Holder<Structure> RUINED_PORTAL_STANDARD;
   public static final Holder<Structure> RUINED_PORTAL_DESERT;
   public static final Holder<Structure> RUINED_PORTAL_JUNGLE;
   public static final Holder<Structure> RUINED_PORTAL_SWAMP;
   public static final Holder<Structure> RUINED_PORTAL_MOUNTAIN;
   public static final Holder<Structure> RUINED_PORTAL_OCEAN;
   public static final Holder<Structure> RUINED_PORTAL_NETHER;
   public static final Holder<Structure> ANCIENT_CITY;

   public Structures() {
      super();
   }

   public static Holder<? extends Structure> bootstrap(Registry<Structure> var0) {
      return MINESHAFT;
   }

   private static Structure.StructureSettings structure(TagKey<Biome> var0, Map<MobCategory, StructureSpawnOverride> var1, GenerationStep.Decoration var2, TerrainAdjustment var3) {
      return new Structure.StructureSettings(biomes(var0), var1, var2, var3);
   }

   private static Structure.StructureSettings structure(TagKey<Biome> var0, GenerationStep.Decoration var1, TerrainAdjustment var2) {
      return structure(var0, Map.of(), var1, var2);
   }

   private static Structure.StructureSettings structure(TagKey<Biome> var0, TerrainAdjustment var1) {
      return structure(var0, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, var1);
   }

   private static Holder<Structure> register(ResourceKey<Structure> var0, Structure var1) {
      return BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, (ResourceKey)var0, var1);
   }

   private static HolderSet<Biome> biomes(TagKey<Biome> var0) {
      return BuiltinRegistries.BIOME.getOrCreateTag(var0);
   }

   static {
      PILLAGER_OUTPOST = register(BuiltinStructures.PILLAGER_OUTPOST, new JigsawStructure(structure(BiomeTags.HAS_PILLAGER_OUTPOST, Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create((WeightedEntry[])(new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 1, 1, 1))))), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN), PillagerOutpostPools.START, 7, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      MINESHAFT = register(BuiltinStructures.MINESHAFT, new MineshaftStructure(structure(BiomeTags.HAS_MINESHAFT, GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE), MineshaftStructure.Type.NORMAL));
      MINESHAFT_MESA = register(BuiltinStructures.MINESHAFT_MESA, new MineshaftStructure(structure(BiomeTags.HAS_MINESHAFT_MESA, GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE), MineshaftStructure.Type.MESA));
      WOODLAND_MANSION = register(BuiltinStructures.WOODLAND_MANSION, new WoodlandMansionStructure(structure(BiomeTags.HAS_WOODLAND_MANSION, TerrainAdjustment.NONE)));
      JUNGLE_TEMPLE = register(BuiltinStructures.JUNGLE_TEMPLE, new JungleTempleStructure(structure(BiomeTags.HAS_JUNGLE_TEMPLE, TerrainAdjustment.NONE)));
      DESERT_PYRAMID = register(BuiltinStructures.DESERT_PYRAMID, new DesertPyramidStructure(structure(BiomeTags.HAS_DESERT_PYRAMID, TerrainAdjustment.NONE)));
      IGLOO = register(BuiltinStructures.IGLOO, new IglooStructure(structure(BiomeTags.HAS_IGLOO, TerrainAdjustment.NONE)));
      SHIPWRECK = register(BuiltinStructures.SHIPWRECK, new ShipwreckStructure(structure(BiomeTags.HAS_SHIPWRECK, TerrainAdjustment.NONE), false));
      SHIPWRECK_BEACHED = register(BuiltinStructures.SHIPWRECK_BEACHED, new ShipwreckStructure(structure(BiomeTags.HAS_SHIPWRECK_BEACHED, TerrainAdjustment.NONE), true));
      SWAMP_HUT = register(BuiltinStructures.SWAMP_HUT, new SwampHutStructure(structure(BiomeTags.HAS_SWAMP_HUT, Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create((WeightedEntry[])(new MobSpawnSettings.SpawnerData(EntityType.WITCH, 1, 1, 1)))), MobCategory.CREATURE, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create((WeightedEntry[])(new MobSpawnSettings.SpawnerData(EntityType.CAT, 1, 1, 1))))), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
      STRONGHOLD = register(BuiltinStructures.STRONGHOLD, new StrongholdStructure(structure(BiomeTags.HAS_STRONGHOLD, TerrainAdjustment.BURY)));
      OCEAN_MONUMENT = register(BuiltinStructures.OCEAN_MONUMENT, new OceanMonumentStructure(structure(BiomeTags.HAS_OCEAN_MONUMENT, Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create((WeightedEntry[])(new MobSpawnSettings.SpawnerData(EntityType.GUARDIAN, 1, 2, 4)))), MobCategory.UNDERGROUND_WATER_CREATURE, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, MobSpawnSettings.EMPTY_MOB_LIST), MobCategory.AXOLOTLS, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, MobSpawnSettings.EMPTY_MOB_LIST)), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
      OCEAN_RUIN_COLD = register(BuiltinStructures.OCEAN_RUIN_COLD, new OceanRuinStructure(structure(BiomeTags.HAS_OCEAN_RUIN_COLD, TerrainAdjustment.NONE), OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
      OCEAN_RUIN_WARM = register(BuiltinStructures.OCEAN_RUIN_WARM, new OceanRuinStructure(structure(BiomeTags.HAS_OCEAN_RUIN_WARM, TerrainAdjustment.NONE), OceanRuinStructure.Type.WARM, 0.3F, 0.9F));
      FORTRESS = register(BuiltinStructures.FORTRESS, new NetherFortressStructure(structure(BiomeTags.HAS_NETHER_FORTRESS, Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, NetherFortressStructure.FORTRESS_ENEMIES)), GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE)));
      NETHER_FOSSIL = register(BuiltinStructures.NETHER_FOSSIL, new NetherFossilStructure(structure(BiomeTags.HAS_NETHER_FOSSIL, GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.BEARD_THIN), UniformHeight.of(VerticalAnchor.absolute(32), VerticalAnchor.belowTop(2))));
      END_CITY = register(BuiltinStructures.END_CITY, new EndCityStructure(structure(BiomeTags.HAS_END_CITY, TerrainAdjustment.NONE)));
      BURIED_TREASURE = register(BuiltinStructures.BURIED_TREASURE, new BuriedTreasureStructure(structure(BiomeTags.HAS_BURIED_TREASURE, GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE)));
      BASTION_REMNANT = register(BuiltinStructures.BASTION_REMNANT, new JigsawStructure(structure(BiomeTags.HAS_BASTION_REMNANT, TerrainAdjustment.NONE), BastionPieces.START, 6, ConstantHeight.of(VerticalAnchor.absolute(33)), false));
      VILLAGE_PLAINS = register(BuiltinStructures.VILLAGE_PLAINS, new JigsawStructure(structure(BiomeTags.HAS_VILLAGE_PLAINS, TerrainAdjustment.BEARD_THIN), PlainVillagePools.START, 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      VILLAGE_DESERT = register(BuiltinStructures.VILLAGE_DESERT, new JigsawStructure(structure(BiomeTags.HAS_VILLAGE_DESERT, TerrainAdjustment.BEARD_THIN), DesertVillagePools.START, 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      VILLAGE_SAVANNA = register(BuiltinStructures.VILLAGE_SAVANNA, new JigsawStructure(structure(BiomeTags.HAS_VILLAGE_SAVANNA, TerrainAdjustment.BEARD_THIN), SavannaVillagePools.START, 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      VILLAGE_SNOWY = register(BuiltinStructures.VILLAGE_SNOWY, new JigsawStructure(structure(BiomeTags.HAS_VILLAGE_SNOWY, TerrainAdjustment.BEARD_THIN), SnowyVillagePools.START, 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      VILLAGE_TAIGA = register(BuiltinStructures.VILLAGE_TAIGA, new JigsawStructure(structure(BiomeTags.HAS_VILLAGE_TAIGA, TerrainAdjustment.BEARD_THIN), TaigaVillagePools.START, 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
      RUINED_PORTAL_STANDARD = register(BuiltinStructures.RUINED_PORTAL_STANDARD, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_STANDARD, TerrainAdjustment.NONE), List.of(new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.UNDERGROUND, 1.0F, 0.2F, false, false, true, false, 0.5F), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE, 0.5F, 0.2F, false, false, true, false, 0.5F))));
      RUINED_PORTAL_DESERT = register(BuiltinStructures.RUINED_PORTAL_DESERT, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_DESERT, TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED, 0.0F, 0.0F, false, false, false, false, 1.0F)));
      RUINED_PORTAL_JUNGLE = register(BuiltinStructures.RUINED_PORTAL_JUNGLE, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_JUNGLE, TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE, 0.5F, 0.8F, true, true, false, false, 1.0F)));
      RUINED_PORTAL_SWAMP = register(BuiltinStructures.RUINED_PORTAL_SWAMP, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_SWAMP, TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0F, 0.5F, false, true, false, false, 1.0F)));
      RUINED_PORTAL_MOUNTAIN = register(BuiltinStructures.RUINED_PORTAL_MOUNTAIN, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_MOUNTAIN, TerrainAdjustment.NONE), List.of(new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN, 1.0F, 0.2F, false, false, true, false, 0.5F), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE, 0.5F, 0.2F, false, false, true, false, 0.5F))));
      RUINED_PORTAL_OCEAN = register(BuiltinStructures.RUINED_PORTAL_OCEAN, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_OCEAN, TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0F, 0.8F, false, false, true, false, 1.0F)));
      RUINED_PORTAL_NETHER = register(BuiltinStructures.RUINED_PORTAL_NETHER, new RuinedPortalStructure(structure(BiomeTags.HAS_RUINED_PORTAL_NETHER, TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.IN_NETHER, 0.5F, 0.0F, false, false, false, true, 1.0F)));
      ANCIENT_CITY = register(BuiltinStructures.ANCIENT_CITY, new JigsawStructure(structure(BiomeTags.HAS_ANCIENT_CITY, (Map)Arrays.stream(MobCategory.values()).collect(Collectors.toMap((var0) -> {
         return var0;
      }, (var0) -> {
         return new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create());
      })), GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.BEARD_BOX), AncientCityStructurePieces.START, Optional.of(new ResourceLocation("city_anchor")), 7, ConstantHeight.of(VerticalAnchor.absolute(-27)), false, Optional.empty(), 116));
   }
}
