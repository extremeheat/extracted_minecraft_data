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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockBlobConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BuriedTreasureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.CountFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureRadiusConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MegaTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SeagrassFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VillageConfiguration;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;

public abstract class Feature {
   public static final StructureFeature PILLAGER_OUTPOST = (StructureFeature)register("pillager_outpost", new PillagerOutpostFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature MINESHAFT = (StructureFeature)register("mineshaft", new MineshaftFeature(MineshaftConfiguration::deserialize));
   public static final StructureFeature WOODLAND_MANSION = (StructureFeature)register("woodland_mansion", new WoodlandMansionFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature JUNGLE_TEMPLE = (StructureFeature)register("jungle_temple", new JunglePyramidFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature DESERT_PYRAMID = (StructureFeature)register("desert_pyramid", new DesertPyramidFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature IGLOO = (StructureFeature)register("igloo", new IglooFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature SHIPWRECK = (StructureFeature)register("shipwreck", new ShipwreckFeature(ShipwreckConfiguration::deserialize));
   public static final SwamplandHutFeature SWAMP_HUT = (SwamplandHutFeature)register("swamp_hut", new SwamplandHutFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature STRONGHOLD = (StructureFeature)register("stronghold", new StrongholdFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature OCEAN_MONUMENT = (StructureFeature)register("ocean_monument", new OceanMonumentFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature OCEAN_RUIN = (StructureFeature)register("ocean_ruin", new OceanRuinFeature(OceanRuinConfiguration::deserialize));
   public static final StructureFeature NETHER_BRIDGE = (StructureFeature)register("nether_bridge", new NetherFortressFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature END_CITY = (StructureFeature)register("end_city", new EndCityFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature BURIED_TREASURE = (StructureFeature)register("buried_treasure", new BuriedTreasureFeature(BuriedTreasureConfiguration::deserialize));
   public static final StructureFeature VILLAGE = (StructureFeature)register("village", new VillageFeature(VillageConfiguration::deserialize));
   public static final Feature NO_OP = register("no_op", new NoOpFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature NORMAL_TREE = register("normal_tree", new TreeFeature(SmallTreeConfiguration::deserialize));
   public static final Feature ACACIA_TREE = register("acacia_tree", new AcaciaFeature(SmallTreeConfiguration::deserialize));
   public static final Feature FANCY_TREE = register("fancy_tree", new FancyTreeFeature(SmallTreeConfiguration::deserialize));
   public static final Feature JUNGLE_GROUND_BUSH = register("jungle_ground_bush", new GroundBushFeature(TreeConfiguration::deserialize));
   public static final Feature DARK_OAK_TREE = register("dark_oak_tree", new DarkOakFeature(MegaTreeConfiguration::deserialize));
   public static final Feature MEGA_JUNGLE_TREE = register("mega_jungle_tree", new MegaJungleTreeFeature(MegaTreeConfiguration::deserialize));
   public static final Feature MEGA_SPRUCE_TREE = register("mega_spruce_tree", new MegaPineTreeFeature(MegaTreeConfiguration::deserialize));
   public static final AbstractFlowerFeature FLOWER = (AbstractFlowerFeature)register("flower", new DefaultFlowerFeature(RandomPatchConfiguration::deserialize));
   public static final Feature RANDOM_PATCH = register("random_patch", new RandomPatchFeature(RandomPatchConfiguration::deserialize));
   public static final Feature BLOCK_PILE = register("block_pile", new BlockPileFeature(BlockPileConfiguration::deserialize));
   public static final Feature SPRING = register("spring_feature", new SpringFeature(SpringConfiguration::deserialize));
   public static final Feature CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature EMERALD_ORE = register("emerald_ore", new ReplaceBlockFeature(ReplaceBlockConfiguration::deserialize));
   public static final Feature VOID_START_PLATFORM = register("void_start_platform", new VoidStartPlatformFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature DESERT_WELL = register("desert_well", new DesertWellFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature FOSSIL = register("fossil", new FossilFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature HUGE_RED_MUSHROOM = register("huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfiguration::deserialize));
   public static final Feature HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfiguration::deserialize));
   public static final Feature ICE_SPIKE = register("ice_spike", new IceSpikeFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature FREEZE_TOP_LAYER = register("freeze_top_layer", new SnowAndFreezeFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature VINES = register("vines", new VinesFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature MONSTER_ROOM = register("monster_room", new MonsterRoomFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature BLUE_ICE = register("blue_ice", new BlueIceFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature ICEBERG = register("iceberg", new IcebergFeature(BlockStateConfiguration::deserialize));
   public static final Feature FOREST_ROCK = register("forest_rock", new BlockBlobFeature(BlockBlobConfiguration::deserialize));
   public static final Feature DISK = register("disk", new DiskReplaceFeature(DiskConfiguration::deserialize));
   public static final Feature ICE_PATCH = register("ice_patch", new IcePatchFeature(FeatureRadiusConfiguration::deserialize));
   public static final Feature LAKE = register("lake", new LakeFeature(BlockStateConfiguration::deserialize));
   public static final Feature ORE = register("ore", new OreFeature(OreConfiguration::deserialize));
   public static final Feature END_SPIKE = register("end_spike", new SpikeFeature(SpikeConfiguration::deserialize));
   public static final Feature END_ISLAND = register("end_island", new EndIslandFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayConfiguration::deserialize));
   public static final Feature SEAGRASS = register("seagrass", new SeagrassFeature(SeagrassFeatureConfiguration::deserialize));
   public static final Feature KELP = register("kelp", new KelpFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature CORAL_TREE = register("coral_tree", new CoralTreeFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature CORAL_CLAW = register("coral_claw", new CoralClawFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(CountFeatureConfiguration::deserialize));
   public static final Feature SIMPLE_BLOCK = register("simple_block", new SimpleBlockFeature(SimpleBlockConfiguration::deserialize));
   public static final Feature BAMBOO = register("bamboo", new BambooFeature(ProbabilityFeatureConfiguration::deserialize));
   public static final Feature FILL_LAYER = register("fill_layer", new FillLayerFeature(LayerConfiguration::deserialize));
   public static final BonusChestFeature BONUS_CHEST = (BonusChestFeature)register("bonus_chest", new BonusChestFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature RANDOM_RANDOM_SELECTOR = register("random_random_selector", new RandomRandomFeature(RandomRandomFeatureConfiguration::deserialize));
   public static final Feature RANDOM_SELECTOR = register("random_selector", new RandomSelectorFeature(RandomFeatureConfiguration::deserialize));
   public static final Feature SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", new SimpleRandomSelectorFeature(SimpleRandomFeatureConfiguration::deserialize));
   public static final Feature RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", new RandomBooleanSelectorFeature(RandomBooleanFeatureConfiguration::deserialize));
   public static final Feature DECORATED = register("decorated", new DecoratedFeature(DecoratedFeatureConfiguration::deserialize));
   public static final Feature DECORATED_FLOWER = register("decorated_flower", new DecoratedFlowerFeature(DecoratedFeatureConfiguration::deserialize));
   public static final BiMap STRUCTURES_REGISTRY = (BiMap)Util.make(HashBiMap.create(), (var0) -> {
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
   public static final List NOISE_AFFECTING_FEATURES;
   private final Function configurationFactory;

   private static Feature register(String var0, Feature var1) {
      return (Feature)Registry.register(Registry.FEATURE, (String)var0, var1);
   }

   public Feature(Function var1) {
      this.configurationFactory = var1;
   }

   public ConfiguredFeature configured(FeatureConfiguration var1) {
      return new ConfiguredFeature(this, var1);
   }

   public FeatureConfiguration createSettings(Dynamic var1) {
      return (FeatureConfiguration)this.configurationFactory.apply(var1);
   }

   protected void setBlock(LevelWriter var1, BlockPos var2, BlockState var3) {
      var1.setBlock(var2, var3, 3);
   }

   public abstract boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, FeatureConfiguration var5);

   public List getSpecialEnemies() {
      return Collections.emptyList();
   }

   public List getSpecialAnimals() {
      return Collections.emptyList();
   }

   protected static boolean isStone(Block var0) {
      return var0 == Blocks.STONE || var0 == Blocks.GRANITE || var0 == Blocks.DIORITE || var0 == Blocks.ANDESITE;
   }

   protected static boolean isDirt(Block var0) {
      return var0 == Blocks.DIRT || var0 == Blocks.GRASS_BLOCK || var0 == Blocks.PODZOL || var0 == Blocks.COARSE_DIRT || var0 == Blocks.MYCELIUM;
   }

   static {
      NOISE_AFFECTING_FEATURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE);
   }
}
