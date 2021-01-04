package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;

public abstract class Feature<FC extends FeatureConfiguration> {
   public static final StructureFeature<PillagerOutpostConfiguration> PILLAGER_OUTPOST = (StructureFeature)register("pillager_outpost", new PillagerOutpostFeature(PillagerOutpostConfiguration::deserialize));
   public static final StructureFeature<MineshaftConfiguration> MINESHAFT = (StructureFeature)register("mineshaft", new MineshaftFeature(MineshaftConfiguration::deserialize));
   public static final StructureFeature<NoneFeatureConfiguration> WOODLAND_MANSION = (StructureFeature)register("woodland_mansion", new WoodlandMansionFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature<NoneFeatureConfiguration> JUNGLE_TEMPLE = (StructureFeature)register("jungle_temple", new JunglePyramidFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature<NoneFeatureConfiguration> DESERT_PYRAMID = (StructureFeature)register("desert_pyramid", new DesertPyramidFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature<NoneFeatureConfiguration> IGLOO = (StructureFeature)register("igloo", new IglooFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature<ShipwreckConfiguration> SHIPWRECK = (StructureFeature)register("shipwreck", new ShipwreckFeature(ShipwreckConfiguration::deserialize));
   public static final SwamplandHutFeature SWAMP_HUT = (SwamplandHutFeature)register("swamp_hut", new SwamplandHutFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature<NoneFeatureConfiguration> STRONGHOLD = (StructureFeature)register("stronghold", new StrongholdFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature<NoneFeatureConfiguration> OCEAN_MONUMENT = (StructureFeature)register("ocean_monument", new OceanMonumentFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature<OceanRuinConfiguration> OCEAN_RUIN = (StructureFeature)register("ocean_ruin", new OceanRuinFeature(OceanRuinConfiguration::deserialize));
   public static final StructureFeature<NoneFeatureConfiguration> NETHER_BRIDGE = (StructureFeature)register("nether_bridge", new NetherFortressFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature<NoneFeatureConfiguration> END_CITY = (StructureFeature)register("end_city", new EndCityFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature<BuriedTreasureConfiguration> BURIED_TREASURE = (StructureFeature)register("buried_treasure", new BuriedTreasureFeature(BuriedTreasureConfiguration::deserialize));
   public static final StructureFeature<VillageConfiguration> VILLAGE = (StructureFeature)register("village", new VillageFeature(VillageConfiguration::deserialize));
   public static final Feature<NoneFeatureConfiguration> FANCY_TREE = register("fancy_tree", new BigTreeFeature(NoneFeatureConfiguration::deserialize, false));
   public static final Feature<NoneFeatureConfiguration> BIRCH_TREE = register("birch_tree", new BirchFeature(NoneFeatureConfiguration::deserialize, false, false));
   public static final Feature<NoneFeatureConfiguration> SUPER_BIRCH_TREE = register("super_birch_tree", new BirchFeature(NoneFeatureConfiguration::deserialize, false, true));
   public static final Feature<NoneFeatureConfiguration> JUNGLE_GROUND_BUSH;
   public static final Feature<NoneFeatureConfiguration> JUNGLE_TREE;
   public static final Feature<NoneFeatureConfiguration> PINE_TREE;
   public static final Feature<NoneFeatureConfiguration> DARK_OAK_TREE;
   public static final Feature<NoneFeatureConfiguration> SAVANNA_TREE;
   public static final Feature<NoneFeatureConfiguration> SPRUCE_TREE;
   public static final Feature<NoneFeatureConfiguration> SWAMP_TREE;
   public static final Feature<NoneFeatureConfiguration> NORMAL_TREE;
   public static final Feature<NoneFeatureConfiguration> MEGA_JUNGLE_TREE;
   public static final Feature<NoneFeatureConfiguration> MEGA_PINE_TREE;
   public static final Feature<NoneFeatureConfiguration> MEGA_SPRUCE_TREE;
   public static final FlowerFeature DEFAULT_FLOWER;
   public static final FlowerFeature FOREST_FLOWER;
   public static final FlowerFeature PLAIN_FLOWER;
   public static final FlowerFeature SWAMP_FLOWER;
   public static final FlowerFeature GENERAL_FOREST_FLOWER;
   public static final Feature<NoneFeatureConfiguration> JUNGLE_GRASS;
   public static final Feature<NoneFeatureConfiguration> TAIGA_GRASS;
   public static final Feature<GrassConfiguration> GRASS;
   public static final Feature<NoneFeatureConfiguration> VOID_START_PLATFORM;
   public static final Feature<NoneFeatureConfiguration> CACTUS;
   public static final Feature<NoneFeatureConfiguration> DEAD_BUSH;
   public static final Feature<NoneFeatureConfiguration> DESERT_WELL;
   public static final Feature<NoneFeatureConfiguration> FOSSIL;
   public static final Feature<NoneFeatureConfiguration> HELL_FIRE;
   public static final Feature<HugeMushroomFeatureConfig> HUGE_RED_MUSHROOM;
   public static final Feature<HugeMushroomFeatureConfig> HUGE_BROWN_MUSHROOM;
   public static final Feature<NoneFeatureConfiguration> ICE_SPIKE;
   public static final Feature<NoneFeatureConfiguration> GLOWSTONE_BLOB;
   public static final Feature<NoneFeatureConfiguration> MELON;
   public static final Feature<NoneFeatureConfiguration> PUMPKIN;
   public static final Feature<NoneFeatureConfiguration> REED;
   public static final Feature<NoneFeatureConfiguration> FREEZE_TOP_LAYER;
   public static final Feature<NoneFeatureConfiguration> VINES;
   public static final Feature<NoneFeatureConfiguration> WATERLILY;
   public static final Feature<NoneFeatureConfiguration> MONSTER_ROOM;
   public static final Feature<NoneFeatureConfiguration> BLUE_ICE;
   public static final Feature<IcebergConfiguration> ICEBERG;
   public static final Feature<BlockBlobConfiguration> FOREST_ROCK;
   public static final Feature<NoneFeatureConfiguration> HAY_PILE;
   public static final Feature<NoneFeatureConfiguration> SNOW_PILE;
   public static final Feature<NoneFeatureConfiguration> ICE_PILE;
   public static final Feature<NoneFeatureConfiguration> MELON_PILE;
   public static final Feature<NoneFeatureConfiguration> PUMPKIN_PILE;
   public static final Feature<BushConfiguration> BUSH;
   public static final Feature<DiskConfiguration> DISK;
   public static final Feature<DoublePlantConfiguration> DOUBLE_PLANT;
   public static final Feature<HellSpringConfiguration> NETHER_SPRING;
   public static final Feature<FeatureRadius> ICE_PATCH;
   public static final Feature<LakeConfiguration> LAKE;
   public static final Feature<OreConfiguration> ORE;
   public static final Feature<RandomRandomFeatureConfig> RANDOM_RANDOM_SELECTOR;
   public static final Feature<RandomFeatureConfig> RANDOM_SELECTOR;
   public static final Feature<SimpleRandomFeatureConfig> SIMPLE_RANDOM_SELECTOR;
   public static final Feature<RandomBooleanFeatureConfig> RANDOM_BOOLEAN_SELECTOR;
   public static final Feature<ReplaceBlockConfiguration> EMERALD_ORE;
   public static final Feature<SpringConfiguration> SPRING;
   public static final Feature<SpikeConfiguration> END_SPIKE;
   public static final Feature<NoneFeatureConfiguration> END_ISLAND;
   public static final Feature<NoneFeatureConfiguration> CHORUS_PLANT;
   public static final Feature<EndGatewayConfiguration> END_GATEWAY;
   public static final Feature<SeagrassFeatureConfiguration> SEAGRASS;
   public static final Feature<NoneFeatureConfiguration> KELP;
   public static final Feature<NoneFeatureConfiguration> CORAL_TREE;
   public static final Feature<NoneFeatureConfiguration> CORAL_MUSHROOM;
   public static final Feature<NoneFeatureConfiguration> CORAL_CLAW;
   public static final Feature<CountFeatureConfiguration> SEA_PICKLE;
   public static final Feature<SimpleBlockConfiguration> SIMPLE_BLOCK;
   public static final Feature<ProbabilityFeatureConfiguration> BAMBOO;
   public static final Feature<DecoratedFeatureConfiguration> DECORATED;
   public static final Feature<DecoratedFeatureConfiguration> DECORATED_FLOWER;
   public static final Feature<NoneFeatureConfiguration> SWEET_BERRY_BUSH;
   public static final Feature<LayerConfiguration> FILL_LAYER;
   public static final BonusChestFeature BONUS_CHEST;
   public static final BiMap<String, StructureFeature<?>> STRUCTURES_REGISTRY;
   public static final List<StructureFeature<?>> NOISE_AFFECTING_FEATURES;
   private final Function<Dynamic<?>, ? extends FC> configurationFactory;
   protected final boolean doUpdate;

   private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String var0, F var1) {
      return (Feature)Registry.register(Registry.FEATURE, (String)var0, var1);
   }

   public Feature(Function<Dynamic<?>, ? extends FC> var1) {
      super();
      this.configurationFactory = var1;
      this.doUpdate = false;
   }

   public Feature(Function<Dynamic<?>, ? extends FC> var1, boolean var2) {
      super();
      this.configurationFactory = var1;
      this.doUpdate = var2;
   }

   public FC createSettings(Dynamic<?> var1) {
      return (FeatureConfiguration)this.configurationFactory.apply(var1);
   }

   protected void setBlock(LevelWriter var1, BlockPos var2, BlockState var3) {
      if (this.doUpdate) {
         var1.setBlock(var2, var3, 3);
      } else {
         var1.setBlock(var2, var3, 2);
      }

   }

   public abstract boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, FC var5);

   public List<Biome.SpawnerData> getSpecialEnemies() {
      return Collections.emptyList();
   }

   public List<Biome.SpawnerData> getSpecialAnimals() {
      return Collections.emptyList();
   }

   static {
      JUNGLE_GROUND_BUSH = register("jungle_ground_bush", new GroundBushFeature(NoneFeatureConfiguration::deserialize, Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.OAK_LEAVES.defaultBlockState()));
      JUNGLE_TREE = register("jungle_tree", new JungleTreeFeature(NoneFeatureConfiguration::deserialize, false, 4, Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.JUNGLE_LEAVES.defaultBlockState(), true));
      PINE_TREE = register("pine_tree", new PineFeature(NoneFeatureConfiguration::deserialize));
      DARK_OAK_TREE = register("dark_oak_tree", new DarkOakFeature(NoneFeatureConfiguration::deserialize, false));
      SAVANNA_TREE = register("savanna_tree", new SavannaTreeFeature(NoneFeatureConfiguration::deserialize, false));
      SPRUCE_TREE = register("spruce_tree", new SpruceFeature(NoneFeatureConfiguration::deserialize, false));
      SWAMP_TREE = register("swamp_tree", new SwampTreeFeature(NoneFeatureConfiguration::deserialize));
      NORMAL_TREE = register("normal_tree", new TreeFeature(NoneFeatureConfiguration::deserialize, false));
      MEGA_JUNGLE_TREE = register("mega_jungle_tree", new MegaJungleTreeFeature(NoneFeatureConfiguration::deserialize, false, 10, 20, Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.JUNGLE_LEAVES.defaultBlockState()));
      MEGA_PINE_TREE = register("mega_pine_tree", new MegaPineTreeFeature(NoneFeatureConfiguration::deserialize, false, false));
      MEGA_SPRUCE_TREE = register("mega_spruce_tree", new MegaPineTreeFeature(NoneFeatureConfiguration::deserialize, false, true));
      DEFAULT_FLOWER = (FlowerFeature)register("default_flower", new DefaultFlowerFeature(NoneFeatureConfiguration::deserialize));
      FOREST_FLOWER = (FlowerFeature)register("forest_flower", new ForestFlowerFeature(NoneFeatureConfiguration::deserialize));
      PLAIN_FLOWER = (FlowerFeature)register("plain_flower", new PlainFlowerFeature(NoneFeatureConfiguration::deserialize));
      SWAMP_FLOWER = (FlowerFeature)register("swamp_flower", new SwampFlowerFeature(NoneFeatureConfiguration::deserialize));
      GENERAL_FOREST_FLOWER = (FlowerFeature)register("general_forest_flower", new GeneralForestFlowerFeature(NoneFeatureConfiguration::deserialize));
      JUNGLE_GRASS = register("jungle_grass", new JungleGrassFeature(NoneFeatureConfiguration::deserialize));
      TAIGA_GRASS = register("taiga_grass", new TaigaGrassFeature(NoneFeatureConfiguration::deserialize));
      GRASS = register("grass", new GrassFeature(GrassConfiguration::deserialize));
      VOID_START_PLATFORM = register("void_start_platform", new VoidStartPlatformFeature(NoneFeatureConfiguration::deserialize));
      CACTUS = register("cactus", new CactusFeature(NoneFeatureConfiguration::deserialize));
      DEAD_BUSH = register("dead_bush", new DeadBushFeature(NoneFeatureConfiguration::deserialize));
      DESERT_WELL = register("desert_well", new DesertWellFeature(NoneFeatureConfiguration::deserialize));
      FOSSIL = register("fossil", new FossilFeature(NoneFeatureConfiguration::deserialize));
      HELL_FIRE = register("hell_fire", new HellFireFeature(NoneFeatureConfiguration::deserialize));
      HUGE_RED_MUSHROOM = register("huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfig::deserialize));
      HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfig::deserialize));
      ICE_SPIKE = register("ice_spike", new IceSpikeFeature(NoneFeatureConfiguration::deserialize));
      GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneFeature(NoneFeatureConfiguration::deserialize));
      MELON = register("melon", new MelonFeature(NoneFeatureConfiguration::deserialize));
      PUMPKIN = register("pumpkin", new CentralSpikedFeature(NoneFeatureConfiguration::deserialize, Blocks.PUMPKIN.defaultBlockState()));
      REED = register("reed", new ReedsFeature(NoneFeatureConfiguration::deserialize));
      FREEZE_TOP_LAYER = register("freeze_top_layer", new SnowAndFreezeFeature(NoneFeatureConfiguration::deserialize));
      VINES = register("vines", new VinesFeature(NoneFeatureConfiguration::deserialize));
      WATERLILY = register("waterlily", new WaterlilyFeature(NoneFeatureConfiguration::deserialize));
      MONSTER_ROOM = register("monster_room", new MonsterRoomFeature(NoneFeatureConfiguration::deserialize));
      BLUE_ICE = register("blue_ice", new BlueIceFeature(NoneFeatureConfiguration::deserialize));
      ICEBERG = register("iceberg", new IcebergFeature(IcebergConfiguration::deserialize));
      FOREST_ROCK = register("forest_rock", new BlockBlobFeature(BlockBlobConfiguration::deserialize));
      HAY_PILE = register("hay_pile", new HayBlockPileFeature(NoneFeatureConfiguration::deserialize));
      SNOW_PILE = register("snow_pile", new SnowBlockPileFeature(NoneFeatureConfiguration::deserialize));
      ICE_PILE = register("ice_pile", new IceBlockPileFeature(NoneFeatureConfiguration::deserialize));
      MELON_PILE = register("melon_pile", new MelonBlockPileFeature(NoneFeatureConfiguration::deserialize));
      PUMPKIN_PILE = register("pumpkin_pile", new PumpkinBlockPileFeature(NoneFeatureConfiguration::deserialize));
      BUSH = register("bush", new BushFeature(BushConfiguration::deserialize));
      DISK = register("disk", new DiskReplaceFeature(DiskConfiguration::deserialize));
      DOUBLE_PLANT = register("double_plant", new DoublePlantFeature(DoublePlantConfiguration::deserialize));
      NETHER_SPRING = register("nether_spring", new NetherSpringFeature(HellSpringConfiguration::deserialize));
      ICE_PATCH = register("ice_patch", new IcePatchFeature(FeatureRadius::deserialize));
      LAKE = register("lake", new LakeFeature(LakeConfiguration::deserialize));
      ORE = register("ore", new OreFeature(OreConfiguration::deserialize));
      RANDOM_RANDOM_SELECTOR = register("random_random_selector", new RandomRandomFeature(RandomRandomFeatureConfig::deserialize));
      RANDOM_SELECTOR = register("random_selector", new RandomSelectorFeature(RandomFeatureConfig::deserialize));
      SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", new SimpleRandomSelectorFeature(SimpleRandomFeatureConfig::deserialize));
      RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", new RandomBooleanSelectorFeature(RandomBooleanFeatureConfig::deserialize));
      EMERALD_ORE = register("emerald_ore", new ReplaceBlockFeature(ReplaceBlockConfiguration::deserialize));
      SPRING = register("spring_feature", new SpringFeature(SpringConfiguration::deserialize));
      END_SPIKE = register("end_spike", new SpikeFeature(SpikeConfiguration::deserialize));
      END_ISLAND = register("end_island", new EndIslandFeature(NoneFeatureConfiguration::deserialize));
      CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(NoneFeatureConfiguration::deserialize));
      END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayConfiguration::deserialize));
      SEAGRASS = register("seagrass", new SeagrassFeature(SeagrassFeatureConfiguration::deserialize));
      KELP = register("kelp", new KelpFeature(NoneFeatureConfiguration::deserialize));
      CORAL_TREE = register("coral_tree", new CoralTreeFeature(NoneFeatureConfiguration::deserialize));
      CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(NoneFeatureConfiguration::deserialize));
      CORAL_CLAW = register("coral_claw", new CoralClawFeature(NoneFeatureConfiguration::deserialize));
      SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(CountFeatureConfiguration::deserialize));
      SIMPLE_BLOCK = register("simple_block", new SimpleBlockFeature(SimpleBlockConfiguration::deserialize));
      BAMBOO = register("bamboo", new BambooFeature(ProbabilityFeatureConfiguration::deserialize));
      DECORATED = register("decorated", new DecoratedFeature(DecoratedFeatureConfiguration::deserialize));
      DECORATED_FLOWER = register("decorated_flower", new DecoratedFlowerFeature(DecoratedFeatureConfiguration::deserialize));
      SWEET_BERRY_BUSH = register("sweet_berry_bush", new CentralSpikedFeature(NoneFeatureConfiguration::deserialize, (BlockState)Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3)));
      FILL_LAYER = register("fill_layer", new FillLayerFeature(LayerConfiguration::deserialize));
      BONUS_CHEST = (BonusChestFeature)register("bonus_chest", new BonusChestFeature(NoneFeatureConfiguration::deserialize));
      STRUCTURES_REGISTRY = (BiMap)Util.make(HashBiMap.create(), (var0) -> {
         var0.put("Pillager_Outpost".toLowerCase(Locale.ROOT), PILLAGER_OUTPOST);
         var0.put("Mineshaft".toLowerCase(Locale.ROOT), MINESHAFT);
         var0.put("Mansion".toLowerCase(Locale.ROOT), WOODLAND_MANSION);
         var0.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), JUNGLE_TEMPLE);
         var0.put("Desert_Pyramid".toLowerCase(Locale.ROOT), DESERT_PYRAMID);
         var0.put("Igloo".toLowerCase(Locale.ROOT), IGLOO);
         var0.put("Shipwreck".toLowerCase(Locale.ROOT), SHIPWRECK);
         var0.put("Swamp_Hut".toLowerCase(Locale.ROOT), SWAMP_HUT);
         var0.put("Stronghold".toLowerCase(Locale.ROOT), STRONGHOLD);
         var0.put("Monument".toLowerCase(Locale.ROOT), OCEAN_MONUMENT);
         var0.put("Ocean_Ruin".toLowerCase(Locale.ROOT), OCEAN_RUIN);
         var0.put("Fortress".toLowerCase(Locale.ROOT), NETHER_BRIDGE);
         var0.put("EndCity".toLowerCase(Locale.ROOT), END_CITY);
         var0.put("Buried_Treasure".toLowerCase(Locale.ROOT), BURIED_TREASURE);
         var0.put("Village".toLowerCase(Locale.ROOT), VILLAGE);
      });
      NOISE_AFFECTING_FEATURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE);
   }
}
