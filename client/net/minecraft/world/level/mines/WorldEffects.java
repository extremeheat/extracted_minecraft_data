package net.minecraft.world.level.mines;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.EndPlacements;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WorldModifiers;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.UnlockCondition;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.GridChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.apache.commons.lang3.function.TriFunction;

public class WorldEffects {
   public static final float NON_UNLOCKED_RANDOM_EFFECTS_RARITY_FACTOR = 0.1F;
   public static final WorldEffectSet WORLD_TYPE = mustHaveOnlyOne("world_type");
   public static final WorldEffect SURFACE_WORLD;
   public static final WorldEffect FLOATING_ISLANDS_WORLD;
   public static final WorldEffect AMPLIFIED;
   public static final WorldEffect CAVE_WORLD;
   public static final WorldEffect SHATTERED_BLOCKS_WORLD;
   public static final WorldEffect GRID_WORLD;
   public static final WorldEffect DARK_CAVE_WORLD;
   public static final WorldEffectSet EXITS;
   public static final WorldEffect SURFACE_EXITS;
   public static final WorldEffect CAVE_EXITS;
   public static final WorldEffect RARE_SURFACE_EXITS;
   public static final WorldEffect EVENT_EXIT;
   public static final WorldEffectSet BIOMES;
   public static final WorldEffect PLAINS;
   public static final WorldEffect SAVANNAS;
   public static final WorldEffect FORESTS;
   public static final WorldEffect TAIGAS;
   public static final WorldEffect JUNGLES;
   public static final WorldEffect SNOWY;
   public static final WorldEffect DESERT;
   public static final WorldEffect BADLANDS;
   public static final WorldEffect SWAMPS;
   public static final WorldEffect DARK_FORESTS;
   public static final WorldEffect ICE_SPIKES;
   public static final WorldEffect MUSHROOM_FIELDS;
   public static final WorldEffect NETHER_BARRENS;
   public static final WorldEffect NETHER_FORESTS;
   public static final WorldEffect DEEP_DARK;
   public static final WorldEffect END;
   public static final WorldEffect DRY_LAND;
   public static final WorldEffect WATER_WORLD;
   public static final WorldEffectSet PASSIVE_MOBS;
   public static final Style PASSIVE_MOBS_STYLE;
   public static final WorldEffect SHEEP;
   public static final WorldEffect COWS;
   public static final WorldEffect PIGS;
   public static final WorldEffect CHICKENS;
   public static final WorldEffect FROGS;
   public static final WorldEffect FOXES;
   public static final WorldEffect GOATS;
   public static final WorldEffect OCELOTS;
   public static final WorldEffect AXOLOTLS;
   public static final WorldEffect ARMADILLOS;
   public static final WorldEffect MOOSHROOMS;
   public static final WorldEffect PANDAS;
   public static final WorldEffect PARROTS;
   public static final WorldEffect RABBITS;
   public static final WorldEffect SNIFFERS;
   public static final WorldEffect STRIDERS;
   public static final WorldEffectSet HOSTILE_MOBS;
   public static final Style HOSTILE_MOB_STYLE;
   public static final WorldEffect ZOMBIES;
   public static final WorldEffect SKELETONS;
   public static final WorldEffect SPIDERS;
   public static final WorldEffect CAVE_SPIDERS;
   public static final WorldEffect CREEPERS;
   public static final WorldEffect SLIMES;
   public static final WorldEffect ENDERMEN;
   public static final WorldEffect WITCHES;
   public static final WorldEffect MAGMA_CUBES;
   public static final WorldEffect BLAZES;
   public static final WorldEffect BREEZES;
   public static final WorldEffect PILLAGERS;
   public static final WorldEffect VINDICATORS;
   public static final WorldEffect EVOKERS;
   public static final WorldEffect RAVAGERS;
   public static final WorldEffect ILLUSIONERS;
   public static final WorldEffect GUARDIANS;
   public static final WorldEffect ENDERMITES;
   public static final WorldEffect SHULKERS;
   public static final WorldEffect GHASTS;
   public static final WorldEffect ZOMBIFIED_PIGLINS;
   public static final WorldEffect PIGLINS;
   public static final WorldEffect WITHER_SKELETONS;
   public static final WorldEffect BEES;
   public static final WorldEffect HOGLINS;
   public static final WorldEffect ZOGLINS;
   public static final WorldEffect ICY;
   public static final WorldEffect BOUNCY;
   public static final WorldEffectSet BASE_STONE;
   public static final WorldEffect STONE;
   public static final WorldEffect BLACKSTONE;
   public static final WorldEffect DIORITE;
   public static final WorldEffect ANDESITE;
   public static final WorldEffect GRANITE;
   public static final WorldEffect TUFF;
   public static final WorldEffect DEEPSLATE;
   public static final WorldEffect END_STONE;
   public static final Style CHALLENGE_STYLE;
   public static final WorldEffect ONE_HP;
   public static final WorldEffect WEDNESDAY_FROGS;
   public static final WorldEffect UNIVERSAL_ANGER;
   public static final WorldEffect SOUL_LINK;
   public static final WorldEffect ETERNAL_NIGHT;
   public static final WorldEffect ETERNAL_RAIN;
   public static final WorldEffect ETERNAL_LIGHTNING;
   public static final WorldEffect INSOMIAC;
   public static final WorldEffect NO_DROPS;
   public static final WorldEffect ULTRAWARM;
   public static final WorldEffect EXPLOSIVE_TRAPS;
   public static final WorldEffect FISH_OUT_OF_WATER;
   public static final WorldEffect KUIPER_WORLD;
   public static final WorldEffect ENDER_DRAGON_BOSS_FIGHT;
   public static final WorldEffect ANGRY_GHAST_BOSS_FIGHT;
   public static final WorldEffect RAID;
   public static final WorldEffect WITHER_BOSS_FIGHT;
   public static final WorldEffect THE_ENDERMAN_BOSS_FIGHT;
   public static final WorldEffect SPOOKY_SCARY_SKELETONS;
   public static final WorldEffect WARDEN_BOSS_FIGHT;
   public static final WorldEffect SMALL_BUT_DEADLY;

   public WorldEffects() {
      super();
   }

   public static boolean isDarkPlace(ServerLevel var0, BlockPos var1) {
      for(BlockPos var3 : BlockPos.betweenClosed(var1.offset(-1, -1, -1), var1.offset(1, 1, 1))) {
         if (var0.getMaxLocalRawBrightness(var3) > 0) {
            return false;
         }
      }

      return true;
   }

   public static WorldEffect bootstrap(Registry<WorldEffect> var0) {
      return ONE_HP;
   }

   public static WorldEffectSet sets(Registry<WorldEffectSet> var0) {
      return WORLD_TYPE;
   }

   public static WorldEffectSet mustHaveAtLeastOne(String var0) {
      return (WorldEffectSet)Registry.register(BuiltInRegistries.WORLD_EFFECT_SET, (String)var0, new WorldEffectSet(false));
   }

   public static WorldEffectSet mustHaveOnlyOne(String var0) {
      return (WorldEffectSet)Registry.register(BuiltInRegistries.WORLD_EFFECT_SET, (String)var0, new WorldEffectSet(true));
   }

   public static ItemStack createEffectItem(WorldEffect var0, boolean var1) {
      return createEffectItem(Component.translatable("item.minecraft.mine_ingredient.desc", var0.name()), var1, List.of(var0));
   }

   public static ItemStack createEffectItem(Component var0, boolean var1, List<WorldEffect> var2) {
      ItemStack var3 = Items.MINE_INGREDIENT.getDefaultInstance();
      var3.set(DataComponents.WORLD_MODIFIERS, new WorldModifiers(var2, var2.size() <= 1));
      var3.set(DataComponents.ITEM_NAME, var0);
      if (var1) {
         var3.set(DataComponents.WORLD_EFFECT_UNLOCK, Unit.INSTANCE);
      }

      return var3;
   }

   public static <T extends WorldEffectComponent> Stream<T> componentsOfType(List<WorldEffect> var0, Class<T> var1) {
      return var0.stream().flatMap((var1x) -> var1x.componentsOfType(var1));
   }

   public static void boop() {
   }

   static {
      SURFACE_WORLD = WorldEffect.builder("surface_world").modifyingWorldGen((var0) -> var0.setNoiseGenerationBase(NoiseGeneratorSettings.OVERWORLD)).withCustomIcon("surface_world").inSet(WORLD_TYPE).unlockedByDefault().register();
      FLOATING_ISLANDS_WORLD = WorldEffect.builder("floating_islands_world").modifyingWorldGen((var0) -> var0.setNoiseGenerationBase(NoiseGeneratorSettings.FLOATING_ISLANDS)).withCustomIcon("floating_islands_world").inSet(WORLD_TYPE).unlockedBy(UnlockCondition.mineWon((var0, var1) -> true)).register();
      AMPLIFIED = WorldEffect.builder("amplified").modifyingWorldGen((var0) -> var0.setNoiseGenerationBase(NoiseGeneratorSettings.AMPLIFIED)).inSet(WORLD_TYPE).withCustomIcon("amplified").unlockedAfter(FLOATING_ISLANDS_WORLD).unlockedBy(UnlockCondition.blockPlace((TriFunction)((var0, var1, var2) -> var2.getY() >= 100))).register();
      CAVE_WORLD = WorldEffect.builder("cave_world").modifyingWorldGen((var0) -> var0.setNoiseGenerationBase(NoiseGeneratorSettings.CAVES).withSpawnStrategy(MineSpawnStrategy.CAVE).changeDimensionType(DimensionType::withAmbientLight)).withCustomIcon("cave_world").inSet(WORLD_TYPE).unlockedAfter(FLOATING_ISLANDS_WORLD).unlockedBy(UnlockCondition.blockBreak((TriFunction)((var0, var1, var2) -> var2.getY() < 0))).register();
      SHATTERED_BLOCKS_WORLD = WorldEffect.builder("shattered_blocks_world").modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var1) -> var1.noiseRouter(NoiseRouterData.overworld(var0.registries().lookupOrThrow(Registries.DENSITY_FUNCTION), var0.registries().lookupOrThrow(Registries.NOISE), false, true)))).withCustomIcon("shattered_blocks").inSet(WORLD_TYPE).unlockedAfter(AMPLIFIED, CAVE_WORLD).unlockedBy(UnlockCondition.specialCompleted(true)).register();
      GRID_WORLD = WorldEffect.builder("grid_world").modifyingWorldGen((var0) -> var0.withCustomChunkGenerator((var0x, var1, var2) -> {
            RandomSource var3 = ((NoiseGeneratorSettings)var2.value()).getRandomSource().newInstance(((NoiseGeneratorSettings)var2.value()).salt());
            int var4 = 2 << var3.nextInt(3);
            int var5 = var3.nextIntBetweenInclusive(1, Math.min(var4 - 1, 3));
            int var6 = var3.nextIntBetweenInclusive(0, 64);
            return new GridChunkGenerator(var1, var2, var4, var5, var6, true);
         })).withCustomIcon("grid_world").inSet(WORLD_TYPE).unlockedAfter(SHATTERED_BLOCKS_WORLD).unlockedBy(UnlockCondition.specialCompleted(true)).register();
      DARK_CAVE_WORLD = WorldEffect.builder("dark_cave_world").modifyingWorldGen((var0) -> var0.setNoiseGenerationBase(NoiseGeneratorSettings.CAVES).withSpawnStrategy(MineSpawnStrategy.CAVE)).withCustomIcon("dark_cave_world").inSet(WORLD_TYPE).unlockedAfter(GRID_WORLD).unlockedBy(UnlockCondition.specialCompleted(true)).register();
      EXITS = mustHaveOnlyOne("exits");
      SURFACE_EXITS = WorldEffect.builder("surface_exits").withCustomIcon("surface_exits").modifyingWorldGen((var0) -> var0.changeBiomes((var0x) -> var0x.modifySpecialEffects((var0) -> var0.withExitType(BiomeSpecialEffects.ExitType.SURFACE)))).inSet(EXITS).incompatibleWith(CAVE_WORLD, DARK_CAVE_WORLD).unlockedByDefault().register();
      CAVE_EXITS = WorldEffect.builder("cave_exits").withCustomIcon("cave_exits").modifyingWorldGen((var0) -> var0.changeBiomes((var0x) -> var0x.modifySpecialEffects((var0) -> var0.withExitType(BiomeSpecialEffects.ExitType.CAVE)))).inSet(EXITS).unlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> var1.is(Blocks.DEEPSLATE) && var0.getRandom().nextFloat() < 0.01F))).unlockedBy(UnlockCondition.unlocked(CAVE_WORLD)).register();
      RARE_SURFACE_EXITS = WorldEffect.builder("rare_surface_exits").withCustomIcon("rare_surface_exits").modifyingWorldGen((var0) -> var0.changeBiomes((var0x) -> var0x.modifySpecialEffects((var0) -> var0.withExitType(BiomeSpecialEffects.ExitType.RARE_SURFACE)))).inSet(EXITS).incompatibleWith(CAVE_WORLD, DARK_CAVE_WORLD).unlockedBy(UnlockCondition.craftedItem((var0, var1, var2) -> var2.is(Items.EXIT_EYE))).register();
      EVENT_EXIT = WorldEffect.builder("event_exit").notRandomizable().neverUnlocked().register();
      BIOMES = mustHaveAtLeastOne("biome");
      PLAINS = WorldEffect.builder("plains").withItemModelOf(Items.GRASS_BLOCK).addBiomes(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW).unlockedByDefault().inSet(BIOMES).register();
      SAVANNAS = WorldEffect.builder("savannas").withItemModelOf(Items.ACACIA_SAPLING).addBiomes(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA).unlockedByDefault().inSet(BIOMES).register();
      FORESTS = WorldEffect.builder("forests").withItemModelOf(Items.OAK_SAPLING).addBiomes(Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.FLOWER_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.GROVE, Biomes.CHERRY_GROVE).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(BlockTags.LEAVES) && var0.getRandom().nextFloat() < 0.01F)).unlockedByWinning().inSet(BIOMES).register();
      TAIGAS = WorldEffect.builder("taigas").withItemModelOf(Items.SPRUCE_SAPLING).addBiomes(Biomes.TAIGA, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(BlockTags.LEAVES) && var0.getRandom().nextFloat() < 0.01F)).unlockedByWinning().inSet(BIOMES).register();
      JUNGLES = WorldEffect.builder("jungles").withItemModelOf(Items.JUNGLE_SAPLING).addBiomes(Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.SPARSE_JUNGLE).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(BlockTags.LEAVES) && var0.getRandom().nextFloat() < 0.01F)).unlockedByWinning().inSet(BIOMES).register();
      SNOWY = WorldEffect.builder("snowy").withItemModelOf(Items.SNOWBALL).addBiomes(Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA, Biomes.SNOWY_SLOPES, Biomes.SNOWY_BEACH, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.FROZEN_PEAKS, Biomes.FROZEN_RIVER).inSet(BIOMES).unlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> (var1.is(BlockTags.SNOW) || var1.is(BlockTags.ICE)) && var0.getRandom().nextFloat() < 0.05F))).unlockedByWinning().register();
      DESERT = WorldEffect.builder("desert").withItemModelOf(Items.SAND).addBiomes(Biomes.DESERT).inSet(BIOMES).unlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> var1.is(BlockTags.SAND) && var0.getRandom().nextFloat() < 0.01F))).unlockedByWinning().register();
      BADLANDS = WorldEffect.builder("badlands").withItemModelOf(Items.GRAY_TERRACOTTA).addBiomes(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS, Biomes.DRIPSTONE_CAVES).unlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> (var1.is(BlockTags.TERRACOTTA) || var1.is(Blocks.CLAY)) && var0.getRandom().nextFloat() < 0.05F))).unlockedByWinning().inSet(BIOMES).register();
      SWAMPS = WorldEffect.builder("swamps").withItemModelOf(Items.MANGROVE_ROOTS).addBiomes(Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.LUSH_CAVES).inSet(BIOMES).unlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> var1.is(Blocks.FIREFLY_BUSH)))).unlockedByWinning().register();
      DARK_FORESTS = WorldEffect.builder("dark_forests").withItemModelOf(Items.CREAKING_HEART).addBiomes(Biomes.DARK_FOREST, Biomes.PALE_GARDEN).inSet(BIOMES).unlockedAfter(FORESTS, JUNGLES, TAIGAS).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(BlockTags.LEAVES) && var0.getRandom().nextFloat() < 0.01F)).unlockedByWinning().register();
      ICE_SPIKES = WorldEffect.builder("ice_spikes").withItemModelOf(Items.PACKED_ICE).addBiomes(Biomes.ICE_SPIKES).inSet(BIOMES).unlockedBy(UnlockCondition.mineCompletedWith(true, SNOWY)).unlockedByWinning().register();
      MUSHROOM_FIELDS = WorldEffect.builder("mushroom_fields").withItemModelOf(Items.RED_MUSHROOM).addBiomes(Biomes.MUSHROOM_FIELDS).inSet(BIOMES).unlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> (var1.is(Blocks.BROWN_MUSHROOM) || var1.is(Blocks.RED_MUSHROOM)) && var0.getRandom().nextFloat() < 0.1F))).unlockedByWinning().register();
      NETHER_BARRENS = WorldEffect.builder("nether_barrens").withItemModelOf(Items.NETHERRACK).modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.seaLevel(var0x.seaLevel() - 64))).addBiomes(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS).inSet(BIOMES).unlockedBy(UnlockCondition.obtainedItem((var0, var1, var2) -> var2.is(Items.OBSIDIAN))).register();
      NETHER_FORESTS = WorldEffect.builder("nether_forests").withItemModelOf(Items.WARPED_FUNGUS).addBiomes(Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST).modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.seaLevel(var0x.seaLevel() - 64))).inSet(BIOMES).unlockedBy(UnlockCondition.playerKilledEntity(0.2F, EntityType.ENDERMAN)).register();
      DEEP_DARK = WorldEffect.builder("deep_dark").withItemModelOf(Items.SCULK).inSet(BIOMES).addBiomes(Biomes.DEEP_DARK).unlockedBy(UnlockCondition.blockBreak((TriFunction)((var0, var1, var2) -> var0.getRandom().nextFloat() < 0.1F && isDarkPlace(var0, var2)))).register();
      END = WorldEffect.builder("end").withItemModelOf(Items.END_STONE).inSet(BIOMES).modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.seaLevel(var0x.seaLevel() - 64))).addBiomes(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_BARRENS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS).unlockedBy(UnlockCondition.playerKilledEntity(0.5F, EntityType.BLAZE)).register();
      DRY_LAND = WorldEffect.builder("dry_land").modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.seaLevel(-64))).unlockedBy(UnlockCondition.obtainedItem((var0, var1, var2) -> var0.getBiome(var1.blockPosition()).is(BiomeTags.IS_BADLANDS) && var2.is(Items.LAVA_BUCKET))).withItemModelOf(Items.BUCKET).register();
      WATER_WORLD = WorldEffect.builder("water_world").modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.seaLevel(128))).incompatibleWith(DRY_LAND).unlockedBy(UnlockCondition.mineCompletedWith(true, DRY_LAND)).withItemModelOf(Items.WATER_BUCKET).register();
      PASSIVE_MOBS = mustHaveAtLeastOne("passive_mobs");
      PASSIVE_MOBS_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GREEN);
      SHEEP = WorldEffect.builder("sheep").withNameStyle(PASSIVE_MOBS_STYLE).unlockedByDefault().withItemModelOf(Items.SHEEP_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 12, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 4, 4))).register();
      COWS = WorldEffect.builder("cows").withNameStyle(PASSIVE_MOBS_STYLE).unlockedByDefault().withItemModelOf(Items.COW_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.COW, 4, 4))).register();
      PIGS = WorldEffect.builder("pigs").withNameStyle(PASSIVE_MOBS_STYLE).unlockedByDefault().withItemModelOf(Items.PIG_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.PIG, 4, 4))).register();
      CHICKENS = WorldEffect.builder("chickens").withNameStyle(PASSIVE_MOBS_STYLE).unlockedByDefault().withItemModelOf(Items.CHICKEN_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 4, 4))).register();
      FROGS = WorldEffect.builder("frogs").withNameStyle(PASSIVE_MOBS_STYLE).unlockedByDefault().withItemModelOf(Items.FROG_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.FROG, 4, 4))).register();
      FOXES = WorldEffect.builder("foxes").withNameStyle(PASSIVE_MOBS_STYLE).unlockedBy(UnlockCondition.obtainedItem((var0, var1, var2) -> var2.is(Items.SWEET_BERRIES))).withItemModelOf(Items.FOX_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.FOX, 1, 3))).register();
      GOATS = WorldEffect.builder("goats").withNameStyle(PASSIVE_MOBS_STYLE).unlockedBy(UnlockCondition.blockBreak((TriFunction)((var0, var1, var2) -> var2.getY() > 128 && (double)var0.getRandom().nextFloat() < 0.1))).withItemModelOf(Items.GOAT_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 1, 3))).register();
      OCELOTS = WorldEffect.builder("ocelots").withNameStyle(PASSIVE_MOBS_STYLE).unlockedBy(UnlockCondition.obtainedItem((var0, var1, var2) -> var2.is(Items.STRING))).alwaysRandomizable().withItemModelOf(Items.OCELOT_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 20, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 15, 15))).register();
      AXOLOTLS = WorldEffect.builder("axolotls").withNameStyle(PASSIVE_MOBS_STYLE).unlockedBy(UnlockCondition.obtainedItem((var0, var1, var2) -> var2.is(Items.AXOLOTL_BUCKET))).alwaysRandomizable().withItemModelOf(Items.AXOLOTL_SPAWN_EGG).inSet(PASSIVE_MOBS).addBiomes(Biomes.LUSH_CAVES).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.AXOLOTLS, 20, new MobSpawnSettings.SpawnerData(EntityType.AXOLOTL, 15, 15))).incompatibleWith(DRY_LAND).register();
      ARMADILLOS = WorldEffect.builder("armadillos").withNameStyle(PASSIVE_MOBS_STYLE).unlockedBy(UnlockCondition.craftedItem((var0, var1, var2) -> var2.is(Items.BRUSH))).withItemModelOf(Items.ARMADILLO_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 20, new MobSpawnSettings.SpawnerData(EntityType.ARMADILLO, 1, 3))).register();
      MOOSHROOMS = WorldEffect.builder("mooshrooms").withNameStyle(PASSIVE_MOBS_STYLE).unlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> (double)var0.getRandom().nextFloat() < 0.1 && (var1.is(Blocks.RED_MUSHROOM) || var1.is(Blocks.BROWN_MUSHROOM))))).withItemModelOf(Items.MOOSHROOM_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 20, new MobSpawnSettings.SpawnerData(EntityType.MOOSHROOM, 1, 3))).register();
      PANDAS = WorldEffect.builder("pandas").withNameStyle(PASSIVE_MOBS_STYLE).unlockedBy(UnlockCondition.usedItem((var0, var1, var2) -> var2.is(Items.BAMBOO))).withItemModelOf(Items.PANDA_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 100, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 3, 5))).register();
      PARROTS = WorldEffect.builder("parrots").withNameStyle(PASSIVE_MOBS_STYLE).unlockedBy(UnlockCondition.usedItem((var0, var1, var2) -> var2.is(Items.COOKIE))).withItemModelOf(Items.PARROT_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 100, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 3, 5))).register();
      RABBITS = WorldEffect.builder("rabbits").withNameStyle(PASSIVE_MOBS_STYLE).unlockedBy(UnlockCondition.playerTookDamage((var0, var1, var2, var3) -> var2.is(DamageTypes.FALL) && var3 > 10.0F)).withItemModelOf(Items.RABBIT_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 100, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 3, 5))).register();
      SNIFFERS = WorldEffect.builder("sniffers").withNameStyle(PASSIVE_MOBS_STYLE).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(BlockTags.FLOWERS) && var0.getRandom().nextFloat() < 0.05F)).withItemModelOf(Items.SNIFFER_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.SNIFFER, 1, 2))).register();
      STRIDERS = WorldEffect.builder("striders").withNameStyle(PASSIVE_MOBS_STYLE).unlockedBy(UnlockCondition.obtainedItem((var0, var1, var2) -> var2.is(Items.SADDLE))).withItemModelOf(Items.STRIDER_SPAWN_EGG).inSet(PASSIVE_MOBS).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 25, new MobSpawnSettings.SpawnerData(EntityType.STRIDER, 1, 2))).register();
      HOSTILE_MOBS = mustHaveAtLeastOne("hostile_mobs");
      HOSTILE_MOB_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);
      ZOMBIES = WorldEffect.builder("zombies").inSet(HOSTILE_MOBS).withItemModelOf(Items.ZOMBIE_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var0.getRandom().nextFloat() < 0.05F && var2.getType().getCategory() == MobCategory.MONSTER))).unlockedByWinning().alwaysRandomizable().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 95, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 4, 4))).register();
      SKELETONS = WorldEffect.builder("skeletons").inSet(HOSTILE_MOBS).withItemModelOf(Items.SKELETON_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var0.getRandom().nextFloat() < 0.05F && var2.getType().getCategory() == MobCategory.MONSTER))).unlockedByWinning().alwaysRandomizable().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 100, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 4, 4))).register();
      SPIDERS = WorldEffect.builder("spiders").inSet(HOSTILE_MOBS).withItemModelOf(Items.SPIDER_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var0.getRandom().nextFloat() < 0.05F && var2.getType().getCategory() == MobCategory.MONSTER))).unlockedByWinning().alwaysRandomizable().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 100, new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 4, 4))).register();
      CAVE_SPIDERS = WorldEffect.builder("cave_spiders").inSet(HOSTILE_MOBS).withItemModelOf(Items.CAVE_SPIDER_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var0.getRandom().nextFloat() < 0.2F && var2 instanceof Spider))).unlockedByWinning().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 50, new MobSpawnSettings.SpawnerData(EntityType.CAVE_SPIDER, 4, 4))).register();
      CREEPERS = WorldEffect.builder("creepers").inSet(HOSTILE_MOBS).withItemModelOf(Items.CREEPER_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var0.getRandom().nextFloat() < 0.05F && var2.getType().getCategory() == MobCategory.MONSTER))).unlockedByWinning().alwaysRandomizable().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 100, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 4, 4))).register();
      SLIMES = WorldEffect.builder("slimes").inSet(HOSTILE_MOBS).withItemModelOf(Items.SLIME_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var0.getRandom().nextFloat() < 0.05F && var2.getType().getCategory() == MobCategory.MONSTER))).unlockedByWinning().alwaysRandomizable().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 100, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 4, 4))).register();
      ENDERMEN = WorldEffect.builder("endermen").inSet(HOSTILE_MOBS).withItemModelOf(Items.ENDERMAN_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedAfter(ZOMBIES, SKELETONS, CREEPERS, SPIDERS).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var0.getRandom().nextFloat() < 0.05F && var2.getType().getCategory() == MobCategory.MONSTER))).unlockedByWinning().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 10, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 1, 4))).register();
      WITCHES = WorldEffect.builder("witches").inSet(HOSTILE_MOBS).withItemModelOf(Items.WITCH_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedAfter(ZOMBIES, SKELETONS, CREEPERS, SPIDERS).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var0.getRandom().nextFloat() < 0.05F && var2.getType().getCategory() == MobCategory.MONSTER))).unlockedByWinning().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 5, new MobSpawnSettings.SpawnerData(EntityType.WITCH, 1, 1))).register();
      MAGMA_CUBES = WorldEffect.builder("magma_cubes").inSet(HOSTILE_MOBS).withItemModelOf(Items.MAGMA_CUBE_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy(UnlockCondition.obtainedItem((var0, var1, var2) -> var2.is(Items.MAGMA_CREAM) || var2.is(Items.MAGMA_BLOCK))).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 100, new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 2, 5))).register();
      BLAZES = WorldEffect.builder("blazes").inSet(HOSTILE_MOBS).withItemModelOf(Items.BLAZE_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> var1.is(Blocks.NETHER_QUARTZ_ORE) || var1.is(Blocks.NETHER_GOLD_ORE)))).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 10, new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 2, 3))).register();
      BREEZES = WorldEffect.builder("breezes").inSet(HOSTILE_MOBS).withItemModelOf(Items.BREEZE_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy(UnlockCondition.obtainedItem((var0, var1, var2) -> var2.is(Items.TRIAL_KEY))).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 10, new MobSpawnSettings.SpawnerData(EntityType.BREEZE, 2, 3))).register();
      PILLAGERS = WorldEffect.builder("pillagers").inSet(HOSTILE_MOBS).withItemModelOf(Items.PILLAGER_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 10, new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 1, 5))).register();
      VINDICATORS = WorldEffect.builder("vindicators").inSet(HOSTILE_MOBS).withItemModelOf(Items.VINDICATOR_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 10, new MobSpawnSettings.SpawnerData(EntityType.VINDICATOR, 1, 1))).register();
      EVOKERS = WorldEffect.builder("evokers").inSet(HOSTILE_MOBS).withItemModelOf(Items.EVOKER_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 10, new MobSpawnSettings.SpawnerData(EntityType.EVOKER, 1, 1))).register();
      RAVAGERS = WorldEffect.builder("ravagers").inSet(HOSTILE_MOBS).withItemModelOf(Items.RAVAGER_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).unlockedBy().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 1, new MobSpawnSettings.SpawnerData(EntityType.RAVAGER, 1, 1))).register();
      ILLUSIONERS = WorldEffect.builder("illusioners").inSet(HOSTILE_MOBS).withNameStyle(HOSTILE_MOB_STYLE).withCustomIcon("illusioners").unlockedBy().modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 1, new MobSpawnSettings.SpawnerData(EntityType.ILLUSIONER, 1, 1))).register();
      GUARDIANS = WorldEffect.builder("guardians").inSet(HOSTILE_MOBS).withNameStyle(HOSTILE_MOB_STYLE).withItemModelOf(Items.GUARDIAN_SPAWN_EGG).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var2.getType().is(EntityTypeTags.AQUATIC)))).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 10, new MobSpawnSettings.SpawnerData(EntityType.GUARDIAN, 1, 1))).register();
      ENDERMITES = WorldEffect.builder("endermites").inSet(HOSTILE_MOBS).withNameStyle(HOSTILE_MOB_STYLE).withItemModelOf(Items.ENDERMITE_SPAWN_EGG).unlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> var1.is(Blocks.END_STONE) && (double)var0.getRandom().nextFloat() < 0.1))).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 100, new MobSpawnSettings.SpawnerData(EntityType.ENDERMITE, 2, 4))).register();
      SHULKERS = WorldEffect.builder("shulkers").inSet(HOSTILE_MOBS).withNameStyle(HOSTILE_MOB_STYLE).withItemModelOf(Items.SHULKER_SPAWN_EGG).unlockedBy(UnlockCondition.playerKilledEntity(0.1F, EntityType.ENDERMITE)).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 50, new MobSpawnSettings.SpawnerData(EntityType.SHULKER, 1, 1))).register();
      GHASTS = WorldEffect.builder("ghasts").inSet(HOSTILE_MOBS).withNameStyle(HOSTILE_MOB_STYLE).withItemModelOf(Items.GHAST_SPAWN_EGG).unlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> var1.is(Blocks.BONE_BLOCK)))).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 50, new MobSpawnSettings.SpawnerData(EntityType.GHAST, 1, 1))).register();
      ZOMBIFIED_PIGLINS = WorldEffect.builder("zombified_piglins").inSet(HOSTILE_MOBS).withNameStyle(HOSTILE_MOB_STYLE).withItemModelOf(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> {
         boolean var10000;
         if (var2 instanceof Pig var3) {
            if (var3.isOnFire()) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }))).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 100, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 4, 4))).register();
      PIGLINS = WorldEffect.builder("piglins").inSet(HOSTILE_MOBS).withNameStyle(HOSTILE_MOB_STYLE).withItemModelOf(Items.PIGLIN_SPAWN_EGG).unlockedBy(UnlockCondition.fedAnimal((var0, var1, var2, var3) -> {
         boolean var10000;
         if (var2 instanceof Pig var4) {
            if (var3.is(Items.GOLDEN_CARROT)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      })).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 100, new MobSpawnSettings.SpawnerData(EntityType.PIGLIN, 4, 4)).addMobSpawn(MobCategory.MONSTER, 2, new MobSpawnSettings.SpawnerData(EntityType.PIGLIN_BRUTE, 1, 1)).changeDimensionType((var0x) -> var0x.asPiglinSafe())).register();
      WITHER_SKELETONS = WorldEffect.builder("wither_skeletons").inSet(HOSTILE_MOBS).withNameStyle(HOSTILE_MOB_STYLE).withItemModelOf(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG).unlockedBy(UnlockCondition.playerKilledEntity(0.1F, EntityType.SKELETON)).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 1, 3))).register();
      BEES = WorldEffect.builder("bees").inSet(HOSTILE_MOBS).withNameStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)).withItemModelOf(Items.BEE_SPAWN_EGG).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var2 instanceof Bee))).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.CREATURE, 250, new MobSpawnSettings.SpawnerData(EntityType.BEE, 5, 5))).register();
      HOGLINS = WorldEffect.builder("hoglins").inSet(HOSTILE_MOBS).withNameStyle(HOSTILE_MOB_STYLE).withItemModelOf(Items.HOGLIN_SPAWN_EGG).unlockedBy(UnlockCondition.playerKilledEntity(0.01F, EntityType.PIG)).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(EntityType.HOGLIN, 1, 3)).changeDimensionType((var0x) -> var0x.asPiglinSafe())).register();
      ZOGLINS = WorldEffect.builder("zoglins").inSet(HOSTILE_MOBS).withNameStyle(HOSTILE_MOB_STYLE).withItemModelOf(Items.ZOGLIN_SPAWN_EGG).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> {
         boolean var10000;
         if (var2 instanceof Zombie var3) {
            if (var3.getBlockStateOn().is(BlockTags.NYLIUM)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }))).modifyingWorldGen((var0) -> var0.addMobSpawn(MobCategory.MONSTER, 2, new MobSpawnSettings.SpawnerData(EntityType.ZOGLIN, 1, 3))).register();
      ICY = WorldEffect.builder("icy").withNameStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)).withItemModelOf(Items.BLUE_ICE).unlockedBy(UnlockCondition.usedItem((var0, var1, var2) -> var2.is(ItemTags.BOATS))).xpModifier(2.0F).register();
      BOUNCY = WorldEffect.builder("bouncy").withNameStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)).withItemModelOf(Items.SLIME_BLOCK).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var2 instanceof Slime))).xpModifier(2.0F).register();
      BASE_STONE = mustHaveOnlyOne("base_stone");
      STONE = WorldEffect.builder("base_stone").inSet(BASE_STONE).withItemModelOf(Items.STONE).unlockedByDefault().register();
      BLACKSTONE = WorldEffect.builder("base_blackstone").inSet(BASE_STONE).withItemModelOf(Items.BLACKSTONE).modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.defaultBlock(Blocks.BLACKSTONE.defaultBlockState()))).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(Blocks.BLACKSTONE) && var0.getRandom().nextFloat() < 0.1F)).unlockedByWinning().alwaysRandomizable().register();
      DIORITE = WorldEffect.builder("base_diorite").inSet(BASE_STONE).withItemModelOf(Items.DIORITE).modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.defaultBlock(Blocks.DIORITE.defaultBlockState()))).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(Blocks.DIORITE) && var0.getRandom().nextFloat() < 0.1F)).unlockedByWinning().alwaysRandomizable().register();
      ANDESITE = WorldEffect.builder("base_andesite").inSet(BASE_STONE).withItemModelOf(Items.ANDESITE).modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.defaultBlock(Blocks.ANDESITE.defaultBlockState()))).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(Blocks.ANDESITE) && var0.getRandom().nextFloat() < 0.1F)).unlockedByWinning().alwaysRandomizable().register();
      GRANITE = WorldEffect.builder("base_granite").inSet(BASE_STONE).withItemModelOf(Items.GRANITE).modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.defaultBlock(Blocks.GRANITE.defaultBlockState()))).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(Blocks.GRANITE) && var0.getRandom().nextFloat() < 0.1F)).unlockedByWinning().alwaysRandomizable().register();
      TUFF = WorldEffect.builder("base_tuff").inSet(BASE_STONE).withItemModelOf(Items.TUFF).modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.defaultBlock(Blocks.TUFF.defaultBlockState()))).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(Blocks.TUFF) && var0.getRandom().nextFloat() < 0.1F)).unlockedByWinning().alwaysRandomizable().register();
      DEEPSLATE = WorldEffect.builder("base_deepslate").inSet(BASE_STONE).withItemModelOf(Items.DEEPSLATE).modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.defaultBlock(Blocks.DEEPSLATE.defaultBlockState()))).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(Blocks.DEEPSLATE) && var0.getRandom().nextFloat() < 0.1F)).unlockedByWinning().alwaysRandomizable().register();
      END_STONE = WorldEffect.builder("base_end_stone").inSet(BASE_STONE).withItemModelOf(Items.END_STONE).modifyingWorldGen((var0) -> var0.changeNoiseGeneration((var0x) -> var0x.defaultBlock(Blocks.END_STONE.defaultBlockState()))).unlockedBy(UnlockCondition.blockDrops((var0, var1) -> var1.is(Blocks.END_STONE) && var0.getRandom().nextFloat() < 0.1F)).register();
      CHALLENGE_STYLE = Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE);
      ONE_HP = WorldEffect.builder("one_hp").withNameStyle(CHALLENGE_STYLE).withItemModelOf(Items.POTION).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var1.getHealth() <= 1.0F))).xpModifier(4.0F).register();
      WEDNESDAY_FROGS = WorldEffect.builder("wednesday_frogs").withNameStyle(CHALLENGE_STYLE).withItemModelOf(Items.FROG_SPAWN_EGG).unlockedBy(UnlockCondition.playerKilledEntity((TriFunction)((var0, var1, var2) -> var2 instanceof Frog))).xpModifier(1.2F).register();
      UNIVERSAL_ANGER = WorldEffect.builder("universal_anger").withNameStyle(CHALLENGE_STYLE).withItemModelOf(Items.ROTTEN_FLESH).unlockedBy(UnlockCondition.mineCompletedWith(true, BEES)).xpModifier(2.0F).register();
      SOUL_LINK = WorldEffect.builder("soul_link").withNameStyle(CHALLENGE_STYLE).withItemModelOf(Items.LEAD).unlockedBy(UnlockCondition.mineCompletedWith(true, ONE_HP)).xpModifier(2.0F).multiplayerOnly().register();
      ETERNAL_NIGHT = WorldEffect.builder("eternal_night").withItemModelOf(Items.BLUE_DYE).withNameStyle(CHALLENGE_STYLE).onMineEnter((var0) -> {
         var0.theGame().overworld().setDayTime(18000L);
         ((GameRules.BooleanValue)var0.theGame().getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(false, var0.theGame());
      }).onMineLeave((var0) -> ((GameRules.BooleanValue)var0.theGame().getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(true, var0.theGame())).incompatibleWith(CAVE_WORLD, DARK_CAVE_WORLD).xpModifier(5.0F).requiresUnlockCount(20).register();
      ETERNAL_RAIN = WorldEffect.builder("eternal_rain").withItemModelOf(Items.BLACK_DYE).withNameStyle(CHALLENGE_STYLE).onMineEnter((var0) -> {
         var0.setRainLevel(1.0F);
         ((GameRules.BooleanValue)var0.theGame().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE)).set(false, var0.theGame());
      }).onMineLeave((var0) -> {
         var0.setRainLevel(0.0F);
         ((GameRules.BooleanValue)var0.theGame().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE)).set(true, var0.theGame());
      }).unlockedBy(UnlockCondition.mineCompletedWith(true, ETERNAL_NIGHT)).incompatibleWith(CAVE_WORLD, DARK_CAVE_WORLD).xpModifier(2.0F).register();
      ETERNAL_LIGHTNING = WorldEffect.builder("eternal_lightning").withItemModelOf(Items.YELLOW_DYE).withNameStyle(CHALLENGE_STYLE).onMineEnter((var0) -> {
         var0.setThunderLevel(1.0F);
         var0.setRainLevel(1.0F);
         ((GameRules.BooleanValue)var0.theGame().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE)).set(false, var0.theGame());
      }).onMineLeave((var0) -> {
         var0.setThunderLevel(0.0F);
         var0.setRainLevel(0.0F);
         ((GameRules.BooleanValue)var0.theGame().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE)).set(true, var0.theGame());
      }).unlockedBy(UnlockCondition.mineCompletedWith(true, ETERNAL_RAIN)).incompatibleWith(CAVE_WORLD, DARK_CAVE_WORLD).xpModifier(5.0F).register();
      INSOMIAC = WorldEffect.builder("insomniacs").withItemModelOf(Items.PHANTOM_SPAWN_EGG).withNameStyle(HOSTILE_MOB_STYLE).onPlayerMineEnter((var0) -> var0.getStats().setValue(var0, Stats.CUSTOM.get(Stats.TIME_SINCE_REST), 72000)).unlockedBy(UnlockCondition.mineCompletedWith(true, ETERNAL_NIGHT)).incompatibleWith(CAVE_WORLD, DARK_CAVE_WORLD).xpModifier(2.0F).register();
      NO_DROPS = WorldEffect.builder("no_drops").withNameStyle(CHALLENGE_STYLE).withItemModelOf(Items.DIRT).requiresUnlockCount(20).xpModifier(5.0F).register();
      ULTRAWARM = WorldEffect.builder("ultrawarm").withNameStyle(CHALLENGE_STYLE).withItemModelOf(Items.LAVA_BUCKET).modifyingWorldGen((var0) -> var0.changeDimensionType(DimensionType::madeUltrawarm).changeBiomes((var0x) -> var0x.modifyClimate((var0) -> var0.temperature(var0.temperature() + 1.0F)).modifySpecialEffects((var0) -> var0.skyColor(ARGB.color(234, 178, 255)))).changeNoiseGeneration((var0x) -> var0x.defaultFluid(Blocks.LAVA.defaultBlockState()))).unlockedBy(UnlockCondition.playerTookDamage((var0, var1, var2, var3) -> var2.is(DamageTypes.LAVA))).unlockedByWinning().requiresUnlockCount(20).register();
      EXPLOSIVE_TRAPS = WorldEffect.builder("explosive_traps").withNameStyle(CHALLENGE_STYLE).withItemModelOf(Items.TNT).xpModifier(1.5F).incompatibleWith(WATER_WORLD, CAVE_WORLD, DARK_CAVE_WORLD).unlockedBy(UnlockCondition.mineWon((var0, var1) -> var0.theGame().getAllLevels().size() > 10)).register();
      FISH_OUT_OF_WATER = WorldEffect.builder("fish_out_of_water").withItemModelOf(Items.TROPICAL_FISH).withNameStyle(CHALLENGE_STYLE).unlockedBy(UnlockCondition.usedItem((var0, var1, var2) -> var2.is(ItemTags.FISHING_ENCHANTABLE))).incompatibleWith(DRY_LAND).xpModifier(5.0F).notRandomizable().register();
      KUIPER_WORLD = WorldEffect.builder("kuiper_world").onMineEnter((var0) -> {
         var0.theGame().overworld().setDayTime(18000L);
         ((GameRules.BooleanValue)var0.theGame().getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(false, var0.theGame());
      }).onMineLeave((var0) -> ((GameRules.BooleanValue)var0.theGame().getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(true, var0.theGame())).modifyingWorldGen((var0) -> var0.withCustomChunkGenerator((var0x, var1, var2) -> {
            HolderLookup.RegistryLookup var3 = var0x.lookupOrThrow(Registries.BIOME);
            FlatLevelGeneratorSettings var4 = new FlatLevelGeneratorSettings(Optional.of(HolderSet.direct()), var3.getOrThrow(Biomes.THE_VOID), List.of());
            var4.getLayersInfo().add(new FlatLayerInfo(1, Blocks.AIR));
            var4.updateLayers();
            return new FlatLevelSource(var4);
         })).neverUnlocked().withCustomIcon("kuiper_world").register();
      ENDER_DRAGON_BOSS_FIGHT = WorldEffect.builder("ender_dragon_boss_fight").withItemModelOf(Items.ENDER_DRAGON_SPAWN_EGG).onMineEnter((var0) -> var0.startEvent(new EndDragonBattle())).modifyingWorldGen((var0) -> var0.changeBiomes((var1) -> var1.modifyGenerationSettings((var1x) -> var1x.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, var0.getOrThrow(EndPlacements.END_PLATFORM))))).neverUnlocked().notRandomizable().incompatibleWith(CAVE_WORLD, DARK_CAVE_WORLD).register();
      ANGRY_GHAST_BOSS_FIGHT = WorldEffect.builder("angry_ghast_boss_fight").withItemModelOf(Items.GHAST_TEAR).onMineEnter((var0) -> var0.startEvent(Battle.builder(var0, "angry_ghast").withWave((var0x) -> var0x.spawns((var0) -> var0.type(EntityType.ANGRY_GHAST).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(25, 25, 25)))).noBossBar().countdownLabel(Component.translatable("world.event.angry_ghast")).ticksDelay(1200)).build())).neverUnlocked().notRandomizable().register();
      RAID = WorldEffect.builder("raid").withItemModelOf(Items.OMINOUS_BOTTLE).onMineEnter((var0) -> var0.startEvent(new RaidEvent())).onPlayerMineEnter((var0) -> var0.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, -1))).neverUnlocked().notRandomizable().register();
      WITHER_BOSS_FIGHT = WorldEffect.builder("wither_boss_fight").withItemModelOf(Items.WITHER_SPAWN_EGG).onMineEnter((var0) -> var0.startEvent(Battle.builder(var0, "wither").withWave((var0x) -> var0x.spawns((var0) -> var0.type(EntityType.WITHER).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).noBossBar().countdownLabel(Component.translatable("world.event.wither")).ticksDelay(600)).build())).neverUnlocked().notRandomizable().register();
      THE_ENDERMAN_BOSS_FIGHT = WorldEffect.builder("the_enderman_boss_fight").withItemModelOf(Items.ENDER_PEARL).onMineEnter((var0) -> var0.startEvent(Battle.builder(var0, "enderman").withWave((var0x) -> var0x.spawns((var0) -> var0.type(EntityType.ENDERMAN).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).countdownLabel(Component.translatable("world.event.enderman")).ticksDelay(600)).build())).neverUnlocked().notRandomizable().register();
      SPOOKY_SCARY_SKELETONS = WorldEffect.builder("spooky_scary_skeletons_boss_fight").withItemModelOf(Items.SKELETON_SKULL).onMineEnter((var0) -> var0.startEvent(Battle.builder(var0, "spooky_scary_skeletons").withWave((var0x) -> var0x.spawns((var0) -> var0.type(EntityType.SKELETON).count(10).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).ticksDelay(600)).withWave((var0x) -> var0x.spawns((var0) -> var0.type(EntityType.WITHER_SKELETON).count(10).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).ticksDelay(600)).withWave((var0x) -> var0x.spawns((var0) -> var0.type(EntityType.WITHER_SKELETON).count(20).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).spawns((var0) -> var0.type(EntityType.SKELETON).count(20).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).ticksDelay(600)).build())).neverUnlocked().notRandomizable().register();
      WARDEN_BOSS_FIGHT = WorldEffect.builder("warden_boss_fight").withItemModelOf(Items.WARDEN_SPAWN_EGG).onMineEnter((var0) -> var0.startEvent(Battle.builder(var0, "warden").withWave((var0x) -> var0x.spawns((var0) -> var0.type(EntityType.WARDEN).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.WARDEN_ARENA))).noBossBar().countdownLabel(Component.translatable("world.event.warden"))).build())).unlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> var1.is(Blocks.AMETHYST_BLOCK) || var1.is(Blocks.AMETHYST_CLUSTER) || var1.is(Blocks.BUDDING_AMETHYST) || var1.is(Blocks.SMALL_AMETHYST_BUD) || var1.is(Blocks.MEDIUM_AMETHYST_BUD) || var1.is(Blocks.LARGE_AMETHYST_BUD)))).notRandomizable().register();
      SMALL_BUT_DEADLY = WorldEffect.builder("small_but_deadly_boss_fight").withItemModelOf(Items.SILVERFISH_SPAWN_EGG).addBiomes(Biomes.WINDSWEPT_GRAVELLY_HILLS).onMineEnter((var0) -> var0.startEvent(Battle.builder(var0, "small_but_deadly").withWave((var0x) -> var0x.spawns((var0) -> var0.type(EntityType.SILVERFISH).count(25).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).ticksDelay(300)).withWave((var0x) -> var0x.spawns((var0) -> var0.type(EntityType.SILVERFISH).count(25).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).spawns((var0) -> var0.type(EntityType.ZOMBIE).useBabyMobs(true).count(3).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).ticksDelay(600)).withWave((var0x) -> var0x.spawns((var0) -> var0.type(EntityType.SILVERFISH).count(30).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).spawns((var0) -> var0.type(EntityType.ZOMBIE).useBabyMobs(true).count(4).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).spawns((var0) -> var0.type(EntityType.VEX).count(3).withSpawnStrategy((var0x) -> var0x.type(Battle.SpawnType.NEAR_PLAYER).range(40).offset(new BlockPos(0, 3, 0)))).ticksDelay(1200)).build())).neverUnlocked().notRandomizable().register();
   }
}
